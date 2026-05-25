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
- En el primer arranque (Room vacío), se importan recetas desde un **asset JSON embebido** en el APK, mostrando una pantalla de espera.
- El usuario puede disparar manualmente una **actualización desde el servicio REST**: descarga un ZIP, lo descomprime, parsea el JSON e importa a Room.
- El servicio REST no se consulta para mostrar datos en pantalla; solo sirve para refrescar la base local.