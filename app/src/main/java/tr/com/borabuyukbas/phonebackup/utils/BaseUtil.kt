package tr.com.borabuyukbas.phonebackup.utils

import android.content.Context
import android.database.Cursor
import androidx.compose.runtime.MutableState

inline fun <reified T> getValue(cursor: Cursor, index: Int): T {
    val isNull = cursor.isNull(index)

    return when (T::class) {
        String::class -> (if (isNull) "" else cursor.getString(index)) as T
        Boolean::class -> (if (isNull) false else cursor.getInt(index) == 1) as T
        Int::class -> (if (isNull) 0 else cursor.getInt(index)) as T
        else -> throw Exception("Unhandled return type")
    }
}

interface BaseUtil {
    fun importToDevice(context: Context)
    fun isExist(context: Context): Boolean
}

interface BaseUtilHelper<T> {
    suspend fun getAll(context: Context, progress: MutableState<Float>? = null): List<T>
}