import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Report generator for the module project.
 * Reads products, salesmen and sales files, then generates sorted reports.
 *
 * Named in lowercase ("main") because the assignment literally requires
 * the second main-method class to be called "main".
 *
 * Includes extra features:
 * - Supports multiple sales files per salesman.
 * - Generates binary serialized reports (reporte.ser).
 * - Detects invalid formats or inconsistent data (reporte_errores.txt).
 *
 * Usage (from the project folder, after running {@code GenerateInfoFiles}):
 * {@code java main}
 *
 * @author Isaac Diaz Perez &lt;idiazppe@poligran.edu.co&gt;
 * @author Julio Cesar Alvarado Avila
 */
public class main {

    /** Input file with the products catalog: {@code ID;Name;UnitPrice}. */
    private static final String PRODUCTS_FILE = "products.txt";
    /** Input file with the salesmen info: {@code DocType;DocNumber;FirstNames;LastNames}. */
    private static final String SALESMEN_FILE = "salesmen_info.txt";
    /** Prefix shared by every sales file (one or more per salesman). */
    private static final String SALES_PREFIX = "salesman_";
    /** Extension shared by every sales file. */
    private static final String SALES_SUFFIX = ".txt";

    /** Output CSV: salesmen sorted by money collected, highest first. */
    private static final String VENDORS_REPORT_FILE = "reporte_vendedores.txt";
    /** Output CSV: sold products sorted by quantity sold, highest first. */
    private static final String PRODUCTS_REPORT_FILE = "reporte_productos.txt";
    /** Output binary file with both reports serialized (extra feature). */
    private static final String SERIALIZED_REPORT_FILE = "reporte.ser";
    /** Output file listing every invalid or inconsistent line found (extra feature). */
    private static final String ERRORS_REPORT_FILE = "reporte_errores.txt";

    /**
     * Entry point. Reads the input files from the working directory, builds
     * both reports and prints a success message, or an error message if
     * something goes wrong. It never asks the user for input.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        List<String> warnings = new ArrayList<String>();

        try {
            Map<Long, Product> products = loadProducts(PRODUCTS_FILE, warnings);
            Map<Long, String> salesmen = loadSalesmen(SALESMEN_FILE, warnings);

            Map<Long, Double> moneyBySalesman = new HashMap<Long, Double>();
            Map<Long, Long> quantityByProduct = new HashMap<Long, Long>();

            List<File> salesFiles = getSalesFiles(new File("."));
            if (salesFiles.isEmpty()) {
                warnings.add("No sales files found with prefix: " + SALES_PREFIX);
            }

            for (File salesFile : salesFiles) {
                processSalesFile(salesFile, products, moneyBySalesman, quantityByProduct, warnings);
            }

            List<SalesmanReport> vendorReports = buildVendorReports(moneyBySalesman, salesmen);
            List<ProductReport> productReports = buildProductReports(quantityByProduct, products);

            writeVendorReport(vendorReports);
            writeProductReport(productReports);
            serializeReports(vendorReports, productReports);
            writeWarnings(warnings);

            System.out.println("Processing completed successfully.");
            System.out.println("- " + VENDORS_REPORT_FILE + " (" + vendorReports.size() + " salesmen)");
            System.out.println("- " + PRODUCTS_REPORT_FILE + " (" + productReports.size() + " products)");
            System.out.println("- " + SERIALIZED_REPORT_FILE + " (serialized data)");
            if (!warnings.isEmpty()) {
                System.out.println("- Warnings logged in " + ERRORS_REPORT_FILE + ": " + warnings.size());
            }

        } catch (Exception e) {
            System.err.println("Error generating reports: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Loads the products catalog. Lines with a wrong format, non-numeric
     * values or a negative price are skipped and reported as warnings.
     *
     * @param filename path of the products file
     * @param warnings list where invalid lines are reported
     * @return products indexed by their ID
     * @throws IOException if the file does not exist or cannot be read
     */
    private static Map<Long, Product> loadProducts(String filename, List<String> warnings) throws IOException {
        Map<Long, Product> products = new HashMap<Long, Product>();
        File file = new File(filename);

        if (!file.exists()) {
            throw new IOException("Products file not found: " + filename);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        try {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split(";", -1);
                if (parts.length < 3) {
                    warnings.add(filename + " (line " + lineNumber + "): invalid format: " + line);
                    continue;
                }

                try {
                    long id = Long.parseLong(parts[0].trim());
                    String name = parts[1].trim();
                    double price = Double.parseDouble(parts[2].trim());

                    if (price < 0) {
                        warnings.add(filename + " (line " + lineNumber + "): negative price ignored: " + price);
                        continue;
                    }

                    products.put(id, new Product(id, name, price));
                } catch (NumberFormatException nfe) {
                    warnings.add(filename + " (line " + lineNumber + "): invalid number: " + line);
                }
            }
        } finally {
            reader.close();
        }

        return products;
    }

