package com.aether.application.feature.auth.domain.repository

class RecoveryCodeStorage {
    var email: String? = null
        private set
    var key: String? = null
        private set

    fun saveEmail(email: String) {
        this.email = email
    }

    fun saveKey(key: String) {
        this.key = key
    }

    fun clear() {
        email = null
        key = null
    }
}
