# Sistema de Gestión de Flotas y Rutas "RapidExpress"



Introducción

La empresa de logística y mensajería "RapidExpress" se ha consolidado en el mercado nacional gracias a su compromiso con la puntualidad y la seguridad en la entrega de paquetería. Sin embargo, el crecimiento acelerado de sus operaciones ha puesto en evidencia las limitaciones de sus procesos manuales, generando ineficiencias, costos operativos elevados y dificultades para mantener la calidad del servicio.



El presente proyecto tiene como objetivo el desarrollo de un sistema de información de backend, operado exclusivamente a través de una interfaz de línea de comandos (CLI), para automatizar y optimizar la gestión integral de su flota de vehículos, el personal de conducción, la planificación de rutas y el seguimiento de paquetes.



Contexto del Problema

Actualmente, la asignación de rutas y el seguimiento de los envíos en "RapidExpress" dependen de hojas de cálculo y comunicación manual entre los operadores de logística y los conductores. Este método es propenso a errores humanos, retrasa la comunicación de incidencias y no ofrece una visibilidad en tiempo real del estado de las entregas. La falta de un sistema centralizado dificulta la planificación de mantenimientos preventivos para los vehículos y la evaluación del desempeño de los conductores.



La dirección de "RapidExpress" requiere una solución de software robusta que centralice la información, automatice las tareas repetitivas y proporcione datos fiables para la toma de decisiones estratégicas.



Descripción del Sistema a Desarrollar

El sistema debe ser capaz de gestionar las siguientes funcionalidades principales, organizadas en módulos interconectados:



**1. Gestión de la Flota de Vehículos**

El sistema debe permitir la administración completa del parque automotor de la empresa. Esto incluye:



Registrar nuevos vehículos con información clave: placa, marca, modelo, año de fabricación y capacidad máxima de carga (en kilogramos).



Actualizar y consultar el estado de cualquier vehículo. Los estados válidos son: Disponible, En Ruta, En Mantenimiento.



Programar y registrar los mantenimientos realizados a cada vehículo. El historial de mantenimiento de cada vehículo deberá ser persistido de forma individual para consultas futuras.



**2. Gestión de Personal (Conductores)**

El sistema deberá administrar la información de los conductores asignados a la flota. Para ello, debe permitir:



Registrar, actualizar y consultar los datos de los conductores, incluyendo: número de identificación, nombre completo, tipo de licencia y número de contacto.



Gestionar el estado de cada conductor: Activo, De Vacaciones, Inactivo.



Asignar un conductor a un vehículo Disponible. Un conductor no puede estar asignado a más de un vehículo simultáneamente.



**3. Gestión de Paquetes y Envíos**

Este módulo es el núcleo de la operación logística y debe permitir:



Registrar nuevos paquetes en el sistema, generando un identificador único de seguimiento para cada uno. La información a registrar incluye: descripción del contenido, peso, dimensiones, dirección de origen y dirección de destino.



Asociar cada paquete con la información de un remitente y un destinatario.



Administrar el ciclo de vida de un paquete a través de los siguientes estados: En Bodega, Asignado a Ruta, En Tránsito, Entregado, y Devuelto.



**4. Planificación y Seguimiento de Rutas**

El sistema debe facilitar la logística de distribución de paquetes. Sus responsabilidades son:



Permitir a un operador de logística la creación de una "hoja de ruta" diaria, la cual consiste en asignar un conjunto de paquetes (En Bodega) a un vehículo y su conductor correspondiente. El sistema no debe permitir exceder la capacidad de carga del vehículo.



Al iniciar una ruta, el estado del vehículo y del conductor debe cambiar a En Ruta, y el de los paquetes a En Tránsito.



Permitir el monitoreo de las rutas activas. El sistema debe ser capaz de gestionar la actualización de estado de los paquetes (ej. a Entregado) mientras la ruta está en curso, reflejando estos cambios de manera consistente para cualquier consulta concurrente que se realice desde la terminal.



