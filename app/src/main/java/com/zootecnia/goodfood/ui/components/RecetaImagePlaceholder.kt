package com.zootecnia.goodfood.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Blender
import androidx.compose.material.icons.outlined.BreakfastDining
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.LocalDining
import androidx.compose.material.icons.outlined.LunchDining
import androidx.compose.material.icons.outlined.NoFood
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.SoupKitchen
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun RecetaImagePlaceholder(
    category: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(Color(0xFF66BB6A))
    ) {
        Icon(
            imageVector = categoryIcon(category),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 24.dp)
                .size(72.dp),
            tint = Color.White.copy(alpha = 0.55f)
        )
    }
}

private fun categoryIcon(category: String?): ImageVector = when (category) {
    "breakfast" -> Icons.Outlined.BreakfastDining
    "lunch" -> Icons.Outlined.LunchDining
    "desserts" -> Icons.Outlined.Cake
    "salad" -> Icons.Outlined.Eco
    "dressing" -> Icons.Outlined.LocalDining
    "sauce" -> Icons.Outlined.SoupKitchen
    "jam" -> Icons.Outlined.Blender
    "glutenFree" -> Icons.Outlined.NoFood
    else -> Icons.Outlined.Restaurant
}
