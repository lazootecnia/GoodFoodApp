package com.zootecnia.goodfood.food.repositories.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zootecnia.goodfood.food.entities.Measure

@Dao
interface MeasureDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnore(measure: Measure): Long

    @Query("SELECT * FROM measure WHERE text = :text LIMIT 1")
    suspend fun getByText(text: String): Measure?

    @Query("SELECT * FROM measure")
    suspend fun getAll(): List<Measure>
}
