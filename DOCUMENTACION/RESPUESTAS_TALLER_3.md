# Solución - Taller 3: Ejercicios Java Stream API

Este documento contiene la solución completa, explicada paso a paso y con código fuente de los 15 ejercicios planteados en el **Taller 3 (Stream API)**.

---

## 📁 Estructura del Proyecto en `taller 3`

- [`pom.xml`](file:///c:/Users/sergi/OneDrive/Desktop/git%20java/JAVA-main/taller%203/pom.xml): Archivo de configuración Maven.
- [`src/main/java/com/mycompany/taller3/model/`](file:///c:/Users/sergi/OneDrive/Desktop/git%20java/JAVA-main/taller%203/src/main/java/com/mycompany/taller3/model/): Modelos de dominio (`Persona.java`, `Producto.java`, `Empleado.java`, `Cliente.java`, `Transaccion.java`).
- [`src/main/java/com/mycompany/taller3/EjerciciosStream.java`](file:///c:/Users/sergi/OneDrive/Desktop/git%20java/JAVA-main/taller%203/src/main/java/com/mycompany/taller3/EjerciciosStream.java): Métodos puros con las soluciones de los 15 ejercicios.
- [`src/main/java/com/mycompany/taller3/Taller3.java`](file:///c:/Users/sergi/OneDrive/Desktop/git%20java/JAVA-main/taller%203/src/main/java/com/mycompany/taller3/Taller3.java): Menú interactivo y ejecutor de pruebas para la consola.

---

## 🟢 Nivel Básico (1–5)

### 1. Filtrar números pares
- **Descripción:** Dada una lista de enteros, filtra solo los números pares.
- **Objetivo:** Usar `.filter()` y `.collect()`.
```java
List<Integer> pares = numeros.stream()
        .filter(n -> n % 2 == 0)
        .collect(Collectors.toList());
```

### 2. Convertir cadenas a mayúsculas
- **Descripción:** Convierte una lista de cadenas a mayúsculas.
- **Objetivo:** Usar `.map()`.
```java
List<String> mayusculas = palabras.stream()
        .map(String::toUpperCase)
        .toList(); // o .collect(Collectors.toList())
```

### 3. Calcular la suma de una lista de enteros
- **Descripción:** Suma todos los elementos de una lista.
- **Objetivo:** Usar `.reduce()`.
```java
int suma = numeros.stream()
        .reduce(0, Integer::sum);
```

### 4. Contar elementos que cumplen una condición
- **Descripción:** Cuenta cuántos nombres tienen más de 5 letras.
- **Objetivo:** Usar `.filter()` y `.count()`.
```java
long cantidad = nombres.stream()
        .filter(nombre -> nombre != null && nombre.length() > 5)
        .count();
```

### 5. Obtener el máximo y mínimo
- **Descripción:** Encuentra el número máximo y mínimo de una lista.
- **Objetivo:** Usar `.max()` y `.min()`.
```java
Optional<Integer> maximo = numeros.stream()
        .max(Integer::compareTo);

Optional<Integer> minimo = numeros.stream()
        .min(Integer::compareTo);
```

---

## 🟡 Nivel Intermedio (6–10)

### 6. Eliminar duplicados
- **Descripción:** Quita los elementos duplicados de una lista.
- **Objetivo:** Usar `.distinct()`.
```java
List<Integer> unicos = numeros.stream()
        .distinct()
        .collect(Collectors.toList());
```

### 7. Ordenar una lista de cadenas
- **Descripción:** Ordena alfabéticamente una lista de nombres.
- **Objetivo:** Usar `.sorted()`.
```java
List<String> ordenados = nombres.stream()
        .sorted()
        .collect(Collectors.toList());
```

### 8. Concatenar elementos en una sola cadena
- **Descripción:** Une una lista de palabras separadas por comas.
- **Objetivo:** Usar `Collectors.joining(", ")`.
```java
String resultado = palabras.stream()
        .collect(Collectors.joining(", "));
```

### 9. Promedio de una lista de números
- **Descripción:** Calcula el promedio de una lista de enteros.
- **Objetivo:** Usar `.mapToInt()` y `.average()`.
```java
OptionalDouble promedio = numeros.stream()
        .mapToInt(Integer::intValue)
        .average();
```

### 10. Transformar una lista de objetos
- **Descripción:** Dada una lista de objetos `Persona`, obtener solo sus nombres.
- **Objetivo:** Usar `.map()` para extraer un atributo.
```java
List<String> nombres = personas.stream()
        .map(Persona::getNombre)
        .toList();
```

---

## 🔴 Nivel Avanzado (11–15)

### 11. Agrupar por categoría
- **Descripción:** Dada una lista de productos con categoría, agrúpalos por categoría.
- **Objetivo:** Usar `Collectors.groupingBy()`.
```java
Map<String, List<Producto>> porCategoria = productos.stream()
        .collect(Collectors.groupingBy(Producto::getCategoria));
```

### 12. Contar elementos por grupo
- **Descripción:** A partir del ejercicio anterior, cuenta cuántos productos hay por categoría.
- **Objetivo:** Usar `Collectors.counting()`.
```java
Map<String, Long> conteoPorCategoria = productos.stream()
        .collect(Collectors.groupingBy(
                Producto::getCategoria, 
                Collectors.counting()
        ));
```

### 13. Calcular el salario promedio por departamento
- **Descripción:** Dada una lista de empleados, agrupar por departamento y calcular el promedio de salario.
- **Objetivo:** `groupingBy()` + `averagingDouble()`.
```java
Map<String, Double> salarioPromedioPorDepto = empleados.stream()
        .collect(Collectors.groupingBy(
                Empleado::getDepartamento,
                Collectors.averagingDouble(Empleado::getSalario)
        ));
```

### 14. Combinar dos listas en un solo stream
- **Descripción:** Tienes dos listas de enteros, combínalas y obtén los números únicos ordenados.
- **Objetivo:** Usar `Stream.concat()` y operaciones encadenadas.
```java
List<Integer> combinadosUnicosOrdenados = Stream.concat(lista1.stream(), lista2.stream())
        .distinct()
        .sorted()
        .collect(Collectors.toList());
```

### 15. Pipeline complejo: Filtrar, mapear, agrupar y reducir
- **Descripción:**
  Dada una lista de transacciones:
  - Filtra las mayores a 1000,
  - Extrae el nombre del cliente,
  - Agrupa por cliente,
  - Suma el total gastado por cada uno.
- **Objetivo:** Componer múltiples operaciones Stream.
```java
Map<String, Double> totalPorCliente = transacciones.stream()
        .filter(t -> t.getMonto() > 1000)
        .collect(Collectors.groupingBy(
                t -> t.getCliente().getNombre(),
                Collectors.summingDouble(Transaccion::getMonto)
        ));
```

---

## 🚀 Cómo ejecutar

Para compilar y ejecutar el proyecto desde la terminal:

```powershell
# Compilar los archivos Java
$files = Get-ChildItem -Path "taller 3/src/main/java" -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -d "taller 3/target/classes" $files

# Ejecutar el menú interactivo
java -cp "taller 3/target/classes" com.mycompany.taller3.Taller3
```
