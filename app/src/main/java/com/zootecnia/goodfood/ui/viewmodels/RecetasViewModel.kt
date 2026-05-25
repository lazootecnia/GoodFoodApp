package com.zootecnia.goodfood.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zootecnia.goodfood.food.controllers.RecetaController
import com.zootecnia.goodfood.food.dto.RecetaDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface RecetasUiState {
    data object Loading : RecetasUiState
    data class Content(
        val recetas: List<RecetaDto>,
        val categories: List<String>,
        val selectedCategory: String?,
        val searchQuery: String
    ) : RecetasUiState
    data class Error(val message: String) : RecetasUiState
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class RecetasViewModel @Inject constructor(
    private val recetaController: RecetaController
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<String?>(null)

    val uiState: StateFlow<RecetasUiState> = combine(
        _searchQuery,
        _selectedCategory,
        recetaController.observeRecetas(),
        recetaController.observeCategories()
    ) { query, category, allRecetas, categories ->
        val filtered = allRecetas.filter { receta ->
            val matchesQuery = query.isBlank() ||
                receta.title.contains(query, ignoreCase = true)
            val matchesCategory = category == null ||
                receta.categories.contains(category)
            matchesQuery && matchesCategory
        }
        RecetasUiState.Content(
            recetas = filtered,
            categories = categories,
            selectedCategory = category,
            searchQuery = query
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RecetasUiState.Loading
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: String?) {
        _selectedCategory.value = category
    }
}
