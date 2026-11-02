package com.aether.application.feature.profile.data.repository

import android.net.Uri
import android.util.Log
import com.aether.application.core.network.cloudinary.CloudinaryApi
import com.aether.application.core.network.cloudinary.CloudinaryFileType
import com.aether.application.core.network.cloudinary.CloudinaryUploader
import com.aether.application.core.network.readErrorMessage
import com.aether.application.feature.auth.data.remote.ProfileApi
import com.aether.application.feature.profile.data.remote.dto.ImageRequest
import com.aether.application.feature.profile.data.remote.dto.ReportProblemRequest
import com.aether.application.feature.profile.domain.model.ProblemCategory
import com.aether.application.feature.profile.domain.model.ProblemException
import com.aether.application.feature.profile.domain.model.screenshotMimeType
import com.aether.application.feature.profile.domain.repository.ProblemRepository
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

private const val TAG = "ProblemRepository"

class ProblemRepositoryImpl(
    private val profileApi: ProfileApi,
    private val cloudinaryApi: CloudinaryApi,
    private val uploader: CloudinaryUploader
) : ProblemRepository {

    override suspend fun getCategories(): List<ProblemCategory> =
        call("Não foi possível carregar as categorias. Tente novamente.") {
            profileApi.getProblemCategories().map { ProblemCategory(code = it.code, label = it.label) }
        }

    override fun displayName(uri: String): String? =
        try {
            uploader.displayName(Uri.parse(uri))
        } catch (e: Exception) {
            Log.w(TAG, "Failed to read the name of $uri", e)
            null
        }

    override suspend fun reportProblem(
        categoryCode: String,
        description: String,
        screenshotUri: String?,
        screenshotName: String?
    ) {
        val image = if (screenshotUri != null && screenshotName != null) {
            val mimeType = screenshotMimeType(screenshotName)
                ?: throw ProblemException("Selecione uma imagem PNG, JPG ou JPEG.")
            call("Não foi possível enviar o print. Tente novamente.") {
                val signature = cloudinaryApi.getUploadSignature(CloudinaryFileType.IMAGE)
                val url = uploader.upload(Uri.parse(screenshotUri), screenshotName, mimeType, signature)
                ImageRequest(imageName = screenshotName, imageUrl = url)
            }
        } else {
            null
        }

        call("Não foi possível enviar o problema. Tente novamente.") {
            profileApi.reportProblem(
                ReportProblemRequest(category = categoryCode, description = description, image = image)
            )
        }
    }

    private suspend fun <T> call(fallbackMessage: String, block: suspend () -> T): T =
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            Log.e(TAG, "Request failed (${e.code()})", e)
            // Only 4xx messages are written for the user; a 5xx carries the raw exception text
            val serverMessage = if (e.code() in 400..499 && e.code() != 404) e.readErrorMessage() else null
            throw ProblemException(serverMessage ?: fallbackMessage, e)
        } catch (e: IOException) {
            Log.e(TAG, "Request failed", e)
            throw ProblemException("Sem conexão com o servidor. Tente novamente.", e)
        } catch (e: SerializationException) {
            Log.e(TAG, "Unexpected response", e)
            throw ProblemException(fallbackMessage, e)
        }
}
