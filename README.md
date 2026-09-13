# FundamentosProgramacionPoli

Proyecto del modulo **Conceptos Fundamentales de Programacion** - Politécnico Grancolombiano.

## Entrega 1 (Semana 3)

Generacion de archivos planos de prueba mediante la clase `GenerateInfoFiles.java`:
- `createProductsFile(int productsCount)`: Catalogo de productos (`products.txt`).
- `createSalesManInfoFile(int salesmanCount)`: Informacion de vendedores (`salesmen_info.txt`).
- `createSalesMenFile(int randomSalesCount, String name, long id)`: Archivo de ventas por vendedor (`salesman_<id>.txt`).

### Compilacion y Ejecucion (Java 8)
```bash
cd Semana3
javac GenerateInfoFiles.java
java GenerateInfoFiles
```
