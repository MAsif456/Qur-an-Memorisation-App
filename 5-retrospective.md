**Mobile Development 2025/26 Portfolio**
# Retrospective

Student ID: `C23042139`

The application delivered the core functionality set out at the start: searchable Surah browsing,
verse-level memorisation tracking, spaced-repetition revision, audio playback, AI-assisted recitation 
analysis with mistake highlighting, and a recommendations engine on the Home screen.

The most significant difficulty involved the on-device Speech Recogniser, which produced inconsistent
transcription quality that varied with ambient noise, recitation pace, and apparent fluctuations in
service availability. The pluggable SpeechRecogniser interface absorbed this risk by isolating 
recognition behind a single abstraction, and the word-level Levenshtein diff tolerated transcription 
noise more gracefully than a strict equality check would have.

With more time available, I would focus more on the UI. Functionality was prioritised over visual 
polish during development, producing an interface that is functional and consistent rather than 
distinctive. An OpenAI Whisper backend deployed on a stable virtual private server would also 
replace the on-device path as the recognition default.

Because Qur'an memorisation is personal to me, I will continue to work on this project past this time. 
Mobile applications have been shown to improve Quranic education outcomes (Hakimi et al. 2024), and 
continued development with eventual publication could provide meaningful benefit to other learners 
pursuing the same goal.

References

Hakimi, M., Akrami, M., Ahrari, M., Akrami, K. and Akrami, F. 2024. The impact of mobile applications on Quran education: a survey of student performance and satisfaction. *Journal of Digital Learning and Distance Education* 2(8), pp. 722–736. doi: 10.56778/jdlde.v2i8.220.