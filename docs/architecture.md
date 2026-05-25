# Arquitectura - GoodFood

Aplicación Android nativa. Stack:

- **UI**: Jetpack Compose, adaptativo (celular y tablet)
- **DI**: Hilt
- **Navegación**: Jetpack Navigation 3
- **Persistencia**: Room (única fuente de verdad en tiempo de uso)
- **Async**: Coroutines + Flow
- **HTTP**: OkHttp + WorkManager (descarga del ZIP en background)
- **Serialización**: kotlinx.serialization (JSON)

## Paquete raíz

`com.zootecnia.goodfood`

Cada módulo es un sub-paquete directo de la raíz. Hoy existen dos:

```
com.zootecnia.goodfood/
├── food/    -- módulo de negocio (recetas)
└── ui/      -- módulo de presentación
```

## Estructura de un módulo de negocio

Cada módulo de negocio sigue siempre la misma estructura interna:

```
<modulo>/
├── entities/     -- entidades del negocio (Room @Entity)
├── dto/          -- objetos de transferencia con el backend (JSON)
├── repositories/ -- acceso a datos: Room, lectura de assets, descarga REST, descompresión ZIP
├── controllers/  -- punto de entrada al negocio; único contrato visible desde fuera del módulo
├── mappers/      -- conversión entre entities y dto
├── validators/   -- validación de datos
└── README.md     -- describe el módulo, sus responsabilidades y límites
```

## Módulo de presentación (`ui`)

```
ui/
├── screens/      -- pantallas Compose
├── viewmodels/   -- ViewModels inyectados con Hilt
├── components/   -- componentes Compose reutilizables
├── navigation/   -- grafo de navegación (Nav3)
└── theme/        -- Material3: colores, tipografía, dimensiones
```

## Reglas de frontera

1. La UI **solo** consume `controllers` de los módulos de negocio. Nunca accede a `repositories`, `entities` ni `mappers` directamente.
2. Los `repositories` nunca conocen la UI ni el ciclo de vida de Compose/ViewModels.
3. Los `controllers` reciben/devuelven **DTOs** del propio módulo. Las `entities` no cruzan la frontera del módulo: son internas a `repositories/` y `mappers/`.
4. Un módulo nunca importa clases internas de otro módulo (`entities`, `repositories`, `mappers`, `validators`); si necesita interactuar, lo hace a través de su `controller`, que expone `dto`.

## Flujo de datos en runtime

- La app trabaja **siempre contra Room**.
- Hay dos fuentes de importación, ambas con el **mismo formato**: un ZIP que contiene el JSON de recetas + las imágenes asociadas.
  - **Seed** (primer arranque, Room vacío): ZIP embebido en el APK bajo `app/src/main/assets/seed/recipes.zip`. Se muestra una pantalla de espera mientras se importa.
  - **Actualización**: el usuario la dispara manualmente; se descarga el ZIP desde el servicio REST.
- Estructura del ZIP:
  ```
  recipes.zip
  ├── images/         -- PNGs referenciadas por las recetas
  └── recipes/recipes.json
  ```
- Pipeline de importación (idéntico para seed y para REST):
  1. Descomprimir el ZIP a un **directorio temporal** (`cacheDir`).
  2. Parsear `recipes/recipes.json`. Para cada receta:
     - Sus `categories` (strings) se buscan en Room por `text`; si no existen, se crean. Mismo patrón para las `measure` que detecte el parser de ingredientes.
     - Parsear cada string de `ingredients` extrayendo `quantity` y `measure` cuando sea posible. Cuando no se puede parsear (ej. "Mix de semillas"), `quantity` y `measure` quedan nulos y el string original viaja en `text`.
     - El campo `imageUrl` del JSON (formato `assets/images/001.png`) se traduce a la ruta relativa final dentro de `filesDir/recetas/images/` quitando el prefijo `assets/`.
  3. Copiar a `filesDir/recetas/images/` **solo las imágenes referenciadas** por alguna receta. Imágenes huérfanas dentro del ZIP (ej. un `loading.gif` no referenciado) se ignoran.
  4. En cada `Receta`, Room guarda la **ruta relativa** de la imagen dentro de ese directorio (no una URL).
  5. Borrar el directorio temporal.
- El servicio REST no se consulta para mostrar datos en pantalla; solo dispara el pipeline para refrescar Room y las imágenes locales.
- En tiempo de uso, las imágenes se leen desde `filesDir`. Coil acepta `File` / `Uri` para esa ruta.