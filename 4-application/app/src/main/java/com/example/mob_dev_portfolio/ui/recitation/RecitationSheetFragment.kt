package com.example.mob_dev_portfolio.ui.recitation

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.databinding.FragmentRecitationSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/** Bottom-sheet host for the record-and-analyse flow. */
@AndroidEntryPoint
class RecitationSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentRecitationSheetBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RecitationViewModel by viewModels()

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) viewModel.startRecording()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRecitationSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recitationTestModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setTestMode(isChecked)
        }
        binding.recitationRecordButton.setOnClickListener { onRecordTap() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { render(it) }
            }
        }
    }

    private fun onRecordTap() {
        when (viewModel.state.value) {
            is RecitationUiState.Recording -> viewModel.stopRecording()
            is RecitationUiState.Result -> viewModel.resetToIdle()
            else -> requestPermission.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun render(state: RecitationUiState) {
        when (state) {
            is RecitationUiState.Idle -> renderIdle(state)
            is RecitationUiState.Recording -> renderRecording(state)
            is RecitationUiState.Processing -> renderProcessing(state)
            is RecitationUiState.Result -> renderResult(state)
            is RecitationUiState.Error -> renderError(state)
        }
    }

    private fun renderIdle(state: RecitationUiState.Idle) {
        binding.recitationExpectedText.text = if (state.isTestMode) "" else state.expected
        binding.recitationStatus.setText(R.string.recitation_prompt)
        binding.recitationRecordButton.setText(R.string.action_record)
        binding.recitationProcessing.visibility = View.GONE
        binding.recitationWaveform.reset()
    }

    private fun renderRecording(state: RecitationUiState.Recording) {
        binding.recitationExpectedText.text = if (state.isTestMode) "" else state.expected
        binding.recitationStatus.text = getString(R.string.recitation_recording, formatElapsed(state.elapsedMs))
        binding.recitationRecordButton.setText(R.string.action_stop)
        binding.recitationProcessing.visibility = View.GONE
        binding.recitationWaveform.setAmplitude(state.amplitude)
    }

    private fun renderProcessing(state: RecitationUiState.Processing) {
        binding.recitationExpectedText.text = state.expected
        binding.recitationStatus.setText(R.string.recitation_processing)
        binding.recitationProcessing.visibility = View.VISIBLE
        binding.recitationRecordButton.isEnabled = false
    }

    private fun renderResult(state: RecitationUiState.Result) {
        binding.recitationExpectedText.text =
            MistakeHighlighter.render(requireContext(), state.expected, state.mistakes)
        binding.recitationStatus.text = getString(R.string.recitation_score_value, state.score.value)
        binding.recitationRecordButton.isEnabled = true
        binding.recitationRecordButton.setText(R.string.action_done)
        binding.recitationProcessing.visibility = View.GONE
    }

    private fun renderError(state: RecitationUiState.Error) {
        binding.recitationExpectedText.text = state.expected
        binding.recitationStatus.setText(
            when (state.message) {
                ErrorMessage.PERMISSION_DENIED -> R.string.recitation_permission_required
                ErrorMessage.RECOGNISER_UNAVAILABLE -> R.string.recitation_recogniser_unavailable
                ErrorMessage.NO_SPEECH_DETECTED -> R.string.recitation_no_speech
                ErrorMessage.GENERIC -> R.string.error_generic
            },
        )
        binding.recitationRecordButton.isEnabled = true
        binding.recitationRecordButton.setText(R.string.action_retry)
        binding.recitationProcessing.visibility = View.GONE
    }

    private fun formatElapsed(elapsedMs: Long): String {
        val totalSeconds = elapsedMs / 1000L
        val minutes = totalSeconds / 60L
        val seconds = totalSeconds % 60L
        return "%d:%02d".format(minutes, seconds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
