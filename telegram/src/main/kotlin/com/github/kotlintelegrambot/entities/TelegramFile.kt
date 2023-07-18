package com.github.kotlintelegrambot.entities

import java.io.File
import java.security.MessageDigest
import java.util.*

fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }

sealed class TelegramFile {
    data class ByFileId(val fileId: String) : TelegramFile()
    data class ByUrl(val url: String) : TelegramFile()
    data class ByFile(val file: File) : TelegramFile()
    data class ByByteArray(val fileBytes: ByteArray, var filename: String = "") : TelegramFile()
    {
        init {
            // If the filename is not provided, we need to create a unique name (that should be the same if the same
            // data becomes another Telegram file, so that "equals" matches
            if (filename == "") {
                val md = MessageDigest.getInstance("SHA-256")
                // val encoder: Base64.Encoder = Base64.getEncoder()
                // filename = encoder.encodeToString(md.digest(fileBytes))
                filename = md.digest(fileBytes).toHexString()
            }
        }
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ByByteArray) return false

            if (!fileBytes.contentEquals(other.fileBytes)) return false
            if (filename != other.filename) return false

            return true
        }

        override fun hashCode(): Int {
            var result = fileBytes.contentHashCode()
            result = 31 * result + filename.hashCode()
            return result
        }
    }
}
