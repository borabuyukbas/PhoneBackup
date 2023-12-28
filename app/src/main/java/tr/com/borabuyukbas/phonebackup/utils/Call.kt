package tr.com.borabuyukbas.phonebackup.utils

import android.content.Context
import android.provider.CallLog.Calls
import androidx.compose.runtime.MutableState

data class Call (
    val number: String?,
    val date: Int?,
    val type: Int?,
    val new: Boolean?,
    val duration: Int?
) : BaseUtil {
    override fun importToDevice(context: Context) {
        TODO("Not yet implemented")
    }

    companion object : BaseUtilHelper<Call> {
        override suspend fun getAll(context: Context, progress: MutableState<Float>?): List<Call> {
            val returnList = mutableListOf<Call>()

            val cursor = context.contentResolver.query(
                Calls.CONTENT_URI,
                arrayOf(
                    Calls.NUMBER,
                    Calls.DATE,
                    Calls.TYPE,
                    Calls.NEW,
                    Calls.DURATION
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
                    Call(
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