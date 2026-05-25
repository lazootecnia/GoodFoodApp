package com.zootecnia.goodfood.food.mappers

data class ParsedIngredient(
    val quantity: Double?,
    val measureText: String?,
    val text: String
)

private val KNOWN_MEASURES = setOf(
    "g", "kg", "mg", "ml", "l", "cc",
    "taza", "tazas",
    "cda", "cdas", "cucharada", "cucharadas",
    "cucharadita", "cucharaditas",
    "vaso", "vasos",
    "kilo", "kilos",
    "litro", "litros",
    "unidad", "unidades",
    "pizca", "pizcas"
)

private val WORD_NUMBERS = mapOf(
    "un" to 1.0, "una" to 1.0,
    "medio" to 0.5, "media" to 0.5,
    "dos" to 2.0, "tres" to 3.0,
    "cuatro" to 4.0, "cinco" to 5.0
)

private val FRACTION_REGEX = Regex("""^(\d+)/(\d+)""")
private val NUMBER_REGEX = Regex("""^(\d+(?:\.\d+)?)""")
private val NUMBER_UNIT_JOINED_REGEX = Regex("""^(\d+(?:\.\d+)?)\s*(g|kg|mg|ml|l|cc)\b""", RegexOption.IGNORE_CASE)

fun parseIngredient(raw: String): ParsedIngredient {
    val trimmed = raw.trim()

    NUMBER_UNIT_JOINED_REGEX.find(trimmed)?.let { match ->
        val qty = match.groupValues[1].toDouble()
        val measure = match.groupValues[2].lowercase()
        val rest = trimmed.substring(match.range.last + 1).trimStart().removePrefix("de ").trimStart()
        return ParsedIngredient(qty, measure, rest.ifEmpty { trimmed })
    }

    val fractionMatch = FRACTION_REGEX.find(trimmed)
    if (fractionMatch != null) {
        val num = fractionMatch.groupValues[1].toDouble()
        val den = fractionMatch.groupValues[2].toDouble()
        val qty = if (den != 0.0) num / den else null
        val afterFraction = trimmed.substring(fractionMatch.range.last + 1).trimStart()
            .removePrefix("de ").trimStart()
        return extractMeasureAndText(qty, afterFraction, trimmed)
    }

    val numberMatch = NUMBER_REGEX.find(trimmed)
    if (numberMatch != null) {
        val qty = numberMatch.groupValues[1].toDouble()
        val afterNumber = trimmed.substring(numberMatch.range.last + 1).trimStart()
        return extractMeasureAndText(qty, afterNumber, trimmed)
    }

    val firstWord = trimmed.split(" ", limit = 2).firstOrNull()?.lowercase() ?: ""
    val wordQty = WORD_NUMBERS[firstWord]
    if (wordQty != null) {
        val afterWord = trimmed.substring(firstWord.length).trimStart()
        return extractMeasureAndText(wordQty, afterWord, trimmed)
    }

    return ParsedIngredient(null, null, trimmed)
}

private fun extractMeasureAndText(
    qty: Double?,
    remaining: String,
    original: String
): ParsedIngredient {
    if (remaining.isEmpty()) return ParsedIngredient(qty, null, original)

    val firstWord = remaining.split(" ", limit = 2).firstOrNull()?.lowercase() ?: ""
    if (firstWord in KNOWN_MEASURES) {
        val rest = remaining.substring(firstWord.length).trimStart()
            .removePrefix("de ").trimStart()
        return ParsedIngredient(qty, firstWord, rest.ifEmpty { original })
    }
    return ParsedIngredient(qty, null, remaining)
}
