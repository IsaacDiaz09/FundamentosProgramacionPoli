import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase principal encargada de generar los archivos planos de prueba
 * para el proyecto del modulo Conceptos Fundamentales de Programacion.
 * 
 * Cumple con los requisitos establecidos para la Entrega 1 (Semana 3):
 * 1. Generacion de archivo de informacion de productos (createProductsFile).
 * 2. Generacion de archivo de informacion de vendedores (createSalesManInfoFile).
 * 3. Generacion de archivo de ventas por cada vendedor (createSalesMenFile).
 * 
 *
 * @author Isaac Diaz Perez &lt;idiazppe@poligran.edu.co&gt;
 */
public class GenerateInfoFiles {

    /**
     * Objeto Random compartido para la generacion de datos pseudoaleatorios.
     */
    private static final Random RANDOM = new Random();

    /**
     * Nombre por defecto del archivo de informacion de productos.
     */
    private static final String PRODUCTS_FILE_NAME = "products.txt";

    /**
     * Nombre por defecto del archivo de informacion de vendedores.
     */
    private static final String SALESMEN_INFO_FILE_NAME = "salesmen_info.txt";

    /**
     * Prefijo para los archivos individuales de ventas de cada vendedor.
     */
    private static final String SALES_FILE_PREFIX = "salesman_";

    /**
     * Extension de los archivos generados.
     */
    private static final String FILE_EXTENSION = ".txt";

    /**
     * Lista de nombres reales para generar vendedores de manera coherente.
     */
    private static final String[] FIRST_NAMES = {
        "Carlos", "Maria", "Juan", "Ana", "Andres", "Laura", "Pedro", 
        "Sofia", "Diego", "Valentina", "Mateo", "Camila", "Luis", "Daniela", "Jose"
    };

    /**
     * Lista de apellidos reales para generar vendedores de manera coherente.
     */
    private static final String[] LAST_NAMES = {
        "Gomez", "Rodriguez", "Perez", "Lopez", "Martinez", "Garcia", 
        "Hernandez", "Sanchez", "Ramirez", "Torres", "Vargas", "Castro", "Rojas", "Morales"
    };

    /**
     * Tipos de documento permitidos en Colombia segun los requerimientos del proyecto.
     */
    private static final String[] DOCUMENT_TYPES = {
        "CC", "CE", "TI"
    };

    /**
     * Catalogo base de nombres de productos tecnologicos para generar informacion realista.
     */
    private static final String[] PRODUCT_NAMES = {
        "Laptop Portatil 15 Pulgadas",
        "Mouse Ergonomico Inalambrico",
        "Teclado Mecanico RGB",
        "Monitor LED 24 Pulgadas",
        "Auriculares Inalambricos Bluetooth",
        "Disco Duro Externo 1TB",
        "Memoria USB 64GB 3.0",
        "Camara Web HD 1080p",
        "Impresora Multifuncional Tinta Continua",
        "Silla Ergonomica de Oficina",
        "Base Refrigerante para Portatil",
        "Parlante Bluetooth Portatil",
        "Tablet 10 Pulgadas",
        "Router Inalambrico Doble Banda",
        "Cable HDMI Alta Velocidad 2m"
    };

    /**
     * Rango de precios base en pesos colombianos para los productos generados.
     */
    private static final double MIN_PRODUCT_PRICE = 20000.0;
    private static final double MAX_PRODUCT_PRICE = 3500000.0;

    /**
     * Estructura interna auxiliar para almacenar los datos generados de un vendedor
     * y poder generar posteriormente su respectivo archivo de ventas.
     */
    private static class SalesmanRecord {
        private final String documentType;
        private final long idNumber;
        private final String firstName;
        private final String lastName;

        public SalesmanRecord(String documentType, long idNumber, String firstName, String lastName) {
            this.documentType = documentType;
            this.idNumber = idNumber;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getDocumentType() {
            return documentType;
        }

        public long getIdNumber() {
            return idNumber;
        }

        public String getFullName() {
            return firstName + " " + lastName;
        }
    }

    /**
     * Almacen temporal de los IDs de productos generados en la ejecucion actual.
     * Garantiza que los archivos de ventas referencien unicamente productos existentes.
     */
    private static final List<Long> registeredProductIds = new ArrayList<>();

