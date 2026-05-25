package com.zootecnia.goodfood.food.repositories

import android.content.Context
import com.zootecnia.goodfood.food.dto.RecetaDto
import com.zootecnia.goodfood.food.entities.Category
import com.zootecnia.goodfood.food.entities.Measure
import com.zootecnia.goodfood.food.entities.RecetaCategory
import com.zootecnia.goodfood.food.mappers.parseIngredient
import com.zootecnia.goodfood.food.mappers.toEntity
import com.zootecnia.goodfood.food.mappers.toStepEntities
import com.zootecnia.goodfood.food.entities.Ingredient
import com.zootecnia.goodfood.food.repositories.room.CategoryDao
import com.zootecnia.goodfood.food.repositories.room.GoodFoodDatabase
import com.zootecnia.goodfood.food.repositories.room.IngredientDao
import com.zootecnia.goodfood.food.repositories.room.MeasureDao
import com.zootecnia.goodfood.food.repositories.room.RecetaCategoryDao
import com.zootecnia.goodfood.food.repositories.room.RecetaDao
import com.zootecnia.goodfood.food.repositories.room.StepDao
import androidx.room.withTransaction
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import java.util.zip.ZipInputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImportRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: GoodFoodDatabase,
    private val recetaDao: RecetaDao,
    private val ingredientDao: IngredientDao,
    private val stepDao: StepDao,
    private val measureDao: MeasureDao,
    private val categoryDao: CategoryDao,
    private val recetaCategoryDao: RecetaCategoryDao
) {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun isDbEmpty(): Boolean = recetaDao.count() == 0

    suspend fun importFromAssets() = withContext(Dispatchers.IO) {
        val tempDir = File(context.cacheDir, "import_temp")
        try {
            extractZip(tempDir)
            val recetariosDir = File(tempDir, "recetarios")
            val subdirs = recetariosDir.listFiles { f -> f.isDirectory } ?: emptyArray()
            for (subdir in subdirs) {
                val jsonFile = File(subdir, "recetario.json")
                if (!jsonFile.exists()) continue
                val recipes = json.decodeFromString<List<RecetaDto>>(jsonFile.readText())
                importRecipes(recipes, subdir)
            }
        } finally {
            tempDir.deleteRecursively()
        }
    }

    private fun extractZip(destDir: File) {
        destDir.deleteRecursively()
        destDir.mkdirs()
        context.assets.open("seed/recipes.zip").use { input ->
            ZipInputStream(input).use { zip ->
                var entry = zip.nextEntry
                while (entry != null) {
                    val outFile = File(destDir, entry.name)
                    if (entry.isDirectory) {
                        outFile.mkdirs()
                    } else {
                        outFile.parentFile?.mkdirs()
                        outFile.outputStream().use { output ->
                            zip.copyTo(output)
                        }
                    }
                    zip.closeEntry()
                    entry = zip.nextEntry
                }
            }
        }
    }

    private suspend fun importRecipes(recipes: List<RecetaDto>, recetarioDir: File) {
        database.withTransaction {
            val imagesDir = File(context.filesDir, "recetas/images")
            imagesDir.mkdirs()

            for (dto in recipes) {
                val entity = dto.toEntity()
                val recetaId = recetaDao.insert(entity)

                val categoryIds = dto.categories.map { catText ->
                    resolveCategory(catText)
                }
                recetaCategoryDao.insertAll(
                    categoryIds.map { catId -> RecetaCategory(recetaId, catId) }
                )

                val ingredientEntities = dto.ingredients.mapIndexed { index, raw ->
                    val parsed = parseIngredient(raw)
                    val measureId = parsed.measureText?.let { resolveMeasure(it) }
                    Ingredient(
                        recetaId = recetaId,
                        order = index.toLong(),
                        quantity = parsed.quantity,
                        measureId = measureId,
                        text = parsed.text
                    )
                }
                ingredientDao.insertAll(ingredientEntities)

                stepDao.insertAll(dto.toStepEntities(recetaId))

                val filename = dto.imageFilename ?: continue
                val srcFile = File(recetarioDir, "images/$filename")
                if (srcFile.exists()) {
                    val destFile = File(imagesDir, filename)
                    srcFile.copyTo(destFile, overwrite = true)
                }
            }
        }
    }

    private suspend fun resolveCategory(text: String): Long {
        categoryDao.getByText(text)?.let { return it.id }
        categoryDao.insertOrIgnore(Category(text = text))
        return categoryDao.getByText(text)!!.id
    }

    private suspend fun resolveMeasure(text: String): Long {
        measureDao.getByText(text)?.let { return it.id }
        measureDao.insertOrIgnore(Measure(text = text))
        return measureDao.getByText(text)!!.id
    }
}
