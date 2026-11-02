package com.aether.application.core.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

private const val DATABASE_NAME = "aether.db"
// v2: profile.phone became nullable
// v3: enterprise table
private const val DATABASE_VERSION = 3

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

    object Enterprise {
        const val TABLE = "enterprise"
        const val ID = "id"
        const val NAME = "name"
        const val TRADE_NAME = "trade_name"
        const val CNPJ = "cnpj"
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
        createProfileTable(db, Profile.TABLE)
        createEnterpriseTable(db)
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

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) relaxProfilePhone(db)
        if (oldVersion < 3) createEnterpriseTable(db)
    }

    /**
     * SQLite can't drop a NOT NULL constraint, so the profile table is rebuilt. Without this,
     * installs created at v1 kept failing to store profiles that have no phone.
     */
    private fun relaxProfilePhone(db: SQLiteDatabase) {
        val tempTable = "${Profile.TABLE}_new"
        db.beginTransaction()
        try {
            createProfileTable(db, tempTable)
            db.execSQL("INSERT INTO $tempTable SELECT * FROM ${Profile.TABLE}")
            db.execSQL("DROP TABLE ${Profile.TABLE}")
            db.execSQL("ALTER TABLE $tempTable RENAME TO ${Profile.TABLE}")
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    private fun createEnterpriseTable(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE ${Enterprise.TABLE} (
                ${Enterprise.ID} INTEGER PRIMARY KEY,
                ${Enterprise.NAME} TEXT NOT NULL,
                ${Enterprise.TRADE_NAME} TEXT,
                ${Enterprise.CNPJ} TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    private fun createProfileTable(db: SQLiteDatabase, table: String) {
        db.execSQL(
            """
            CREATE TABLE $table (
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
    }
}
