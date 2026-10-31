package com.aether.application.feature.profile.domain.repository

import com.aether.application.feature.profile.domain.model.ProblemCategory

/** Every method throws a [com.aether.application.feature.profile.domain.model.ProblemException] on failure. */
interface ProblemRepository {

    suspend fun getCategories(): List<ProblemCategory>

    fun displayName(uri: String): String?

    /** Uploads the screenshot, when there is one, and then registers the problem. */
    suspend fun reportProblem(
        categoryCode: String,
        description: String,
        screenshotUri: String?,
        screenshotName: String?
    )
}
