package com.aether.application.feature.profile.data.remote.dto

import kotlinx.serialization.Serializable

/** Body of ms-auth `PATCH /api/profile`; null fields are left unchanged. */
@Serializable
data class UpdateProfileRequest(
    val image: ImageRequest? = null,
    val deviceToken: String? = null,
)

@Serializable
data class ImageRequest(
    val imageName: String,
    val imageUrl: String,
)

/** Body of `POST /api/profile/problem`; [image] is the optional screenshot, already uploaded to Cloudinary. */
@Serializable
data class ReportProblemRequest(
    /** A [ProblemCategoryResponse.code] from `GET /api/profile/problem/categories`. */
    val category: String,
    /** Required, trimmed, at most 100 characters (reported_problem.description). */
    val description: String,
    val image: ImageRequest? = null,
)

/** Response of `POST /api/profile/problem` (201). */
@Serializable
data class ReportProblemResponse(
    val id: Long,
)

/** One item of `GET /api/profile/problem/categories`, mirroring the PROBLEM_CATEGORY enum. */
@Serializable
data class ProblemCategoryResponse(
    /** Enum name, e.g. `LOGIN_AND_ACCOUNT`; this is what the report request sends back. */
    val code: String,
    /** Display text for the dropdown. */
    val label: String,
)
