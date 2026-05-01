package com.example.mob_dev_portfolio.ui.surahs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.databinding.FragmentSurahDetailBinding
import com.example.mob_dev_portfolio.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

/** Surah detail: header, play-all toggle, and the ayah list. */
@AndroidEntryPoint
class SurahDetailFragment : BaseFragment() {

    private var _binding: FragmentSurahDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SurahDetailViewModel by viewModels()
    private val adapter by lazy {
        AyahAdapter(
            onMemoriseToggle = { viewModel.toggleMemorised(it) },
            onRecite = { row ->
                findNavController().navigate(
                    R.id.action_surahDetail_to_recitation,
                    Bundle().apply {
                        putInt("surahNumber", row.ayah.surahNumber)
                        putInt("ayahNumber", row.ayah.ayahNumber)
                    },
                )
            },
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSurahDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.surahDetailToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.surahDetailRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.surahDetailRecycler.adapter = adapter
        binding.surahDetailPlayAll.setOnClickListener { togglePlayback() }

        collectViewLifecycle(viewModel.surah) { surah ->
            if (surah != null) {
                binding.surahDetailName.text = surah.nameTransliterated
                binding.surahDetailMetaText.text = getString(
                    R.string.surahs_meta_format,
                    surah.nameTranslation,
                    surah.ayahCount,
                )
            }
        }
        collectViewLifecycle(viewModel.rows) { items ->
            adapter.submitList(items)
            binding.surahDetailLoading.visibility =
                if (items.isEmpty()) View.VISIBLE else View.GONE
        }
        collectViewLifecycle(viewModel.playback) { state ->
            binding.surahDetailPlayAll.setText(
                if (state.isPlaying) R.string.action_pause else R.string.surah_detail_play_all,
            )
        }
    }

    private fun togglePlayback() {
        if (viewModel.playback.value.isPlaying) viewModel.pause() else viewModel.playSurah()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.surahDetailRecycler.adapter = null
        _binding = null
    }
}
