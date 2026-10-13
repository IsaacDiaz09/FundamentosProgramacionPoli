# FundamentosProgramacionPoli

Proyecto del módulo **Conceptos Fundamentales de Programación** - Politécnico Grancolombiano.
Entrega: *Generación y clasificación de datos*.

### Integrantes del Grupo
* **Isaac Diaz Perez**
* **Julio Cesar Alvarado Ávila**

## Estructura del Repositorio

```text
FundamentosProgramacionPoli/
├── Semana3/
│   └── GenerateInfoFiles.java   # Entrega 1 (version historica, sin metodo main ejecutable)
├── Semana5/
│   ├── GenerateInfoFiles.java   # Entrega 2 (version historica, sin metodo main ejecutable)
│   └── Main.java                # Entrega 2 (version historica, sin metodo main ejecutable)
├── Semana7/                     # Entrega 3: proyecto Eclipse final (Java 8)
│   ├── .project                 # Descriptor del proyecto Eclipse
│   ├── .classpath               # Carpeta src + JRE JavaSE-1.8
│   ├── .settings/               # Compilador en nivel 1.8 y codificacion UTF-8
│   └── src/
│       ├── GenerateInfoFiles.java   # Clase main 1: genera los archivos de prueba
│       └── main.java                # Clase main 2: genera los reportes
├── PENDIENTES_ENTREGA2.md       # Documento de partes faltantes (Entrega 2)
├── conslusion.txt               # Conclusiones del proyecto (Entrega 3)
├── .gitignore                   # Exclusion de binarios y archivos generados
└── README.md                    # Documentacion general del proyecto
```

> **Dos (y solo dos) clases con metodo `main`**: `Semana7/src/GenerateInfoFiles.java`
> y `Semana7/src/main.java`. Las carpetas `Semana3/` y `Semana5/` se conservan
> como evidencia de las Entregas 1 y 2, pero sus metodos `main` se renombraron a
> `demoEntrega1` y `demoEntrega2`. Solo `Semana7/` forma parte del proyecto Eclipse.

---

## Entrega 3 (Semanas 7 y 8) - Entrega final

### Importar en Eclipse
1. *File > Import > General > Existing Projects into Workspace*.
2. Seleccionar la carpeta `Semana7` (proyecto `FundamentosProgramacionPoli`).
3. Ejecutar `GenerateInfoFiles` (*Run As > Java Application*) y luego `main`.

Eclipse ejecuta ambas clases con la carpeta del proyecto (`Semana7/`) como
directorio de trabajo, por lo que los archivos generados y los reportes quedan
en la misma carpeta del proyecto, como exige el enunciado.

### Compilación y ejecución por consola
```bash
cd Semana7
javac --release 8 -d bin src/*.java
java -cp bin GenerateInfoFiles
java -cp bin main
```

Ambas clases deben ejecutarse en ese orden: `main` necesita que
`GenerateInfoFiles` haya generado previamente los archivos de entrada.
Ninguno de los dos programas solicita información al usuario; ambos muestran
un mensaje de finalización exitosa o un mensaje de error.

### Archivos de entrada (generados por `GenerateInfoFiles`)
| Archivo | Formato |
|---|---|
| `products.txt` | `IDProducto;NombreProducto;PrecioPorUnidad` |
| `salesmen_info.txt` | `TipoDocumento;NumeroDocumento;Nombres;Apellidos` |
| `salesman_<id>.txt` | Línea 1: `TipoDocumento;NumeroDocumento`, luego `IDProducto;Cantidad;` |

Métodos de generación solicitados (punto 5 del enunciado):
* `createProductsFile(int productsCount)`
* `createSalesManInfoFile(int salesmanCount)`
* `createSalesMenFile(int randomSalesCount, String name, long id)`

### Archivos de salida (generados por `main`)
| Archivo | Contenido |
|---|---|
| `reporte_vendedores.txt` | `NombreCompleto;DineroRecaudado`, de mayor a menor (punto 3) |
| `reporte_productos.txt` | `NombreProducto;PrecioPorUnidad` de los productos vendidos, de mayor a menor cantidad vendida (punto 4) |
| `reporte.ser` | Ambos reportes serializados (extra b) |
| `reporte_errores.txt` | Líneas inválidas o incoherentes encontradas (extra c); solo se crea si hay alguna |

### Elementos extra implementados
* **a.** Más de un archivo por vendedor: se procesa todo archivo `salesman_*.txt`
  (por ejemplo `salesman_1001001_2.txt`) y las ventas se suman por número de documento.
* **b.** Archivos serializados: los reportes se guardan también en `reporte.ser`.
* **c.** Detección de datos erróneos: formato inválido, valores no numéricos,
  IDs de producto inexistentes, cantidades o precios negativos. Se registran en
  `reporte_errores.txt` sin detener el programa.

### Conclusiones
El archivo [conslusion.txt](conslusion.txt) contiene, por cada integrante, lo
aprendido, las aplicaciones en la vida profesional y las dificultades del proyecto.

---

## Entrega 2 (Semana 5)

Versión preliminar del proyecto completo, con el documento de partes faltantes
[PENDIENTES_ENTREGA2.md](PENDIENTES_ENTREGA2.md). Sus archivos se conservan en
`Semana5/` y evolucionaron en `Semana7/src/` para la Entrega 3.

## Entrega 1 (Semana 3)

Diseño e implementación de la clase `GenerateInfoFiles` con los métodos de
generación de archivos planos pseudoaleatorios (`Semana3/`).

---

## Buenas Prácticas y Requisitos Técnicos

* **Java 8 y Eclipse**: el proyecto está configurado con nivel de compilación 1.8 y se compila con `--release 8`.
* **Nomenclatura estándar en Java**: Clases en UpperCamelCase (`GenerateInfoFiles`), salvo `main`, que se nombra en minúscula de forma deliberada porque el enunciado del proyecto lo exige textualmente: *"La segunda clase debe llamarse main"*.
* **Documentación Javadoc** en clases, constantes y métodos (`@param`, `@return`, `@throws`).
* **Sin dependencias externas**: solo Java SE estándar.
* **Sin paquetes**: paquete por defecto, para ejecutar directamente en consola o Eclipse.
* **Código en inglés**: clases, métodos, variables, comentarios y mensajes de consola.
