package com.zootecnia.goodfood.food.di

import android.content.Context
import androidx.room.Room
import com.zootecnia.goodfood.food.repositories.room.CategoryDao
import com.zootecnia.goodfood.food.repositories.room.GoodFoodDatabase
import com.zootecnia.goodfood.food.repositories.room.IngredientDao
import com.zootecnia.goodfood.food.repositories.room.MeasureDao
import com.zootecnia.goodfood.food.repositories.room.RecetaCategoryDao
import com.zootecnia.goodfood.food.repositories.room.RecetaDao
import com.zootecnia.goodfood.food.repositories.room.StepDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FoodDatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GoodFoodDatabase =
        Room.databaseBuilder(context, GoodFoodDatabase::class.java, "goodfood.db")
            .build()

    @Provides
    fun provideRecetaDao(db: GoodFoodDatabase): RecetaDao = db.recetaDao()

    @Provides
    fun provideIngredientDao(db: GoodFoodDatabase): IngredientDao = db.ingredientDao()

    @Provides
    fun provideStepDao(db: GoodFoodDatabase): StepDao = db.stepDao()

    @Provides
    fun provideMeasureDao(db: GoodFoodDatabase): MeasureDao = db.measureDao()

    @Provides
    fun provideCategoryDao(db: GoodFoodDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideRecetaCategoryDao(db: GoodFoodDatabase): RecetaCategoryDao = db.recetaCategoryDao()
}
