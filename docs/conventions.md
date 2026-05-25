# Convenciones - GoodFood

Estas reglas aplican a todo el código del proyecto. Lo que no esté acá se rige por el [estilo oficial de Kotlin](https://kotlinlang.org/docs/coding-conventions.html) y las recomendaciones de [Android Architecture](https://developer.android.com/topic/architecture).

## 1. Naming

- **Paquetes**: minúsculas, sin guiones bajos. Ej.: `com.zootecnia.goodfood.food.repositories`.
- **Clases**: `PascalCase`. Sufijos obligatorios según rol:
  - Entidades Room: sin sufijo (`Receta`, `Ingredient`).
  - DTOs: sufijo `Dto` (`RecetaDto`, `IngredientDto`).
  - DAOs Room: sufijo `Dao` (`RecetaDao`).
  - Repositorios: sufijo `Repository` (`RecetaRepository`).
  - Controllers: sufijo `Controller` (`RecetaController`).
  - Mappers: sufijo `Mapper` (`RecetaMapper`).
  - Validators: sufijo `Validator` (`RecetaValidator`).
  - ViewModels: sufijo `ViewModel` (`RecetasViewModel`).
  - Screens Compose: sufijo `Screen` (`RecetasScreen`, `RecetaDetalleScreen`).
- **Funciones y variables**: `camelCase`.
- **Constantes**: `UPPER_SNAKE_CASE` dentro de `companion object` o top-level `const val`.
- **Archivos**: un archivo por clase pública. Nombre del archivo = nombre de la clase.

## 2. Kotlin style

- **Formato**: ktlint (configuración por defecto). Indent 4 espacios.
- **Largo de línea**: 120 caracteres.
- **Inmutabilidad**: `val` por defecto, `var` solo cuando hay reasignación real.
- **Data class** para entidades, DTOs y estados de UI.
- **Nulabilidad**: evitar `!!`. Preferir `?.`, `?:`, `requireNotNull(...)` cuando aplique.
- **Funciones de extensión**: agrupadas en archivos `Xxx.kt` (sin clase asociada).
- **Imports**: sin wildcards (`import foo.bar.*` prohibido).

## 3. Room

- **Tablas**: `snake_case`, en singular. Ej.: `@Entity(tableName = "receta")`.
- **Columnas**: `snake_case`. Mapear con `@ColumnInfo(name = "...")` cuando el campo Kotlin difiera.
- **PK**: `@PrimaryKey(autoGenerate = true) val id: Long = 0L`.
- **Relaciones**: usar `@Relation` con clases de relación dedicadas (sufijo `WithXxx`, ej. `RecetaWithIngredients`). Nunca exponer estas clases fuera del repositorio.
- **Migraciones**: archivo por versión bajo `food/repositories/room/migrations/`.

## 4. DTOs vs Entities

- Una **entity** nunca sale del módulo. Es interna: vive atada a Room y solo la conocen `repositories/` y `mappers/` del propio módulo.
- Un **DTO** es la superficie pública del módulo: lo exponen los `controllers/` y es lo que viaja cuando un dato cruza la frontera del módulo.
- La conversión `Entity ↔ Dto` vive en `mappers/`, en funciones de extensión:
  ```kotlin
  fun Receta.toDto(): RecetaDto
  fun RecetaDto.toEntity(): Receta
  ```
- DTOs son `@Serializable` (kotlinx.serialization). Entidades son `@Entity` (Room).
- Si un DTO y una entity tienen forma idéntica, igual viven separados; representan capas distintas.
- **Catálogos compartidos** (`Category`, `Measure`): el mapper hace *upsert por `text`* — busca por texto; si no existe, lo crea; si existe, reutiliza el id. Nunca duplicar registros con el mismo `text`.
- **Parser de ingredientes** (string → `Ingredient` estructurado): vive en `mappers/` y es **tolerante**. Cuando no logra extraer cantidad o medida, deja esos campos en `null` y conserva el string original completo en `text`. Nunca lanza excepción por un string "raro".

## 5. Controllers

- Único contrato visible desde fuera del módulo.
- Exponen **DTOs**, nunca entities. Internamente mapean entity → dto antes de devolver.
- Firma estándar:
  - Lecturas reactivas: `fun observeRecetas(): Flow<List<RecetaDto>>`.
  - Lecturas puntuales: `suspend fun getReceta(id: Long): RecetaDto?`.
  - Acciones: `suspend fun importarDesdeServicio(): Result<Unit>`.
- **Errores**: las acciones devuelven `Result<T>`. Las lecturas reactivas propagan vía `Flow` (no atrapar excepciones sin necesidad).
- Antes de invocar al repository: pasar por el validator correspondiente.

## 6. Validators

- Función pura: recibe el objeto, devuelve `ValidationResult` (sealed: `Valid` / `Invalid(errors: List<String>)`).
- Sin dependencias a Room ni a la red.
- Se invocan desde el controller, **antes** de tocar el repository.

## 7. Compose / UI

- **Composables**: `PascalCase`, sin sufijo. Excepción: pantallas completas llevan sufijo `Screen`.
- **State hoisting** obligatorio: ningún composable reusable maneja estado interno mutable.
- Cada composable público lleva una `@Preview` mínima (light + dark cuando aplique).
- **Adaptive**: usar `WindowSizeClass` para decidir layouts (list-detail en tablet, single-pane en celular).
- Recursos de texto siempre en `strings.xml` (nunca hardcodeados en composables).
- Imágenes remotas/locales: **Coil** (`AsyncImage`).
- **Estados de carga**: usar los componentes nativos de Compose (`CircularProgressIndicator`, `LinearProgressIndicator`). No incluir GIFs ni imágenes propias de loading.

## 8. ViewModels

- Un único `StateFlow<UiState>` por pantalla, donde `UiState` es `sealed interface` (`Loading`, `Content`, `Error`).
- Eventos one-shot (navegación, snackbars) vía `Channel` expuesto como `Flow` (`receiveAsFlow()`).
- Los ViewModels solo dependen de `controllers/` y consumen los DTOs que estos exponen. Nunca de repositorios, DAOs ni entities.
- Inyección con `@HiltViewModel` y `@Inject constructor(...)`.

## 9. Hilt

- **Scopes**:
  - `@Singleton`: repositories, base de datos Room, OkHttp client.
  - `@ViewModelScoped`: nada por defecto; agregar solo si hay estado compartido entre casos de uso de la misma pantalla.
- **Módulos**: uno por sub-paquete con dependencias (`FoodRepositoryModule`, `FoodControllerModule`, `NetworkModule`). Viven en `<modulo>/di/`.
- Interfaces se atan con `@Binds` (preferido sobre `@Provides` cuando alcance).

## 10. Testing

- **Validators y mappers**: tests unitarios puros (JVM, sin Android). Cobertura alta esperada.
- **Controllers**: tests con repositorios fake (no mocks de Mockito; clases fake escritas a mano).
- **Repositorios Room**: tests instrumentados con base in-memory (`Room.inMemoryDatabaseBuilder`).
- **UI**: tests de Compose para flujos críticos (importación inicial, listado, detalle).
- Estructura: el test refleja la ruta del código (`food/validators/RecetaValidator.kt` → `food/validators/RecetaValidatorTest.kt`).

## 11. Git / commits

- **Conventional Commits** en español:
  - `feat: agregar pantalla de importación inicial`
  - `fix: corregir cálculo de cantidad en ingredientes`
  - `refactor: extraer mapper de receta`
  - `docs: actualizar architecture.md`
  - `test: agregar tests de RecetaValidator`
  - `chore: subir versión de Compose`
- Una unidad de cambio = un commit. Evitar commits de "varias cosas".
- Mensaje en imperativo, sin punto final, máximo 72 caracteres en el asunto.

## 12. Idioma

- **Código** (clases, funciones, variables): **inglés**.
- **Dominio** (entidades, conceptos del negocio): **español** cuando el término no tiene traducción natural o ya está fijado por el negocio. Ej.: `Receta`, `Ingredient` (mixto aceptable, ya consolidado en `entities.md`).
- **Comentarios y docs**: **español**.
- **Mensajes de commit**: **español**.
- **Strings de UI**: **español** (no se planea i18n por ahora).