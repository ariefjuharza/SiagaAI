package studio.bonodigital.jagratara.ui.edukasi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import studio.bonodigital.jagratara.R
import studio.bonodigital.jagratara.data.model.EdukasiItem
import studio.bonodigital.jagratara.databinding.FragmentEdukasiBinding
import studio.bonodigital.jagratara.util.EdukasiCondition

class EdukasiFragment : Fragment() {

    private var _binding: FragmentEdukasiBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: EdukasiAdapter
    private var currentCondition: String = EdukasiCondition.UMUM
    private var currentArea: String? = null
    private var isContextual: Boolean = false
    private var youTubePlayer: YouTubePlayer? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEdukasiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parseArguments()
        setupTabs()
        setupRecyclerView()
        renderConditionHeader()
        renderEdukasi()
        initVideoPlayer()

    }

    private fun parseArguments() {
        arguments?.let {
            currentCondition = it.getString("condition", EdukasiCondition.UMUM)
            currentArea = it.getString("area")
            isContextual = true
        } ?: run {
            currentCondition = EdukasiCondition.UMUM
            currentArea = null
            isContextual = false
        }
    }

    private fun setupTabs() {
        val tabs = listOf(
            "UMUM" to EdukasiCondition.UMUM,
            "GEMPA" to EdukasiCondition.GEMPA,
            "HUJAN" to EdukasiCondition.HUJAN,
            "PANAS" to EdukasiCondition.PANAS,
            "EKSTREM" to EdukasiCondition.EKSTREM
        )

        tabs.forEach { (title, _) ->
            binding.tabTopics.addTab(
                binding.tabTopics.newTab().setText(title)
            )
        }

        val defaultIndex = tabs.indexOfFirst { it.second == currentCondition }.takeIf { it >= 0 } ?: 0

        binding.tabTopics.getTabAt(defaultIndex)?.select()

        binding.tabTopics.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                currentCondition = tabs[tab.position].second
                isContextual = false
                currentArea = null

                renderConditionHeader()
                renderEdukasi()

                val newVideoId = getYoutubeVideoId(currentCondition)

                if (newVideoId == null) {
                    binding.youtubePlayerView.visibility = View.GONE
                    return
                }

                binding.youtubePlayerView.visibility = View.VISIBLE
                youTubePlayer?.cueVideo(newVideoId, 0f)

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setupRecyclerView() {
        adapter = EdukasiAdapter(emptyList())
        binding.rvEdukasi.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEdukasi.adapter = adapter
    }

    private fun renderConditionHeader() {
        if (!isContextual) {
            when (currentCondition) {
                EdukasiCondition.HUJAN -> {
                    binding.tvConditionTitle.text = getString(R.string.edukasi_kesiapsiagaan_cuaca_hujan)
                    binding.tvConditionDesc.text =
                        getString(R.string.informasi_kesiapsiagaan_untuk_menghadapi_kondisi_hujan_yang_berpotensi_berdampak)
                }

                EdukasiCondition.GEMPA -> {
                    binding.tvConditionTitle.text = getString(R.string.edukasi_kesiapsiagaan_gempa_bumi)
                    binding.tvConditionDesc.text = getString(R.string.informasi_umum_untuk_membantu_memahami_dan_menghadapi_kejadian_gempa_bumi)
                }

                EdukasiCondition.PANAS -> {
                    binding.tvConditionTitle.text = getString(R.string.edukasi_kesiapsiagaan_cuaca_panas)
                    binding.tvConditionDesc.text =
                        getString(R.string.informasi_untuk_membantu_menjaga_kesehatan_saat_suhu_udara_lebih_tinggi_dari_biasanya)
                }

                EdukasiCondition.EKSTREM -> {
                    binding.tvConditionTitle.text = getString(R.string.edukasi_kesiapsiagaan_cuaca_ekstrem)
                    binding.tvConditionDesc.text = getString(R.string.informasi_umum_untuk_memahami_dan_bersiap_menghadapi_kondisi_cuaca_ekstrem)
                }

                else -> {
                    binding.tvConditionTitle.text = getString(R.string.edukasi_kesiapsiagaan_bencana)
                    binding.tvConditionDesc.text =
                        getString(R.string.informasi_dasar_untuk_membantu_anda_lebih_siap_menghadapi_berbagai_kondisi_kebencanaan)
                }
            }
            return
        }

        when (currentCondition) {
            EdukasiCondition.HUJAN -> {
                binding.tvConditionTitle.text = getString(R.string.edukasi_kesiapsiagaan_cuaca_hujan)
                binding.tvConditionDesc.text =
                    getString(R.string.informasi_kesiapsiagaan_terkait_potensi_hujan, currentArea?.let { getString(R.string.di_wilayah, it) } ?: ".")
            }

            EdukasiCondition.GEMPA -> {
                binding.tvConditionTitle.text = getString(R.string.edukasi_kesiapsiagaan_gempa_bumi)
                binding.tvConditionDesc.text = getString(R.string.informasi_umum_untuk_membantu_memahami_dan_menghadapi_kejadian_gempa_bumi)
            }

            EdukasiCondition.PANAS -> {
                binding.tvConditionTitle.text = getString(R.string.edukasi_kesiapsiagaan_cuaca_panas)
                binding.tvConditionDesc.text =
                    getString(R.string.informasi_untuk_membantu_menjaga_kesehatan_saat_suhu_udara_lebih_tinggi_dari_biasanya)
            }

            EdukasiCondition.EKSTREM -> {
                binding.tvConditionTitle.text = getString(R.string.edukasi_kesiapsiagaan_cuaca_ekstrem)
                binding.tvConditionDesc.text = getString(R.string.informasi_umum_untuk_memahami_dan_bersiap_menghadapi_kondisi_cuaca_ekstrem)
            }
        }
    }

    private fun renderEdukasi() {
        val edukasiList = when (currentCondition) {

            EdukasiCondition.UMUM -> listOf(
                EdukasiItem(
                    getString(R.string.edukasi_umum_title_1), getString(R.string.edukasi_umum_desc_1)
                ), EdukasiItem(
                    getString(R.string.edukasi_umum_title_2), getString(R.string.edukasi_umum_desc_2)
                ), EdukasiItem(
                    getString(R.string.edukasi_umum_title_3), getString(R.string.edukasi_umum_desc_3)
                )
            )

            EdukasiCondition.HUJAN -> listOf(
                EdukasiItem(
                    getString(R.string.edukasi_hujan_title_1), getString(R.string.edukasi_hujan_desc_1)
                ), EdukasiItem(
                    getString(R.string.edukasi_hujan_title_2), getString(R.string.edukasi_hujan_desc_2)
                ), EdukasiItem(
                    getString(R.string.edukasi_hujan_title_3), getString(R.string.edukasi_hujan_desc_3)
                )
            )

            EdukasiCondition.GEMPA -> listOf(
                EdukasiItem(
                    getString(R.string.edukasi_gempa_title_1), getString(R.string.edukasi_gempa_desc_1)
                ), EdukasiItem(
                    getString(R.string.edukasi_gempa_title_2), getString(R.string.edukasi_gempa_desc_2)
                ), EdukasiItem(
                    getString(R.string.edukasi_gempa_title_3), getString(R.string.edukasi_gempa_desc_3)
                )
            )

            EdukasiCondition.PANAS -> listOf(
                EdukasiItem(
                    getString(R.string.edukasi_panas_title_1), getString(R.string.edukasi_panas_desc_1)
                ), EdukasiItem(
                    getString(R.string.edukasi_panas_title_2), getString(R.string.edukasi_panas_desc_2)
                ), EdukasiItem(
                    getString(R.string.edukasi_panas_title_3), getString(R.string.edukasi_panas_desc_3)
                )
            )

            EdukasiCondition.EKSTREM -> listOf(
                EdukasiItem(
                    getString(R.string.edukasi_ekstrem_title_1), getString(R.string.edukasi_ekstrem_desc_1)
                ), EdukasiItem(
                    getString(R.string.edukasi_ekstrem_title_2), getString(R.string.edukasi_ekstrem_desc_2)
                ), EdukasiItem(
                    getString(R.string.edukasi_ekstrem_title_3), getString(R.string.edukasi_ekstrem_desc_3)
                )
            )

            else -> emptyList()
        }

        adapter.updateData(edukasiList)
    }

    private fun initVideoPlayer() {
        lifecycle.addObserver(binding.youtubePlayerView)

        binding.youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                this@EdukasiFragment.youTubePlayer = youTubePlayer

                // Initial video (jika ada)
                getYoutubeVideoId(currentCondition)?.let {
                    binding.youtubePlayerView.visibility = View.VISIBLE
                    youTubePlayer.cueVideo(it, 0f)
                } ?: run {
                    binding.youtubePlayerView.visibility = View.GONE
                }
            }
        })
    }

    private fun getYoutubeVideoId(condition: String): String? {
        return when (condition) {
            EdukasiCondition.GEMPA -> "yTRwQO3BA7U"
            EdukasiCondition.HUJAN -> "nH00wYZPzJk"
            EdukasiCondition.PANAS -> "_TVXmi7gakQ"
            EdukasiCondition.EKSTREM -> "yTMgNN1IJXo"
            EdukasiCondition.UMUM -> null
            else -> null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.youtubePlayerView.release()
        youTubePlayer = null
        _binding = null
    }
}
