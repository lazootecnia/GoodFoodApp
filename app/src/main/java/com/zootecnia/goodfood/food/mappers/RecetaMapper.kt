package com.zootecnia.goodfood.food.mappers

import com.zootecnia.goodfood.food.dto.RecetaDto
import com.zootecnia.goodfood.food.entities.Ingredient
import com.zootecnia.goodfood.food.entities.Measure
import com.zootecnia.goodfood.food.entities.Receta
import com.zootecnia.goodfood.food.entities.RecetaWithDetails
import com.zootecnia.goodfood.food.entities.Step
import java.io.File

fun RecetaDto.toEntity(): Receta {
    val imageName = imageUrl.removePrefix("assets/")
    return Receta(
        title = title,
        imagePath = imageName
    )
}

fun RecetaDto.toIngredientEntities(
    recetaId: Long,
    measureResolver: suspend (String) -> Long
): List<suspend () -> Ingredient> =
    ingredients.mapIndexed { index, raw ->
        suspend {
            val parsed = parseIngredient(raw)
            val measureId = parsed.measureText?.let { measureResolver(it) }
            Ingredient(
                recetaId = recetaId,
                order = index.toLong(),
                quantity = parsed.quantity,
                measureId = measureId,
                text = parsed.text
            )
        }
    }

fun RecetaDto.toStepEntities(recetaId: Long): List<Step> =
    steps.mapIndexed { index, text ->
        Step(
            recetaId = recetaId,
            order = index.toLong(),
            text = text
        )
    }

fun RecetaWithDetails.toDto(imagesDir: File): RecetaDto {
    val measuresById = ingredients
        .mapNotNull { it.measureId }
        .distinct()

    return RecetaDto(
        id = receta.id,
        title = receta.title,
        imageUrl = File(imagesDir, receta.imagePath).absolutePath,
        categories = categories.map { it.text },
        ingredients = ingredients
            .sortedBy { it.order }
            .map { ing -> formatIngredient(ing) },
        steps = steps
            .sortedBy { it.order }
            .map { it.text }
    )
}

fun RecetaWithDetails.toDto(imagesDir: File, measuresById: Map<Long, Measure>): RecetaDto =
    RecetaDto(
        id = receta.id,
        title = receta.title,
        imageUrl = File(imagesDir, receta.imagePath).absolutePath,
        categories = categories.map { it.text },
        ingredients = ingredients
            .sortedBy { it.order }
            .map { ing -> formatIngredient(ing, measuresById) },
        steps = steps
            .sortedBy { it.order }
            .map { it.text }
    )

private fun formatIngredient(ing: Ingredient, measuresById: Map<Long, Measure> = emptyMap()): String {
    val parts = mutableListOf<String>()
    ing.quantity?.let { q ->
        parts.add(if (q == q.toLong().toDouble()) q.toLong().toString() else q.toString())
    }
    ing.measureId?.let { mid ->
        measuresById[mid]?.let { parts.add(it.text) }
    }
    parts.add(ing.text)
    return parts.joinToString(" ")
}
