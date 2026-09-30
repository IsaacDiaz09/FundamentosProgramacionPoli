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
 * @author Isaac Diaz Perez <idiazppe@poligran.edu.co>
 */
public class GenerateInfoFiles {

    private static final Random RANDOM = new Random();

    private static final String PRODUCTS_FILE_NAME = "products.txt";
    private static final String SALESMEN_INFO_FILE_NAME = "salesmen_info.txt";
    private static final String SALES_FILE_PREFIX = "salesman_";
    private static final String FILE_EXTENSION = ".txt";

    private static final String[] FIRST_NAMES = {
        "Carlos", "Maria", "Juan", "Ana", "Andres", "Laura", "Pedro", 
        "Sofia", "Diego", "Valentina", "Mateo", "Camila", "Luis", "Daniela", "Jose"
    };

    private static final String[] LAST_NAMES = {
        "Gomez", "Rodriguez", "Perez", "Lopez", "Martinez", "Garcia", 
        "Hernandez", "Sanchez", "Ramirez", "Torres", "Vargas", "Castro", "Rojas", "Morales"
    };

    private static final String[] DOCUMENT_TYPES = {
        "CC", "CE", "TI"
    };

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

    private static final double MIN_PRODUCT_PRICE = 20000.0;
    private static final double MAX_PRODUCT_PRICE = 3500000.0;

    private static final List<Long> registeredProductIds = new ArrayList<Long>();
    private static final List<SalesmanRecord> registeredSalesmen = new ArrayList<SalesmanRecord>();

    private static class SalesmanRecord {
        private final String documentType;
        private final long idNumber;
        private final String fullName;

        public SalesmanRecord(String documentType, long idNumber, String fullName) {
            this.documentType = documentType;
            this.idNumber = idNumber;
            this.fullName = fullName;
        }

        public String getDocumentType() {
            return documentType;
        }

        public long getIdNumber() {
            return idNumber;
        }

        public String getFullName() {
            return fullName;
        }
    }

    /**
     * Creates a pseudo-random products catalog file.
     * Line format: IDProducto;NombreProducto;PrecioPorUnidad
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
    }

    private static List<Long> getDefaultProductIds() {
        List<Long> list = new ArrayList<Long>();
        for (int i = 1; i <= 10; i++) {
            list.add(100L + i);
        }
        return list;
    }

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
                System.out.println("  * " + SALES_FILE_PREFIX + salesman.getIdNumber() + FILE_EXTENSION + " (" + salesCount + " sales)");
            }

            System.out.println("\nProcess completed successfully.");

        } catch (Exception e) {
            System.err.println("Error generating files: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
