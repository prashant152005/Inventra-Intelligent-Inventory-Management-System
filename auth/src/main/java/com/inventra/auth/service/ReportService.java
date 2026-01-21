package com.inventra.auth.service;

import com.inventra.auth.dto.*;
import com.inventra.auth.inventory.entity.Product;
import com.inventra.auth.entity.StockTransaction;
import com.inventra.auth.inventory.repository.ProductRepository;
import com.inventra.auth.repository.StockTransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final ProductRepository productRepo;
    private final StockTransactionRepository transactionRepo;

    public ReportService(ProductRepository productRepo, StockTransactionRepository transactionRepo) {
        this.productRepo = productRepo;
        this.transactionRepo = transactionRepo;
    }

    public SalesReportDto generateSalesReport(LocalDate start, LocalDate end) {
        List<StockTransaction> sales = transactionRepo.findByTransactionTypeAndTransactionDateBetween(
                "OUT", start.atStartOfDay(), end.atTime(23, 59, 59));

        // Calculate total revenue using BigDecimal for precision
        BigDecimal totalRevenue = sales.stream()
                .map(t -> {
                    BigDecimal qty = BigDecimal.valueOf(t.getQuantity());
                    Double priceDouble = t.getProduct().getUnitPrice();
                    BigDecimal price = (priceDouble != null)
                            ? BigDecimal.valueOf(priceDouble)
                            : BigDecimal.ZERO;
                    return qty.multiply(price);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalTransactions = sales.size();

        // Top products
        Map<Product, Integer> productSales = sales.stream()
                .collect(Collectors.groupingBy(
                        StockTransaction::getProduct,
                        Collectors.summingInt(StockTransaction::getQuantity)));

        List<TopProductDto> topProducts = productSales.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .map(e -> {
                    Product prod = e.getKey();
                    Double priceDouble = prod.getUnitPrice();
                    BigDecimal revenue = BigDecimal.valueOf(e.getValue())
                            .multiply(priceDouble != null ? BigDecimal.valueOf(priceDouble) : BigDecimal.ZERO);

                    TopProductDto dto = new TopProductDto();
                    dto.setProductName(prod.getName());
                    dto.setSku(prod.getSku());
                    dto.setQuantitySold(e.getValue());
                    dto.setRevenue(revenue);
                    return dto;
                })
                .collect(Collectors.toList());

        // Daily breakdown
        Map<LocalDate, BigDecimal> dailyRevenue = sales.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getTransactionDate().toLocalDate(),
                        Collectors.reducing(BigDecimal.ZERO,
                                t -> {
                                    BigDecimal qty = BigDecimal.valueOf(t.getQuantity());
                                    Double priceDouble = t.getProduct().getUnitPrice();
                                    BigDecimal price = (priceDouble != null)
                                            ? BigDecimal.valueOf(priceDouble)
                                            : BigDecimal.ZERO;
                                    return qty.multiply(price);
                                },
                                BigDecimal::add)));

        List<DailySalesDto> dailySales = dailyRevenue.entrySet().stream()
                .map(e -> {
                    DailySalesDto dto = new DailySalesDto();
                    dto.setDate(e.getKey());
                    dto.setRevenue(e.getValue());
                    dto.setTransactions((int) sales.stream()
                            .filter(t -> t.getTransactionDate().toLocalDate().equals(e.getKey()))
                            .count());
                    return dto;
                })
                .sorted(Comparator.comparing(DailySalesDto::getDate))
                .collect(Collectors.toList());

        SalesReportDto report = new SalesReportDto();
        report.setStartDate(start);
        report.setEndDate(end);
        report.setTotalRevenue(totalRevenue);
        report.setTotalTransactions(totalTransactions);
        report.setTopProducts(topProducts);
        report.setDailySales(dailySales);

        return report;
    }

    public String salesReportToCsv(SalesReportDto report) {
        StringBuilder csv = new StringBuilder();
        csv.append("Sales Report,").append(report.getStartDate())
                .append(" to ").append(report.getEndDate()).append("\n\n");
        csv.append("Total Revenue,₹").append(report.getTotalRevenue()
                .setScale(2, RoundingMode.HALF_UP)).append("\n");
        csv.append("Total Transactions,").append(report.getTotalTransactions()).append("\n\n");

        csv.append("Top 5 Products\n");
        csv.append("Name,SKU,Quantity Sold,Revenue\n");
        report.getTopProducts().forEach(p ->
                csv.append(p.getProductName()).append(",")
                        .append(p.getSku()).append(",")
                        .append(p.getQuantitySold()).append(",")
                        .append(p.getRevenue().setScale(2, RoundingMode.HALF_UP)).append("\n"));

        csv.append("\nDaily Sales\n");
        csv.append("Date,Revenue,Transactions\n");
        report.getDailySales().forEach(d ->
                csv.append(d.getDate()).append(",")
                        .append(d.getRevenue().setScale(2, RoundingMode.HALF_UP)).append(",")
                        .append(d.getTransactions()).append("\n"));

        return csv.toString();
    }

    public InventoryReportDto generateInventoryReport() {
        List<Product> all = productRepo.findAll();
        List<Product> lowStock = productRepo.findLowStockProducts();

        BigDecimal totalStockValue = all.stream()
                .map(p -> {
                    BigDecimal qty = BigDecimal.valueOf(p.getQuantity() != null ? p.getQuantity() : 0);
                    Double priceDouble = p.getUnitPrice();
                    BigDecimal price = (priceDouble != null)
                            ? BigDecimal.valueOf(priceDouble)
                            : BigDecimal.ZERO;
                    return qty.multiply(price);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int outOfStock = (int) all.stream()
                .filter(p -> p.getQuantity() <= 0)
                .count();

        List<LowStockProductDto> lowList = lowStock.stream()
                .map(p -> {
                    LowStockProductDto dto = new LowStockProductDto();
                    dto.setName(p.getName());
                    dto.setSku(p.getSku());
                    dto.setQuantity(p.getQuantity());
                    dto.setReorderLevel(p.getReorderLevel());
                    Double priceDouble = p.getUnitPrice();
                    dto.setValue(BigDecimal.valueOf(p.getQuantity())
                            .multiply(priceDouble != null ? BigDecimal.valueOf(priceDouble) : BigDecimal.ZERO));
                    return dto;
                })
                .collect(Collectors.toList());

        InventoryReportDto report = new InventoryReportDto();
        report.setTotalProducts(all.size());
        report.setTotalStockValue(totalStockValue);
        report.setLowStockCount(lowStock.size());
        report.setLowStockProducts(lowList);
        report.setOutOfStockCount(outOfStock);

        return report;
    }

    public String inventoryReportToCsv(InventoryReportDto report) {
        StringBuilder csv = new StringBuilder();
        csv.append("Inventory Report - ").append(LocalDate.now()).append("\n\n");
        csv.append("Total Products,").append(report.getTotalProducts()).append("\n");
        csv.append("Total Stock Value,₹").append(report.getTotalStockValue()
                .setScale(2, RoundingMode.HALF_UP)).append("\n");
        csv.append("Low Stock Items,").append(report.getLowStockCount()).append("\n");
        csv.append("Out of Stock,").append(report.getOutOfStockCount()).append("\n\n");

        csv.append("Low Stock Products\n");
        csv.append("Name,SKU,Quantity,Reorder Level,Value\n");
        report.getLowStockProducts().forEach(p ->
                csv.append(p.getName()).append(",")
                        .append(p.getSku()).append(",")
                        .append(p.getQuantity()).append(",")
                        .append(p.getReorderLevel()).append(",")
                        .append(p.getValue().setScale(2, RoundingMode.HALF_UP)).append("\n"));

        return csv.toString();
    }
}