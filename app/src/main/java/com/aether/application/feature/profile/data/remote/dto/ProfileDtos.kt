package com.aether.application.feature.profile.data.remote.dto

import kotlinx.serialization.Serializable

/** Body of ms-auth `PATCH /api/profile`; null fields are left unchanged. */
@Serializable
data class UpdateProfileRequest(
    val image: ImageRequest? = null,
    val deviceToken: String? = null,
    val notifications: NotificationPreferencesDto? = null,
)

/** Which channels ms-inventory may notify the user on; null means "unchanged" in a PATCH and "never set" in a GET. */
@Serializable
data class NotificationPreferencesDto(
    val email: Boolean? = null,
    val mobile: Boolean? = null,
)

@Serializable
data class ImageRequest(
    val imageName: String,
    val imageUrl: String,
)

/** Body of ms-auth `POST /api/support`; [image] is the optional screenshot, already uploaded to Cloudinary. */
@Serializable
data class ReportProblemRequest(
    /** A [ProblemCategoryResponse.code] from `GET /api/support/categories`. */
    val category: String,
    /** The screen the problem happened on, 2 to 50 characters. */
    val screen: String,
    /** Required, trimmed, 3 to 200 characters (reported_problem.description). */
    val description: String,
    val image: ImageRequest? = null,
)

/** Response of `POST /api/support` (200); the echoed category, screen, description and image are ignored. */
@Serializable
data class ReportProblemResponse(
    val id: Long,
)

/** One item of `GET /api/support/categories`, mirroring the PROBLEM_CATEGORY enum. */
@Serializable
data class ProblemCategoryResponse(
    /** Enum name, e.g. `LOGIN_AND_ACCOUNT`; this is what the report request sends back. */
    val code: String,
    /** Display text for the dropdown. */
    val label: String,
)
