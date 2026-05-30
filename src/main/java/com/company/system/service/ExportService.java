package com.company.system.service;

import com.company.system.model.Contract;
import com.company.system.model.Employee;
import com.company.system.model.Salary;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class ExportService {

    // EMPLOYEES

    public static void exportEmployeesToExcel(List<Employee> employees, File file) throws Exception {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Employees");
            String[] headers = {"ID", "First Name", "Last Name", "Email", "Phone", "Position", "Base Salary", "Status"};
            createHeaderRow(wb, sheet, headers);

            int row = 1;
            for (Employee e : employees) {
                Row r = sheet.createRow(row++);
                r.createCell(0).setCellValue(e.getId());
                r.createCell(1).setCellValue(nvl(e.getFirstName()));
                r.createCell(2).setCellValue(nvl(e.getLastName()));
                r.createCell(3).setCellValue(nvl(e.getEmail()));
                r.createCell(4).setCellValue(nvl(e.getPhone()));
                r.createCell(5).setCellValue(nvl(e.getPosition()));
                r.createCell(6).setCellValue(e.getBaseSalary());
                r.createCell(7).setCellValue(nvl(e.getStatus()));
            }

            autoSize(sheet, headers.length);
            try (FileOutputStream fos = new FileOutputStream(file)) { wb.write(fos); }
        }
    }

    public static void exportEmployeesToPdf(List<Employee> employees, File file) throws Exception {
        Document doc = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(doc, new FileOutputStream(file));
        doc.open();
        addTitle(doc, "Employees Report");

        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 2, 2, 3, 2, 2, 2, 1.5f});
        addPdfHeaders(table, "ID", "First Name", "Last Name", "Email", "Phone", "Position", "Base Salary", "Status");

        boolean alt = false;
        for (Employee e : employees) {
            BaseColor bg = alt ? new BaseColor(240, 245, 255) : BaseColor.WHITE;
            addPdfRow(table, bg,
                    String.valueOf(e.getId()),
                    nvl(e.getFirstName()), nvl(e.getLastName()),
                    nvl(e.getEmail()), nvl(e.getPhone()),
                    nvl(e.getPosition()),
                    String.format("%.2f", e.getBaseSalary()),
                    nvl(e.getStatus()));
            alt = !alt;
        }

        doc.add(table);
        doc.close();
    }

    // CONTRACTS

    public static void exportContractsToExcel(List<Contract> contracts, File file) throws Exception {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Contracts");
            String[] headers = {"ID", "Employee", "Type", "Start Date", "End Date", "Salary", "Status"};
            createHeaderRow(wb, sheet, headers);

            int row = 1;
            for (Contract c : contracts) {
                Row r = sheet.createRow(row++);
                r.createCell(0).setCellValue(c.getId());
                r.createCell(1).setCellValue(nvl(c.getEmployeeName()));
                r.createCell(2).setCellValue(nvl(c.getContractType()));
                r.createCell(3).setCellValue(c.getStartDate() != null ? c.getStartDate().toString() : "-");
                r.createCell(4).setCellValue(c.getEndDate() != null ? c.getEndDate().toString() : "-");
                r.createCell(5).setCellValue(c.getSalary());
                r.createCell(6).setCellValue(nvl(c.getStatus()));
            }

            autoSize(sheet, headers.length);
            try (FileOutputStream fos = new FileOutputStream(file)) { wb.write(fos); }
        }
    }

    public static void exportContractsToPdf(List<Contract> contracts, File file) throws Exception {
        Document doc = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(doc, new FileOutputStream(file));
        doc.open();
        addTitle(doc, "Contracts Report");

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 3, 2, 2, 2, 2, 2});
        addPdfHeaders(table, "ID", "Employee", "Type", "Start Date", "End Date", "Salary", "Status");

        boolean alt = false;
        for (Contract c : contracts) {
            BaseColor bg = alt ? new BaseColor(240, 245, 255) : BaseColor.WHITE;
            addPdfRow(table, bg,
                    String.valueOf(c.getId()),
                    nvl(c.getEmployeeName()), nvl(c.getContractType()),
                    c.getStartDate() != null ? c.getStartDate().toString() : "-",
                    c.getEndDate() != null ? c.getEndDate().toString() : "-",
                    String.format("%.2f", c.getSalary()),
                    nvl(c.getStatus()));
            alt = !alt;
        }

        doc.add(table);
        doc.close();
    }

    //  SALARIES

    public static void exportSalariesToExcel(List<Salary> salaries, File file) throws Exception {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Salaries");
            String[] headers = {"ID", "Employee", "Gross Salary", "Bonus", "Deductions", "Net Salary", "Payment Date"};
            createHeaderRow(wb, sheet, headers);

            int row = 1;
            for (Salary s : salaries) {
                Row r = sheet.createRow(row++);
                r.createCell(0).setCellValue(s.getId());
                r.createCell(1).setCellValue(nvl(s.getEmployeeName()));
                r.createCell(2).setCellValue(s.getGrossSalary());
                r.createCell(3).setCellValue(s.getBonus());
                r.createCell(4).setCellValue(s.getDeductions());
                r.createCell(5).setCellValue(s.getNetSalary());
                r.createCell(6).setCellValue(s.getPaymentDate() != null ? s.getPaymentDate().toString() : "-");
            }

            autoSize(sheet, headers.length);
            try (FileOutputStream fos = new FileOutputStream(file)) { wb.write(fos); }
        }
    }

    public static void exportSalariesToPdf(List<Salary> salaries, File file) throws Exception {
        Document doc = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(doc, new FileOutputStream(file));
        doc.open();
        addTitle(doc, "Salaries Report");

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 3, 2, 2, 2, 2, 2});
        addPdfHeaders(table, "ID", "Employee", "Gross", "Bonus", "Deductions", "Net", "Payment Date");

        boolean alt = false;
        for (Salary s : salaries) {
            BaseColor bg = alt ? new BaseColor(240, 245, 255) : BaseColor.WHITE;
            addPdfRow(table, bg,
                    String.valueOf(s.getId()),
                    nvl(s.getEmployeeName()),
                    String.format("%.2f", s.getGrossSalary()),
                    String.format("%.2f", s.getBonus()),
                    String.format("%.2f", s.getDeductions()),
                    String.format("%.2f", s.getNetSalary()),
                    s.getPaymentDate() != null ? s.getPaymentDate().toString() : "-");
            alt = !alt;
        }

        doc.add(table);
        doc.close();
    }

    //  HELPERS

    private static void createHeaderRow(Workbook wb, Sheet sheet, String[] headers) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Row row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    private static void autoSize(Sheet sheet, int cols) {
        for (int i = 0; i < cols; i++) sheet.autoSizeColumn(i);
    }

    private static void addTitle(Document doc, String title) throws DocumentException {
        com.itextpdf.text.Font f = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.DARK_GRAY);
        Paragraph p = new Paragraph(title, f);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingAfter(14);
        doc.add(p);
    }

    private static void addPdfHeaders(PdfPTable table, String... headers) {
        com.itextpdf.text.Font f = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.WHITE);
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, f));
            cell.setBackgroundColor(new BaseColor(25, 49, 108));
            cell.setPadding(6);
            table.addCell(cell);
        }
    }

    private static void addPdfRow(PdfPTable table, BaseColor bg, String... values) {
        com.itextpdf.text.Font f = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);
        for (String v : values) {
            PdfPCell cell = new PdfPCell(new Phrase(v, f));
            cell.setBackgroundColor(bg);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }

    private static String nvl(String s) {
        return s != null ? s : "-";
    }
}
