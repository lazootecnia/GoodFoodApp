package com.zootecnia.goodfood.food.controllers

import android.content.Context
import com.zootecnia.goodfood.food.dto.RecetaDto
import com.zootecnia.goodfood.food.mappers.toDto
import com.zootecnia.goodfood.food.repositories.ImportRepository
import com.zootecnia.goodfood.food.repositories.RecetaRepository
import com.zootecnia.goodfood.food.repositories.room.MeasureDao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecetaController @Inject constructor(
    @ApplicationContext private val context: Context,
    private val recetaRepository: RecetaRepository,
    private val importRepository: ImportRepository,
    private val measureDao: MeasureDao
) {

    private val imagesDir: File
        get() = File(context.filesDir, "recetas/images")

    fun observeRecetas(): Flow<List<RecetaDto>> =
        recetaRepository.observeAll().map { list ->
            val measures = measureDao.getAll().associateBy { it.id }
            list.map { it.toDto(imagesDir, measures) }
        }

    suspend fun getReceta(id: Long): RecetaDto? {
        val entity = recetaRepository.getById(id) ?: return null
        val measures = measureDao.getAll().associateBy { it.id }
        return entity.toDto(imagesDir, measures)
    }

    fun observeCategories(): Flow<List<String>> =
        recetaRepository.observeCategories().map { list ->
            list.map { it.text }
        }

    fun searchRecetas(query: String): Flow<List<RecetaDto>> =
        recetaRepository.searchByTitle(query).map { list ->
            val measures = measureDao.getAll().associateBy { it.id }
            list.map { it.toDto(imagesDir, measures) }
        }

    suspend fun importSeed(): Result<Unit> = runCatching {
        importRepository.importFromAssets()
    }

    suspend fun isFirstLaunch(): Boolean = importRepository.isDbEmpty()

    fun observeFavoriteIds(): Flow<Set<Long>> = recetaRepository.observeFavoriteIds()

    suspend fun toggleFavorite(recetaId: Long) = recetaRepository.toggleFavorite(recetaId)

    fun observeRecetaCount(): Flow<Int> = recetaRepository.observeRecetaCount()

    fun observeFavoriteCount(): Flow<Int> = recetaRepository.observeFavoriteCount()

    fun observeCategoryCount(): Flow<Int> = recetaRepository.observeCategoryCount()
}
