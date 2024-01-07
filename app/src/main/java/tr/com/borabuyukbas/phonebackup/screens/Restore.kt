package tr.com.borabuyukbas.phonebackup.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Alignment
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

@Composable
fun Restore() {
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

    val parsedObject = remember { mutableStateOf<AllUtils?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = {
            if (it != null) {
                returnStr.value = ""
                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.IO) {
                        val stream = context.contentResolver.openInputStream(it)
                        if (stream != null) {
                            val fileContent = stream.readBytes().decodeToString()
                            try {
                                parsedObject.value = Gson().fromJson(fileContent, AllUtils::class.java)
                                log("Backup successfully loaded.")
                            } catch (e: Exception) {
                                parsedObject.value = null
                                log("Cannot load backup file (parsing error).")
                            } finally {
                                stream.close()
                            }
                        }
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
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column (
                    modifier = Modifier.padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "${if(parsedObject.value == null) 0 else parsedObject.value!!.sms.size} SMS")
                    Text(text = "${if(parsedObject.value == null) 0 else parsedObject.value!!.contacts.size} Contacts")
                }
                Column (
                    modifier = Modifier.padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "${if(parsedObject.value == null) 0 else parsedObject.value!!.calls.size} Calls")
                    Text(text = "${if(parsedObject.value == null) 0 else parsedObject.value!!.calendar.size} Events")
                }
                Button(
                    onClick = {
                        launcher.launch(arrayOf("application/json"))
                    }
                ) {
                    Text(text = "Choose")
                }
            }
            PermissionCheckbox("SMS", Icons.Filled.Email, Manifest.permission.READ_SMS, smsChecked, smsProgress, smsLoading, parsedObject.value == null || parsedObject.value!!.sms.isEmpty(), checkDefaultSMS = true)
            PermissionCheckbox("Contacts", Icons.Filled.Person, Manifest.permission.WRITE_CONTACTS, contactsChecked, contactsProgress, contactsLoading, parsedObject.value == null || parsedObject.value!!.contacts.isEmpty())
            PermissionCheckbox("Call Logs", Icons.Filled.Phone, Manifest.permission.WRITE_CALL_LOG, callLogsChecked, callLogsProgress, callLogsLoading, parsedObject.value == null || parsedObject.value!!.calls.isEmpty())
            PermissionCheckbox("Calendar", Icons.Filled.DateRange, Manifest.permission.WRITE_CALENDAR, calendarChecked, calendarProgress, calendarLoading, parsedObject.value == null || parsedObject.value!!.calendar.isEmpty())
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = smsChecked.value || contactsChecked.value || callLogsChecked.value || calendarChecked.value,
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    smsLoading.value = false
                    contactsLoading.value = false
                    callLogsLoading.value = false
                    calendarLoading.value = false
                    returnStr.value = ""

                    if (parsedObject.value != null) {
                        if (smsChecked.value) {
                            smsLoading.value = true
                            withContext(Dispatchers.IO) {
                                val sms = SMS.getAll(context, smsProgress)
                                log("Found ${sms.size} SMS")

                                val filteredSMS = parsedObject.value!!.sms.filter { it !in sms }
                                log("Importing ${filteredSMS.size} different SMS.")
                                filteredSMS.forEach { it.importToDevice(context) }
                            }
                        }

                        if (contactsChecked.value) {
                            contactsLoading.value = true
                            withContext(Dispatchers.IO) {
                                val contacts = Contact.getAll(context, contactsProgress)
                                log("Found ${contacts.size} Contacts")

                                val filteredContacts = parsedObject.value!!.contacts.filter { it !in contacts }
                                log("Importing ${filteredContacts.size} different Contacts.")
                                filteredContacts.forEach { it.importToDevice(context) }
                            }
                        }

                        if (callLogsChecked.value) {
                            callLogsLoading.value = true
                            withContext(Dispatchers.IO) {
                                val calls = Call.getAll(context, callLogsProgress)
                                log("Found ${calls.size} Calls")

                                val filteredCalls = parsedObject.value!!.calls.filter { it !in calls }
                                log("Importing ${filteredCalls.size} different Calls.")
                                filteredCalls.forEach { it.importToDevice(context) }
                            }
                        }

                        if (calendarChecked.value) {
                            calendarLoading.value = true
                            withContext(Dispatchers.IO) {
                                val calendar = Calendar.getAll(context, calendarProgress)
                                log("Found ${calendar.size} Calendar Events")

                                val filteredCalendar = parsedObject.value!!.calendar.filter { it !in calendar }
                                log("Importing ${filteredCalendar.size} different Events.")
                                filteredCalendar.forEach { it.importToDevice(context) }
                            }
                        }
                    }

                    log("Restoring successfully completed.")
                    smsLoading.value = false
                    contactsLoading.value = false
                    callLogsLoading.value = false
                    calendarLoading.value = false
                }
            }
        ) {
            Text(text = "Restore")
        }
        Text(
            text = returnStr.value,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(returnScroll)
        )
    }
}