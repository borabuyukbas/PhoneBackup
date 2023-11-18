package tr.com.borabuyukbas.phonebackup.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

class NavbarItem(val name: String,  val icon: ImageVector)

@Preview(showBackground = true)
@Composable
fun Navbar() {
    var selectedItem by remember { mutableStateOf(0) }

    val items = listOf(
        NavbarItem("SMS", Icons.Filled.Email),
        NavbarItem("Contacts", Icons.Filled.Person),
        NavbarItem("Call Logs", Icons.Filled.Call),
        NavbarItem("Calendar", Icons.Filled.DateRange),
    )

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.name) },
                label = { Text(item.name) },
                selected = selectedItem == index,
                onClick = { selectedItem = index }
            )
        }
    }
}