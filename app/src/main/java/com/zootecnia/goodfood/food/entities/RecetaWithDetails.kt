package com.zootecnia.goodfood.food.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class RecetaWithDetails(
    @Embedded val receta: Receta,
    @Relation(
        parentColumn = "id",
        entityColumn = "receta_id"
    )
    val ingredients: List<Ingredient>,
    @Relation(
        parentColumn = "id",
        entityColumn = "receta_id"
    )
    val steps: List<Step>,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = RecetaCategory::class,
            parentColumn = "receta_id",
            entityColumn = "category_id"
        )
    )
    val categories: List<Category>
)
