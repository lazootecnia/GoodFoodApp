package com.zootecnia.goodfood.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.zootecnia.goodfood.ui.screens.BootstrapScreen
import com.zootecnia.goodfood.ui.screens.RecetasScreen
import com.zootecnia.goodfood.ui.screens.SettingsScreen
import com.zootecnia.goodfood.ui.viewmodels.BootstrapViewModel

@Composable
fun GoodFoodApp() {
    val bootstrapViewModel: BootstrapViewModel = hiltViewModel()
    val isReady by bootstrapViewModel.isReady.collectAsState()

    if (!isReady) {
        BootstrapScreen(viewModel = bootstrapViewModel)
        return
    }

    var selectedDestination by rememberSaveable { mutableStateOf(Destination.Recetas) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            Destination.entries.forEach { destination ->
                item(
                    icon = { Icon(destination.icon, contentDescription = null) },
                    label = { Text(stringResource(destination.labelRes)) },
                    selected = selectedDestination == destination,
                    onClick = { selectedDestination = destination }
                )
            }
        }
    ) {
        when (selectedDestination) {
            Destination.Recetas -> RecetasScreen()
            Destination.Settings -> SettingsScreen()
        }
    }
}
