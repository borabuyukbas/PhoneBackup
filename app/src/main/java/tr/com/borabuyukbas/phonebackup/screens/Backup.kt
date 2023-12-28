package tr.com.borabuyukbas.phonebackup.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tr.com.borabuyukbas.phonebackup.components.PermissionCheckbox
import tr.com.borabuyukbas.phonebackup.utils.AllUtils
import tr.com.borabuyukbas.phonebackup.utils.Calendar
import tr.com.borabuyukbas.phonebackup.utils.Call
import tr.com.borabuyukbas.phonebackup.utils.Contact
import tr.com.borabuyukbas.phonebackup.utils.SMS
import tr.com.borabuyukbas.phonebackup.utils.createLogFunction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
    val returnScroll = rememberScrollState()
    val log = createLogFunction(returnStr, returnScroll)

    val saveContent = remember { mutableStateOf("") }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
        onResult = {
            if (it != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.IO) {
                        val stream = context.contentResolver.openOutputStream(it)
                        if (stream != null) {
                            stream.write(saveContent.value.toByteArray())
                            stream.close()
                        }
                        log("Backup successfully completed.")
                    }
                }
            }
        }
    )


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
                    returnStr.value = ""

                    var sms: List<SMS> = listOf()
                    var contacts: List<Contact> = listOf()
                    var calls: List<Call> = listOf()
                    var calendar: List<Calendar> = listOf()

                    if (smsChecked.value) {
                        smsLoading.value = true
                        withContext(Dispatchers.IO) {
                            sms = SMS.getAll(context, smsProgress)
                            log("Found ${sms.size} SMS")
                        }
                    }

                    if (contactsChecked.value) {
                        contactsLoading.value = true
                        withContext(Dispatchers.IO) {
                            contacts = Contact.getAll(context, contactsProgress)
                            log("Found ${contacts.size} Contacts")
                        }
                    }

                    if (callLogsChecked.value) {
                        callLogsLoading.value = true
                        withContext(Dispatchers.IO) {
                            calls = Call.getAll(context, callLogsProgress)
                            log("Found ${calls.size} Calls")
                        }
                    }

                    if (calendarChecked.value) {
                        calendarLoading.value = true
                        withContext(Dispatchers.IO) {
                            calendar = Calendar.getAll(context, calendarProgress)
                            log("Found ${calendar.size} Calendar Events")
                        }
                    }

                    withContext(Dispatchers.IO) {
                        log("Converting data to JSON object.")
                        saveContent.value = Gson().toJson(AllUtils(
                            sms,
                            contacts,
                            calls,
                            calendar
                        ))

                        launcher.launch("backup_${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"))}")
                    }

                    smsLoading.value = false
                    contactsLoading.value = false
                    callLogsLoading.value = false
                    calendarLoading.value = false
                }
            }
        ) {
            Text(text = "Backup")
        }
        Text(
            text = returnStr.value,
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(returnScroll)
        )
    }
}