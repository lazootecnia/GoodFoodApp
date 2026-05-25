package com.zootecnia.goodfood.food.validators

import com.zootecnia.goodfood.food.dto.RecetaDto

fun validateReceta(dto: RecetaDto): ValidationResult {
    val errors = mutableListOf<String>()
    if (dto.title.isBlank()) errors.add("El título no puede estar vacío")
    if (dto.ingredients.isEmpty()) errors.add("Debe tener al menos un ingrediente")
    if (dto.steps.isEmpty()) errors.add("Debe tener al menos un paso")
    return if (errors.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(errors)
}
