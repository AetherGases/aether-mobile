package com.aether.application.core.database

import android.content.ContentValues
import android.database.Cursor
import com.aether.application.core.database.AetherDatabase.Address
import com.aether.application.core.database.AetherDatabase.Permission as PermissionTable
import com.aether.application.core.database.AetherDatabase.Profile
import com.aether.application.core.database.model.AddressKind
import com.aether.application.core.database.model.LocalProfile
import com.aether.application.core.database.model.WorkplaceAddress
import com.aether.application.core.security.Encryptor
import com.aether.application.feature.auth.domain.model.Permission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

/** Personal data (e-mail, CPF, phone) and permission names are stored encrypted. */
class ProfileLocalDataSourceImpl(
    private val database: AetherDatabase,
    private val encryptor: Encryptor
) : ProfileLocalDataSource {

    override suspend fun saveProfile(profile: LocalProfile) = withContext(Dispatchers.IO) {
        val db = database.writableDatabase
        db.beginTransaction()
        try {
            db.delete(Profile.TABLE, null, null)
            db.delete(PermissionTable.TABLE, null, null)

            db.insertOrThrow(Profile.TABLE, null, ContentValues().apply {
                put(Profile.ID, profile.id)
                put(Profile.NAME, profile.name)
                put(Profile.EMAIL, encryptor.encrypt(profile.email))
                put(Profile.CPF, encryptor.encrypt(profile.cpf))
                put(Profile.PHONE, encryptor.encrypt(profile.phone))
                put(Profile.AVATAR_URL, profile.avatarUrl)
                put(Profile.CREATED_AT, profile.createdAt?.toEpochMilli())
            })

            profile.permissions.forEach { permission ->
                db.insertOrThrow(PermissionTable.TABLE, null, ContentValues().apply {
                    put(PermissionTable.ID, permission.id)
                    put(PermissionTable.NAME, encryptor.encrypt(permission.name))
                    put(PermissionTable.DESCRIPTION, permission.description)
                })
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    override suspend fun getProfile(): LocalProfile? = withContext(Dispatchers.IO) {
        val db = database.readableDatabase

        val permissions = db.query(PermissionTable.TABLE, null, null, null, null, null, null).use { cursor ->
            buildList {
                while (cursor.moveToNext()) {
                    add(
                        Permission(
                            id = cursor.long(PermissionTable.ID),
                            name = encryptor.decrypt(cursor.string(PermissionTable.NAME)),
                            description = cursor.stringOrNull(PermissionTable.DESCRIPTION)
                        )
                    )
                }
            }
        }

        db.query(Profile.TABLE, null, null, null, null, null, null, "1").use { cursor ->
            if (!cursor.moveToFirst()) return@withContext null
            LocalProfile(
                id = cursor.long(Profile.ID),
                name = cursor.string(Profile.NAME),
                email = encryptor.decrypt(cursor.string(Profile.EMAIL)),
                cpf = encryptor.decrypt(cursor.string(Profile.CPF)),
                phone = encryptor.decrypt(cursor.string(Profile.PHONE)),
                avatarUrl = cursor.stringOrNull(Profile.AVATAR_URL),
                createdAt = cursor.longOrNull(Profile.CREATED_AT)?.let(Instant::ofEpochMilli),
                permissions = permissions
            )
        }
    }

    override suspend fun updateAvatar(avatarUrl: String) {
        withContext(Dispatchers.IO) {
            database.writableDatabase.update(
                Profile.TABLE,
                ContentValues().apply { put(Profile.AVATAR_URL, avatarUrl) },
                null,
                null
            )
        }
    }

    override suspend fun saveAddresses(addresses: List<WorkplaceAddress>) = withContext(Dispatchers.IO) {
        val db = database.writableDatabase
        db.beginTransaction()
        try {
            db.delete(Address.TABLE, null, null)
            addresses.forEach { address ->
                db.insertOrThrow(Address.TABLE, null, ContentValues().apply {
                    put(Address.KIND, address.kind.name)
                    put(Address.ZIP_CODE, address.zipCode)
                    put(Address.STATE, address.state)
                    put(Address.CITY, address.city)
                    put(Address.NEIGHBORHOOD, address.neighborhood)
                    put(Address.STREET, address.street)
                    put(Address.NUMBER, address.number)
                    put(Address.COMPLEMENT, address.complement)
                    put(Address.LATITUDE, address.latitude)
                    put(Address.LONGITUDE, address.longitude)
                })
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    override suspend fun getAddress(kind: AddressKind): WorkplaceAddress? = withContext(Dispatchers.IO) {
        database.readableDatabase.query(
            Address.TABLE, null, "${Address.KIND} = ?", arrayOf(kind.name), null, null, null
        ).use { cursor ->
            if (!cursor.moveToFirst()) return@withContext null
            WorkplaceAddress(
                kind = kind,
                zipCode = cursor.stringOrNull(Address.ZIP_CODE),
                state = cursor.string(Address.STATE),
                city = cursor.string(Address.CITY),
                neighborhood = cursor.string(Address.NEIGHBORHOOD),
                street = cursor.string(Address.STREET),
                number = cursor.long(Address.NUMBER).toInt(),
                complement = cursor.stringOrNull(Address.COMPLEMENT),
                latitude = cursor.doubleOrNull(Address.LATITUDE),
                longitude = cursor.doubleOrNull(Address.LONGITUDE)
            )
        }
    }

    override suspend fun saveCoordinates(kind: AddressKind, latitude: Double, longitude: Double) {
        withContext(Dispatchers.IO) {
            database.writableDatabase.update(
                Address.TABLE,
                ContentValues().apply {
                    put(Address.LATITUDE, latitude)
                    put(Address.LONGITUDE, longitude)
                },
                "${Address.KIND} = ?",
                arrayOf(kind.name)
            )
        }
    }

    override suspend fun clear() = withContext(Dispatchers.IO) {
        val db = database.writableDatabase
        db.beginTransaction()
        try {
            db.delete(Profile.TABLE, null, null)
            db.delete(PermissionTable.TABLE, null, null)
            db.delete(Address.TABLE, null, null)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    private fun Cursor.string(column: String): String = getString(getColumnIndexOrThrow(column))

    private fun Cursor.stringOrNull(column: String): String? {
        val index = getColumnIndexOrThrow(column)
        return if (isNull(index)) null else getString(index)
    }

    private fun Cursor.long(column: String): Long = getLong(getColumnIndexOrThrow(column))

    private fun Cursor.longOrNull(column: String): Long? {
        val index = getColumnIndexOrThrow(column)
        return if (isNull(index)) null else getLong(index)
    }

    private fun Cursor.doubleOrNull(column: String): Double? {
        val index = getColumnIndexOrThrow(column)
        return if (isNull(index)) null else getDouble(index)
    }
}
