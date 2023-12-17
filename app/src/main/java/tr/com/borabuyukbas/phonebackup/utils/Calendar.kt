package tr.com.borabuyukbas.phonebackup.utils

import android.content.Context
import android.provider.CalendarContract
import androidx.compose.runtime.MutableState

class Calendar(
    dtStart: Int,
    dtEnd: Int,
    duration: String,
    description: String,
    eventLocation: String,
    eventTimeZone: String,
    allDay: Boolean,
    exDate: String,
    exRule: String,
    rDate: String,
    rRule: String,
    hasAlarm: Boolean,
    status: Int,
    selfAttendeeStatus: Int,
    organizer: String,
    hasAttendeeData: Boolean,
    accessLevel: Int,
    availability: Int
) : BaseUtil {
    override fun importToDevice(context: Context) {
        TODO("Not yet implemented")
    }

    override fun isExist(context: Context): Boolean {
        TODO("Not yet implemented")
    }

    companion object : BaseUtilHelper<Calendar> {
        override suspend fun getAll(
            context: Context,
            progress: MutableState<Float>?
        ): List<Calendar> {
            val returnList = mutableListOf<Calendar>()

            val cursor = context.contentResolver.query(
                CalendarContract.Events.CONTENT_URI,
                arrayOf(
                    CalendarContract.Events.DTSTART,
                    CalendarContract.Events.DTEND,
                    CalendarContract.Events.DURATION,
                    CalendarContract.Events.DESCRIPTION,
                    CalendarContract.Events.EVENT_LOCATION,
                    CalendarContract.Events.EVENT_TIMEZONE,
                    CalendarContract.Events.ALL_DAY,
                    CalendarContract.Events.EXDATE,
                    CalendarContract.Events.EXRULE,
                    CalendarContract.Events.RDATE,
                    CalendarContract.Events.RRULE,
                    CalendarContract.Events.HAS_ALARM,
                    CalendarContract.Events.STATUS,
                    CalendarContract.Events.SELF_ATTENDEE_STATUS,
                    CalendarContract.Events.ORGANIZER,
                    CalendarContract.Events.HAS_ATTENDEE_DATA,
                    CalendarContract.Events.ACCESS_LEVEL,
                    CalendarContract.Events.AVAILABILITY,
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
                    Calendar(
                        getValue(cursor, 0),
                        getValue(cursor, 1),
                        getValue(cursor, 2),
                        getValue(cursor, 3),
                        getValue(cursor, 4),
                        getValue(cursor, 5),
                        getValue(cursor, 6),
                        getValue(cursor, 7),
                        getValue(cursor, 8),
                        getValue(cursor, 9),
                        getValue(cursor, 10),
                        getValue(cursor, 11),
                        getValue(cursor, 12),
                        getValue(cursor, 13),
                        getValue(cursor, 14),
                        getValue(cursor, 15),
                        getValue(cursor, 16),
                        getValue(cursor, 17),
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