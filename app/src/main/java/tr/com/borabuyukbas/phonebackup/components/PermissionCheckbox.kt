package tr.com.borabuyukbas.phonebackup.components

import android.app.Activity
import android.app.role.RoleManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionCheckbox(
    text: String,
    icon: ImageVector,
    requiredPermission: String,
    checked: MutableState<Boolean>,
    progress: MutableFloatState,
    loading: MutableState<Boolean>,
    forceDisable: Boolean = false,
    checkDefaultSMS: Boolean = false,
) {
    val (checkedState, onStateChange) = checked

    val permissionState = rememberPermissionState(requiredPermission)

    val isDefaultSMSApp = remember { mutableStateOf(false) }

    val context = LocalContext.current
    val roleManager = context.getSystemService(RoleManager::class.java)
    val roleRequestIntent = roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS)

    if (roleManager.isRoleAvailable(RoleManager.ROLE_SMS)) {
        isDefaultSMSApp.value = roleManager.isRoleHeld(RoleManager.ROLE_SMS)
    }

    val defaultSMSAppRequest = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            isDefaultSMSApp.value = true
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .toggleable(
                value = checkedState,
                onValueChange = { onStateChange(!checkedState) },
                role = Role.Checkbox,
                enabled = permissionState.status == PermissionStatus.Granted && (!checkDefaultSMS || isDefaultSMSApp.value) && !forceDisable
            )
            .background(if (permissionState.status != PermissionStatus.Granted || (checkDefaultSMS && !isDefaultSMSApp.value) || forceDisable) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.inversePrimary)
            .padding(18.dp),
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
        if ((permissionState.status != PermissionStatus.Granted) || (checkDefaultSMS && !isDefaultSMSApp.value)) {
            Button(
                onClick = {
                    if (checkDefaultSMS) defaultSMSAppRequest.launch(roleRequestIntent)
                    else permissionState.launchPermissionRequest()
                },
                enabled = !forceDisable
            ) {
                Text("Allow")
            }
        }
        if (loading.value) {
            CircularProgressIndicator(
                progress = progress.floatValue
            )
        }
    }
}