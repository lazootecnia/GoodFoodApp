package com.zootecnia.goodfood.food.repositories.room

import androidx.room.Dao
import androidx.room.Insert
import com.zootecnia.goodfood.food.entities.Step

@Dao
interface StepDao {

    @Insert
    suspend fun insertAll(steps: List<Step>)
}
