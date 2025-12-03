import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CallLog
import android.provider.ContactsContract
import android.util.Log
import com.example.pitabmdmstudent.models.request.CallLogEntry

object ContactsUtils {

    fun getAllContacts(context: Context): List<ContactModel> {
        val contacts = mutableListOf<ContactModel>()
        val resolver = context.contentResolver

        val cursor = resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            val nameIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val name = it.getString(nameIdx)
                val number = it.getString(numberIdx)
                contacts.add(ContactModel(name, number))
            }
        }

        return contacts
    }

    fun getCallLogs(context: Context): List<CallLogEntry> {

        val list = mutableListOf<CallLogEntry>()

        val cursor = context.contentResolver.query(
            android.provider.CallLog.Calls.CONTENT_URI,
            null,
            null,
            null,
            android.provider.CallLog.Calls.DATE + " DESC"
        ) ?: return emptyList()

        cursor.use {
            val nameIdx = it.getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME)
            val numberIdx = it.getColumnIndex(android.provider.CallLog.Calls.NUMBER)
            val typeIdx = it.getColumnIndex(android.provider.CallLog.Calls.TYPE)
            val dateIdx = it.getColumnIndex(android.provider.CallLog.Calls.DATE)
            val durationIdx = it.getColumnIndex(android.provider.CallLog.Calls.DURATION)

            while (it.moveToNext()) {
                val name = it.getString(nameIdx)
                val numberRaw = it.getString(numberIdx) ?: ""
                val type = it.getInt(typeIdx)
                val dateMs = it.getLong(dateIdx)
                val duration = it.getLong(durationIdx)

                val cleanNumber = numberRaw.replace(Regex("[^0-9]"), "")
                val numLong = cleanNumber.toLongOrNull() ?: 0L

                val callType = when (type) {
                    android.provider.CallLog.Calls.INCOMING_TYPE -> "incoming_call"
                    android.provider.CallLog.Calls.OUTGOING_TYPE -> "outgoing_call"
                    android.provider.CallLog.Calls.MISSED_TYPE -> "missed_call"
                    else -> "missed_call"
                }

                val isoTime = java.text.SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss'Z'",
                    java.util.Locale.US
                ).apply {
                    timeZone = java.util.TimeZone.getTimeZone("UTC")
                }.format(java.util.Date(dateMs))

                list.add(
                    CallLogEntry(
                        name = name,
                        number = numLong,
                        callType = callType,
                        duration = duration,
                        time = isoTime
                    )
                )
            }
        }

        return list
    }
}



data class ContactModel(val name: String, val number: String)
data class CallLogModel(
    val name: String?,
    val number: String,
    val type: Int,
    val timestamp: Long,
    val duration: Long
)
