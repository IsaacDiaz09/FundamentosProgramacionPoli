# Partes que le Faltan al Proyecto (Entrega 2 - Semana 5)

Documento adjunto obligatorio requerido por el enunciado oficial del módulo:
> *"Esta entrega consiste en una versión preliminar del proyecto completo, con posibles elementos faltantes que pueden depender de las dinámicas de evolución de la solución en cada grupo de trabajo. Debe tener un documento adjunto indicando qué partes le faltan al proyecto."*

---

## 1. Estado Actual del Proyecto (Elementos Completados)

Hasta la presente Entrega 2 (Semana 5), se han implementado y verificado satisfactoriamente los siguientes componentes:

* **Generación de Archivos de Prueba (`GenerateInfoFiles.java`)**:
  * Creación del catálogo de productos con identificador, nombre y precio unitario (`products.txt`).
  * Creación del registro de vendedores con tipo y número de documento, nombres y apellidos reales (`salesmen_info.txt`).
  * Creación de archivos individuales de ventas por vendedor (`salesman_<id>.txt`).

* **Procesamiento de Datos y Generación de Reportes (`main.java`)**:
  * Lectura y validación de los catálogos de productos y vendedores.
  * Consolidación y procesamiento de todos los archivos de ventas disponibles en la carpeta de trabajo.
  * Generación del reporte de vendedores ordenado de mayor a menor dinero recaudado (`reporte_vendedores.txt`).
  * Generación del reporte de productos ordenado de mayor a menor cantidad de unidades vendidas (`reporte_productos.txt`).

* **Criterios Adicionales (Extras) Implementados**:
  * Capacidad de procesar más de un archivo de ventas por cada vendedor.
  * Generación de archivo serializado binario (`reporte.ser`).
  * Detección y registro de archivos con formato erróneo o información incoherente (cantidades negativas, productos no existentes, etc.) en `reporte_errores.txt` sin interrumpir la ejecución del programa.

---

## 2. Partes Faltantes del Proyecto para la Entrega 3 y Sustentación (Semanas 7 y 8)

Para dar cumplimiento total a la fase final del proyecto grupal (Entrega 3 y Sustentación), se encuentran pendientes los siguientes puntos:

1. **Elaboración del archivo `conslusion.txt`**:
   * Creación del archivo de texto plano con el nombre exacto especificado en la guía (`conslusion.txt`), el cual debe contener el resumen de:
     * Lo aprendido durante el desarrollo del proyecto.
     * Posibles aplicaciones en la vida profesional de las destrezas y conocimientos adquiridos y practicados.
     * Las dificultades presentadas durante el desarrollo del proyecto.

2. **Batería de Pruebas de Estrés y Casos Borde**:
   * Pruebas de rendimiento con grandes volúmenes de datos (más de 100 archivos de ventas simultáneos).
   * Verificación del comportamiento frente a diferentes codificaciones de caracteres (UTF-8, ISO-8859-1) y saltos de línea (CRLF de Windows frente a LF de Linux).

3. **Preparación para la Sustentación Oral y Empaquetado Final**:
   * Verificación final de portabilidad e importación directa en el entorno de desarrollo Eclipse para Java Developers con Java 8.
   * Preparación de la sustentación técnica individual/grupal, explicando las decisiones de diseño, estructuras de datos empleadas y complejidad algorítmica de los ordenamientos.

---

## 3. Estado en la Entrega 3 (Semana 7)

| Pendiente | Estado |
|---|---|
| Archivo `conslusion.txt` | ✅ Completado por ambos integrantes |
| Proyecto Eclipse con Java 8 | ✅ `Semana7/` con `.project`, `.classpath` y nivel de compilación 1.8 |
| Documentación Javadoc completa | ✅ Clases, constantes y métodos documentados |
| Pruebas de casos borde | ✅ Probado con archivos vacíos, IDs inexistentes, cantidades y precios negativos, valores no numéricos y varios archivos por vendedor |
| Pruebas de estrés con grandes volúmenes | ⏳ Opcional, no exigido por el enunciado |
| Sustentación oral | ⏳ Semana 8 |
