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
 * Main report generator for the module project.
 * Reads products, salesmen and sales files, then generates sorted reports.
 * 
 * Includes extra features:
 * - Supports multiple sales files per salesman.
 * - Generates binary serialized reports (reporte.ser).
 * - Detects invalid formats or inconsistent data (reporte_errores.txt).
 * 
 * @author Isaac Diaz Perez <idiazppe@poligran.edu.co>
 */
public class Main {

    private static final String PRODUCTS_FILE = "products.txt";
    private static final String SALESMEN_FILE = "salesmen_info.txt";
    private static final String SALES_PREFIX = "salesman_";
    private static final String SALES_SUFFIX = ".txt";

    private static final String VENDORS_REPORT_FILE = "reporte_vendedores.txt";
    private static final String PRODUCTS_REPORT_FILE = "reporte_productos.txt";
    private static final String SERIALIZED_REPORT_FILE = "reporte.ser";
    private static final String ERRORS_REPORT_FILE = "reporte_errores.txt";

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
        }
    }

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

    private static List<ProductReport> buildProductReports(Map<Long, Long> quantityByProduct, Map<Long, Product> products) {
        List<ProductReport> list = new ArrayList<ProductReport>();

        for (Product product : products.values()) {
            Long quantity = quantityByProduct.get(product.getId());
            long totalQuantity = (quantity != null) ? quantity : 0L;
            list.add(new ProductReport(product.getName(), product.getPrice(), totalQuantity));
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

    private static void serializeReports(List<SalesmanReport> vendors, List<ProductReport> products) throws IOException {
        CompleteReport data = new CompleteReport(vendors, products);
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SERIALIZED_REPORT_FILE));
        try {
            oos.writeObject(data);
        } finally {
            oos.close();
        }
    }

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

    private static String formatNumber(double value) {
        if (value == Math.floor(value)) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }

    // --- Simple Helper Classes ---

    private static class Product {
        private final long id;
        private final String name;
        private final double price;

        public Product(long id, String name, double price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }
    }

    private static class SalesmanReport implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String fullName;
        private final double totalMoney;

        public SalesmanReport(String fullName, double totalMoney) {
            this.fullName = fullName;
            this.totalMoney = totalMoney;
        }

        public String getFullName() {
            return fullName;
        }

        public double getTotalMoney() {
            return totalMoney;
        }
    }

    private static class ProductReport implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String name;
        private final double price;
        private final long quantitySold;

        public ProductReport(String name, double price, long quantitySold) {
            this.name = name;
            this.price = price;
            this.quantitySold = quantitySold;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public long getQuantitySold() {
            return quantitySold;
        }
    }

    private static class CompleteReport implements Serializable {
        private static final long serialVersionUID = 1L;
        private final List<SalesmanReport> salesmen;
        private final List<ProductReport> products;

        public CompleteReport(List<SalesmanReport> salesmen, List<ProductReport> products) {
            this.salesmen = salesmen;
            this.products = products;
        }
    }
}
