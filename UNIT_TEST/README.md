# Test Unitarios

## Qué es
Valga la redundancia, es código que escribes para probar tu código.Además es usado para asegurar la calidad del producto.Estas pruebas nos sirven para asegurar que nuestro código está trabajando como debe de trabajar. Otra manera de usar los test unitarios es para que otros desarrolladores entiendan cómo usar nuestro código. Otro uso  de las pruebas es  que resultan ser más útiles que comentarios largos sobre el código.
# Las tres partes del Test
* Arrange (Preparar)
* Act (Actuar)
* Assert (Afirmar)

* La parte de Preparación puede estar contenida en el método SetUp, si es común a todos los test de la clase.Si la etapa de preparación es común a varios test de la clase pero no a todos, entonces no le pondremos la etiqueta de test de la misma clase.Todo lo anterior se resume en que son las precondiciones de la prueba.
* Acto, consiste en hacer  la llamada al código que queremos probar y las afirmaciones se hacer sobre el resultado de la ejecución, bien mediante validación del estado o bien mediante la interacción.
* Afirmar  que nuestras expectativas sobre el resultado se cumplen.Si no se cumplen el framework maracará en rojo cada falsa expectativa.


# Realización de una prueba unitaria
...
