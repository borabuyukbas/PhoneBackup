package tr.com.borabuyukbas.phonebackup.utils

import android.content.Context
import android.os.Bundle
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
                    val id = getValue<String>(cursor, 0)
                    val name = getValue<String>(cursor, 1)
                    val phoneNumbers = mutableListOf<String>()

                    val queryArgs = Bundle()
                    queryArgs.putString(ContactsContract.CommonDataKinds.Phone.CONTACT_ID, id)

                    val phoneNumberCursor = context.contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        arrayOf(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                        ),
                        queryArgs,
                        null
                    )
                    if (phoneNumberCursor != null) {
                        while (phoneNumberCursor.moveToNext()) {
                            val phoneNo = getValue<String>(phoneNumberCursor, 0)
                            if (phoneNo != null) phoneNumbers.add(phoneNo)
                        }
                        phoneNumberCursor.close()
                    }


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