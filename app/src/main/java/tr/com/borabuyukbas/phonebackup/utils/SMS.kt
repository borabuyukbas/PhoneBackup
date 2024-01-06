package tr.com.borabuyukbas.phonebackup.utils

import android.content.ContentValues
import android.content.Context
import android.provider.Telephony
import androidx.compose.runtime.MutableState

data class SMS(
    val address: String?,
    val body: String?,
    val type: Int?,
    val date: Long?,
) : BaseUtil {
    private var read: Boolean? = null // To exclude it from comparison/de-duplication.

    constructor(
        address: String?,
        body: String?,
        type: Int?,
        date: Long?,
        read: Boolean?
    ) : this(address, body, type, date) {
        this.read = read
    }

    override fun importToDevice(context: Context) {
        if (address == null) return

        if (!threadIdMap.containsKey(address)) {
            val threadId = Telephony.Threads.getOrCreateThreadId(
                context,
                address
            )
            threadIdMap[address] = threadId
        }

        val values = ContentValues().apply {
            put(Telephony.Sms.ADDRESS, address)
            put(Telephony.Sms.BODY, body)
            put(Telephony.Sms.TYPE, type)
            put(Telephony.Sms.DATE, date)
            put(Telephony.Sms.READ, read)

            put(Telephony.Sms.THREAD_ID, threadIdMap[address])
        }

        context.contentResolver.insert(Telephony.Sms.CONTENT_URI, values)
    }

    companion object : BaseUtilHelper<SMS> {
        private val threadIdMap: MutableMap<String, Long> = mutableMapOf()

        override suspend fun getAll(context: Context, progress: MutableState<Float>?): List<SMS> {
            val returnList = mutableListOf<SMS>()

            val cursor = context.contentResolver.query(
                Telephony.Sms.CONTENT_URI,
                arrayOf(
                    Telephony.Sms.ADDRESS,
                    Telephony.Sms.BODY,
                    Telephony.Sms.TYPE,
                    Telephony.Sms.DATE,
                    Telephony.Sms.READ,
                ),
                null,
                null,
                null
            ) ?: return returnList

            if (progress != null) {
                progress.value = 0.0f
            }

            val totalRows = cursor.count.toFloat()
            var i = 0
            while (cursor.moveToNext()) {
                returnList.add(
                    SMS(
                        getValue(cursor, 0),
                        getValue(cursor, 1),
                        getValue(cursor, 2),
                        getValue(cursor, 3),
                        getValue(cursor, 4),
                    )
                )

                i++
                if (progress != null) {
                    progress.value = i / totalRows
                }
            }

            return returnList
        }
    }
}