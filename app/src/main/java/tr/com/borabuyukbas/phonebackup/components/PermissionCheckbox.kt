package tr.com.borabuyukbas.phonebackup.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionCheckbox(text: String, icon: ImageVector, requiredPermission: String) {
    val (checkedState, onStateChange) = remember { mutableStateOf(false) }

    val permissionState = rememberPermissionState(requiredPermission)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .toggleable(
                value = checkedState,
                onValueChange = { onStateChange(!checkedState) },
                role = Role.Checkbox,
                enabled = permissionState.status == PermissionStatus.Granted
            )
            .background(if(permissionState.status != PermissionStatus.Granted) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.inversePrimary)
            .padding(18.dp)
        ,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row {
            Checkbox (
                checked = checkedState,
                onCheckedChange = null
            )
            Icon(
                icon,
                contentDescription = text,
                modifier = Modifier.padding(start = 8.dp)
            )
            Text(
                text = text,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        if (permissionState.status != PermissionStatus.Granted) {
            Button(
                onClick = { permissionState.launchPermissionRequest() },
            ) {
                Text("Allow")
            }
        }
    }
}