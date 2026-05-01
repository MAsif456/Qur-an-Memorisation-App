package com.example.mob_dev_portfolio.ui.tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.databinding.FragmentTrackerPageBinding
import com.example.mob_dev_portfolio.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

/** One Tracker page; binds the matching tab flow on the parent's [TrackerViewModel]. */
@AndroidEntryPoint
class TrackerPageFragment : BaseFragment() {

    private var _binding: FragmentTrackerPageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TrackerViewModel by viewModels({ requireParentFragment() })
    private val adapter = TrackerRowAdapter()

    private val tab: Tab by lazy {
        val name = requireArguments().getString(ARG_TAB) ?: Tab.MEMORISED.name
        Tab.valueOf(name)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTrackerPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.trackerPageRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.trackerPageRecycler.adapter = adapter
        bind()
    }

    private fun bind() = when (tab) {
        Tab.MEMORISED -> collectViewLifecycle(viewModel.memorised) { rows ->
            submit(
                rows.map {
                    TrackerRow(
                        key = "${it.surahNumber}:${it.ayahNumber}",
                        title = getString(R.string.home_recommendation_title_format, it.surahNumber, it.ayahNumber),
                        subtitle = it.status.name.lowercase().replaceFirstChar { c -> c.titlecase() },
                    )
                },
                R.string.tracker_empty_memorised,
            )
        }
        Tab.DUE -> collectViewLifecycle(viewModel.due) { rows ->
            submit(
                rows.map {
                    TrackerRow(
                        key = "${it.surahNumber}:${it.ayahNumber}",
                        title = getString(R.string.home_recommendation_title_format, it.surahNumber, it.ayahNumber),
                        subtitle = if (it.overdueDays > 0) "Overdue ${it.overdueDays}d" else "Due today",
                    )
                },
                R.string.tracker_empty_due,
            )
        }
        Tab.WEAK -> collectViewLifecycle(viewModel.weak) { rows ->
            submit(
                rows.map {
                    TrackerRow(
                        key = "${it.surahNumber}:${it.ayahNumber}",
                        title = getString(R.string.home_recommendation_title_format, it.surahNumber, it.ayahNumber),
                        subtitle = "Last accuracy ${it.lastAccuracy ?: 0}%",
                    )
                },
                R.string.tracker_empty_weak,
            )
        }
    }

    private fun submit(rows: List<TrackerRow>, emptyRes: Int) {
        adapter.submitList(rows)
        binding.trackerPageEmpty.text = getString(emptyRes)
        binding.trackerPageEmpty.visibility = if (rows.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.trackerPageRecycler.adapter = null
        _binding = null
    }

    enum class Tab { MEMORISED, DUE, WEAK }

    companion object {
        const val ARG_TAB = "tab"

        fun newInstance(tab: Tab): TrackerPageFragment = TrackerPageFragment().apply {
            arguments = Bundle().apply { putString(ARG_TAB, tab.name) }
        }
    }
}
