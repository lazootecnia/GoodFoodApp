package com.zootecnia.goodfood.food.repositories.room

import androidx.room.Dao
import androidx.room.Insert
import com.zootecnia.goodfood.food.entities.Ingredient

@Dao
interface IngredientDao {

    @Insert
    suspend fun insertAll(ingredients: List<Ingredient>)
}
