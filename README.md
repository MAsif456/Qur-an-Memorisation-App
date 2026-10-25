## Quran Memorisation App

An Android app for students memorising the Quran. It covers all the
surahs, and students can mark their progress ayah by ayah. The app
sorts everything into memorised, due for revision and weak ayahs, so a
student always knows what is strong and what needs work. A home screen
shows a streak, daily-goal progress and recommendations on what to
study next, and a daily reminder prompts revision.

The main feature is voice recitation checking. A student records
themselves reciting, the app transcribes it, compares it with the
correct text word by word, and returns an accuracy percentage with the
mistakes highlighted. Students can also listen to recitations from a
choice of reciters, and the app works in light and dark themes.

*Commits in this project appear under my former university username,
MASAsif.*

**Solo project** | **Tech:** Kotlin, Android, Hilt, Room, Retrofit,
Moshi, Media3 ExoPlayer, WorkManager, Quran.com API

## My contribution

I designed and built the whole app.

### Recitation checking
- Used Android's built-in speech recogniser to transcribe recitations,
  then compared the transcript to the expected text word by word.
- Built an Arabic text normaliser, a word-level Levenshtein differ and
  an accuracy scorer.
- Built the recitation screen with a record button, waveform and a
  result view that highlights mistakes.
- Stabilised the speech recognition lifecycle to make it reliable
  across sessions.

### Memorisation and revision
- Built a searchable surah list and surah detail screen with per-ayah
  "memorise" and "recite" actions.
- Built a progress tracker with tabs for memorised, revision-due and
  weak ayahs.
- Built a home screen with a streak card, daily-goal progress and
  ranked recommendations on what to study next.
- Added daily revision reminders using WorkManager.
- Added audio playback with a Media3 ExoPlayer wrapper.
- Built a settings screen for reciter, reminder time, daily goal and
  dark mode.

### Data and architecture
- Integrated the Quran.com API with Retrofit and Moshi, with
  defensive mappers that handle malformed and partial responses.
- Built the local database with Room and cache-first repositories for
  surahs, ayahs, memorisation, revision, recitation and settings.
- Bundled the surah catalogue and reciter data, loaded on first
  launch.
- Used Hilt for dependency injection and a single-activity design with
  bottom navigation.
- Built light and dark themes with Arabic typography.

### Testing and quality
- Wrote tests for the recitation logic (normaliser, Levenshtein,
  differ, scorer, scheduler, streaks, recommender), the Room DAOs, the
  seed loader, the repositories and the API mappers.
- Set up detekt and ktlint, and wrote the project documentation
  (overview, requirements, API description).