    /**
     * Almacen temporal de los vendedores generados en la ejecucion actual.
     */
    private static final List<SalesmanRecord> registeredSalesmen = new ArrayList<>();

    /**
     * Crea un archivo pseudoaleatorio con la informacion de los productos disponibles.
     * Cada linea representa un producto con el formato:
     * IDProducto;NombreProducto;PrecioPorUnidadProducto
     *
     * @param productsCount Cantidad de productos a generar en el archivo.
     * @throws IOException Si ocurre un error de escritura al crear el archivo.
     */
    public static void createProductsFile(int productsCount) throws IOException {
        if (productsCount <= 0) {
            throw new IllegalArgumentException("La cantidad de productos debe ser mayor a cero.");
        }

        registeredProductIds.clear();
        File file = new File(PRODUCTS_FILE_NAME);

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            long baseId = 101L; // ID inicial para los productos
            for (int i = 0; i < productsCount; i++) {
                long productId = baseId + i;
                registeredProductIds.add(productId);

                // Seleccionar nombre del producto ciclica o aleatoriamente
                String productName = (i < PRODUCT_NAMES.length) 
                        ? PRODUCT_NAMES[i] 
                        : "Producto Tecnologico " + (i + 1);

                // Generar precio unitario escalonado de forma coherente (redondeado a miles)
                double rawPrice = MIN_PRODUCT_PRICE + (RANDOM.nextDouble() * (MAX_PRODUCT_PRICE - MIN_PRODUCT_PRICE));
                long unitPrice = Math.round(rawPrice / 1000.0) * 1000L;

                // Formato: IDProducto;NombreProducto;PrecioPorUnidadProducto
                writer.write(productId + ";" + productName + ";" + unitPrice);
                writer.newLine();
            }
        }
    }

