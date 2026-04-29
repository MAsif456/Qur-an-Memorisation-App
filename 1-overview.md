**Mobile Development 2025/26 Portfolio**
# Overview

Student ID: `C23042139`

This project delivers an Android application that helps Muslims memorise the Qur'an by addressing
the three obstacles I have encountered personally: forgetting previously memorised verses, losing
motivation without visible progress, and being unable to self-correct recitation mistakes without
a teacher present. The app responds with four connected features. A spaced-repetition tracker 
schedules revisions at expanding intervals so verses are reviewed before they are forgotten
(Cepeda et al. 2006). A streak and daily-goal system on the Home screen makes consistency
visible. An AI-assisted recitation analyser records the user, transcribes the audio on-device 
using Android's Arabic speech recogniser, compares it against the expected verse using a word-level
Levenshtein diff, and highlights missing, extra, or substituted words. A recommendations engine 
combines revision due-dates with mistake history to suggest the next verse to work on.
The app uses the Quran.com API for verse text, audio, and reciter metadata. Offline 
data sample is provided within the app, making it fully functional immediately and mitigating
potential latency or connectivity issues during the initial user session. Arabic renders
right-to-left using the Amiri font within an otherwise left-to-right interface. The intended 
users are Muslim learners of any age memorising independently and needing a structured, 
self-correcting companion.

References:

Cepeda, N., Pashler, H., Vul, E., Wixted, J. and Rohrer, D. (2006). Distributed practice in verbal recall tasks: A review and quantitative synthesis.. Psychological bulletin, 132 3, pp. 354-80 . doi: 10.1037/0033-2909.132.3.354. 