**5. Módulo de Reportes y Auditoría**

Para facilitar la toma de decisiones y la trazabilidad, el sistema debe ser capaz de:



Generar reportes operativos. Por ejemplo, un listado de todas las entregas realizadas por un conductor en un rango de fechas específico o un informe sobre el historial de rutas de un vehículo.



Mantener un registro de auditoría de todas las operaciones críticas que ocurren en el sistema (creación de paquetes, inicio y fin de una ruta, cambios de estado importantes). Esta información debe ser registrada en un archivo de texto centralizado.



Resultado esperado

#### **1. Estructura de Archivos y Carpetas**

El proyecto deberá ser entregado en un repositorio de Git con una estructura de archivos limpia y profesional, siguiendo el estándar de un proyecto Maven. El estudiante será responsable de definir y justificar toda la estructura de paquetes y clases del proyecto, basándose en los principios de diseño y arquitectura.

```
rapidexpress-management-system/
├── .gitignore
├── README.md
└── database/
    ├── diagrama_entidad_relacion.png
    ├── 1_schema_ddl.sql
    └── 2_data_dml.sql
└── src/
    └── main/
        └── java/
            └── com/
                └── rapidexpress/
                    # El estudiante define la estructura de paquetes y clases
```

####  

#### **2. Arquitectura y Base de Datos**

El sistema deberá ser desarrollado aplicando rigurosamente el patrón de arquitectura **Modelo-Vista-Controlador (MVC)**.

La persistencia de los datos se gestionará a través de una base de datos **MySQL**, la cual deberá estar alojada en un **servicio en la nube** de la elección del estudiante (ej. AWS RDS, Google Cloud SQL, Azure, PlanetScale, etc.).

####  

#### **3. Gestión de la Base de Datos**

El estudiante es responsable del diseño e implementación completa de la base de datos. Se debe entregar lo siguiente:

- **Diagrama Entidad-Relación (ERD):** Un diagrama visual que represente las entidades, sus atributos y las relaciones entre ellas.
- **Scripts SQL:**
- Un script DDL (1_schema_ddl.sql) con las sentencias CREATE TABLE y todas las restricciones necesarias (claves primarias, foráneas, etc.).
- Un script DML (2_data_dml.sql) con las sentencias INSERT para poblar la base de datos con un mínimo de **20 registros significativos por cada entidad principal**.

####  

#### **4. Contenido del Archivo README.md**

El repositorio debe incluir un archivo README.md completo y profesional, que contenga como mínimo las siguientes secciones:

- **Título del Proyecto**.
- **Descripción del Proyecto**.
- **Tecnologías Utilizadas**.
- **Diseño de la Base de Datos:** Breve explicación del modelo relacional, incluyendo el diagrama ERD.
- **Instalación y Ejecución:** Instrucciones detalladas sobre cómo un usuario puede descargar, configurar y ejecutar el proyecto. Esto debe incluir los pasos para configurar la base de datos en la nube y ejecutar los scripts SQL.
- **Guía de Uso:** Una sección que explique cómo operar el sistema desde la terminal o que dirija a un enlace con la documentación funcional completa (ej. un video, una wiki en el repositorio, etc.).
- **Autores**.

####  

#### **5. Control de Versiones (Git y GitHub)**

Se evaluará el uso de buenas prácticas en el control de versiones. Los requisitos son:

- **Número de Commits:** Se requiere un mínimo de **10 commits significativos**.
- **Estrategia de Ramas:** Se debe trabajar en al menos **dos ramas** adicionales (ej. develop, feature/nombre-funcionalidad). La rama main debe mantenerse limpia y solo debe recibir código a través de fusiones (*merges*). **No se deben realizar commits directos a main**.
- **Calidad de los Commits:** Los mensajes deben estar en inglés y seguir un formato descriptivo y consistente.