# GUIA DE CLASES EN JAVA

## Modificadores de acceso para los tipos de datos

```java
// Modificadores de acceso para los tipos de datos
fieldName //esto es default, solo se puede acceder desde el mismo paquete las mismas carpetas del proyecto.
          // util para usarse en un modulo pero que no sea visibles en otro modulo, ejemplo se ve en inventario pero no en facturacion.
private fieldName // solo se puede acceder a este campo o metodo dentro de la misma clase en la que fue declarado.
                  // Potege el codigo, nadie puede modificar algo sin antes pasar por las instancias del codigo.
protected fieldName // Esto es para mi y para mis hijos. se puede acceder desde el mismo paquete y ademas por las subclases.
                    // Se puede acceder incluso si la subclase esta en otro paquete
                    // ejemplo clase producto y la clase productoImportado hereda de ella, esta calse accede a cierta logica pero el resto no.
public fieldName // para todo el mundo, se puede acceder incluso de otro proyecto con librerias 
```

## Palabras reservadas de "contexto u objeto"

```java
//=== this === 
this.dataType fieldName=valor; //se refiere a la instancia actual del objeto.

//=== super === 

//("el padre") Es el equivalente a this, pero para la superclase (clase padre). Se usa cuando hay herencia.
/*
llama al constructor de la clase padre
debe ir en la primera linea
*/
super.atributo // accede a un atributo
super.metodo() // accede a un metodo de la clase

//=== static (EL NIVEL DE CLASE) ====

// mientras this se refiere a la instancia actual del objeto static se refiere a la clase
// una variable o metodo static pertenece a la clase en general, no a un objeto en especifico
// se comparte entre todos los objetos
// NO PUEDE USAR this NI super

public class Nombre{
    static dataType fieldName; // va a ser la misma para todos los objetos banco
}

//=== new (el creador) ===
// con esta palabra creamos el objeto fisico en memoria
// curiosidad "this solo existe porque previamente usamos el new"
// se usa para llamar al constructor
//'new' crea el objeto y llama al constructor

Nombre d1 = new fieldName(parametros);

//=== final (el restrictor) ===

// Variable final: es una contante no cambia.
// Metodo final: no puede ser sobrescrito (override) por una clase hija
// Clase final: No puede tener clases hijas (no puede ser heredada)

//=== instanceof (el verificador de tipo)

Nombre d1 = new fieldName(parametros);
System.out.println(d1 intanceof Nombre); //si se cumple regresa true si no se cumple false

// EJEMPLO
Persona p1 = new Empleado("Luis", 30, "Gerente");

System.out.println(p1 instanceof Persona);   // true (Porque Empleado hereda de Persona)
System.out.println(p1 instanceof Empleado);  // true
System.out.println(p1 instanceof String);    // false
```

## Pasos para crear una clase

#### 1. Se crea la clase

```java
public class Nombre{
    dataType fieldName; // field (campos o atributos) variable de instancia atributo
}
```

#### Se crea el constructor DENTRO DE LA CLASE no por fuera de las llaves

La palabra reservada this se refiere a la instancia actual del objeto. Es decir, es una forma en que el objeto dice "yo mismo" o "el objeto en el que estoy ahora". En el contexto de un constructor, this se utiliza principalmente para dos cosas: Resolver conflictos de nombres (El uso más común) Cuando los parámetros del constructor tienen el mismo nombre que las variables de instancia (atributos) de la clase, Java no sabe a cuál te refieres. this se usa para aclarar que te refieres al atributo de la clase.

```java
//this NUNCA SE USA DENTRO DE UN METODO O VARIABLE static
public class Nombre{
    dataType fieldName; // field (campos o atributos) variable de instancia atributo

    ClassName(parameters){
    dataType fieldName = valor; // sin this, java no sabria si es parametro o atributo
    this.dataType fieldName=valor; //this.dataTyPe es el atributo y nombre es el parametro
    }
}
```

#### Se crea el metodo SEGUIMOS DENTRO DE LA CLASE

```java
public class Nombre{
    dataType fieldName; // field (campos o atributos) variable de instancia atributo

    ClassName(parameters){
    dataType fieldName = valor; // sin this, java no sabria si es parametro o atributo
    this.dataType fieldName=valor; //this.dataTyPe es el atributo y nombre es el parametro
    }

    public returnType metodoName(parametros){
        // cuerpo del metodo
    }  
}
```

#### Se crea el metodo GETTER

```java
//1.4 SE CREA EL METODO GETTER
public class Nombre{
    dataType fieldName; // field (campos o atributos) variable de instancia atributo

    ClassName(parameters){
    dataType fieldName = valor; // sin this, java no sabria si es parametro o atributo
    this.dataType fieldName=valor; //this.dataTyPe es el atributo y nombre es el parametro
    }

    public returnType metodoName(parametros){
        // cuerpo del metodo
    }

    public dataType getFieldName(){ // metodo para obtener algo de una varible dentro de la clase
        return this.fieldName; // dependiendo del dataType da el return 
    }   
}
```

#### Ejemplo

```java
public class Producto {
    // 1. FIELDS: El estado interno (privado para protegerlo)
    private String nombre;
    private double precio;

    // 2. CONSTRUCTOR: La lógica de nacimiento
    public Producto(String nombreInicial, double precioInicial) {
        this.nombre = nombreInicial;      // "this" significa "mi propio nombre"
        this.precio = precioInicial;      // Asigna el valor que llegó al campo
    }

    // 3. METHODS: El comportamiento
    // returnType: void (no devuelve nada, solo hace una acción)
    // methodName: aplicarDescuento
    // parameters: double porcentaje
    public void aplicarDescuento(double porcentaje) {
        double descuento = this.precio * (porcentaje / 100);
        this.precio = this.precio - descuento; // Modifica su propio estado
    }

    // Método para obtener el precio (Getter)
    public double getPrecio() {
        return this.precio; // returnType es double, así que devuelve un número
    }
}
```

#### Resumen de la logica en java

Resumen de la Lógica General Cualquier clase que escribas en Java, por compleja que sea, siempre responde a estas 3 preguntas lógicas:
¿Qué datos guarda? (Fields)
¿Cómo se prepara al nacer? (Constructor)
¿Qué acciones puede realizar? (Methods)

#### Pasos para crear una clase con herencia

```java
public class ClaseHija extends ClasePadre {

    // 1. FIELDS: Atributos exclusivos de esta clase (el estado interno adicional)
    private TipoDato atributoPropio;

    // 2. CONSTRUCTOR: La lógica de nacimiento
    public ClaseHija(TipoDato valorPadre1, TipoDato valorPadre2, TipoDato valorPropio) {
        super(valorPadre1, valorPadre2);       // Llama al constructor de la ClasePadre para inicializar lo heredado
        this.atributoPropio = valorPropio;     // Inicializa el atributo exclusivo de esta clase
    }

    // 3. METHODS (Opcional): Getters y Setters para el atributo propio
    public TipoDato getAtributoPropio() {
        return this.atributoPropio;
    }

    public void setAtributoPropio(TipoDato nuevoValor) {
        this.atributoPropio = nuevoValor;
    }
}
```
