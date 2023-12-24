package tr.com.borabuyukbas.phonebackup.utils

import android.content.Context
import android.provider.ContactsContract
import androidx.compose.runtime.MutableState


class Contact (
    val name: String?,
    val phoneNumbers: List<String>
) : BaseUtil {

    override fun importToDevice(context: Context) {
        TODO("Not yet implemented")
    }

    override fun isExist(context: Context): Boolean {
        TODO("Not yet implemented")
    }

    companion object : BaseUtilHelper<Contact> {
        override suspend fun getAll(
            context: Context,
            progress: MutableState<Float>?
        ): List<Contact> {
            val returnList = mutableListOf<Contact>()

            val phoneNumberMap: MutableMap<Int, MutableList<String>> = mutableMapOf()

            val phoneNumberCursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                ),
                null,
                null
            )
            if (phoneNumberCursor != null) {
                while (phoneNumberCursor.moveToNext()) {
                    val phoneNo = getValue<String>(phoneNumberCursor, 0)
                    val contactId = getValue<Int>(phoneNumberCursor, 1)

                    if (phoneNo == null || contactId == null) continue

                    if (!phoneNumberMap.containsKey(contactId)) {
                        phoneNumberMap[contactId] = mutableListOf()
                    }

                    phoneNumberMap[contactId]!!.add(phoneNo)
                }
                phoneNumberCursor.close()
            }

            val cursor = context.contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                arrayOf(
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER
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

                val hasPhoneNumber = getValue<Boolean>(cursor, 2)

                if (hasPhoneNumber != null && hasPhoneNumber) {
                    val id = getValue<Int>(cursor, 0)
                    val name = getValue<String>(cursor, 1)
                    val phoneNumbers = phoneNumberMap[id] ?: listOf()

                    returnList.add(
                        Contact(
                            name,
                            phoneNumbers
                        )
                    )
                }

                i++
                if (progress != null) {
                    progress.value = i / totalRows
                }
            }

            return returnList
        }
    }
}