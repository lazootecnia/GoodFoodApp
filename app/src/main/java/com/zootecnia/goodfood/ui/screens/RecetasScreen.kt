package com.zootecnia.goodfood.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zootecnia.goodfood.R
import com.zootecnia.goodfood.ui.components.RecetaCard
import com.zootecnia.goodfood.ui.components.RecetaDetalleContent
import com.zootecnia.goodfood.ui.components.localizedCategoryName
import com.zootecnia.goodfood.ui.viewmodels.RecetasUiState
import com.zootecnia.goodfood.ui.viewmodels.RecetasViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecetasScreen(
    viewModel: RecetasViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is RecetasUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is RecetasUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(state.message, color = MaterialTheme.colorScheme.error)
            }
        }
        is RecetasUiState.Content -> {
            RecetasContent(
                state = state,
                onSearchQueryChanged = viewModel::onSearchQueryChanged,
                onCategorySelected = viewModel::onCategorySelected,
                onToggleFavorite = viewModel::onToggleFavorite
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun RecetasContent(
    state: RecetasUiState.Content,
    onSearchQueryChanged: (String) -> Unit,
    onCategorySelected: (String?) -> Unit,
    onToggleFavorite: (Long) -> Unit
) {
    val navigator = rememberListDetailPaneScaffoldNavigator<Long>()
    val scope = rememberCoroutineScope()

    NavigableListDetailPaneScaffold(
        navigator = navigator,
        listPane = {
            AnimatedPane {
                Column {
                    SearchBar(
                        inputField = {
                            SearchBarDefaults.InputField(
                                query = state.searchQuery,
                                onQueryChange = onSearchQueryChanged,
                                onSearch = {},
                                expanded = false,
                                onExpandedChange = {},
                                placeholder = { Text(stringResource(R.string.search_placeholder)) },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                            )
                        },
                        expanded = false,
                        onExpandedChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {}

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = state.selectedCategory == null,
                                onClick = { onCategorySelected(null) },
                                label = { Text(stringResource(R.string.filter_all)) }
                            )
                        }
                        items(state.categories) { category ->
                            FilterChip(
                                selected = state.selectedCategory == category,
                                onClick = {
                                    onCategorySelected(
                                        if (state.selectedCategory == category) null else category
                                    )
                                },
                                label = { Text(localizedCategoryName(category)) }
                            )
                        }
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = state.recetas,
                            key = { it.id }
                        ) { receta ->
                            RecetaCard(
                                receta = receta,
                                isSelected = navigator.currentDestination?.contentKey == receta.id,
                                isFavorite = receta.id in state.favoriteIds,
                                onClick = {
                                    scope.launch {
                                        navigator.navigateTo(
                                            ListDetailPaneScaffoldRole.Detail,
                                            receta.id
                                        )
                                    }
                                },
                                onToggleFavorite = { onToggleFavorite(receta.id) }
                            )
                        }
                    }
                }
            }
        },
        detailPane = {
            AnimatedPane {
                val selectedId = navigator.currentDestination?.contentKey
                val selectedReceta = state.recetas.find { it.id == selectedId }
                if (selectedReceta != null) {
                    var checkedSteps by remember(selectedReceta.id) {
                        mutableStateOf(emptySet<Int>())
                    }
                    RecetaDetalleContent(
                        receta = selectedReceta,
                        checkedSteps = checkedSteps,
                        onStepCheckedChange = { index, checked ->
                            checkedSteps = if (checked) checkedSteps + index else checkedSteps - index
                        },
                        isFavorite = selectedReceta.id in state.favoriteIds,
                        onToggleFavorite = { onToggleFavorite(selectedReceta.id) }
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.detail_placeholder),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    )
}
