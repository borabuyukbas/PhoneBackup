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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tr.com.borabuyukbas.phonebackup.components.PermissionCheckbox
import tr.com.borabuyukbas.phonebackup.utils.Call
import tr.com.borabuyukbas.phonebackup.utils.Contact

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
            enabled = smsChecked.value || contactsChecked.value || callLogsChecked.value || calendarChecked.value,
            onClick = { /*TODO*/ }
        ) {
            Text(text = "Backup")
        }
    }
}