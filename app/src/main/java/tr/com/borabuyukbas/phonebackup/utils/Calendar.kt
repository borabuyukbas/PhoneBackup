package tr.com.borabuyukbas.phonebackup.utils

import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import androidx.compose.runtime.MutableState

data class Calendar(
    val dtStart: Long?,
    val dtEnd: Long?,
    val duration: String?,
    val title: String?,
    val description: String?,
    val eventLocation: String?,
    val eventTimeZone: String?,
    val allDay: Boolean?,
    val exDate: String?,
    val exRule: String?,
    val rDate: String?,
    val rRule: String?,
    val hasAlarm: Boolean?,
    val status: Int?,
    val selfAttendeeStatus: Int?,
    val organizer: String?,
    val hasAttendeeData: Boolean?,
    val accessLevel: Int?,
    val availability: Int?
) : BaseUtil {
    override fun importToDevice(context: Context) {
        val values = ContentValues().apply {
            put(CalendarContract.Events.CALENDAR_ID, defaultCalendarId.value(context))
            put(CalendarContract.Events.DTSTART, dtStart)
            put(CalendarContract.Events.DTEND, dtEnd)
            put(CalendarContract.Events.DURATION, duration)
            put(CalendarContract.Events.TITLE, title)
            put(CalendarContract.Events.DESCRIPTION, description)
            put(CalendarContract.Events.EVENT_LOCATION, eventLocation)
            put(CalendarContract.Events.EVENT_TIMEZONE, eventTimeZone)
            put(CalendarContract.Events.ALL_DAY, allDay)
            put(CalendarContract.Events.EXDATE, exDate)
            put(CalendarContract.Events.EXRULE, exRule)
            put(CalendarContract.Events.RDATE, rDate)
            put(CalendarContract.Events.RRULE, rRule)
            put(CalendarContract.Events.HAS_ALARM, hasAlarm)
            put(CalendarContract.Events.STATUS, status)
            put(CalendarContract.Events.SELF_ATTENDEE_STATUS, selfAttendeeStatus)
            put(CalendarContract.Events.ORGANIZER, organizer)
            put(CalendarContract.Events.HAS_ATTENDEE_DATA, hasAttendeeData)
            put(CalendarContract.Events.ACCESS_LEVEL, accessLevel)
            put(CalendarContract.Events.AVAILABILITY, availability)
        }

        context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
    }

    companion object : BaseUtilHelper<Calendar> {
        private val defaultCalendarId: Lazy<(Context) -> Long?> by lazy {
            lazy {
                { context: Context ->
                    getDefaultCalendarId(context)
                        ?: throw IllegalStateException("Default calendar ID not found.")
                }
            }
        }

        private fun getDefaultCalendarId(context: Context): Long? {
            val projection = arrayOf(CalendarContract.Calendars._ID)
            val selection = "${CalendarContract.Calendars.IS_PRIMARY} = 1"

            val cursor = context.contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                projection,
                selection,
                null,
                null
            )

            cursor?.use {
                if (cursor.moveToFirst()) {
                    return getValue<Long>(cursor, 0)
                }
            }

            return null
        }

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
                    CalendarContract.Events.TITLE,
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
                        getValue(cursor, 18),
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