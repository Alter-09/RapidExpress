# ESTRUCTURAS DE DECISION

## if-else

```java
if (condición) { 
    // Bloque then 
} else if{ 
    // Bloque then
} else { 
    // Bloque else 
}


int nota = 85; 
if (nota >= 90){ 
    System.out.println("Obtuviste una A"); 
} else if (nota >= 80) { 
    System.out.println("Obtuviste una B"); 
} else if (nota >= 70) { 
    System.out.println("Obtuviste una C"); 
} else { 
    System.out.println("Obtuviste una D"); 
}

// notacion mas sencilla para el bloque if

 if (nota>=4.5 && nota<=5)notaLetra='S'; // Bloque then
 else if(nota>=4)notaLetra='E'; // Bloque then
 else if(nota>=3.5)notaLetra='A'; // Bloque then
 else if(nota>=2)notaLetra='I'; // Bloque then
 else if(nota>=0 && nota<=1.9)notaLetra='D'; // Bloque then
 else System.out.println("Nota no valida"); // Bloque else
```

## Switch

La estructura switch en Java se utiliza para evaluar múltiples casos y ejecutar diferentes bloques de código según el valor de una expresión. Proporciona una forma más concisa de manejar múltiples opciones y flujos de ejecución en comparación con el uso de múltiples declaraciones if-else if-else. La sintaxis básica de switch es la siguiente:

```java
switch (expresion) { 
	case valor1: 
		bloque1; 
		break; 
	case valor2: 
		bloque2; 
		break; 
	case valor3: 
 		bloque3; 
 		break;
... 
	default: 
		bloque_por_defecto; 
}
```

- La expresión dentro del `switch` se evalúa y se compara con los valores en cada caso.

- Cada case especifica un valor posible que se compara con la expresión.

- Si la expresión coincide con el valor en un `case`, se ejecuta el bloque de código asociado a ese case.

- Es importante incluir la instrucción `break` al final de cada bloque de código de un case para evitar que se ejecuten los bloques de código de otros case inadvertidamente.

- Si ninguno de los casos coincide con la expresión, se ejecuta el bloque de código del `default` (opcional) al final del switch.

```java
int diaSemana = 3; 
switch (diaSemana) { 
    case 1: 
        System.out.println("Lunes"); 
        break; 
    case 2: 
        System.out.println("Martes"); 
        break; 
    case 3: 
        System.out.println("Miércoles"); 
        break; 
    case 4: 
        System.out.println("Jueves"); 
        break; 
    case 5: 
        System.out.println("Viernes"); 
        break; 
    default: 
        System.out.println("Fin de semana"); 
        break; 
} 

// =====================================================================//

String diaSemana = "lunes"; 
switch (diaSemana) { 
    case "lunes": 
        System.out.println("Primer día de la semana");
        break; 
    case "martes": 
        System.out.println("Segundo día de la semana"); 
        break; 
    // Otros casos para los demás días de la semana 
    default: 
        System.out.println("Día no valido"); 
        break; 
}
```
