package com.codepan.security

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class KeyManager(private val key: String) {

    private val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    private var iv: IvParameterSpec
    private var secret: SecretKeySpec

    private val generatedKey: ByteArray?
        get() {
            try {
                val data = key.toByteArray(Charsets.UTF_8)
                val md = MessageDigest.getInstance("SHA-1")
                val hash = md.digest(data)
                return Arrays.copyOf(hash, 32)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }


    init {
        val data = ByteArray(cipher.blockSize)
        val random = SecureRandom()
        random.nextBytes(data)
        this.iv = IvParameterSpec(data)
        this.secret = SecretKeySpec(generatedKey, "AES")
    }

    fun encrypt(text: String): String {
        val data = text.toByteArray(Charsets.UTF_8)
        return encryptBytes(data)
    }

    fun encryptBytes(data: ByteArray): String {
        cipher.init(Cipher.ENCRYPT_MODE, secret, iv)
        val encoded = Base64.encode(data, Base64.DEFAULT)
        return String(encoded)
    }

    fun decrypt(text: String): String {
        val data = text.toByteArray(Charsets.UTF_8)
        return decryptBytes(data)
    }

    fun decryptBytes(data: ByteArray): String {
        cipher.init(Cipher.DECRYPT_MODE, secret, iv)
        val encoded = Base64.decode(data, Base64.DEFAULT)
        return String(encoded)
    }

    fun decryptUnsorted(input: ByteArray): String {
        val data = sort(input)
        return decryptBytes(data)
    }

    companion object {

        fun sort(input: ByteArray): ByteArray {
            val output = arrayListOf<Byte>()
            val half = input.size / 2
            var counter = 0
            val odds = arrayListOf<Byte>()
            for (i in input.indices) {
                val char = input[i]
                if (i % 2 == 0) {
                    if (counter <= half) {
                        output.add(char)
                        counter++;
                    }
                } else {
                    odds.add(char)
                }
            }
            for (i in half - 1 downTo 0) {
                output.add(odds[i])
            }
            return output.toByteArray()
        }

        fun fromBytes(data: ByteArray): KeyManager {
            val key = String(data)
            return KeyManager(key)
        }

        fun from(input: ByteArray): KeyManager {
            val data = sort(input)
            return fromBytes(data)
        }

        fun getElements(encrypted: String): String {
            val builder = StringBuilder("{")
            val collections = arrayListOf<Char>()
            encrypted.trim().toCharArray().toCollection(collections)
            var first = true
            while (collections.isNotEmpty()) {
                first = if (first) {
                    val char = collections.first()
                    builder.append(char.toByte())
                    collections.removeFirst()
                    false
                } else {
                    val char = collections.last()
                    builder.append(char.toByte())
                    collections.removeLast()
                    true
                }
                if (collections.isNotEmpty()) {
                    builder.append(",")
                }
            }
            builder.append("}")
            return builder.toString()
        }
    }
}