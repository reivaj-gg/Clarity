package com.reivaj.clarity.presentation.ema

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


import androidx.compose.ui.text.style.TextAlign
import org.koin.compose.koinInject
import org.koin.core.annotation.KoinExperimentalAPI

import clarity.composeapp.generated.resources.Res
import clarity.composeapp.generated.resources.logoClarity
import org.jetbrains.compose.resources.painterResource
import androidx.compose.foundation.Image
import com.reivaj.clarity.domain.model.*
import androidx.compose.foundation.clickable


@OptIn(KoinExperimentalAPI::class, ExperimentalMaterial3Api::class)
@Composable
fun EMAScreen(
    onNavigateToGames: () -> Unit,
    viewModel: EMAViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()
    var currentStep by remember { mutableStateOf(0) }
    // Steps:
    // 0: Intro/Logo
    // 1: Anger
    // 2: Anxiety
    // 3: Sadness
    // 4: Happiness
    // 5: Recent Stress
    // 6: Sleep Hours
    // 7: Sleep Quality
    // 8: Caffeine
    // 9: Alcohol/Meds
    
    val totalQuestions = 15

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Check-in") }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (currentStep) {
                0 -> IntroStep(
                    onNext = { currentStep++ }
                )
                1 -> EmaQuestionScreen(
                    questionNumber = 1, totalQuestions = totalQuestions,
                    questionText = "To what extent do you feel Angry?",
                    value = state.anger, onValueChange = viewModel::onAngerChange,
                    onBack = { currentStep-- }, onNext = { currentStep++ }
                )
                2 -> EmaQuestionScreen(
                    questionNumber = 2, totalQuestions = totalQuestions,
                    questionText = "To what extent do you feel Anxious?",
                    value = state.anxiety, onValueChange = viewModel::onAnxietyChange,
                    onBack = { currentStep-- }, onNext = { currentStep++ }
                )
                3 -> EmaQuestionScreen(
                    questionNumber = 3, totalQuestions = totalQuestions,
                    questionText = "To what extent do you feel Sad?",
                    value = state.sadness, onValueChange = viewModel::onSadnessChange,
                    onBack = { currentStep-- }, onNext = { currentStep++ }
                )
                4 -> EmaQuestionScreen(
                    questionNumber = 4, totalQuestions = totalQuestions,
                    questionText = "To what extent do you feel Happy?",
                    value = state.happiness, onValueChange = viewModel::onHappinessChange,
                    onBack = { currentStep-- }, onNext = { currentStep++ }
                )
                5 -> BooleanQuestionScreen(
                     questionNumber = 5, totalQuestions = totalQuestions,
                     questionText = "Have you experienced a stressful event in the last 2 hours?",
                     value = state.recentStressfulEvent, onValueChange = viewModel::onStressfulEventChange,
                     onBack = { currentStep-- }, onNext = { currentStep++ }
                )
                6 -> SliderQuestionScreen(
                     questionNumber = 6, totalQuestions = totalQuestions,
                     questionText = "How many hours did you sleep last night?",
                     value = state.sleepHours, onValueChange = viewModel::onSleepHoursChange,
                     range = 0.5f..12f, steps = 22,
                     labelFormat = "%.1f hours",
                     onBack = { currentStep-- }, onNext = { currentStep++ }
                )
                7 -> EmaQuestionScreen(
                    questionNumber = 7, totalQuestions = totalQuestions,
                    questionText = "How was your sleep quality?",
                    value = state.sleepQuality, onValueChange = viewModel::onSleepQualityChange,
                    valueLabelLeft = "Poor", valueLabelRight = "Excellent",
                    onBack = { currentStep-- }, onNext = { currentStep++ }
                )
                8 -> BooleanQuestionScreen(
                     questionNumber = 8, totalQuestions = totalQuestions,
                     questionText = "Have you had caffeine in the last hour?",
                     value = state.caffeineRecent, onValueChange = viewModel::onCaffeineChange,
                     onBack = { currentStep-- }, onNext = { currentStep++ }
                )
                9 -> SingleChoiceEnumQuestionScreen(
                     questionNumber = 9, totalQuestions = 15,
                     questionText = "Alcohol consumption today:",
                     options = AlcoholUseToday.values().toList(),
                     selectedOption = state.alcoholUse,
                     onOptionSelected = viewModel::onAlcoholChange,
                     labelProvider = { 
                         when(it) {
                             AlcoholUseToday.NONE -> "No alcohol today"
                             AlcoholUseToday.SMALL -> "Yes, 1–2 drinks"
                             AlcoholUseToday.MODERATE -> "Yes, 3–4 drinks"
                             AlcoholUseToday.HIGH -> "Yes, 5+ drinks"
                         }
                     },
                     onBack = { currentStep-- }, onNext = { currentStep++ }
                )
                10 -> SingleChoiceEnumQuestionScreen(
                     questionNumber = 10, totalQuestions = 15,
                     questionText = "Medications or substances today:",
                     options = SubstanceType.values().toList(),
                     selectedOption = state.substanceType,
                     onOptionSelected = viewModel::onSubstanceTypeChange,
                     labelProvider = {
                         when(it) {
                             SubstanceType.NONE -> "No"
                             SubstanceType.PRESCRIBED -> "Yes – prescribed medication"
                             SubstanceType.OTC -> "Yes – over-the-counter"
                             SubstanceType.RECREATIONAL -> "Yes – recreational substances"
                         }
                     },
                     onBack = { currentStep-- }, onNext = { currentStep++ },
                     showTextField = (state.substanceType != SubstanceType.NONE),
                     textValue = state.substanceDescription,
                     onTextChange = viewModel::onSubstanceDescriptionChange,
                     textLabel = "Optional description (e.g. Advil, etc.)"
                )
                11 -> EventQuestionScreen(
                    questionNumber = 11, totalQuestions = 15,
                    questionText = "Have you experienced a POSITIVE event since your last check-in?",
                    hasEvent = state.hasPositiveEvent, onHasEventChange = viewModel::onHasPositiveEventChange,
                    intensity = state.positiveEventIntensity, onIntensityChange = viewModel::onPositiveEventIntensityChange,
                    description = state.positiveEventDescription, onDescriptionChange = viewModel::onPositiveEventDescriptionChange,
                    onBack = { currentStep-- }, onNext = { currentStep++ }
                )
                12 -> EventQuestionScreen(
                    questionNumber = 12, totalQuestions = 15,
                    questionText = "Have you experienced a NEGATIVE or stressful event since your last check-in?",
                    hasEvent = state.hasNegativeEvent, onHasEventChange = viewModel::onHasNegativeEventChange,
                    intensity = state.negativeEventIntensity, onIntensityChange = viewModel::onNegativeEventIntensityChange,
                    description = state.negativeEventDescription, onDescriptionChange = viewModel::onNegativeEventDescriptionChange,
                    onBack = { currentStep-- }, onNext = { currentStep++ }
                )
                13 -> SingleChoiceEnumQuestionScreen(
                     questionNumber = 13, totalQuestions = 15,
                     questionText = "What were you doing 15 minutes before this session?",
                     options = PreSessionActivity.values().toList(),
                     selectedOption = state.preSessionActivity,
                     onOptionSelected = viewModel::onActivityChange,
                     labelProvider = { it.name.replace("_", " ").lowercase().replaceFirstChar { char -> char.uppercase() } },
                     onBack = { currentStep-- }, onNext = { currentStep++ }
                )
                14 -> SingleChoiceEnumQuestionScreen(
                     questionNumber = 14, totalQuestions = 15,
                     questionText = "Right now, are you alone or with someone?",
                     options = SocialContext.values().toList(),
                     selectedOption = state.socialContext,
                     onOptionSelected = viewModel::onSocialContextChange,
                     labelProvider = { it.name.replace("_", " ").lowercase().replaceFirstChar { char -> char.uppercase() } },
                     onBack = { currentStep-- }, onNext = { currentStep++ }
                )
                15 -> SingleChoiceEnumQuestionScreen(
                     questionNumber = 15, totalQuestions = 15,
                     questionText = "Current environment noise level:",
                     options = EnvironmentContext.values().toList(),
                     selectedOption = state.environmentContext,
                     onOptionSelected = viewModel::onEnvironmentContextChange,
                     labelProvider = { it.name.lowercase().replaceFirstChar { char -> char.uppercase() } },
                     onBack = { currentStep-- }, 
                     onNext = { 
                         viewModel.submitEMA(onComplete = onNavigateToGames)
                     },
                     isNextLabelFinish = true
                )
            }
        }
    }
}

