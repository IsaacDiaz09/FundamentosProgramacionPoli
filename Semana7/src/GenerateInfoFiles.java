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
 * Test data files generator for the course project.
 * Generates products catalog, salesmen info, and random sales files.
 *
 * All files are written to the working directory, which is where the
 * {@code main} class looks for them.
 *
 * Usage (from the project folder): {@code java GenerateInfoFiles}
 *
 * @author Isaac Diaz Perez &lt;idiazppe@poligran.edu.co&gt;
 * @author Julio Cesar Alvarado Avila
 */
public class GenerateInfoFiles {

    /** Shared pseudo-random generator for every generated value. */
    private static final Random RANDOM = new Random();

    /** Output file with the products catalog. */
    private static final String PRODUCTS_FILE_NAME = "products.txt";
    /** Output file with the salesmen info. */
    private static final String SALESMEN_INFO_FILE_NAME = "salesmen_info.txt";
    /** Prefix of each sales file; the salesman document number follows it. */
    private static final String SALES_FILE_PREFIX = "salesman_";
    /** Extension of every generated file. */
    private static final String FILE_EXTENSION = ".txt";

    /** Real first names used to build coherent salesmen. */
    private static final String[] FIRST_NAMES = {
        "Carlos", "Maria", "Juan", "Ana", "Andres", "Laura", "Pedro",
        "Sofia", "Diego", "Valentina", "Mateo", "Camila", "Luis", "Daniela", "Jose"
    };

    /** Real last names used to build coherent salesmen. */
    private static final String[] LAST_NAMES = {
        "Gomez", "Rodriguez", "Perez", "Lopez", "Martinez", "Garcia",
        "Hernandez", "Sanchez", "Ramirez", "Torres", "Vargas", "Castro", "Rojas", "Morales"
    };

    /** Colombian document types: citizenship card, foreigner card, identity card. */
    private static final String[] DOCUMENT_TYPES = {
        "CC", "CE", "TI"
    };

    /** Names for the first products; later products get a generic name. */
    private static final String[] PRODUCT_NAMES = {
        "Laptop 15 Inch",
        "Wireless Ergonomic Mouse",
        "Mechanical Keyboard RGB",
        "LED Monitor 24 Inch",
        "Bluetooth Wireless Headphones",
        "External Hard Drive 1TB",
        "USB Flash Drive 64GB",
        "HD Webcam 1080p",
        "Multifunction Inkjet Printer",
        "Ergonomic Office Chair"
    };

    /** Lowest possible unit price (COP). */
    private static final double MIN_PRODUCT_PRICE = 20000.0;
    /** Highest possible unit price (COP). */
    private static final double MAX_PRODUCT_PRICE = 3500000.0;

    /** IDs written by the last call to {@link #createProductsFile(int)}. */
    private static final List<Long> registeredProductIds = new ArrayList<Long>();
    /** Salesmen written by the last call to {@link #createSalesManInfoFile(int)}. */
    private static final List<SalesmanRecord> registeredSalesmen = new ArrayList<SalesmanRecord>();

    /** A generated salesman: document type, document number and full name. */
    private static class SalesmanRecord {
        /** Document type (CC, CE or TI). */
        private final String documentType;
        /** Document number. */
        private final long idNumber;
        /** First and last names. */
        private final String fullName;

        /**
         * @param documentType document type (CC, CE or TI)
         * @param idNumber     document number
         * @param fullName     first and last names
         */
        public SalesmanRecord(String documentType, long idNumber, String fullName) {
            this.documentType = documentType;
            this.idNumber = idNumber;
            this.fullName = fullName;
        }

        /** @return document type */
        public String getDocumentType() {
            return documentType;
        }

        /** @return document number */
        public long getIdNumber() {
            return idNumber;
        }

        /** @return first and last names */
        public String getFullName() {
            return fullName;
        }
    }

    /**
     * Creates a pseudo-random products catalog file.
     * Line format: IDProducto;NombreProducto;PrecioPorUnidad
     *
     * @param productsCount number of products to generate (greater than zero)
     * @throws IOException              if the file cannot be written
     * @throws IllegalArgumentException if {@code productsCount} is not positive
     */
    public static void createProductsFile(int productsCount) throws IOException {
        if (productsCount <= 0) {
            throw new IllegalArgumentException("Product count must be greater than zero.");
        }

        registeredProductIds.clear();
        File file = new File(PRODUCTS_FILE_NAME);

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
        try {
            long baseId = 101L;
            for (int i = 0; i < productsCount; i++) {
                long productId = baseId + i;
                registeredProductIds.add(productId);

                String productName = (i < PRODUCT_NAMES.length) ? PRODUCT_NAMES[i] : "Tech Product " + (i + 1);
                double rawPrice = MIN_PRODUCT_PRICE + (RANDOM.nextDouble() * (MAX_PRODUCT_PRICE - MIN_PRODUCT_PRICE));
                long unitPrice = Math.round(rawPrice / 1000.0) * 1000L;

                writer.write(productId + ";" + productName + ";" + unitPrice);
                writer.newLine();
            }
        } finally {
            writer.close();
        }
    }