    /**
     * Loads the salesmen info file. Lines with a wrong format or a
     * non-numeric document number are skipped and reported as warnings.
     *
     * @param filename path of the salesmen info file
     * @param warnings list where invalid lines are reported
     * @return full name of each salesman indexed by document number
     * @throws IOException if the file does not exist or cannot be read
     */
    private static Map<Long, String> loadSalesmen(String filename, List<String> warnings) throws IOException {
        Map<Long, String> salesmen = new HashMap<Long, String>();
        File file = new File(filename);

        if (!file.exists()) {
            throw new IOException("Salesmen file not found: " + filename);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        try {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split(";", -1);
                if (parts.length < 4) {
                    warnings.add(filename + " (line " + lineNumber + "): invalid format: " + line);
                    continue;
                }

                try {
                    long id = Long.parseLong(parts[1].trim());
                    String firstName = parts[2].trim();
                    String lastName = parts[3].trim();
                    salesmen.put(id, firstName + " " + lastName);
                } catch (NumberFormatException nfe) {
                    warnings.add(filename + " (line " + lineNumber + "): invalid document number: " + line);
                }
            }
        } finally {
            reader.close();
        }

        return salesmen;
    }

    /**
     * Finds every sales file in a directory. Any file whose name starts with
     * {@link #SALES_PREFIX} and ends with {@link #SALES_SUFFIX} is accepted,
     * so a salesman may have more than one sales file.
     *
     * @param directory directory to scan
     * @return sales files sorted by name (empty if none is found)
     */
    private static List<File> getSalesFiles(File directory) {
        List<File> list = new ArrayList<File>();
        File[] files = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(SALES_PREFIX) && name.endsWith(SALES_SUFFIX);
            }
        });

        if (files != null) {
            for (File f : files) {
                list.add(f);
            }
        }

        Collections.sort(list, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                return f1.getName().compareTo(f2.getName());
            }
        });

        return list;
    }

    /**
     * Processes one sales file and adds its sales to the running totals.
     * The first line identifies the salesman; every following line is a
     * sale ({@code ProductID;Quantity;}). Lines with non-numeric values,
     * negative quantities or unknown product IDs are skipped and reported.
     *
     * @param file              sales file to read
     * @param products          products catalog, used to get unit prices
     * @param moneyBySalesman   money collected so far by each salesman
     * @param quantityByProduct units sold so far of each product
     * @param warnings          list where invalid lines are reported
     * @throws IOException if the file cannot be read
     */
    private static void processSalesFile(File file, Map<Long, Product> products,
            Map<Long, Double> moneyBySalesman, Map<Long, Long> quantityByProduct,
            List<String> warnings) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        try {
            String firstLine = reader.readLine();
            if (firstLine == null || firstLine.trim().isEmpty()) {
                warnings.add(file.getName() + ": empty file, ignored.");
                return;
            }

            String[] header = firstLine.trim().split(";", -1);
            if (header.length < 2) {
                warnings.add(file.getName() + " (line 1): invalid header: " + firstLine);
                return;
            }

            long salesmanId;
            try {
                salesmanId = Long.parseLong(header[1].trim());
            } catch (NumberFormatException nfe) {
                warnings.add(file.getName() + " (line 1): invalid salesman ID: " + header[1]);
                return;
            }

            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split(";", -1);
                if (parts.length < 2) {
                    warnings.add(file.getName() + " (line " + lineNumber + "): invalid line: " + line);
                    continue;
                }

                try {
                    long productId = Long.parseLong(parts[0].trim());
                    long quantity = Long.parseLong(parts[1].trim());

                    if (quantity < 0) {
                        warnings.add(file.getName() + " (line " + lineNumber + "): negative quantity ignored: " + quantity);
                        continue;
                    }

                    Product product = products.get(productId);
                    if (product == null) {
                        warnings.add(file.getName() + " (line " + lineNumber + "): unknown product ID: " + productId);
                        continue;
                    }

                    double totalSaleMoney = product.getPrice() * quantity;

                    // Accumulate money for salesman
                    Double currentMoney = moneyBySalesman.get(salesmanId);
                    if (currentMoney == null) {
                        moneyBySalesman.put(salesmanId, totalSaleMoney);
                    } else {
                        moneyBySalesman.put(salesmanId, currentMoney + totalSaleMoney);
                    }

                    // Accumulate quantity for product
                    Long currentQty = quantityByProduct.get(productId);
                    if (currentQty == null) {
                        quantityByProduct.put(productId, quantity);
                    } else {
                        quantityByProduct.put(productId, currentQty + quantity);
                    }

                } catch (NumberFormatException nfe) {
                    warnings.add(file.getName() + " (line " + lineNumber + "): non-numeric value: " + line);
                }
            }
        } finally {
            reader.close();
        }
    }

    /**
     * Builds the salesmen report sorted by money collected, highest first.
     * Salesmen missing from the info file are shown by their document number.
     *
     * @param moneyBySalesman money collected by each salesman
     * @param salesmen        full name of each salesman by document number
     * @return report rows sorted in descending order
     */
    private static List<SalesmanReport> buildVendorReports(Map<Long, Double> moneyBySalesman, Map<Long, String> salesmen) {
        List<SalesmanReport> list = new ArrayList<SalesmanReport>();

        for (Map.Entry<Long, Double> entry : moneyBySalesman.entrySet()) {
            long id = entry.getKey();
            double totalMoney = entry.getValue();
            String name = salesmen.get(id);
            if (name == null) {
                name = "Salesman " + id;
            }
            list.add(new SalesmanReport(name, totalMoney));
        }

        // Sort descending by money
        Collections.sort(list, new Comparator<SalesmanReport>() {
            @Override
            public int compare(SalesmanReport r1, SalesmanReport r2) {
                return Double.compare(r2.getTotalMoney(), r1.getTotalMoney());
            }
        });

        return list;
    }

    /**
     * Builds the products report sorted by quantity sold, highest first.
     * Only products with at least one unit sold are included.
     *
     * @param quantityByProduct units sold of each product
     * @param products          products catalog
     * @return report rows sorted in descending order
     */
    private static List<ProductReport> buildProductReports(Map<Long, Long> quantityByProduct, Map<Long, Product> products) {
        List<ProductReport> list = new ArrayList<ProductReport>();

        for (Product product : products.values()) {
            Long quantity = quantityByProduct.get(product.getId());
            if (quantity == null) {
                // Never sold: not part of the sold products report
                continue;
            }
            list.add(new ProductReport(product.getName(), product.getPrice(), quantity));
        }

        // Sort descending by quantity sold
        Collections.sort(list, new Comparator<ProductReport>() {
            @Override
            public int compare(ProductReport p1, ProductReport p2) {
                return Long.compare(p2.getQuantitySold(), p1.getQuantitySold());
            }
        });

        return list;
    }

    /**
     * Writes the salesmen report, one salesman per line:
     * {@code FullName;MoneyCollected}.
     *
     * @param reports rows already sorted
     * @throws IOException if the file cannot be written
     */
    private static void writeVendorReport(List<SalesmanReport> reports) throws IOException {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(VENDORS_REPORT_FILE), StandardCharsets.UTF_8));
        try {
            for (SalesmanReport report : reports) {
                writer.println(report.getFullName() + ";" + formatNumber(report.getTotalMoney()));
            }
        } finally {
            writer.close();
        }
    }

    /**
     * Writes the products report, one product per line:
     * {@code ProductName;UnitPrice}.
     *
     * @param reports rows already sorted
     * @throws IOException if the file cannot be written
     */
    private static void writeProductReport(List<ProductReport> reports) throws IOException {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(PRODUCTS_REPORT_FILE), StandardCharsets.UTF_8));
        try {
            for (ProductReport report : reports) {
                writer.println(report.getName() + ";" + formatNumber(report.getPrice()));
            }
        } finally {
            writer.close();
        }
    }

    /**
     * Serializes both reports into a single binary file (extra feature).
     *
     * @param vendors  salesmen report rows
     * @param products products report rows
     * @throws IOException if the file cannot be written
     */
    private static void serializeReports(List<SalesmanReport> vendors, List<ProductReport> products) throws IOException {
        CompleteReport data = new CompleteReport(vendors, products);
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SERIALIZED_REPORT_FILE));
        try {
            oos.writeObject(data);
        } finally {
            oos.close();
        }
    }

    /**
     * Writes every warning to the errors file. If there are no warnings, an
     * errors file left by a previous run is deleted.
     *
     * @param warnings invalid or inconsistent lines found
     * @throws IOException if the file cannot be written
     */
    private static void writeWarnings(List<String> warnings) throws IOException {
        File existingErrorFile = new File(ERRORS_REPORT_FILE);
        if (warnings.isEmpty()) {
            if (existingErrorFile.exists()) {
                existingErrorFile.delete();
            }
            return;
        }

        PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(existingErrorFile), StandardCharsets.UTF_8));
        try {
            for (String w : warnings) {
                writer.println(w);
            }
        } finally {
            writer.close();
        }
    }

    /**
     * Formats a number without decimals when it has none (15000 instead of
     * 15000.0), so the reports stay readable.
     *
     * @param value number to format
     * @return the number as text
     */
    private static String formatNumber(double value) {
        if (value == Math.floor(value)) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }

    // --- Simple Helper Classes ---

    /** A product of the catalog: ID, name and unit price. */
    private static class Product {
        /** Product ID. */
        private final long id;
        /** Product name. */
        private final String name;
        /** Unit price. */
        private final double price;

        /**
         * @param id    product ID
         * @param name  product name
         * @param price unit price
         */
        public Product(long id, String name, double price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }

        /** @return product ID */
        public long getId() {
            return id;
        }

        /** @return product name */
        public String getName() {
            return name;
        }

        /** @return unit price */
        public double getPrice() {
            return price;
        }
    }

    /** One row of the salesmen report: full name and money collected. */
    private static class SalesmanReport implements Serializable {
        /** Serialization version. */
        private static final long serialVersionUID = 1L;
        /** Salesman full name. */
        private final String fullName;
        /** Money collected. */
        private final double totalMoney;

        /**
         * @param fullName   salesman full name
         * @param totalMoney money collected across all their sales files
         */
        public SalesmanReport(String fullName, double totalMoney) {
            this.fullName = fullName;
            this.totalMoney = totalMoney;
        }

        /** @return salesman full name */
        public String getFullName() {
            return fullName;
        }

        /** @return money collected */
        public double getTotalMoney() {
            return totalMoney;
        }
    }

    /** One row of the products report: name, unit price and units sold. */
    private static class ProductReport implements Serializable {
        /** Serialization version. */
        private static final long serialVersionUID = 1L;
        /** Product name. */
        private final String name;
        /** Unit price. */
        private final double price;
        /** Total units sold. */
        private final long quantitySold;

        /**
         * @param name         product name
         * @param price        unit price
         * @param quantitySold total units sold
         */
        public ProductReport(String name, double price, long quantitySold) {
            this.name = name;
            this.price = price;
            this.quantitySold = quantitySold;
        }

        /** @return product name */
        public String getName() {
            return name;
        }

        /** @return unit price */
        public double getPrice() {
            return price;
        }

        /** @return total units sold */
        public long getQuantitySold() {
            return quantitySold;
        }
    }

    /** Container with both reports, used to serialize them together. */
    private static class CompleteReport implements Serializable {
        /** Serialization version. */
        private static final long serialVersionUID = 1L;
        /** Salesmen report rows. */
        private final List<SalesmanReport> salesmen;
        /** Products report rows. */
        private final List<ProductReport> products;

        /**
         * @param salesmen salesmen report rows
         * @param products products report rows
         */
        public CompleteReport(List<SalesmanReport> salesmen, List<ProductReport> products) {
            this.salesmen = salesmen;
            this.products = products;
        }
    }
}
