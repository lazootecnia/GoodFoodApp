# Módulo `food`

Gestiona el dominio de **recetas** dentro de GoodFood. Es el único responsable del ciclo de vida de las entidades del negocio y de su persistencia en Room.

## Responsabilidades

- Modelar y persistir recetas, ingredientes, pasos, medidas y categorías.
- Importar datos desde el **asset JSON embebido** en el primer arranque (cuando Room está vacío).
- Refrescar datos desde el **servicio REST** (descarga de ZIP, descompresión, parseo, importación a Room) cuando el usuario lo dispara manualmente.
- Exponer las recetas a la capa de presentación a través de sus `controllers`.

## Contrato público

Solo se accede al módulo a través de `food.controllers`. Todo lo demás (`entities`, `dto`, `repositories`, `mappers`, `validators`) es **privado al módulo**.

Operaciones esperadas (a medida que crezca):

- `RecetaController`
  - `observeRecetas(): Flow<List<Receta>>`
  - `observeRecetasPorCategoria(categoriaId: Long): Flow<List<Receta>>`
  - `getReceta(id: Long): Receta?`
  - `importarDesdeAssetSiVacia(): Result<Unit>`
  - `importarDesdeServicio(): Result<Unit>`

## Entidades que expone

- `Receta`
- `Ingredient`
- `Step`
- `Measure`
- `Category`

Definiciones en `/docs/entities.md`.

## Prohibido

- Conocer la capa `ui` (no importar nada de `com.zootecnia.goodfood.ui`).
- Exponer `Dto`, `Dao`, clases `WithXxx` o cualquier tipo de `repositories/` fuera del módulo.
- Manejar estado de pantalla, navegación o ciclos de vida de Compose/ViewModels.
- Hardcodear strings de UI (no es su responsabilidad).

## Dependencias

- Room (persistencia).
- OkHttp + WorkManager (descarga del ZIP).
- kotlinx.serialization (parseo del JSON).
- Hilt (inyección).

No depende de ningún otro módulo del proyecto.

## Estructura interna

```
food/
├── entities/     -- @Entity de Room
├── dto/          -- @Serializable para JSON del asset y del REST
├── repositories/ -- DAOs, Room database, lectura de assets, descarga REST, descompresión ZIP
├── controllers/  -- contrato público del módulo
├── mappers/      -- Dto ↔ Entity
├── validators/   -- validación de datos antes de persistir
├── di/           -- módulos Hilt del módulo
└── README.md     -- este archivo
```