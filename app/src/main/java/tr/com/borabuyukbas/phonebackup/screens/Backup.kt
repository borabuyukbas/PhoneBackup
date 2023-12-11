package tr.com.borabuyukbas.phonebackup.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tr.com.borabuyukbas.phonebackup.components.PermissionCheckbox

@Composable
fun Backup() {
    Column (
        modifier = Modifier.padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PermissionCheckbox("SMS", Icons.Filled.Email, android.Manifest.permission.READ_SMS)
        PermissionCheckbox("Contacts", Icons.Filled.Person, android.Manifest.permission.READ_CONTACTS)
        PermissionCheckbox("Call Logs", Icons.Filled.Phone, android.Manifest.permission.READ_CALL_LOG)
        PermissionCheckbox("Calendar", Icons.Filled.DateRange, android.Manifest.permission.READ_CALENDAR)
    }
}