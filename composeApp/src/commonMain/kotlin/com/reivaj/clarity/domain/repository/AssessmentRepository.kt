package com.reivaj.clarity.domain.repository

import com.reivaj.clarity.domain.model.UserContextAssessment

/**
 * Defines the contract for accessing and managing user context assessments.
 * This interface is the cornerstone of the domain layer, providing a clean separation
 * between the application's business logic and the data source implementations.
 *
 * It is designed to support both local and remote data operations, ensuring that the
 * application remains functional regardless of network connectivity.
 */
interface AssessmentRepository {

    /**
     * Submits a new user context assessment.
     * This function is responsible for persisting the assessment data to a chosen
     * data source, such as a local database or a remote server.
     *
     * @param assessment The [UserContextAssessment] object to be saved.
     * @return A [Result] object that encapsulates the success or failure of the operation.
     *         On success, it returns `Unit`. On failure, it provides an explanatory [Throwable].
     */
    suspend fun submitAssessment(assessment: UserContextAssessment): Result<Unit>

    /**
     * Retrieves all stored user context assessments.
     * This function fetches all historical assessment data, which is essential for
     * tracking trends and calculating baseline psychological states over time.
     *
     * @return A [Result] object containing a list of [UserContextAssessment] on success,
     *         or a [Throwable] on failure.
     */
    suspend fun getAllAssessments(): Result<List<UserContextAssessment>>
}
