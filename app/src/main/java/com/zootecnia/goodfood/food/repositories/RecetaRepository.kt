package com.zootecnia.goodfood.food.repositories

import com.zootecnia.goodfood.food.entities.Category
import com.zootecnia.goodfood.food.entities.RecetaWithDetails
import com.zootecnia.goodfood.food.repositories.room.CategoryDao
import com.zootecnia.goodfood.food.repositories.room.RecetaDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecetaRepository @Inject constructor(
    private val recetaDao: RecetaDao,
    private val categoryDao: CategoryDao
) {

    fun observeAll(): Flow<List<RecetaWithDetails>> = recetaDao.observeAll()

    suspend fun getById(id: Long): RecetaWithDetails? = recetaDao.getById(id)

    fun observeByCategory(categoryId: Long): Flow<List<RecetaWithDetails>> =
        recetaDao.observeByCategory(categoryId)

    fun searchByTitle(query: String): Flow<List<RecetaWithDetails>> =
        recetaDao.searchByTitle(query)

    fun observeCategories(): Flow<List<Category>> = categoryDao.observeAll()
}
