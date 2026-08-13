package com.messmanagement.controller;

import com.messmanagement.service.DashboardService;
import com.messmanagement.service.ExportService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * Download endpoints for the monthly report, as Excel or PDF.
 * Respects the same status/name filters as the dashboard.
 */
@RestController
@RequestMapping("/export")
public class ExportController {

    private final DashboardService dashboardService;
    private final ExportService exportService;

    public ExportController(DashboardService dashboardService, ExportService exportService) {
        this.dashboardService = dashboardService;
        this.exportService = exportService;
    }

    @GetMapping("/excel")
    public ResponseEntity<byte[]> exportExcel(@RequestParam(required = false) String status,
                                               @RequestParam(required = false) String name) throws Exception {
        byte[] bytes = exportService.exportToExcel(dashboardService.getDashboard(status, name));
        String filename = "mess-report-" + LocalDate.now() + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(filename).build().toString())
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> exportPdf(@RequestParam(required = false) String status,
                                             @RequestParam(required = false) String name) throws Exception {
        byte[] bytes = exportService.exportToPdf(dashboardService.getDashboard(status, name));
        String filename = "mess-report-" + LocalDate.now() + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(filename).build().toString())
                .contentType(MediaType.APPLICATION_PDF)
                .body(bytes);
    }
}
