//package com.example.pitabmdmstudent.extension
//
//import android.content.Context
//import android.util.Log
//import android.util.Patterns
//import com.example.pitabmdmstudent.utils.NetworkUtils
//import java.io.BufferedInputStream
//import java.io.ByteArrayOutputStream
//import java.net.HttpURLConnection
//import java.net.URL
//import java.nio.ByteBuffer
//
//val String.Companion.EMPTY: String by lazy { "" }
//val String.Companion.COMMA: String by lazy { "," }
//
///** This Extension field of type string returns the value of string or empty */
//val String?.value: String get() = this ?: String.EMPTY
//
//
///** This Extension field returns emoji from Unicode {U+F1600} format. Else [String.EMPTY] is returned */
//val String?.emoji: String
//    get() {
//        return if (this == null) value
//        else if (this.isEmpty()) value
//        else if ((this[0] == 'U' || this[0] == 'u') && this[1] == '+' && this.length > 2) {
//            try {
//                val emoji = this.substring(2)
//                String(Character.toChars(Integer.parseInt(emoji, 16)))
//            } catch (e: Exception) {
//                Log.e("StringExtension", "Failed to Parse emoji : $this - $e")
//                value
//            }
//        } else value
//
//    }
//
//// This method is added for comparing contents of strings with ignoring case
//fun String?.containsIgnoreCase(other: String?): Boolean {
//    if (this == null || other == null) return false
//    return this.contains(other, ignoreCase = true)
//}
//
//inline fun <R : CharSequence> R?.ifNullOrEmpty(defaultValue: () -> R): R =
//    if (isNullOrEmpty()) defaultValue() else this
//
//fun String?.checkIsNullOrEmpty(): Boolean {
//    return this?.isEmpty() ?: true
//}
//
//fun String?.isContainsLink(): Boolean {
//    if (this == null) return false
//    return Patterns.WEB_URL.matcher(this.lowercase()).matches();
//}
//
///** Extension for toInt from string **/
//fun String?.toInt(): Int {
//    return if (this == null) 0
//    else {
//        try {
//            Integer.parseInt(this)
//        } catch (e: Exception) {
//            Log.e("StringExtension", "StringExt: toInt() - $e")
//            0
//        }
//    }
//}
//
//fun String?.toLong(): Long {
//    return if (this == null) 0
//    else {
//        try {
//            toLongOrNull() ?: 0L
//        } catch (e: Exception) {
//            Log.e("StringExtension", "StringExt: toLong() - $e")
//            0L
//        }
//    }
//}
//
//suspend fun String.downloadAndGetFileBuffer(
//    context: Context,
//    progressListener: (suspend (downloadPercentage: Int) -> Unit)? = null
//): ByteBuffer? {
//    if (NetworkUtils.isConnectedToInternet(context).not()) return null
//    try {
//        val url = URL(this)
//        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
//        urlConnection.connect()
//        val lengthOfFile = urlConnection.contentLength
//        val inputStream = BufferedInputStream(urlConnection.inputStream)
//        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
//        val byteArrayOutputStream = ByteArrayOutputStream()
//        var bytesRead: Int
//        var totalCount = 0
//        while ((inputStream.read(buffer).also { bytesRead = it }) != -1) {
//            totalCount += bytesRead
//            progressListener?.invoke(((totalCount * 100) / lengthOfFile))
//            byteArrayOutputStream.write(buffer, 0, bytesRead)
//        }
//        val byteArr = byteArrayOutputStream.toByteArray()
//        return ByteBuffer.wrap(byteArr)
//    } catch (e: Exception) {
//        Log.e("StringExtension", "downloadAndGetFileBuffer: ${e.localizedMessage}")
//        return null
//    }
//}
//
//fun String.isIntegerValue(): Boolean {
//    return toIntOrNull() != null
//}
