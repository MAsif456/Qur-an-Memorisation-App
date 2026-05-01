**Mobile Development 2025/26 Portfolio**
# API Description

Student ID: `C23042139`

The application is built as a single-Activity MVVM app in Kotlin with XML layouts. Comparative
empirical work on Android architectures finds that MVVM yields the highest modifiability and the
lowest modification cost among MVP, MVI and MVVM in native Android applications 
(Vijaywargi et al. 2024), which supported its selection here over the alternatives.

`MainActivity` hosts a `NavHostFragment` driven by the Navigation Component, bound to a
`BottomNavigationView`. This was chosen over multiple Activities so the back-stack is centralised,
deep links into specific verses resolve cleanly, and the bottom bar's selected state survives
configuration changes without manual restoration.

Each screen pairs a `Fragment` with a `ViewModel` exposing a single `StateFlow<UiState>`.
State survives rotation without `onSaveInstanceState` plumbing, and 
`viewLifecycleOwner.repeatOnLifecycle(STARTED)` is used on every collector so flows pause when
the fragment is backgrounded, avoiding wasted work.
Room stores Surah and verse caches, memorisation progress, the revision schedule, and 
recitation history, exposing reads as `Flow` so the UI reactively reflects writes.
Preferences DataStore holds user settings because it is type-safe and asynchronous
so I decided to not use `SharedPreferences`.

Verse playback uses Media3 ExoPlayer for efficient streaming and a stable session API.
Recording uses `MediaRecorder` writing AAC audio into the application's private cache.

Recitation analysis uses `android.speech.SpeechRecognizer` with the `ar-SA` locale, 
kept on-device to remove any reliance on external infrastructure that could be unavailable 
at a later time.

Daily revision reminders use WorkManager with a `PeriodicWorkRequest`, posted via 
`NotificationCompat` on a registered channel. Following the introduction of Doze Mode and 
successive background-execution restrictions, WorkManager has emerged as the recommended 
scheduler for deferrable work that must survive reboots and operate within system battery 
constraints (Puduru 2025); `AlarmManager` is unsuitable for my project.

References

Puduru, M. (2025). System-Aware Background Task Management in Android: Navigating Evolving Constraints for Efficient Application Performance. _European Journal of Computer Science and Information Technology_. doi: 10.37745/ejcsit.2013/vol13n477585. 

Vijaywargi, A. and Boddapati, U. (2024). Architectural Patterns in Android Development: Comparing MVP, MVVM, and MVI. _International Journal for Research in Applied Science and Engineering Technology_. doi: 10.22214/ijraset.2024.60762. 