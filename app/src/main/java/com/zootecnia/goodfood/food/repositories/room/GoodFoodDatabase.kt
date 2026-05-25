package com.zootecnia.goodfood.food.repositories.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zootecnia.goodfood.food.entities.Category
import com.zootecnia.goodfood.food.entities.Favorite
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
        RecetaCategory::class,
        Favorite::class
    ],
    version = 2
)
abstract class GoodFoodDatabase : RoomDatabase() {
    abstract fun recetaDao(): RecetaDao
    abstract fun ingredientDao(): IngredientDao
    abstract fun stepDao(): StepDao
    abstract fun measureDao(): MeasureDao
    abstract fun categoryDao(): CategoryDao
    abstract fun recetaCategoryDao(): RecetaCategoryDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `favorite` (
                        `receta_id` INTEGER NOT NULL,
                        PRIMARY KEY(`receta_id`),
                        FOREIGN KEY(`receta_id`) REFERENCES `receta`(`id`) ON DELETE CASCADE
                    )
                """.trimIndent())
            }
        }
    }
}
