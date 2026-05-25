package com.zootecnia.goodfood.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.zootecnia.goodfood.R

enum class Destination(
    val icon: ImageVector,
    @StringRes val labelRes: Int
) {
    Recetas(Icons.Default.Restaurant, R.string.nav_recetas),
    Favoritos(Icons.Default.Favorite, R.string.nav_favoritos),
    Settings(Icons.Default.Settings, R.string.nav_settings)
}
