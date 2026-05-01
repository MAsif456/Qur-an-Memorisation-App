package com.example.mob_dev_portfolio.ui.settings

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.databinding.FragmentSettingsBinding
import com.example.mob_dev_portfolio.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalTime

/** Settings tab. Persists through [SettingsViewModel]; reminder edits reschedule the worker. */
@AndroidEntryPoint
class SettingsFragment : BaseFragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()

    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            viewModel.sendTestNotification()
        } else {
            Toast.makeText(
                requireContext(),
                R.string.settings_reminder_permission_denied,
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.settingsReminderValue.setOnClickListener { showTimePicker() }
        binding.settingsReminderTest.setOnClickListener { onTestNotificationClick() }
        binding.settingsGoalSlider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) viewModel.setGoal(value.toInt())
        }
        binding.settingsDarkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDarkModeFollowsSystem(isChecked)
        }

        collectViewLifecycle(viewModel.state) { state ->
            renderReciters(state)
            renderReminder(state)
            renderGoal(state)
            renderDarkMode(state)
        }
    }

    private fun renderReciters(state: SettingsUiState) {
        val labels = state.reciters.map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, labels)
        binding.settingsReciterValue.setAdapter(adapter)
        val current = state.reciters.firstOrNull { it.id == state.settings.defaultReciterId }
        binding.settingsReciterValue.setText(current?.name.orEmpty(), false)
        binding.settingsReciterValue.setOnItemClickListener { _, _, position, _ ->
            state.reciters.getOrNull(position)?.let { viewModel.setReciter(it.id) }
        }
    }

    private fun renderReminder(state: SettingsUiState) {
        binding.settingsReminderValue.text = state.settings.reminderTime.toString()
    }

    private fun renderGoal(state: SettingsUiState) {
        binding.settingsGoalSlider.value = state.settings.dailyGoal.toFloat()
        binding.settingsGoalSummary.text = getString(R.string.settings_goal_summary, state.settings.dailyGoal)
    }

    private fun renderDarkMode(state: SettingsUiState) {
        if (binding.settingsDarkModeSwitch.isChecked != state.settings.darkModeFollowsSystem) {
            binding.settingsDarkModeSwitch.isChecked = state.settings.darkModeFollowsSystem
        }
    }

    private fun showTimePicker() {
        val current = viewModel.state.value.settings.reminderTime
        TimePickerDialog(
            requireContext(),
            { _, hour, minute -> viewModel.setReminderTime(LocalTime.of(hour, minute)) },
            current.hour,
            current.minute,
            true,
        ).show()
    }

    private fun onTestNotificationClick() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || hasNotificationPermission()) {
            viewModel.sendTestNotification()
            return
        }
        requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun hasNotificationPermission(): Boolean = ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.POST_NOTIFICATIONS,
    ) == PackageManager.PERMISSION_GRANTED

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
