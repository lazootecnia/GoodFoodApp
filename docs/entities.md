# Entidades

Entidades del módulo `food`. Los `id` son `Long` autoincrementales gestionados por Room.

## Receta
- `id: Long`
- `title: String`
- `imageUrl: String`
- `categories: List<Category>`
- `ingredients: List<Ingredient>`
- `steps: List<Step>`

## Ingredient
- `id: Long`
- `order: Long`
- `quantity: Double`
- `measure: Measure`
- `text: String`

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
