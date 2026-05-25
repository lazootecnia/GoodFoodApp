# Entidades

Entidades del módulo `food`. Los `id` son `Long` autoincrementales gestionados por Room.

## Receta
- `id: Long`
- `title: String`
- `imagePath: String` — ruta relativa de la imagen dentro de `filesDir/recetas/images/`. Se rellena durante la importación del ZIP (no es una URL).
- `categories: List<Category>`
- `ingredients: List<Ingredient>`
- `steps: List<Step>`

## Ingredient
- `id: Long`
- `order: Long`
- `quantity: Double?` — nulo cuando el string original no contiene una cantidad numérica (ej. "Mix de semillas", "Condimentos").
- `measure: Measure?` — nulo cuando no se detecta una unidad reconocible.
- `text: String` — siempre presente. Si el parser logró extraer cantidad/medida, contiene solo la parte descriptiva ("harina de avena"); si no, conserva el string original completo.

> Decisión: mantenemos el modelo estructurado (con `quantity`/`measure`) aunque la fuente sean strings, para habilitar a futuro el cálculo de costo/uso por ingrediente. Los strings no parseables no se pierden — quedan completos en `text`.

## Step
- `id: Long`
- `order: Long`
- `text: String`

## Measure
- `id: Long`
- `text: String`

## Category
- `id: Long`
- `text: String`