// ... IntroStep ...

@Composable
fun IntroStep(onNext: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(Res.drawable.logoClarity),
            contentDescription = "Clarity Logo",
            modifier = Modifier.size(160.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            "Welcome to Clarity",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Take a moment to check in with yourself before you start training.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(64.dp))
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Start Check-in")
        }
    }
}

@Composable
fun <T> SingleChoiceEnumQuestionScreen(
    questionNumber: Int, totalQuestions: Int,
    questionText: String,
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    labelProvider: (T) -> String,
    onBack: () -> Unit, onNext: () -> Unit,
    showTextField: Boolean = false,
    textValue: String = "",
    onTextChange: (String) -> Unit = {},
    textLabel: String = "",
    isNextLabelFinish: Boolean = false
) {
    EmaQuestionTemplate(questionNumber, totalQuestions, questionText, onBack, onNext, isNextLabelFinish) {
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            options.forEach { option ->
                Row(
                   modifier = Modifier
                       .fillMaxWidth()
                       .padding(vertical = 4.dp)
                       .clickable { onOptionSelected(option) }
                       .padding(8.dp),
                   verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (option == selectedOption),
                        onClick = null // Handled by Row clickable
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = labelProvider(option), style = MaterialTheme.typography.bodyLarge)
                }
            }
            
            if (showTextField) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = textValue,
                    onValueChange = onTextChange,
                    label = { Text(textLabel) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun EventQuestionScreen(
    questionNumber: Int, totalQuestions: Int,
    questionText: String,
    hasEvent: Boolean, onHasEventChange: (Boolean) -> Unit,
    intensity: Float, onIntensityChange: (Float) -> Unit,
    description: String, onDescriptionChange: (String) -> Unit,
    onBack: () -> Unit, onNext: () -> Unit
) {
    EmaQuestionTemplate(questionNumber, totalQuestions, questionText, onBack, onNext) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
             Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                FilterChip(selected = hasEvent, onClick = { onHasEventChange(true) }, label = { Text("Yes") }, modifier = Modifier.padding(8.dp))
                FilterChip(selected = !hasEvent, onClick = { onHasEventChange(false) }, label = { Text("No") }, modifier = Modifier.padding(8.dp))
            }
            
            if (hasEvent) {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Intensity: ${intensity.toInt()}", style = MaterialTheme.typography.bodyMedium)
                Slider(value = intensity, onValueChange = onIntensityChange, valueRange = 1f..5f, steps = 3)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Describe briefly (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun BooleanQuestionScreen(
    questionNumber: Int, totalQuestions: Int,
    questionText: String,
    value: Boolean, onValueChange: (Boolean) -> Unit,
    onBack: () -> Unit, onNext: () -> Unit,
    isNextLabelFinish: Boolean = false
) {
    EmaQuestionTemplate(questionNumber, totalQuestions, questionText, onBack, onNext, isNextLabelFinish) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = value,
                onClick = { onValueChange(true) },
                label = { Text("Yes") },
                modifier = Modifier.padding(8.dp)
            )
            FilterChip(
                selected = !value,
                onClick = { onValueChange(false) },
                label = { Text("No") },
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun SliderQuestionScreen(
    questionNumber: Int, totalQuestions: Int,
    questionText: String,
    value: Float, onValueChange: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float>, steps: Int,
    labelFormat: String,
    onBack: () -> Unit, onNext: () -> Unit
) {
     EmaQuestionTemplate(questionNumber, totalQuestions, questionText, onBack, onNext) {
         Column(horizontalAlignment = Alignment.CenterHorizontally) {
             Text(labelFormat.format(value), style = MaterialTheme.typography.headlineMedium)
             Slider(
                 value = value,
                 onValueChange = onValueChange,
                 valueRange = range,
                 steps = steps
             )
         }
     }
}

@Composable
fun EmaQuestionTemplate(
    questionNumber: Int, totalQuestions: Int,
    questionText: String,
    onBack: () -> Unit, onNext: () -> Unit,
    isNextLabelFinish: Boolean = false,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Question $questionNumber of $totalQuestions", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(32.dp))
        Text(questionText, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(48.dp))
        content()
        Spacer(modifier = Modifier.weight(1f))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("Back") }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onNext) { Text(if (isNextLabelFinish) "Finish" else "Next") }
        }
    }
}
