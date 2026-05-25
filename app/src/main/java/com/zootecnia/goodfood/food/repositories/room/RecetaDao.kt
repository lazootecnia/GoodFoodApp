package com.zootecnia.goodfood.food.repositories.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.zootecnia.goodfood.food.entities.Receta
import com.zootecnia.goodfood.food.entities.RecetaWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface RecetaDao {

    @Transaction
    @Query("SELECT * FROM receta ORDER BY title ASC")
    fun observeAll(): Flow<List<RecetaWithDetails>>

    @Transaction
    @Query("SELECT * FROM receta WHERE id = :id")
    suspend fun getById(id: Long): RecetaWithDetails?

    @Transaction
    @Query("SELECT * FROM receta WHERE title LIKE '%' || :query || '%' ORDER BY title ASC")
    fun searchByTitle(query: String): Flow<List<RecetaWithDetails>>

    @Transaction
    @Query("""
        SELECT r.* FROM receta r
        INNER JOIN receta_category rc ON rc.receta_id = r.id
        WHERE rc.category_id = :categoryId
        ORDER BY r.title ASC
    """)
    fun observeByCategory(categoryId: Long): Flow<List<RecetaWithDetails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(receta: Receta): Long

    @Query("SELECT COUNT(*) FROM receta")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM receta")
    fun observeCount(): Flow<Int>

    @Query("DELETE FROM receta")
    suspend fun deleteAll()
}
