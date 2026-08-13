package com.messmanagement.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.messmanagement.dto.StudentStatusDTO;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Generates downloadable reports (Excel and PDF) of the current
 * dashboard. Both formats show the same columns as the on-screen table.
 *
 * Note: Apache POI and OpenPDF both have classes named Font/Row/Cell,
 * so each type here is imported explicitly (no wildcard imports) to
 * avoid ambiguity. Anywhere OpenPDF's Font is needed, it is referenced
 * with its full name com.lowagie.text.Font.
 */
@Service
public class ExportService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    private static final String[] HEADERS = {
            "Student", "Status", "Start Date", "End Date", "Days Remaining", "Last Payment Date", "Last Amount Paid"
    };

    public byte[] exportToExcel(List<StudentStatusDTO> rows) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Mess Report");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (StudentStatusDTO dto : rows) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(dto.getStudentName());
                row.createCell(1).setCellValue(displayStatus(dto));
                row.createCell(2).setCellValue(dto.getStartDate() != null ? dto.getStartDate().format(DATE_FMT) : "-");
                row.createCell(3).setCellValue(dto.getEndDate() != null ? dto.getEndDate().format(DATE_FMT) : "-");
                row.createCell(4).setCellValue(dto.getDaysRemaining() != null ? dto.getDaysRemaining() + " days" : "-");
                row.createCell(5).setCellValue(dto.getLastPaymentDate() != null ? dto.getLastPaymentDate().format(DATE_FMT) : "-");
                row.createCell(6).setCellValue(dto.getLastAmountPaid() != null ? dto.getLastAmountPaid().doubleValue() : 0);
            }

            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] exportToPdf(List<StudentStatusDTO> rows) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        com.lowagie.text.Font titleFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 16, com.lowagie.text.Font.BOLD);
        Paragraph title = new Paragraph("Mess Management - Payment Status Report", titleFont);
        title.setSpacingAfter(12f);
        document.add(title);

        PdfPTable table = new PdfPTable(HEADERS.length);
        table.setWidthPercentage(100);
        com.lowagie.text.Font headerFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.BOLD, Color.WHITE);

        for (String header : HEADERS) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(new Color(47, 74, 61)); // deep green
            cell.setPadding(6f);
            table.addCell(cell);
        }

        com.lowagie.text.Font cellFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 9);
        for (StudentStatusDTO dto : rows) {
            table.addCell(new Phrase(dto.getStudentName(), cellFont));
            table.addCell(new Phrase(displayStatus(dto), cellFont));
            table.addCell(new Phrase(dto.getStartDate() != null ? dto.getStartDate().format(DATE_FMT) : "-", cellFont));
            table.addCell(new Phrase(dto.getEndDate() != null ? dto.getEndDate().format(DATE_FMT) : "-", cellFont));
            table.addCell(new Phrase(dto.getDaysRemaining() != null ? dto.getDaysRemaining() + " days" : "-", cellFont));
            table.addCell(new Phrase(dto.getLastPaymentDate() != null ? dto.getLastPaymentDate().format(DATE_FMT) : "-", cellFont));
            table.addCell(new Phrase(dto.getLastAmountPaid() != null ? dto.getLastAmountPaid().toString() : "-", cellFont));
        }

        document.add(table);
        document.close();
        return out.toByteArray();
    }

    private String displayStatus(StudentStatusDTO dto) {
        return switch (dto.getStatus()) {
            case "ACTIVE" -> dto.isExpiringSoon() ? "Active (expiring soon)" : "Active";
            case "UPCOMING" -> "Coming soon";
            case "EXPIRED" -> "Expired";
            default -> "No payment yet";
        };
    }
}
