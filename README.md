# Clarity

## The Origin Story

From 2022 to 2024, I participated in **ESPINA** — a longitudinal study on pesticide exposure in children and adolescents conducted by CIMAS (https://cimas.edu.ec/portafolio/estudio-de-exposicion-secundaria-a-plaguicidas-en-ninos-y-adolescentes-espina/).

As part of the study, researchers asked us to use an app called **NeuroX**. It wasn't just a game collection — it was a *research tool* that combined cognitive games with a daily check-in questionnaire. Every session, I'd answer questions about my mood, sleep, stress, and context. Then I'd play cognitive tasks. Over months, I started noticing patterns:

- *On days with poor sleep, my reaction times were noticeably slower.*
- *After stressful events, my memory performance dropped.*
- *Physical activity before a session seemed to boost my focus.*

The app made me *feel* the connection between my daily life and my cognitive abilities. It was educational, engaging, and genuinely useful for self-understanding. More importantly, it generated data that could be valuable for medical and psychological research.

**Then the study ended, and NeuroX disappeared.**

For over a year, I missed it. I tried other apps like NeuroNation and Lumosity, but they felt... hollow. Just scores, no context. No insights. No reason to use them daily beyond "I should train my brain."

**Clarity is what NeuroX could have been — and more.**

It's a personal cognitive lab that:
- Captures your daily context (mood, sleep, stress, events, activity)
- Trains your brain with science-backed cognitive games
- Analyzes *your* patterns and generates personalized insights
- Evolves with you using AI

Not a game collection. A tool for understanding yourself.

## Why Now? Why Kotlin Multiplatform?

When I learned about the Kotlin Multiplatform Contest, I realized: **This is the perfect opportunity to rebuild what I loved about NeuroX — and make it better.**

With KMP, I can:
1. **Build once, deploy everywhere**: Android and iOS with a single codebase
2. **Share complex logic**: EMA processing, game scoring, and AI insight generation work identically across platforms
3. **Showcase real-world KMP**: Prove that Kotlin Multiplatform isn't just for simple projects — it powers ambitious, data-driven, AI-enhanced applications

Clarity demonstrates that KMP is production-ready for non-trivial apps.

## What Is Clarity?

Clarity is a **Kotlin Multiplatform cognitive training and self-awareness app** that bridges the gap between brain-training games and personalized health insights.

### Core Concept

Every session follows this flow:

1. **Check In**: Answer 13 questions about your current state (mood, sleep, stress, context)
2. **Train**: Play one or more cognitive games
3. **Reflect**: Get personalized insights about how your habits affect your performance

Over time, patterns emerge. The app learns *your* peak performance conditions and helps you optimize.

## Features

### 1. EMA Questionnaire (Ecological Momentary Assessment)

A scientifically-grounded daily check-in that captures your current context:

#### Mood & Emotional State
- Anger (1–5): "Right now, to what extent do you feel angry?"
- Anxiety (1–5): "Right now, to what extent do you feel anxious?"
- Sadness (1–5): "Right now, to what extent do you feel sad?"
- Happiness (1–5): "Right now, to what extent do you feel happy?"

#### Sleep Quality
- Hours slept last night (0.5–12 hours)
- Sleep quality last night (1–5): "How restorative was your sleep?"

#### Stress & Life Events
- Recent stressful event in last 2 hours (Yes/No)
- Recent positive event (Yes/No + optional intensity 1–5)
- Event description (optional text)

#### Substance Use
- Caffeine in last hour (Yes/No)
- Alcohol today with options: "None", "1–2 drinks", "3–4 drinks", "5+ drinks"
- Medications/drugs with options: "None", "Prescribed", "Over-the-counter", "Recreational"

#### Pre-Session Context
- What you were doing 15 minutes ago: "Studying/Working", "Physical activity", "Relaxing", "Social media", "Just woke up", "Other"
- Who you're with: "Alone", "Family", "Friends", "Classmates/colleagues", "Public with strangers", "Other"

Each EMA snapshot is automatically linked to the game session that follows.

### 2. Cognitive Games (4 Core Games)

Each game is based on validated neuroscience tasks and trains a specific cognitive domain:

#### Go/No-Go: Response Inhibition & Reaction Time
- **Gameplay**: Tap quickly for GREEN stimuli, withhold response for RED stimuli
- **Why it matters**: Tests impulse control, sustained attention, and response speed. Sensitive to fatigue, stress, and cognitive load.
- **Metrics**: Average reaction time, accuracy on GO trials, false alarms on NO-GO trials
- **Scientific basis**: Conners Continuous Performance Test (CPT)

#### Pattern Grid: Visuospatial Working Memory
- **Gameplay**: A grid (e.g., 4×4, then larger) shows a pattern of highlighted cells. The pattern disappears. Reproduce it from memory.
- **Why it matters**: Trains visual-spatial reasoning, working memory capacity, and memory updating. Difficulty increases with each successful round.
- **Metrics**: Longest sequence reached, accuracy by difficulty level, number of errors
- **Scientific basis**: Visuospatial span and n-back task variants

#### Simon Sequence: Sequential Working Memory
- **Gameplay**: Watch a sequence of colors/shapes light up. Reproduce the sequence. Each successful round adds one more item to remember.
- **Why it matters**: Classic working memory task. Tests sequencing ability, attention, and recall. Widely used in cognitive research.
- **Metrics**: Sequence length reached, cumulative errors, speed of response
- **Scientific basis**: Simon task, serial recall tests

#### Visual Search: Selective Attention
- **Gameplay**: A grid full of symbols. Find the matching pair among distractors. Difficulty increases with more symbols and more similar distractors.
- **Why it matters**: Measures visual processing speed, selective attention, and resistance to distractors. Sensitive to cognitive load and fatigue.
- **Metrics**: Time to find pair, accuracy, number of incorrect selections, distractor resistance
- **Scientific basis**: Visual search paradigms in attention research

### 3. AI Coach Insights (Personalized Pattern Analysis)

After 3+ game sessions, Clarity analyzes your data and generates personalized insights:

#### Example Insights

🌙 Sleep & Reaction Time
On days with <6 hours of sleep, your reaction time is 18% slower on average.
💡 Recommendation: Try getting 7–8 hours before your next training session.

⚡ Stress & Accuracy
When stress level is high (4–5), your Go/No-Go accuracy drops by 12%.
💡 Recommendation: A 5-minute breathing exercise before training on stressful days may help.

✨ Positive Events Boost Performance
Sessions after positive events show 15% faster reaction times.
💡 Recommendation: Channel that positive energy into focused training.

🏃 Exercise & Memory
Your pattern grid scores are 20% better after physical activity.
💡 Recommendation: A short walk or stretch before challenging sessions.

☕ Caffeine Trade-Off
After caffeine, your reaction times improve but accuracy in memory tasks drops.
💡 Recommendation: Save caffeine for speed-based games, avoid before accuracy-focused tasks.

🧠 Your Peak Hours
Your best Simon sequence performance happens in the morning (6am–12pm).
💡 Recommendation: Schedule harder games for your peak cognitive hours.

🎧 Focus Zone
When alone, your scores are 20% more consistent and stable.
💡 Recommendation: Find a quiet space for best performance.

📊 Keep Playing
Play 5+ sessions to unlock more detailed pattern analysis.
💡 Keep training to see personalized insights about your cognition!

text

**Important**: These are observations about *your* data, not medical diagnoses or prescriptions. Use them to understand yourself better.

### 4. Progress & Reflection

- **Session history**: View all past game sessions with scores, EMA context, and baseline status
- **Filtering**: Analyze performance by game type, time of day, EMA state
- **Trends**: See how your cognition evolves over weeks and months
- **Optimization**: Understand *your* optimal conditions for peak performance

## Why This Matters

### For Students & Professionals
Understand how sleep, stress, exercise, and daily events affect your productivity. Optimize your study/work schedule based on *your* actual data, not generic advice.

### For Self-Trackers
Get personalized insights without needing a medical or research context. Clarity is for understanding yourself — not diagnosing, not competing.

### For Researchers
EMA + cognitive task data is valuable for understanding real-world cognition. Clarity could support future studies on how lifestyle factors, mood, and stress affect brain performance in daily life.

### For Kotlin Multiplatform Community
Clarity proves that KMP is production-ready for complex, data-driven apps:
- Shared business logic (EMA processing, game logic, AI analysis) on `commonMain`
- Shared UI via Compose Multiplatform (no XML layouts, no SwiftUI duplication)
- 90%+ code reuse between Android and iOS
- Real-world patterns and best practices

## How It's Different

| Aspect | NeuroX (Original) | NeuroNation / Lumosity | Clarity |
|--------|------------------|------------------------|---------|
| **Cognitive games** | ✅ Yes (4–5 tasks) | ✅ Yes (20+ games) | ✅ Yes (4 core games) |
| **Daily check-in (EMA)** | ✅ Full questionnaire | ❌ None | ✅ 13-question EMA |
| **Context capture** | ✅ Sleep, mood, stress | ❌ None | ✅ Full daily context |
| **AI-driven insights** | ⚠️ Limited | ❌ None | ✅ Personalized pattern analysis |
| **Long-term tracking** | ✅ Designed for research | ⚠️ Basic progress | ✅ Context-aware analytics |
| **Motivation for daily use** | ✅ Study obligation | ⚠️ Gamification | ✅ Personal insights |
| **Cross-platform** | ❌ Android only | ✅ Android, iOS (separate) | ✅ Android, iOS (KMP) |
| **Data export** | ⚠️ Research-only | ❌ None | ✅ (future feature) |

## How Clarity Could Help Others

### Students
- **Exam preparation**: Track how different sleep amounts, stress levels, and study routines affect your test readiness
- **Learning optimization**: Understand when you learn best (morning vs. evening, alone vs. with friends, after exercise vs. after rest)
- **Procrastination insights**: See if poor sleep or high stress makes you less focused, motivating better habits

### Working Professionals
- **Productivity optimization**: Discover your peak performance hours and schedule important work then
- **Stress management**: Correlate work stress with cognitive decline and practice coping strategies that actually work for you
- **Work-life balance**: Understand how exercise, sleep, and hobbies affect your professional performance

### Athletes & Fitness Enthusiasts
- **Pre-competition prep**: Learn how exercise affects cognitive performance (reaction time, focus)
- **Recovery tracking**: See how sleep quality impacts athletic cognition the next day
- **Peak performance conditions**: Identify your optimal training time and context

### People Managing Health Conditions
- **Sleep disorder tracking**: If you have insomnia or sleep apnea, see exactly how poor sleep impacts cognition
- **Stress management**: Quantify the relationship between stress and mental clarity
- **Medication effects**: Track how medications (prescribed or OTC) affect your cognitive performance

### Psychology Researchers
- **Real-world EMA data**: Clarity could be extended to support research studies on cognition and lifestyle factors
- **Ambulatory assessment**: The EMA + game data combination is valuable for understanding how daily life influences brain function
- **Data collection tool**: Could be adapted for academic studies on cognitive psychology, health psychology, or neuroscience

### General Population
- **Self-discovery**: Simply learn more about yourself — your patterns, your peak times, your optimal conditions
- **Daily motivation**: Get personalized insights that encourage healthy habits (sleep, exercise, stress management)
- **Brain health**: Track and improve your cognitive fitness the way you'd track physical fitness
- **Data-driven decisions**: Stop guessing about what helps you perform better — use your own data to decide

## The Vision

Clarity isn't trying to be a replacement for medical treatment or clinical assessment. Instead, it's a **personal cognitive companion** that:

1. **Educates** you about your own mind
2. **Empowers** you with self-knowledge
3. **Encourages** better habits through personalized insights
4. **Enables** future research through rich, contextualized data

The hope is that thousands of people using Clarity could one day contribute to a collective understanding of how lifestyle, mood, and daily events affect cognition — data that researchers could use to help millions.

## Future Roadmap: The Hybrid Vision
While Clarity is currently a private, offline-first application, we envision a "Hybrid Architecture" for Version 2:
1.  **Offline Foundation (Current)**: Privacy-first tracking and basic statistics on-device.
2.  **Opt-in Cloud Engine**: Users can enable WiFi/Cloud sync to access more powerful AI models (e.g., Gemini Flash) for deep correlation analysis, while keeping basic tracking offline.
3.  **Community Benchmarks**: Anonymized data sharing to compare your reaction times against age-group norms.

---

## Technical Architecture (The "How")

This project is built from scratch for the Kotlin Multiplatform Contest 2025.

### Tech Stack
*   **Language**: Kotlin 2.0
*   **Platform**: Kotlin Multiplatform (Android & iOS)
*   **UI**: Compose Multiplatform (Shared UI)
*   **Dependency Injection**: Koin 4.0
*   **Navigation**: JetPack Navigation Compose
*   **Serialization**: Kotlinx Serialization
*   **Date/Time**: Kotlinx DateTime
*   **Architecture**: MVI (Model-View-Intent) pattern with `StateFlow`

### Project Structure
*   `composeApp/`: Contains 100% of the game logic, UI code, and data repositories.
    *   `commonMain`: The heart of the app.
    *   `androidMain`: Android-specific entry points.
    *   `iosMain`: iOS-specific entry points.
*   `androidApp/`: Thin wrapper for the Android application.
*   `iosApp/`: Thin wrapper for the iOS application.

---