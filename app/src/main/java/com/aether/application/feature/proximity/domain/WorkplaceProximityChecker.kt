package com.aether.application.feature.proximity.domain

import android.location.Location
import android.util.Log
import com.aether.application.core.database.ProfileLocalDataSource
import com.aether.application.core.database.model.AddressKind
import com.aether.application.core.location.AddressGeocoder
import com.aether.application.core.location.DeviceLocationProvider

private const val TAG = "WorkplaceProximity"

/** How far from the workplace the app can still be used. */
const val WORKPLACE_RADIUS_METERS = 1_000f

sealed interface ProximityStatus {
    data object Allowed : ProximityStatus
    data object PermissionRequired : ProximityStatus
    data object LocationDisabled : ProximityStatus
    data object LocationUnavailable : ProximityStatus
    data class TooFar(val distanceMeters: Int) : ProximityStatus
}

/**
 * Checks the device is around the employee's unit (or the enterprise, when the unit has no address).
 * Workplaces without an address, or that can't be geocoded, are not enforced.
 */
class WorkplaceProximityChecker(
    private val profileLocalDataSource: ProfileLocalDataSource,
    private val locationProvider: DeviceLocationProvider,
    private val geocoder: AddressGeocoder
) {
    suspend fun check(): ProximityStatus {
        val workplace = profileLocalDataSource.getAddress(AddressKind.UNIT)
            ?: profileLocalDataSource.getAddress(AddressKind.ENTERPRISE)
            ?: run {
                Log.w(TAG, "No workplace address stored, skipping proximity check")
                return ProximityStatus.Allowed
            }

        val latitude: Double
        val longitude: Double
        if (workplace.latitude != null && workplace.longitude != null) {
            latitude = workplace.latitude
            longitude = workplace.longitude
        } else {
            val coordinates = geocoder.geocode(workplace.fullAddress) ?: run {
                Log.w(TAG, "Workplace address could not be geocoded, skipping proximity check")
                return ProximityStatus.Allowed
            }
            profileLocalDataSource.saveCoordinates(workplace.kind, coordinates.latitude, coordinates.longitude)
            latitude = coordinates.latitude
            longitude = coordinates.longitude
        }

        if (!locationProvider.hasPermission()) return ProximityStatus.PermissionRequired
        if (!locationProvider.isLocationEnabled()) return ProximityStatus.LocationDisabled
        val location = locationProvider.currentLocation() ?: return ProximityStatus.LocationUnavailable

        val distance = FloatArray(1)
        Location.distanceBetween(location.latitude, location.longitude, latitude, longitude, distance)

        // An approximate fix may be off by its accuracy, so it only counts against the user beyond that
        val accuracy = if (location.hasAccuracy()) location.accuracy else 0f
        return if (distance[0] - accuracy <= WORKPLACE_RADIUS_METERS) {
            ProximityStatus.Allowed
        } else {
            ProximityStatus.TooFar(distanceMeters = distance[0].toInt())
        }
    }
}
