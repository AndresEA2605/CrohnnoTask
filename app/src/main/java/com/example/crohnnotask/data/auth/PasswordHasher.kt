package com.example.crohnnotask.data.auth

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

data class PasswordDigest(
    val salt: String,
    val hash: String,
    val iterations: Int
)

class PasswordHasher(
    private val iterations: Int = DEFAULT_ITERATIONS,
    private val random: SecureRandom = SecureRandom()
) {
    fun hash(password: CharArray): PasswordDigest {
        val salt = ByteArray(SALT_BYTES).also(random::nextBytes)
        val derived = derive(password, salt, iterations)
        return PasswordDigest(
            salt = Base64.getEncoder().encodeToString(salt),
            hash = Base64.getEncoder().encodeToString(derived),
            iterations = iterations
        )
    }

    fun verify(password: CharArray, digest: PasswordDigest): Boolean {
        val salt = Base64.getDecoder().decode(digest.salt)
        val expected = Base64.getDecoder().decode(digest.hash)
        val actual = derive(password, salt, digest.iterations)
        return MessageDigest.isEqual(expected, actual)
    }

    private fun derive(password: CharArray, salt: ByteArray, cost: Int): ByteArray {
        val spec = PBEKeySpec(password, salt, cost, KEY_BITS)
        return try {
            SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    companion object {
        const val DEFAULT_ITERATIONS = 600_000
        private const val ALGORITHM = "PBKDF2WithHmacSHA256"
        private const val SALT_BYTES = 16
        private const val KEY_BITS = 256
    }
}
