package tr.com.borabuyukbas.phonebackup

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Refresh
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
import tr.com.borabuyukbas.phonebackup.screens.Backup
import tr.com.borabuyukbas.phonebackup.screens.Restore
import tr.com.borabuyukbas.phonebackup.ui.theme.PhoneBackupTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Application() {
    val navController = rememberNavController()

    val navigationList = listOf(
        NavbarItem("backup", "Backup", Icons.Filled.Build),
        NavbarItem("restore", "Restore", Icons.Filled.Refresh)
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
                NavHost(navController = navController, startDestination = "backup", modifier = Modifier.padding(paddingValues)) {
                    composable("backup") { Backup() }
                    composable("restore") { Restore() }
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