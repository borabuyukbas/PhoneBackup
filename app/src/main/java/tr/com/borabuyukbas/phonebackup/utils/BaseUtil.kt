package tr.com.borabuyukbas.phonebackup.utils

import android.content.Context
import android.database.Cursor
import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.MutableState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

inline fun <reified T> getValue(cursor: Cursor, index: Int): T? {
    val isNull = cursor.isNull(index)

    if (isNull) return null

    return when (T::class) {
        String::class -> cursor.getString(index) as T
        Boolean::class -> (cursor.getInt(index) == 1) as T
        Int::class -> cursor.getInt(index) as T
        Long::class -> cursor.getLong(index) as T
        else -> throw Exception("Unhandled return type")
    }
}

fun createLogFunction (logText: MutableState<String>, scrollState: ScrollState): (String) -> Unit {
    return {
        CoroutineScope(Dispatchers.Default).launch {
            logText.value += "${it}\n"
            scrollState.scrollTo(scrollState.maxValue)
        }
    }
}

data class AllUtils(val sms: List<SMS>,
                    val contacts: List<Contact>,
                    val calls: List<Call>,
                    val calendar: List<Calendar>)

interface BaseUtil {
    fun importToDevice(context: Context)
}

interface BaseUtilHelper<T> {
    suspend fun getAll(context: Context, progress: MutableState<Float>? = null): List<T>
}