package com.zootecnia.goodfood.food.repositories.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.zootecnia.goodfood.food.entities.Category
import com.zootecnia.goodfood.food.entities.Ingredient
import com.zootecnia.goodfood.food.entities.Measure
import com.zootecnia.goodfood.food.entities.Receta
import com.zootecnia.goodfood.food.entities.RecetaCategory
import com.zootecnia.goodfood.food.entities.Step

@Database(
    entities = [
        Receta::class,
        Ingredient::class,
        Step::class,
        Measure::class,
        Category::class,
        RecetaCategory::class
    ],
    version = 1
)
abstract class GoodFoodDatabase : RoomDatabase() {
    abstract fun recetaDao(): RecetaDao
    abstract fun ingredientDao(): IngredientDao
    abstract fun stepDao(): StepDao
    abstract fun measureDao(): MeasureDao
    abstract fun categoryDao(): CategoryDao
    abstract fun recetaCategoryDao(): RecetaCategoryDao
}