    /**
     * Creates a file with pseudo-random salesmen info.
     * Line format: TipoDocumento;NumeroDocumento;Nombres;Apellidos
     *
     * @param salesmanCount number of salesmen to generate (greater than zero)
     * @throws IOException              if the file cannot be written
     * @throws IllegalArgumentException if {@code salesmanCount} is not positive
     */
    public static void createSalesManInfoFile(int salesmanCount) throws IOException {
        if (salesmanCount <= 0) {
            throw new IllegalArgumentException("Salesman count must be greater than zero.");
        }

        registeredSalesmen.clear();
        File file = new File(SALESMEN_INFO_FILE_NAME);

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
        try {
            long baseDoc = 1001001L;
            for (int i = 0; i < salesmanCount; i++) {
                String docType = DOCUMENT_TYPES[RANDOM.nextInt(DOCUMENT_TYPES.length)];
                long docNumber = baseDoc + i;
                String firstName = FIRST_NAMES[RANDOM.nextInt(FIRST_NAMES.length)];
                String lastName = LAST_NAMES[RANDOM.nextInt(LAST_NAMES.length)] + " " + LAST_NAMES[RANDOM.nextInt(LAST_NAMES.length)];

                registeredSalesmen.add(new SalesmanRecord(docType, docNumber, firstName + " " + lastName));

                writer.write(docType + ";" + docNumber + ";" + firstName + ";" + lastName);
                writer.newLine();
            }
        } finally {
            writer.close();
        }
    }

    /**
     * Creates a sales file for a specific salesman.
     * Line 1: TipoDocumento;NumeroDocumento
     * Subsequent lines: IDProducto;Cantidad;
     *
     * Product IDs are taken from the last generated catalog, so every sale
     * refers to an existing product.
     *
     * @param randomSalesCount number of sales lines to generate (zero or more)
     * @param name             salesman full name; the required sales file
     *                         format has no name field, so it is only shown
     *                         in the console message
     * @param id               salesman document number, also used in the file name
     * @throws IOException              if the file cannot be written
     * @throws IllegalArgumentException if {@code randomSalesCount} is negative
     */
    public static void createSalesMenFile(int randomSalesCount, String name, long id) throws IOException {
        if (randomSalesCount < 0) {
            throw new IllegalArgumentException("Sales count cannot be negative.");
        }

        String docType = "CC";
        for (SalesmanRecord record : registeredSalesmen) {
            if (record.getIdNumber() == id) {
                docType = record.getDocumentType();
                break;
            }
        }

        File file = new File(SALES_FILE_PREFIX + id + FILE_EXTENSION);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
        try {
            writer.write(docType + ";" + id);
            writer.newLine();

            List<Long> availableIds = registeredProductIds.isEmpty() ? getDefaultProductIds() : registeredProductIds;
            for (int i = 0; i < randomSalesCount; i++) {
                long productId = availableIds.get(RANDOM.nextInt(availableIds.size()));
                int quantity = 1 + RANDOM.nextInt(15);
                writer.write(productId + ";" + quantity + ";");
                writer.newLine();
            }
        } finally {
            writer.close();
        }

        System.out.println("  * " + file.getName() + " - " + name + " (" + randomSalesCount + " sales)");
    }

    /**
     * Product IDs used when no catalog has been generated in this run
     * (the same IDs {@link #createProductsFile(int)} gives to 10 products).
     *
     * @return IDs 101 to 110
     */
    private static List<Long> getDefaultProductIds() {
        List<Long> list = new ArrayList<Long>();
        for (int i = 1; i <= 10; i++) {
            list.add(100L + i);
        }
        return list;
    }

    /**
     * Entry point. Generates the products catalog, the salesmen info and one
     * sales file per salesman, then prints a success message, or an error
     * message if something goes wrong. It never asks the user for input.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            System.out.println("Generating test files...");

            int productsCount = 10;
            int salesmanCount = 5;

            createProductsFile(productsCount);
            System.out.println("- " + PRODUCTS_FILE_NAME + " (" + productsCount + " products)");

            createSalesManInfoFile(salesmanCount);
            System.out.println("- " + SALESMEN_INFO_FILE_NAME + " (" + salesmanCount + " salesmen)");

            for (SalesmanRecord salesman : registeredSalesmen) {
                int salesCount = 5 + RANDOM.nextInt(8);
                createSalesMenFile(salesCount, salesman.getFullName(), salesman.getIdNumber());
            }

            System.out.println("\nProcess completed successfully.");

        } catch (Exception e) {
            System.err.println("Error generating files: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
