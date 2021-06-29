package com.codepan.security

import android.util.Base64
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class KeyManager(private val seed: String) {

    private val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    private var secret: SecretKeySpec
    private var iv: IvParameterSpec

    private val generatedKey: ByteArray?
        get() {
            try {
                val data = seed.toByteArray(Charsets.UTF_8)
                val md = MessageDigest.getInstance("SHA-1")
                val hash = md.digest(data)
                return Arrays.copyOf(hash, cipher.blockSize)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }


    init {
        val generated = generatedKey
        this.iv = IvParameterSpec(generated)
        this.secret = SecretKeySpec(generated, "AES")
    }

    fun encrypt(text: String): String {
        val data = text.toByteArray(Charsets.UTF_8)
        return encryptBytes(data)
    }

    fun encryptBytes(input: ByteArray): String {
        cipher.init(Cipher.ENCRYPT_MODE, secret, iv)
        val data = cipher.doFinal(input)
        val encoded = Base64.encode(data, Base64.DEFAULT)
        return String(encoded).trim()
    }

    fun decrypt(encrypted: String): String {
        val input = encrypted.toByteArray(Charsets.UTF_8)
        val data = Base64.decode(input, Base64.DEFAULT)
        return decryptBytes(data)
    }

    fun decryptBytes(input: ByteArray): String {
        cipher.init(Cipher.DECRYPT_MODE, secret, iv)
        val data = cipher.doFinal(input)
        return String(data)
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
            builder.append("\n\t")
            val collections = arrayListOf<Char>()
            encrypted.toCharArray().toCollection(collections)
            var first = true
            var counter = 0
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
                if (counter++ == 19) {
                    builder.append("\n\t")
                    counter = 0
                }
            }
            builder.append("\n}")
            return builder.toString()
        }
    }
}