package com.aether.application.core.location

import android.content.Context
import com.aether.application.core.utils.AppLocale
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

private const val TAG = "AddressGeocoder"

data class Coordinates(val latitude: Double, val longitude: Double)

/** Turns a postal address into coordinates with the platform geocoder (needs network). */
class AddressGeocoder(
    private val context: Context
) {
    suspend fun geocode(address: String): Coordinates? {
        if (!Geocoder.isPresent()) return null
        val geocoder = Geocoder(context, AppLocale.current())

        return try {
            val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocationName(address, 1, object : Geocoder.GeocodeListener {
                        override fun onGeocode(addresses: MutableList<Address>) {
                            continuation.resume(addresses.firstOrNull())
                        }

                        override fun onError(errorMessage: String?) {
                            Log.e(TAG, "Failed to geocode address: $errorMessage")
                            continuation.resume(null)
                        }
                    })
                }
            } else {
                withContext(Dispatchers.IO) {
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocationName(address, 1)?.firstOrNull()
                }
            }
            result?.let { Coordinates(it.latitude, it.longitude) }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to geocode address", e)
            null
        }
    }
}
