package studio.bonodigital.siagaai.ui.tanyaai

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import studio.bonodigital.siagaai.R
import studio.bonodigital.siagaai.api.ApiClient
import studio.bonodigital.siagaai.data.model.ChatMessage
import studio.bonodigital.siagaai.data.request.AskRequest
import studio.bonodigital.siagaai.databinding.FragmentTanyaAiBinding
import studio.bonodigital.siagaai.util.AiFormatter
import studio.bonodigital.siagaai.util.ChatStorage
import studio.bonodigital.siagaai.util.EdukasiCondition

class TanyaAiFragment : Fragment() {

    private var _binding: FragmentTanyaAiBinding? = null
    private val binding get() = _binding!!
    private var condition: String? = null
    private var area: String? = null
    private var source: String = "BMKG"
    private val introFlag = "__INTRO_ADDED__"
    private var isContextual: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTanyaAiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom

            v.setPadding(0, 0, 0, if (imeVisible) imeHeight else 0)

            insets
        }

        setupMenu()

        arguments?.let {
            condition = it.getString("condition")
            area = it.getString("area")
            source = it.getString("source") ?: "BMKG"
            isContextual = true
        } ?: run {
            isContextual = false
        }

        val storage = ChatStorage(requireContext())
        val messages = storage.load().toMutableList()
        val introAlreadyAdded = messages.any { it.text == introFlag }

        val adapter = ChatAdapter(messages)
        binding.rvChat.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChat.adapter = adapter

        if (!introAlreadyAdded) {
            val introText = when {
                !isContextual -> getString(R.string.intro_not_contextual)

                condition == EdukasiCondition.GEMPA -> getString(R.string.intro_gempa)

                condition == EdukasiCondition.HUJAN -> getString(R.string.intro_hujan)

                condition == EdukasiCondition.PANAS -> getString(R.string.intro_panas)

                condition == EdukasiCondition.EKSTREM -> getString(R.string.intro_ekstrem)

                else -> getString(R.string.intro_other)
            }

            val introMsg = ChatMessage(introText, false)
            val flagMsg = ChatMessage(introFlag, false)

            messages.add(introMsg)
            messages.add(flagMsg)

            adapter.notifyItemRangeInserted(messages.size - 2, 2)
            storage.save(messages)
        }

        binding.btnSend.setOnClickListener {
            val text = binding.etQuestion.text.toString().trim()
            if (text.isEmpty()) return@setOnClickListener

            val userMsg = ChatMessage(text, true)
            messages.add(userMsg)
            adapter.notifyItemInserted(messages.lastIndex)
            storage.save(messages)

            binding.etQuestion.text?.clear()
            binding.progress.visibility = View.VISIBLE

            lifecycleScope.launch {
                try {
                    val finalPrompt = buildContextAwarePrompt(text)
                    val response = ApiClient.api.askSiagaAI(AskRequest(finalPrompt))
                    val cleanText = AiFormatter.format(response.answer)
                    val aiMsg = ChatMessage(cleanText, false)
                    messages.add(aiMsg)
                    adapter.notifyItemInserted(messages.lastIndex)
                    storage.save(messages)
                } catch (_: Exception) {
                    val errorMsg = ChatMessage(
                        getString(R.string.error_chat_message), false
                    )
                    messages.add(errorMsg)
                    adapter.notifyItemInserted(messages.lastIndex)
                    storage.save(messages)
                } finally {
                    binding.progress.visibility = View.GONE
                    binding.rvChat.scrollToPosition(messages.lastIndex)
                }
            }
        }
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_tanya_ai, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_clear_chat -> {
                        confirmClearChat()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun confirmClearChat() {
        AlertDialog.Builder(requireContext()).setTitle(getString(R.string.hapus_percakapan_dialog_title))
            .setMessage(getString(R.string.hapus_percakapan_message)).setPositiveButton(getString(R.string.hapus)) { _, _ ->
                clearChat()
            }.setNegativeButton(getString(R.string.batal), null).show()
    }

    private fun clearChat() {
        val storage = ChatStorage(requireContext())
        storage.clear()

        val messages = mutableListOf<ChatMessage>()
        val adapter = ChatAdapter(messages)

        binding.rvChat.adapter = adapter
        binding.rvChat.scrollToPosition(0)

    }

    private fun buildContextAwarePrompt(userQuestion: String): String {

        val contextIntro = if (isContextual && condition != null) {
            when (condition) {
                EdukasiCondition.GEMPA -> getString(R.string.context_intro_gempa)

                EdukasiCondition.HUJAN -> getString(R.string.context_intro_hujan)

                EdukasiCondition.PANAS -> getString(R.string.context_intro_panas)

                EdukasiCondition.EKSTREM -> getString(R.string.context_intro_ekstrem)

                else -> getString(R.string.context_intro_other)
            }
        } else {
            getString(R.string.not_context_intro)
        }

        val areaPart = if (isContextual && area != null) {
            "Konteks wilayah: $area."
        } else ""

        return """
        $contextIntro
        $areaPart

        Jelaskan secara netral, edukatif, dan menenangkan.
        Jangan menyampaikan peringatan darurat atau prediksi kejadian.
        Gunakan bahasa yang mudah dipahami masyarakat umum.

        Pertanyaan:
        $userQuestion
    """.trimIndent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}