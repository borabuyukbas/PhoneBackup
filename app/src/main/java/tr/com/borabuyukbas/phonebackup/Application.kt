package tr.com.borabuyukbas.phonebackup

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import tr.com.borabuyukbas.phonebackup.components.Header
import tr.com.borabuyukbas.phonebackup.components.Navbar
import tr.com.borabuyukbas.phonebackup.components.NavbarItem
import tr.com.borabuyukbas.phonebackup.screens.Calendar
import tr.com.borabuyukbas.phonebackup.screens.CallLog
import tr.com.borabuyukbas.phonebackup.screens.Contact
import tr.com.borabuyukbas.phonebackup.screens.SMS
import tr.com.borabuyukbas.phonebackup.ui.theme.PhoneBackupTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Application() {
    val navController = rememberNavController()

    val navigationList = listOf(
        NavbarItem("sms", "SMS", Icons.Filled.Email),
        NavbarItem("contact", "Contacts", Icons.Filled.Person),
        NavbarItem("call", "Call Logs", Icons.Filled.Call),
        NavbarItem("calendar", "Calendar", Icons.Filled.DateRange),
    )

    PhoneBackupTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = {
                    Header()
                },
                bottomBar = {
                    Navbar(navController, navigationList)
                },
            ) { paddingValues ->
                NavHost(navController = navController, startDestination = "sms", modifier = Modifier.padding(paddingValues)) {
                    composable("sms") { SMS() }
                    composable("contact") { Contact() }
                    composable("call") { CallLog() }
                    composable("calendar") { Calendar() }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PhoneBackupTheme {
        Greeting("Android")
    }
}