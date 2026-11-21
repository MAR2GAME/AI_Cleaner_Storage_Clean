package com.aicleanerstorage.spaceclean.data.applock.internal

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

internal object PinCredential {
    fun create(pin: String): String {
        val salt = ByteArray(16).also(SecureRandom()::nextBytes)
        val encoder = Base64.getEncoder()
        return encoder.encodeToString(salt) + "." + encoder.encodeToString(derive(pin, salt))
    }

    fun matches(
        pin: String,
        stored: String,
    ): Boolean {
        val parts = stored.split('.')
        if (parts.size != 2) return false
        return try {
            val decoder = Base64.getDecoder()
            MessageDigest.isEqual(derive(pin, decoder.decode(parts.first())), decoder.decode(parts.last()))
        } catch (_: IllegalArgumentException) {
            false
        }
    }

    private fun derive(
        pin: String,
        salt: ByteArray,
    ): ByteArray {
        val key = PBEKeySpec(pin.toCharArray(), salt, 210_000, 256)
        return try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(key).encoded
        } finally {
            key.clearPassword()
        }
    }
}
