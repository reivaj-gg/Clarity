package com.reivaj.clarity.data.repository

import com.reivaj.clarity.domain.model.UserContextAssessment
import com.reivaj.clarity.domain.repository.AssessmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * An in-memory implementation of the [AssessmentRepository] for demonstration and testing purposes.
 * This class simulates data storage by maintaining a mutable list of assessments in memory.
 * It is not intended for production use, as data will not persist across application restarts.
 *
 * The use of `withContext(Dispatchers.Default)` simulates the behavior of a real-world
 * repository that would perform I/O operations on a background thread.
 */
class AssessmentRepositoryImpl : AssessmentRepository {

    // A private mutable list to act as an in-memory database.
    private val assessments = mutableListOf<UserContextAssessment>()

    /**
     * Adds a new assessment to the in-memory list.
     * This operation is performed on a background thread to simulate non-blocking I/O.
     *
     * @param assessment The [UserContextAssessment] to add.
     * @return A [Result] indicating the success of the operation.
     */
    override suspend fun submitAssessment(assessment: UserContextAssessment): Result<Unit> = withContext(Dispatchers.Default) {
        try {
            assessments.add(assessment)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Retrieves all assessments currently stored in the in-memory list.
     * This operation is also performed on a background thread.
     *
     * @return A [Result] containing the list of [UserContextAssessment] on success,
     *         or an exception on failure.
     */
    override suspend fun getAllAssessments(): Result<List<UserContextAssessment>> = withContext(Dispatchers.Default) {
        try {
            Result.success(assessments.toList()) // Return a copy to prevent external modification
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
