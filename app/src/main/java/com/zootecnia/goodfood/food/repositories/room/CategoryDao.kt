package com.zootecnia.goodfood.food.repositories.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zootecnia.goodfood.food.entities.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnore(category: Category): Long

    @Query("SELECT * FROM category WHERE text = :text LIMIT 1")
    suspend fun getByText(text: String): Category?

    @Query("SELECT * FROM category ORDER BY text ASC")
    fun observeAll(): Flow<List<Category>>
}
