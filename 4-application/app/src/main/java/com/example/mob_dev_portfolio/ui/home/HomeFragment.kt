package com.example.mob_dev_portfolio.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.databinding.FragmentHomeBinding
import com.example.mob_dev_portfolio.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

/** Home tab: streak card, daily goal, recommendations. */
@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private val adapter = RecommendationAdapter { recommendation ->
        findNavController().navigate(
            R.id.action_home_to_surahDetail,
            Bundle().apply { putInt("surahNumber", recommendation.surahNumber) },
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.homeRecommendations.layoutManager = LinearLayoutManager(requireContext())
        binding.homeRecommendations.adapter = adapter

        collectViewLifecycle(viewModel.state) { state ->
            render(state)
        }
    }

    private fun render(state: HomeUiState) {
        binding.homeStreakValue.text = if (state.streakDays > 0) {
            getString(R.string.home_streak_value_days, state.streakDays)
        } else {
            getString(R.string.home_streak_zero)
        }
        binding.homeGoalProgress.max = state.dailyGoal.coerceAtLeast(1)
        binding.homeGoalProgress.progress = state.goalAchieved.coerceAtMost(state.dailyGoal)
        binding.homeGoalValue.text = getString(
            R.string.home_daily_goal_progress,
            state.goalAchieved,
            state.dailyGoal,
        )
        binding.homeLoading.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        binding.homeRecommendationsEmpty.visibility =
            if (!state.isLoading && state.recommendations.isEmpty()) View.VISIBLE else View.GONE
        adapter.submitList(state.recommendations)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.homeRecommendations.adapter = null
        _binding = null
    }
}
