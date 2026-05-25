package com.zootecnia.goodfood.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.zootecnia.goodfood.R

@Composable
fun localizedCategoryName(key: String): String = when (key) {
    "breakfast" -> stringResource(R.string.category_breakfast)
    "lunch" -> stringResource(R.string.category_lunch)
    "desserts" -> stringResource(R.string.category_desserts)
    "glutenFree" -> stringResource(R.string.category_glutenFree)
    "dressing" -> stringResource(R.string.category_dressing)
    "jam" -> stringResource(R.string.category_jam)
    "sauce" -> stringResource(R.string.category_sauce)
    else -> key
}
