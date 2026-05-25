package com.zootecnia.goodfood.food.dto

import kotlinx.serialization.Serializable

@Serializable
data class RecetaDto(
    val id: Long = 0L,
    val title: String,
    val imageUrl: String,
    val categories: List<String>,
    val ingredients: List<String>,
    val steps: List<String>
)
