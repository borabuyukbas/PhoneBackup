package tr.com.borabuyukbas.phonebackup.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tr.com.borabuyukbas.phonebackup.components.PermissionCheckbox
import tr.com.borabuyukbas.phonebackup.utils.Calendar
import tr.com.borabuyukbas.phonebackup.utils.Call
import tr.com.borabuyukbas.phonebackup.utils.Contact
import tr.com.borabuyukbas.phonebackup.utils.SMS

@Composable
fun Backup() {
    val smsChecked = remember { mutableStateOf(false) }
    val contactsChecked = remember { mutableStateOf(false) }
    val callLogsChecked = remember { mutableStateOf(false) }
    val calendarChecked = remember { mutableStateOf(false) }

    val smsLoading = remember { mutableStateOf(false) }
    val contactsLoading = remember { mutableStateOf(false) }
    val callLogsLoading = remember { mutableStateOf(false) }
    val calendarLoading = remember { mutableStateOf(false) }

    val smsProgress = remember { mutableFloatStateOf(0.0f) }
    val contactsProgress = remember { mutableFloatStateOf(0.0f) }
    val callLogsProgress = remember { mutableFloatStateOf(0.0f) }
    val calendarProgress = remember { mutableFloatStateOf(0.0f) }

    val context = LocalContext.current
    val returnStr = remember { mutableStateOf("") }

    Column (
        modifier = Modifier.padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column (
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PermissionCheckbox("SMS", Icons.Filled.Email, android.Manifest.permission.READ_SMS, smsChecked, smsProgress, smsLoading)
            PermissionCheckbox("Contacts", Icons.Filled.Person, android.Manifest.permission.READ_CONTACTS, contactsChecked, contactsProgress, contactsLoading)
            PermissionCheckbox("Call Logs", Icons.Filled.Phone, android.Manifest.permission.READ_CALL_LOG, callLogsChecked, callLogsProgress, callLogsLoading)
            PermissionCheckbox("Calendar", Icons.Filled.DateRange, android.Manifest.permission.READ_CALENDAR, calendarChecked, calendarProgress, calendarLoading)
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = (smsChecked.value || contactsChecked.value || callLogsChecked.value || calendarChecked.value) &&
                    (!smsLoading.value && !contactsLoading.value && !callLogsLoading.value && !calendarLoading.value),
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    smsLoading.value = false
                    contactsLoading.value = false
                    callLogsLoading.value = false
                    calendarLoading.value = false

                    if (smsChecked.value) {
                        smsLoading.value = true
                        withContext(Dispatchers.IO) {
                            val sms = SMS.getAll(context, smsProgress)
                            returnStr.value += "Found ${sms.size} SMS\n"
                        }
                        smsLoading.value = false
                    }

                    if (contactsChecked.value) {
                        contactsLoading.value = true
                        withContext(Dispatchers.IO) {
                            val contacts = Contact.getAll(context, contactsProgress)
                            returnStr.value += "Found ${contacts.size} Contacts\n"
                        }
                        contactsLoading.value = false
                    }

                    if (callLogsChecked.value) {
                        callLogsLoading.value = true
                        withContext(Dispatchers.IO) {
                            val calls = Call.getAll(context, callLogsProgress)
                            returnStr.value += "Found ${calls.size} Calls\n"
                        }
                        callLogsLoading.value = false
                    }

                    if (calendarChecked.value) {
                        calendarLoading.value = true
                        withContext(Dispatchers.IO) {
                            val calendar = Calendar.getAll(context, calendarProgress)
                            returnStr.value += "Found ${calendar.size} Calendar Events\n"
                        }
                        calendarLoading.value = false
                    }
                }
            }
        ) {
            Text(text = "Backup")
        }
        Text(text = returnStr.value)
    }
}