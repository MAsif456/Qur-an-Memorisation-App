**Mobile Development 2025/26 Portfolio**
# Requirements

Student ID: `C23042139`

#### Functional Requirements

FR-Global

- FR-G1 The application shall present four top-level sections (Home, Surahs, Tracker, Settings) 
  reachable at any time from a persistent bottom navigation bar.

FR-Home

- FR-H1 The Home section shall display the user's current daily streak, calculated as the number
  of consecutive calendar days on which at least one memorisation or revision task was completed.
- FR-H2 The Home section shall display the user's configured daily memorisation goal alongside 
  current progress toward it for the present day.
- FR-H3 The Home section shall display an overall memorisation figure expressed as the percentage 
  of Qur'anic verses the user has marked as memorised.
- FR-H4 The Home section shall display a list of up to five recommended tasks for the current day, 
  drawn from verses overdue for revision, verses with low recitation accuracy, and the next 
  unmemorised verse in the user's current Surah.

FR-Surahs

- FR-S1 The Surahs section shall display a searchable list of all 114 Surahs, with search matching
  against both the English transliteration and the Arabic name.
- FR-S2 Selecting a Surah shall open a detail view listing every verse, with Arabic text rendered
  right-to-left and an optional English translation rendered left-to-right beneath it.
- FR-S3 Each verse row shall provide three actions: play audio, toggle memorised status, and 
  launch the recitation analyser.
- FR-S4 The Surah detail view shall provide play, pause, and skip-to-next-verse controls for
  audio recitation.

FR-Tracker

- FR-T1 The Tracker section shall present three tabs: Memorised, Due for revision, and Needs 
  practice.
- FR-T2 The Memorised tab shall list every verse the user has marked as memorised, grouped by
  Surah.
- FR-T3 The Due for revision tab shall list every verse whose scheduled revision date is today
  or earlier, ordered with the most overdue first.
- FR-T4 The Needs practice tab shall list verses whose most recent recitation accuracy fell below
  a fixed threshold of 70 per cent, ordered by lowest accuracy first.
- FR-T5 Completing a revision shall advance that verse's schedule to the next interval on the
  spaced-repetition ladder.

FR-Recitation

- FR-R1 The Recitation sheet shall record audio from the device microphone after the user has
  granted the relevant runtime permission.
- FR-R2 The Recitation sheet shall offer two modes: Open (the verse text remains visible) and
  Test (the verse text is hidden until the recording ends).
- FR-R3 Upon stopping the recording, the application shall produce a transcript, compare it 
  against the expected verse text, and display the result with missing, extra, and substituted
  words highlighted distinctly.
- FR-R4 The result shall include an accuracy score expressed as a percentage from 0 to 100.
- FR-R5 Each recitation attempt shall be persisted and shall feed the Tracker's Needs practice
  tab and the Home recommendations.
- FR-R6 A recitation attempt scoring below the practice threshold shall trigger an earlier next
  revision date for that verse.

FR-Settings

- FR-S1 The Settings section shall allow the user to select a preferred reciter from a list of
  available reciters; this selection determines the audio source for all subsequent playback.
- FR-S2 The Settings section shall allow the user to set a daily memorisation goal as an integer
  between 1 and 50 verses.
- FR-S3 The Settings section shall allow the user to enable or disable daily revision reminders.
- FR-S4 The Settings section shall allow the user, when reminders are enabled, to configure the time
  of day at which reminders are delivered.
- FR-S5 The Settings section shall allow the user to choose between light, dark, and follow-system
  themes.

FR-Notifications

- FR-N1 When reminders are enabled, the application shall deliver a local notification at
  the user's chosen time summarising how many verses are due for revision and how many remain
  toward the daily goal.
- FR-N2 Tapping a notification shall open the Tracker section on the Due for revision tab.

#### Non-Functional Requirements

NFR-Performance

- NFR-P1 Audio playback shall begin within two seconds of the user pressing play, measured
  on the Pixel 3a API 35 emulator under a stable broadband connection.
- NFR-P2 Verse transcription and diff computation shall complete and render a result within 
  three seconds of the user stopping a recording, for recordings up to fifteen seconds in length.
- NFR-P3 Notifications shall fire within five seconds of the user's scheduled time.

NFR-Reliability

- NFR-R1 The application shall be fully usable on a clean install with no network connection, 
  exposing the bundled Surah metadata and at least one fully playable sample Surah (Al-Fatiha).
- NFR-R2 The application shall not crash when the Quran.com API returns a 4xx or 5xx response,
  malformed JSON, or times out; instead, the affected screen shall display cached data if available 
  and a retry affordance otherwise.
- NFR-R3 Recitation analysis shall function end-to-end without any external backend, using on-device
  speech recognition as the default.
- NFR-R4 All user-generated data (memorisation status, schedule, recitation history, settings) shall
  be persisted within 100 milliseconds of the change being made, so that a subsequent restart
  reflects the latest state.

NFR-Usability

- NFR-U1 The application shall operate in portrait orientation only.
- NFR-U2 Arabic verse text shall be rendered using the Amiri font at a minimum size of 24 
  scalable pixels and shall respect the user's system font-size setting up to 200 per cent.
- NFR-U3 Arabic text shall render right-to-left and surrounding interface text shall render 
  left-to-right, with the two coexisting in the same layout without clipping, overlap, or 
  mirroring errors.
- NFR-U4 All interactive elements shall present a minimum touch target of 48 by 48 
  density-independent pixels, in accordance with Material Design guidance.
- NFR-U5 Every screen shall provide a distinct empty state, a distinct loading state, and a 
  distinct error state; no screen shall display a blank view for longer than 200 milliseconds.

NFR-Data

- NFR-D1 Verse text, Surah metadata, and audio URLs shall be retrieved from the Quran.com API 
  on first use and cached locally; subsequent reads shall be served from the cache, with a 
  background refresh when online.
- NFR-D2 The local database schema shall support versioned migrations so that future schema 
  changes do not destroy user data on upgrade.

NFR-Privacy

- NFR-PR1 The application shall request only the permissions required for declared functionality
  (microphone, notifications, internet); recorded audio shall be held only in the application's 
  private cache directory and shall be deleted once transcription and scoring have completed.