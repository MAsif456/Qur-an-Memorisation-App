package com.example.mob_dev_portfolio.data.repository

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import com.example.mob_dev_portfolio.domain.model.UserSettings
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalTime

@RunWith(RobolectricTestRunner::class)
class SettingsRepositoryTest {

    private lateinit var repository: SettingsRepository
    private lateinit var fileName: String

    @Before
    fun setUp() {
        fileName = "settings_test_${System.nanoTime()}"
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val store = PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile(fileName) },
        )
        repository = SettingsRepository(store)
    }

    @After
    fun tearDown() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        context.preferencesDataStoreFile(fileName).delete()
    }

    @Test
    fun `defaults emitted when no value persisted`() = runTest {
        val settings = repository.observe().first()
        assertThat(settings.defaultReciterId).isEqualTo(UserSettings.DEFAULT_RECITER_ID)
        assertThat(settings.dailyGoal).isEqualTo(UserSettings.DEFAULT_DAILY_GOAL)
        assertThat(settings.darkModeFollowsSystem).isTrue()
    }

    @Test
    fun `setReciter persists and emits updated value`() = runTest {
        repository.setReciter(42)
        assertThat(repository.observe().first().defaultReciterId).isEqualTo(42)
    }

    @Test
    fun `setReminderTime persists hour and minute`() = runTest {
        repository.setReminderTime(LocalTime.of(8, 30))
        val settings = repository.observe().first()
        assertThat(settings.reminderTime).isEqualTo(LocalTime.of(8, 30))
    }

    @Test
    fun `setDailyGoal coerces to at least one`() = runTest {
        repository.setDailyGoal(0)
        assertThat(repository.observe().first().dailyGoal).isEqualTo(1)
    }

    @Test
    fun `setDarkModeFollowsSystem flips boolean`() = runTest {
        repository.setDarkModeFollowsSystem(false)
        assertThat(repository.observe().first().darkModeFollowsSystem).isFalse()
    }
}
