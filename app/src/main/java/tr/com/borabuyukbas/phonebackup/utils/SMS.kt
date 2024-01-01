package tr.com.borabuyukbas.phonebackup.utils

import android.content.Context
import android.provider.Telephony
import androidx.compose.runtime.MutableState

data class SMS(
    val address: String?,
    val body: String?,
    val type: Int?,
    val date: Long?,
    val read: Boolean?
) : BaseUtil {
    override fun importToDevice(context: Context) {
        TODO("Not yet implemented")
    }

    companion object : BaseUtilHelper<SMS> {
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