    /**
     * Crea un archivo con informacion pseudoaleatoria y coherente de vendedores.
     * Cada linea contiene los datos de un vendedor en el formato:
     * TipoDocumento;NumeroDocumento;NombresVendedor;ApellidosVendedor
     *
     * @param salesmanCount Cantidad de vendedores a registrar en el archivo.
     * @throws IOException Si ocurre un error de escritura al crear el archivo.
     */
    public static void createSalesManInfoFile(int salesmanCount) throws IOException {
        if (salesmanCount <= 0) {
            throw new IllegalArgumentException("La cantidad de vendedores debe ser mayor a cero.");
        }

        registeredSalesmen.clear();
        File file = new File(SALESMEN_INFO_FILE_NAME);

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            long baseDocumentNumber = 1001001L;

            for (int i = 0; i < salesmanCount; i++) {
                String documentType = DOCUMENT_TYPES[RANDOM.nextInt(DOCUMENT_TYPES.length)];
                long idNumber = baseDocumentNumber + i;

                // Seleccion de nombres y apellidos de listas de nombres reales
                String firstName = FIRST_NAMES[RANDOM.nextInt(FIRST_NAMES.length)];
                String lastName = LAST_NAMES[RANDOM.nextInt(LAST_NAMES.length)] 
                        + " " + LAST_NAMES[RANDOM.nextInt(LAST_NAMES.length)];

                SalesmanRecord salesman = new SalesmanRecord(documentType, idNumber, firstName, lastName);
                registeredSalesmen.add(salesman);

                // Formato: TipoDocumento;NumeroDocumento;NombresVendedor;ApellidosVendedor
                writer.write(documentType + ";" + idNumber + ";" + firstName + ";" + lastName);
                writer.newLine();
            }
        }
    }

    /**
     * Crea un archivo de ventas pseudoaleatorio para un vendedor especifico.
     * Formato del archivo segun especificaciones de la entrega:
     * Linea 1: TipoDocumentoVendedor;NumeroDocumentoVendedor
     * Lineas siguientes (una venta por linea con punto y coma final):
     * IDProducto;CantidadProductoVendido;
     *
     * @param randomSalesCount Cantidad de registros de venta a generar para este vendedor.
     * @param name             Nombre del vendedor.
     * @param id               Numero de identificacion del vendedor.
     * @throws IOException Si ocurre un error de escritura al crear el archivo.
     */
    public static void createSalesMenFile(int randomSalesCount, String name, long id) throws IOException {
        if (randomSalesCount < 0) {
            throw new IllegalArgumentException("La cantidad de ventas no puede ser negativa.");
        }

        // Buscar tipo de documento asociado al vendedor si ya fue registrado previamente
        String documentType = "CC"; // Por defecto Cedula de Ciudadania
        for (SalesmanRecord record : registeredSalesmen) {
            if (record.getIdNumber() == id) {
                documentType = record.getDocumentType();
                break;
            }
        }

        // Definicion del nombre del archivo en la misma carpeta del proyecto
        String fileName = SALES_FILE_PREFIX + id + FILE_EXTENSION;
        File file = new File(fileName);

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            // Linea 1: TipoDocumentoVendedor;NumeroDocumentoVendedor
            writer.write(documentType + ";" + id);
            writer.newLine();

            // Si aun no se han cargado productos en memoria, definir IDs por defecto
            List<Long> availableProductIds = registeredProductIds.isEmpty()
                    ? getDefaultProductIds()
                    : registeredProductIds;

            // Generar las ventas de este vendedor
            for (int i = 0; i < randomSalesCount; i++) {
                int randomProductIndex = RANDOM.nextInt(availableProductIds.size());
                long selectedProductId = availableProductIds.get(randomProductIndex);

                // Cantidad vendida entre 1 y 15 unidades
                int quantitySold = 1 + RANDOM.nextInt(15);

                // Formato: IDProducto;CantidadProductoVendido;
                writer.write(selectedProductId + ";" + quantitySold + ";");
                writer.newLine();
            }
        }
    }

    /**
     * Retorna una lista de IDs de productos predeterminados en caso de invocar
     * createSalesMenFile de forma independiente sin ejecutar previamente createProductsFile.
     *
     * @return Lista con IDs de productos predeterminados.
     */
    private static List<Long> getDefaultProductIds() {
        List<Long> defaultList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            defaultList.add(100L + i);
        }
        return defaultList;
    }

    /**
     * Metodo principal que ejecuta la generacion completa de los archivos planos de prueba.
     * Requisito fundamental: no solicita informacion al usuario por consola.
     * Muestra un mensaje de finalizacion exitosa o un mensaje de error si ocurre un fallo.
     *
     * @param args Argumentos de la linea de comandos (no requeridos).
     */
    public static void main(String[] args) {
        try {
            System.out.println("Iniciando la generacion de archivos de prueba (Semana 3 - Entrega 1)...");

            // Cantidades de prueba representativas
            int productsCount = 10;
            int salesmanCount = 5;

            // 1. Generar archivo de informacion de productos (products.txt)
            createProductsFile(productsCount);
            System.out.println("- Archivo de productos creado: " + PRODUCTS_FILE_NAME 
                    + " (" + productsCount + " productos)");

            // 2. Generar archivo de informacion de vendedores (salesmen_info.txt)
            createSalesManInfoFile(salesmanCount);
            System.out.println("- Archivo de informacion de vendedores creado: " + SALESMEN_INFO_FILE_NAME 
                    + " (" + salesmanCount + " vendedores)");

            // 3. Generar archivo de ventas para cada vendedor generado
            for (SalesmanRecord salesman : registeredSalesmen) {
                // Cantidad de ventas pseudoaleatoria por vendedor entre 5 y 12 ventas
                int salesCount = 5 + RANDOM.nextInt(8);
                createSalesMenFile(salesCount, salesman.getFullName(), salesman.getIdNumber());
                System.out.println("  * Archivo de ventas generado: " 
                        + SALES_FILE_PREFIX + salesman.getIdNumber() + FILE_EXTENSION
                        + " para " + salesman.getFullName() 
                        + " (" + salesCount + " ventas registradas)");
            }

            // Mensaje de exito requerido por las instrucciones de la entrega
            System.out.println("\nProceso finalizado exitosamente. Todos los archivos de prueba fueron creados.");

        } catch (Exception e) {
            // Mensaje de error en caso de que algo salga mal, segun lo solicitado en las instrucciones
            System.err.println("Error durante la generacion de archivos de prueba: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
