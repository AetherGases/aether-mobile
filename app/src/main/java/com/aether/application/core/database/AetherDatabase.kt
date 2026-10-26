package com.aether.application.core.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

private const val DATABASE_NAME = "aether.db"
private const val DATABASE_VERSION = 1

/** Local SQLite store for the logged user's profile data. */
class AetherDatabase(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    object Profile {
        const val TABLE = "profile"
        const val ID = "id"
        const val NAME = "name"
        const val EMAIL = "email"
        const val CPF = "cpf"
        const val PHONE = "phone"
        const val AVATAR_URL = "avatar_url"
        const val CREATED_AT = "created_at"
    }

    object Permission {
        const val TABLE = "permission"
        const val ID = "id"
        const val NAME = "name"
        const val DESCRIPTION = "description"
    }

    object Address {
        const val TABLE = "address"
        const val KIND = "kind"
        const val ZIP_CODE = "zip_code"
        const val STATE = "state"
        const val CITY = "city"
        const val NEIGHBORHOOD = "neighborhood"
        const val STREET = "street"
        const val NUMBER = "number"
        const val COMPLEMENT = "complement"
        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE ${Profile.TABLE} (
                ${Profile.ID} INTEGER PRIMARY KEY,
                ${Profile.NAME} TEXT NOT NULL,
                ${Profile.EMAIL} TEXT NOT NULL,
                ${Profile.CPF} TEXT NOT NULL,
                ${Profile.PHONE} TEXT,
                ${Profile.AVATAR_URL} TEXT,
                ${Profile.CREATED_AT} INTEGER
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE ${Permission.TABLE} (
                ${Permission.ID} INTEGER PRIMARY KEY,
                ${Permission.NAME} TEXT NOT NULL,
                ${Permission.DESCRIPTION} TEXT
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE ${Address.TABLE} (
                ${Address.KIND} TEXT PRIMARY KEY,
                ${Address.ZIP_CODE} TEXT,
                ${Address.STATE} TEXT NOT NULL,
                ${Address.CITY} TEXT NOT NULL,
                ${Address.NEIGHBORHOOD} TEXT NOT NULL,
                ${Address.STREET} TEXT NOT NULL,
                ${Address.NUMBER} INTEGER NOT NULL,
                ${Address.COMPLEMENT} TEXT,
                ${Address.LATITUDE} REAL,
                ${Address.LONGITUDE} REAL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
}
