package studio.bonodigital.siagaai.ui.home

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import studio.bonodigital.siagaai.R
import studio.bonodigital.siagaai.api.ApiClient
import studio.bonodigital.siagaai.data.model.CapAlert
import studio.bonodigital.siagaai.data.model.ChecklistItem
import studio.bonodigital.siagaai.data.model.ChecklistUiItem
import studio.bonodigital.siagaai.databinding.FragmentHomeBinding
import studio.bonodigital.siagaai.repository.BmkgRepository
import studio.bonodigital.siagaai.util.EdukasiCondition

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val uiItems = mutableListOf<ChecklistUiItem>()

    private val bmkgRepository by lazy {
        BmkgRepository(ApiClient.api)
    }
    private var lastCapAlert: CapAlert? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadGempaTerakhir()
        loadPeringatanDiniCuaca()

        buildChecklist()
        loadChecklist()

        setupRecyclerView()

        binding.cardGempa.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val (condition, area, source) = bmkgRepository.determineCondition()
                val bundle = Bundle().apply {
                    putString("condition", condition)
                    area?.let { putString("area", it) }
                    putString("source", source)
                }

                findNavController().navigate(
                    R.id.EdukasiFragment, bundle, NavOptions.Builder().setPopUpTo(R.id.nav_graph, false).setLaunchSingleTop(true).build()
                )
            }
        }

        binding.cardCap.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val alert = lastCapAlert ?: return@launch

                val condition = determineCapCondition(alert)

                val bundle = Bundle().apply {
                    putString("condition", condition)
                    alert.area?.let { putString("area", it) }
                    putString("source", "BMKG")
                }

                findNavController().navigate(
                    R.id.EdukasiFragment, bundle, NavOptions.Builder().setPopUpTo(R.id.nav_graph, false).setLaunchSingleTop(true).build()
                )
            }
        }
    }

    private fun loadChecklist() {
        val prefs = requireContext().getSharedPreferences("siaga_prefs", Context.MODE_PRIVATE)

        uiItems.forEach {
            if (it is ChecklistUiItem.Item) {
                it.data.isChecked = prefs.getBoolean("item_${it.data.id}", false)
            }
        }
    }

    private fun setupRecyclerView() {
        val adapter = ChecklistAdapter(uiItems) {
            saveChecklist()
            updateReadinessProgress()
        }

        binding.rvChecklist.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChecklist.adapter = adapter

        updateReadinessProgress()
    }

    private fun saveChecklist() {
        val prefs = requireContext().getSharedPreferences("siaga_prefs", Context.MODE_PRIVATE)
        prefs.edit {

            uiItems.forEach {
                if (it is ChecklistUiItem.Item) {
                    putBoolean("item_${it.data.id}", it.data.isChecked)
                }
            }
        }
    }

    private fun updateReadinessProgress() {
        val items = uiItems.filterIsInstance<ChecklistUiItem.Item>()
        val total = items.size
        val checked = items.count { it.data.isChecked }

        val progress = (checked * 100) / total

        binding.progressReadiness.progress = progress
        binding.tvReadinessStatus.text = getReadinessMessage(progress)

        val color = when (progress) {
            in 0..30 -> Color.RED
            in 31..70 -> "#FFA000".toColorInt() // orange
            else -> "#4CAF50".toColorInt() // green
        }
        binding.progressReadiness.progressTintList = ColorStateList.valueOf(color)

    }

    private fun getReadinessMessage(progress: Int): String {
        return when (progress) {
            in 0..30 -> getString(R.string.readiness_message_30)
            in 31..70 -> getString(R.string.readiness_message_70)
            in 71..99 -> getString(R.string.readiness_message_99)
            100 -> getString(R.string.readiness_message_100)
            else -> ""
        }
    }

    private fun buildChecklist() {
        uiItems.clear()

        uiItems.add(ChecklistUiItem.Section(getString(R.string.checklist_section_1)))
        uiItems.add(
            ChecklistUiItem.Item(
                ChecklistItem(
                    1, getString(R.string.checklist_section_1_1_title), getString(R.string.checklist_section_1_1_desc), false
                )
            )
        )
        uiItems.add(
            ChecklistUiItem.Item(
                ChecklistItem(
                    2, getString(R.string.checklist_section_1_2_title), getString(R.string.checklist_section_1_2_desc), false
                )
            )
        )
        uiItems.add(
            ChecklistUiItem.Item(
                ChecklistItem(
                    3, getString(R.string.checklist_section_1_3_title), getString(R.string.checklist_section_1_3_desc), false
                )
            )
        )

        uiItems.add(ChecklistUiItem.Section(getString(R.string.checklist_section_2)))
        uiItems.add(
            ChecklistUiItem.Item(
                ChecklistItem(
                    4, getString(R.string.checklist_section_2_1_title), getString(R.string.checklist_section_2_1_desc), false
                )
            )
        )
        uiItems.add(
            ChecklistUiItem.Item(
                ChecklistItem(
                    5, getString(R.string.checklist_section_2_2_title), getString(R.string.checklist_section_2_2_desc), false
                )
            )
        )

        uiItems.add(ChecklistUiItem.Section(getString(R.string.checklist_section_3)))
        uiItems.add(
            ChecklistUiItem.Item(
                ChecklistItem(
                    6, getString(R.string.checklist_section_3_1_title), getString(R.string.checklist_section_3_1_desc), false
                )
            )
        )
        uiItems.add(
            ChecklistUiItem.Item(
                ChecklistItem(
                    7, getString(R.string.checklist_section_3_2_title), getString(R.string.checklist_section_3_2_desc), false
                )
            )
        )
    }

    private fun loadGempaTerakhir() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val gempa = ApiClient.api.getGempaTerakhir()

                val text = buildString {
                    if (!gempa.magnitudo.isNullOrEmpty()) {
                        append(getString(R.string.magnitudo, gempa.magnitudo))
                    }
                    if (!gempa.wilayah.isNullOrEmpty()) {
                        append(getString(R.string.wilayah_gempa, gempa.wilayah))
                    }
                    if (!gempa.tanggal.isNullOrEmpty() && !gempa.jam.isNullOrEmpty()) {
                        append(getString(R.string.waktu_gempa, gempa.tanggal, gempa.jam))
                    }
                }

                binding.tvGempaContent.text = text.ifEmpty { getString(R.string.data_gempa_tidak_tersedia_saat_ini) }

            } catch (_: Exception) {
                binding.tvGempaContent.text = getString(R.string.data_gempa_tidak_tersedia_saat_ini)
            }
        }
    }

    private fun loadPeringatanDiniCuaca() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val cap = ApiClient.api.getPeringatanDini()
                val alerts = cap.alerts ?: emptyList()

                if (alerts.isNotEmpty()) {
                    val alert = alerts.first()
                    lastCapAlert = alert

                    binding.tvCapContent.text = alert.headline ?: getString(R.string.peringatan_dini_cuaca_dari_bmkg)
                } else {
                    lastCapAlert = null
                    binding.tvCapContent.text = getString(R.string.tidak_ada_peringatan_dini_cuaca_dari_bmkg)
                }
            } catch (_: Exception) {
                lastCapAlert = null
                binding.tvCapContent.text = getString(R.string.data_peringatan_dini_cuaca_tidak_tersedia)
            }
        }
    }

    private fun determineCapCondition(alert: CapAlert): String {
        val headline = alert.headline?.lowercase() ?: ""
        return when {
            headline.contains("hujan") -> EdukasiCondition.HUJAN
            headline.contains("panas") -> EdukasiCondition.PANAS
            else -> EdukasiCondition.EKSTREM
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}