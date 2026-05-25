package com.zootecnia.goodfood.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zootecnia.goodfood.food.controllers.RecetaController
import com.zootecnia.goodfood.food.dto.RecetaDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FavoritosUiState {
    data object Loading : FavoritosUiState
    data class Content(
        val recetas: List<RecetaDto>,
        val categories: List<String>,
        val selectedCategory: String?,
        val favoriteIds: Set<Long>
    ) : FavoritosUiState
}

@HiltViewModel
class FavoritosViewModel @Inject constructor(
    private val recetaController: RecetaController
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<String?>(null)

    val uiState: StateFlow<FavoritosUiState> = combine(
        recetaController.observeRecetas(),
        recetaController.observeFavoriteIds(),
        recetaController.observeCategories(),
        _selectedCategory
    ) { allRecetas, favoriteIds, categories, selectedCategory ->
        val favorites = allRecetas.filter { it.id in favoriteIds }
        val filtered = if (selectedCategory == null) {
            favorites
        } else {
            favorites.filter { it.categories.contains(selectedCategory) }
        }
        FavoritosUiState.Content(
            recetas = filtered,
            categories = categories,
            selectedCategory = selectedCategory,
            favoriteIds = favoriteIds
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FavoritosUiState.Loading
    )

    fun onCategorySelected(category: String?) {
        _selectedCategory.value = category
    }

    fun onToggleFavorite(recetaId: Long) {
        viewModelScope.launch {
            recetaController.toggleFavorite(recetaId)
        }
    }
}
