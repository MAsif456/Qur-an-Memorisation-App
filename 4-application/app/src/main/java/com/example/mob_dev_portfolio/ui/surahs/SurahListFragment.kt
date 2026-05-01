package com.example.mob_dev_portfolio.ui.surahs

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.databinding.FragmentSurahListBinding
import com.example.mob_dev_portfolio.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

/** Searchable Surah list. Row tap navigates to [SurahDetailFragment]. */
@AndroidEntryPoint
class SurahListFragment : BaseFragment() {

    private var _binding: FragmentSurahListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SurahListViewModel by viewModels()
    private val adapter = SurahAdapter { surah ->
        findNavController().navigate(
            R.id.action_surahList_to_surahDetail,
            Bundle().apply { putInt("surahNumber", surah.number) },
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSurahListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.surahsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.surahsRecycler.adapter = adapter

        binding.surahsSearchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                viewModel.setQuery(s?.toString().orEmpty())
            }
        })

        collectViewLifecycle(viewModel.surahs) { items ->
            adapter.submitList(items)
            binding.surahsEmpty.visibility =
                if (items.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.surahsRecycler.adapter = null
        _binding = null
    }
}
