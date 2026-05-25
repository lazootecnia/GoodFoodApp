package com.zootecnia.goodfood.food.repositories.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zootecnia.goodfood.food.entities.Favorite
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favorite: Favorite)

    @Query("DELETE FROM favorite WHERE receta_id = :recetaId")
    suspend fun delete(recetaId: Long)

    @Query("SELECT receta_id FROM favorite")
    fun observeAllIds(): Flow<List<Long>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite WHERE receta_id = :recetaId)")
    suspend fun isFavorite(recetaId: Long): Boolean
}
