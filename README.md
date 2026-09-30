# FundamentosProgramacionPoli

Proyecto del módulo **Conceptos Fundamentales de Programación** - Politécnico Grancolombiano.

### Integrantes del Grupo
* **Isaac Diaz Perez**
* **Julio Cesar Alvarado Ávila**

## Estructura del Repositorio

```text
FundamentosProgramacionPoli/
├── Semana3/
│   └── GenerateInfoFiles.java   # Entrega 1: Generador de archivos planos de prueba
├── Semana5/
│   ├── GenerateInfoFiles.java   # Generador de archivos de prueba autónomo
│   └── Main.java                # Entrega 2: Procesador de reportes y cálculo de ventas
├── PENDIENTES_ENTREGA2.md       # Documento adjunto obligatorio de partes faltantes
├── .gitignore                   # Exclusión de binarios y archivos generados
└── README.md                    # Documentación general del proyecto
```

---

## Entrega 1 (Semana 3)

Generación de archivos planos pseudoaleatorios y coherentes para alimentar el sistema:
* `createProductsFile(int productsCount)`: Catálogo de productos (`products.txt`).
* `createSalesManInfoFile(int salesmanCount)`: Información de vendedores (`salesmen_info.txt`).
* `createSalesMenFile(int randomSalesCount, String name, long id)`: Archivo de ventas por vendedor (`salesman_<id>.txt`).

### Compilación y Ejecución (Semana 3)
```bash
cd Semana3
javac GenerateInfoFiles.java
java GenerateInfoFiles
```

---

## Entrega 2 (Semana 5)

Versión preliminar completa del proyecto con procesamiento y consolidación de reportes de ventas:
* **`GenerateInfoFiles.java`**: Genera los archivos de insumo de prueba directamente en el directorio de trabajo.
* **`Main.java`**: Lee los archivos de entrada, procesa las ventas y genera los reportes solicitados:
  * `reporte_vendedores.txt`: Vendedores ordenados de mayor a menor según el total de dinero recaudado. Formato: `NombreCompleto;DineroRecaudado`.
  * `reporte_productos.txt`: Productos ordenados de mayor a menor cantidad vendida. Formato: `NombreProducto;PrecioPorUnidad`.
* **Elementos extra implementados**:
  * Soporte para múltiples archivos de venta por vendedor.
  * Serialización binaria de los objetos de reporte en `reporte.ser`.
  * Detección y registro de inconsistencias o datos con formato erróneo en `reporte_errores.txt` sin detener el flujo del programa.

### Compilación y Ejecución (Semana 5)
```bash
cd Semana5
javac GenerateInfoFiles.java Main.java
java GenerateInfoFiles
java Main
```

---

## Documento de Pendientes (Entrega 2)

En cumplimiento con la directriz del enunciado oficial:
> *"Debe tener un documento adjunto indicando qué partes le faltan al proyecto"*

Se incluye el archivo [PENDIENTES_ENTREGA2.md](PENDIENTES_ENTREGA2.md), en el cual se detallan los entregables pendientes programados para la Entrega 3 y sustentación final (Semanas 7 y 8):
1. Redacción del archivo `conslusion.txt` con los tres puntos de reflexión solicitados.
2. Pruebas de estrés y límites con grandes volúmenes de archivos de ventas.
3. Preparación de la sustentación oral y revisión final de empaquetado.

---

## Buenas Prácticas y Requisitos Técnicos

* **Nomenclatura estándar en Java**: Clases en UpperCamelCase (`GenerateInfoFiles`, `Main`).
* **Código limpio y minimalista**: Implementado en Java SE estándar (compatible con Java 8+), sin dependencias ni librerías externas.
* **Sin paquetes**: Uso del paquete por defecto (default package) para facilitar la ejecución directa en consola o importación en Eclipse.
* **Internacionalización de código**: Clases, métodos, variables, comentarios concisos y mensajes de consola desarrollados en inglés en la Entrega 2.
