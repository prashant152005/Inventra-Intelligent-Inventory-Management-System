package com.inventra.auth.controller;

import com.inventra.auth.dto.*;
import com.inventra.auth.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportsController {

    private final ReportService reportService;

    public ReportsController(ReportService reportService) {
        this.reportService = reportService;
    }

    // Sales Report - JSON
    @GetMapping("/sales")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<SalesReportDto> getSalesReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null) startDate = LocalDate.now().minusMonths(1);
        if (endDate == null) endDate = LocalDate.now();

        SalesReportDto report = reportService.generateSalesReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    // Sales Report - CSV Export
    @GetMapping(value = "/sales/csv", produces = "text/csv")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<byte[]> exportSalesReportCsv(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null) startDate = LocalDate.now().minusMonths(1);
        if (endDate == null) endDate = LocalDate.now();

        SalesReportDto report = reportService.generateSalesReport(startDate, endDate);
        String csv = reportService.salesReportToCsv(report);

        byte[] bytes = csv.getBytes();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "sales-report-" + LocalDate.now() + ".csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(bytes);
    }

    // Inventory Report - JSON
    @GetMapping("/inventory")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    public ResponseEntity<InventoryReportDto> getInventoryReport() {
        return ResponseEntity.ok(reportService.generateInventoryReport());
    }

    // Inventory Report - CSV Export
    @GetMapping(value = "/inventory/csv", produces = "text/csv")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    public ResponseEntity<byte[]> exportInventoryReportCsv() {
        InventoryReportDto report = reportService.generateInventoryReport();
        String csv = reportService.inventoryReportToCsv(report);

        byte[] bytes = csv.getBytes();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "inventory-report-" + LocalDate.now() + ".csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(bytes);
    }
}