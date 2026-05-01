package com.example.mob_dev_portfolio.ui.tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.databinding.FragmentTrackerBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

/** Hosts the Tracker tabs. */
@AndroidEntryPoint
class TrackerFragment : Fragment() {

    private var _binding: FragmentTrackerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTrackerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.trackerPager.adapter = TabPagerAdapter(childFragmentManager, lifecycle)
        TabLayoutMediator(binding.trackerTabs, binding.trackerPager) { tab, position ->
            tab.text = when (TrackerPageFragment.Tab.entries[position]) {
                TrackerPageFragment.Tab.MEMORISED -> getString(R.string.tracker_tab_memorised)
                TrackerPageFragment.Tab.DUE -> getString(R.string.tracker_tab_due)
                TrackerPageFragment.Tab.WEAK -> getString(R.string.tracker_tab_weak)
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.trackerPager.adapter = null
        _binding = null
    }

    private class TabPagerAdapter(
        fragmentManager: FragmentManager,
        lifecycle: Lifecycle,
    ) : FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun getItemCount(): Int = TrackerPageFragment.Tab.entries.size
        override fun createFragment(position: Int): Fragment =
            TrackerPageFragment.newInstance(TrackerPageFragment.Tab.entries[position])
    }
}
