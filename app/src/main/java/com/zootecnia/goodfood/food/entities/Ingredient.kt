package com.zootecnia.goodfood.food.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ingredient",
    foreignKeys = [
        ForeignKey(
            entity = Receta::class,
            parentColumns = ["id"],
            childColumns = ["receta_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Measure::class,
            parentColumns = ["id"],
            childColumns = ["measure_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("receta_id"),
        Index("measure_id")
    ]
)
data class Ingredient(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "receta_id") val recetaId: Long,
    @ColumnInfo(name = "order_index") val order: Long,
    @ColumnInfo(name = "quantity") val quantity: Double?,
    @ColumnInfo(name = "measure_id") val measureId: Long?,
    @ColumnInfo(name = "text") val text: String
)
