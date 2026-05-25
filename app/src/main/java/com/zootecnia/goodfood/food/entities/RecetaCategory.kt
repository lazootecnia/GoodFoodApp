package com.zootecnia.goodfood.food.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "receta_category",
    primaryKeys = ["receta_id", "category_id"],
    foreignKeys = [
        ForeignKey(
            entity = Receta::class,
            parentColumns = ["id"],
            childColumns = ["receta_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("receta_id"),
        Index("category_id")
    ]
)
data class RecetaCategory(
    @ColumnInfo(name = "receta_id") val recetaId: Long,
    @ColumnInfo(name = "category_id") val categoryId: Long
)
