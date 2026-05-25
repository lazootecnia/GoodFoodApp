package com.zootecnia.goodfood.food.repositories.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.zootecnia.goodfood.food.entities.RecetaCategory

@Dao
interface RecetaCategoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(relations: List<RecetaCategory>)
}
