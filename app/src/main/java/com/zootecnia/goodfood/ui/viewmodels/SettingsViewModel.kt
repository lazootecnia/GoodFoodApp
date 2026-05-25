package com.zootecnia.goodfood.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zootecnia.goodfood.food.controllers.RecetaController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class SettingsStats(
    val totalRecetas: Int = 0,
    val totalFavoritos: Int = 0,
    val totalCategorias: Int = 0
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    recetaController: RecetaController
) : ViewModel() {

    val stats: StateFlow<SettingsStats> = combine(
        recetaController.observeRecetaCount(),
        recetaController.observeFavoriteCount(),
        recetaController.observeCategoryCount()
    ) { recetas, favoritos, categorias ->
        SettingsStats(
            totalRecetas = recetas,
            totalFavoritos = favoritos,
            totalCategorias = categorias
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsStats()
    )
}
