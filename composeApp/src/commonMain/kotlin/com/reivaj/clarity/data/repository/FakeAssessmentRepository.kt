package com.reivaj.clarity.data.repository

import com.reivaj.clarity.domain.model.UserContextAssessment
import com.reivaj.clarity.domain.repository.AssessmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock

/**
 * A fake implementation of the [AssessmentRepository] for UI previews and testing.
 * This repository stores data in memory and allows for immediate, predictable responses
 * without requiring actual data sources or network calls.
 */
class FakeAssessmentRepository : AssessmentRepository {

    private val _assessments = MutableStateFlow<List<UserContextAssessment>>(emptyList())
    val assessments: Flow<List<UserContextAssessment>> = _assessments.asStateFlow()

    /**
     * Submits a new assessment to the fake repository.
     * It adds the assessment to the in-memory list and emits the updated list.
     *
     * @param assessment The [UserContextAssessment] to submit.
     * @return A [Result] indicating success or failure.
     */
    override suspend fun submitAssessment(assessment: UserContextAssessment): Result<Unit> {
        _assessments.value = _assessments.value + assessment
        return Result.success(Unit)
    }

    /**
     * Retrieves all assessments from the fake repository.
     * It returns the current in-memory list of assessments.
     *
     * @return A [Result] containing the list of [UserContextAssessment] on success,
     *         or an exception on failure.
     */
    override suspend fun getAllAssessments(): Result<List<UserContextAssessment>> {
        return Result.success(_assessments.value.toList())
    }

    // You might want to add some dummy data for previews here
    init {
        // Example dummy data for immediate preview display
        _assessments.value = listOf(
            UserContextAssessment(
                id = "ema-1",
                timestamp = Clock.System.now(),
                mood = 5,
                stress = 3,
                sleepQuality = 7,
                hoursSlept = 7.5,
                hasRecentNegativeEvent = false
            ),
            UserContextAssessment(
                id = "ema-2",
                timestamp = Clock.System.now(),
                mood = 3,
                stress = 8,
                sleepQuality = 5,
                hoursSlept = 6.0,
                hasRecentNegativeEvent = true
            )
        )
    }
}
