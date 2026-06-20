package com.company.system.service;

import com.company.system.models.Contract;
import com.company.system.models.Employee;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

public class ContractPdfService {

    private static final BaseColor COLOR_DARK = new BaseColor(15, 40, 80);
    private static final BaseColor COLOR_ACCENT = new BaseColor(59, 130, 246);
    private static final BaseColor COLOR_LIGHT = new BaseColor(240, 245, 255);
    private static final BaseColor COLOR_LINE = new BaseColor(200, 215, 240);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static byte[] generateContractPdf(Employee employee, Contract contract)
            throws DocumentException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 72, 72, 72, 72);
        PdfWriter writer = PdfWriter.getInstance(doc, out);

        writer.setPageEvent(new ContractPageEvent());

        doc.open();
        addCoverPage(doc, employee, contract);
        doc.newPage();
        addContractBody(doc, employee, contract);
        doc.close();

        return out.toByteArray();
    }

    private static void addCoverPage(Document doc, Employee employee, Contract contract)
            throws DocumentException {
        doc.add(Chunk.NEWLINE);
        doc.add(Chunk.NEWLINE);
        doc.add(Chunk.NEWLINE);

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 28, COLOR_DARK);
        Paragraph title = new Paragraph("KONTRATE PUNE", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);

        doc.add(Chunk.NEWLINE);
        doc.add(Chunk.NEWLINE);
        addHorizontalLine(doc, COLOR_ACCENT, 2);
        doc.add(Chunk.NEWLINE);

        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA, 12, COLOR_DARK);
        Paragraph between = new Paragraph("Ne mes:", labelFont);
        between.setAlignment(Element.ALIGN_LEFT);
        doc.add(between);
        doc.add(Chunk.NEWLINE);

        addCoverBlock(doc, "Centrix Solutions SH.P.K",
                "Rr. Lidhjes se Prizrenit, Nr. 15, 10000, Prishtine, Kosove\n" +
                        "Numri i biznesit: 920453217\n" +
                        "I perfaqesuar sipas autorizimit nga:\n" +
                        "Erion Cana - Director HR and Administration",
                true);

        doc.add(Chunk.NEWLINE);

        Font andFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, COLOR_ACCENT);
        Paragraph and = new Paragraph("dhe", andFont);
        and.setAlignment(Element.ALIGN_CENTER);
        doc.add(and);

        doc.add(Chunk.NEWLINE);

        String empName = employee.getFirstName() + " " + employee.getLastName();
        String empDetail = "Email: " + nvl(employee.getEmail()) + "\n" +
                "Telefoni: " + nvl(employee.getPhone()) + "\n" +
                "Pozita: " + nvl(employee.getPosition());
        addCoverBlock(doc, empName, empDetail, false);

        doc.add(Chunk.NEWLINE);
        doc.add(Chunk.NEWLINE);
        addHorizontalLine(doc, COLOR_LINE, 1);
        doc.add(Chunk.NEWLINE);

        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 11, COLOR_DARK);
        String dateStr = "Data e nenshkrimit: " +
                (contract.getStartDate() != null
                        ? contract.getStartDate().toLocalDate().format(DATE_FMT)
                        : "-");
        Paragraph datePar = new Paragraph(dateStr, dateFont);
        datePar.setAlignment(Element.ALIGN_CENTER);
        doc.add(datePar);
    }

    private static void addCoverBlock(Document doc, String name, String detail, boolean isCompany)
            throws DocumentException {
        Font nameFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14,
                isCompany ? COLOR_DARK : COLOR_ACCENT);
        Font detFont = FontFactory.getFont(FontFactory.HELVETICA, 11, COLOR_DARK);

        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(85);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell cell = new PdfPCell();
        cell.setBorderColor(isCompany ? COLOR_DARK : COLOR_ACCENT);
        cell.setBorderWidth(1.5f);
        cell.setPadding(14);
        cell.setBackgroundColor(isCompany ? COLOR_LIGHT : BaseColor.WHITE);

        Paragraph namePar = new Paragraph(name, nameFont);
        Paragraph detPar = new Paragraph("\n" + detail, detFont);
        detPar.setLeading(16);

        cell.addElement(namePar);
        cell.addElement(detPar);
        table.addCell(cell);
        doc.add(table);
    }

    private static void addContractBody(Document doc, Employee employee, Contract contract)
            throws DocumentException {
        String empName = employee.getFirstName() + " " + employee.getLastName();
        String position = nvl(employee.getPosition());
        String baseSalary = String.format("%.2f", employee.getBaseSalary());
        String startDate = contract.getStartDate() != null
                ? contract.getStartDate().toLocalDate().format(DATE_FMT) : "-";
        String endDate = contract.getEndDate() != null
                ? contract.getEndDate().toLocalDate().format(DATE_FMT) : "-";
        String contractType = nvl(contract.getContractType());
        int workHours = contractType.equalsIgnoreCase("Part-Time") ? 84 : 168;

        Font h1 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, COLOR_DARK);
        Paragraph mainTitle = new Paragraph("KONTRATE PUNE", h1);
        mainTitle.setAlignment(Element.ALIGN_CENTER);
        mainTitle.setSpacingAfter(4);
        doc.add(mainTitle);

        addHorizontalLine(doc, COLOR_ACCENT, 2);
        doc.add(Chunk.NEWLINE);

        addSection(doc, "1. Pozita dhe pergjegjesite");
        addClause(doc, "1.1", "Punonjesi angazhohet ne poziten **" + position + "**. " +
                "Detyrat dhe pergjegjesite e vendit te punes do te percaktohen me nje aneks te vecante, " +
                "i cili eshte pjese perberese e kesaj kontrate.");
        addClause(doc, "1.2", "Punonjesi eshte i obliguar te kryeje te gjitha detyrat qe mund " +
                "t'i kerkohen nga punedhenesi lidhur me poziten e tij.");
        addClause(doc, "1.3", "Punonjesi eshte i obliguar te veproje ne perputhje me te gjitha aktet " +
                "e brendshme te kompanise.");

        addSection(doc, "2. Kohezgjatja");
        addClause(doc, "2.1", "Punonjesi do t'i kryeje detyrat e saktesuara ne kete kontrate per kohe " +
                "te caktuar, duke filluar nga **" + startDate + "** deri me **" + endDate + "**.");
        addClause(doc, "2.2", "Nese punonjesi nuk e fillon punen ne diten e caktuar sipas kesaj kontrate, " +
                "do te konsiderohet se nuk ka themeluar marredhenie pune, pervec nese eshte penguar " +
                "per shkaqe te arsyeshme.");
        addClause(doc, "2.3", "Puna provuese zgjat gjashte (6) muaj. Gjate kesaj periudhe, punedhenesi " +
                "dhe punonjesi mund ta nderpresin marredhenien e punes me njoftim paraprak prej shtate (7) ditesh.");

        addSection(doc, "3. Paga dhe benefitet financiare");
        addClause(doc, "3.1", "Punonjesit i caktohet paga baze per punen qe kryen per punedhenesin, " +
                "ne vlere prej **" + baseSalary + " Euro** bruto. Kjo page korrespondon me punen ne " +
                "kohezgjatje prej " + workHours + " ore ne muaj.");
        addClause(doc, "3.2", "Paga shtese, bonuset dhe komisionet qe mund t'i shtohen pages baze " +
                "do te rregullohen me Rregulloren e Brendshme.");
        addClause(doc, "3.3", "Punedhenesi do t'i permbushe obligimet tatimore dhe kontributet pensionale " +
                "ne perputhje me legjislacionin ne fuqi.");
        addClause(doc, "3.4", "Paga do t'i paguhet punonjesit ne fund te muajit perkates dhe jo me vone " +
                "se data 10 e muajit vijues.");

        addSection(doc, "4. Orari i punes");
        addClause(doc, "4.1", "Punonjesi themelon marredhenie pune me orar prej " +
                workHours + " ore ne muaj.");
        addClause(doc, "4.2", "Per shkak te natyres se punes, punonjesi mund te angazhohet edhe gjate " +
                "diteve te vikendit, me kompensim sipas kesaj kontrate dhe Ligjit te Punes.");
        addClause(doc, "4.3", "Ne fillim te cdo jave, punedhenesi do ta beje te ditur orarin dhe oret " +
                "e punes per javen vijuese.");

        addSection(doc, "5. Konfidencialiteti");
        addClause(doc, "5.1", "Punonjesi eshte i detyruar te ruaje konfidencialitetin e te gjitha " +
                "informacioneve te kompanise, klienteve dhe partnereve gjate dhe pas perfundimit " +
                "te marredhenies se punes.");
        addClause(doc, "5.2", "Shkelja e detyrimit te konfidencialitetit konsiderohet shkelje e rende " +
                "e detyrave te punes dhe mund te rezultoje me nderprerje te menjehershme te kontrates.");

        addSection(doc, "6. Pushimet dhe lejet");
        addClause(doc, "6.1", "Punonjesi ka te drejte ne pushim vjetor te paguar sipas legjislacionit " +
                "ne fuqi te Republikes se Kosoves.");
        addClause(doc, "6.2", "Pushimi vjetor planifikohet me marreveshje mes punedhenesit dhe punonjesit, " +
                "duke marre parasysh nevojat e punes.");

        addSection(doc, "7. Nderprerja e kontrates");
        addClause(doc, "7.1", "Kjo kontrate mund te nderpritet me marreveshje te ndersjellte, me njoftim " +
                "paraprak prej 30 ditesh ose me shkaqe te arsyeshme sipas Ligjit te Punes.");
        addClause(doc, "7.2", "Punedhenesi rezervon te drejten e nderprerjes se menjehershme te kontrates " +
                "ne rast te shkeljes se rende te detyrave te punes.");

        doc.add(Chunk.NEWLINE);
        doc.add(Chunk.NEWLINE);
        addHorizontalLine(doc, COLOR_LINE, 1);
        doc.add(Chunk.NEWLINE);
        addSignatureSection(doc, empName, startDate);

        doc.add(Chunk.NEWLINE);
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 9, new BaseColor(120, 130, 150));
        Paragraph footer = new Paragraph(
                "Centrix Solutions Headquarters - Rr. Lidhjes se Prizrenit, Nr. 15, 10000, Prishtine, Kosove",
                footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        doc.add(footer);
    }

    private static void addSection(Document doc, String title) throws DocumentException {
        doc.add(Chunk.NEWLINE);
        Font f = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, COLOR_DARK);
        Paragraph p = new Paragraph(title, f);
        p.setSpacingBefore(8);
        p.setSpacingAfter(4);
        doc.add(p);
    }

    private static void addClause(Document doc, String number, String text)
            throws DocumentException {
        Font numFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, COLOR_ACCENT);
        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 11, COLOR_DARK);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, COLOR_DARK);

        Paragraph p = new Paragraph();
        p.setIndentationLeft(16);
        p.setLeading(16);
        p.setSpacingAfter(4);
        p.add(new Chunk(number + "  ", numFont));

        String[] parts = text.split("\\*\\*");
        for (int i = 0; i < parts.length; i++) {
            p.add(new Chunk(parts[i], i % 2 == 1 ? boldFont : bodyFont));
        }

        doc.add(p);
    }

    private static void addHorizontalLine(Document doc, BaseColor color, float width)
            throws DocumentException {
        PdfPTable line = new PdfPTable(1);
        line.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell();
        cell.setBorderWidthTop(width);
        cell.setBorderColorTop(color);
        cell.setBorderWidthBottom(0);
        cell.setBorderWidthLeft(0);
        cell.setBorderWidthRight(0);
        cell.setFixedHeight(1);
        line.addCell(cell);

        doc.add(line);
    }

    private static void addSignatureSection(Document doc, String empName, String date)
            throws DocumentException {
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, COLOR_DARK);
        Font lineFont = FontFactory.getFont(FontFactory.HELVETICA, 11, COLOR_DARK);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(20);

        PdfPCell left = new PdfPCell();
        left.setBorder(Rectangle.NO_BORDER);
        left.addElement(new Paragraph("Punedhenesi:", labelFont));
        left.addElement(new Paragraph("Centrix Solutions SH.P.K", lineFont));
        left.addElement(new Paragraph("Amir Brajshori", lineFont));
        left.addElement(Chunk.NEWLINE);
        left.addElement(new Paragraph("Nenshkrimi: ________________", lineFont));
        left.addElement(new Paragraph("Data: " + date, lineFont));

        PdfPCell right = new PdfPCell();
        right.setBorder(Rectangle.NO_BORDER);
        right.addElement(new Paragraph("Punonjesi:", labelFont));
        right.addElement(new Paragraph(empName, lineFont));
        right.addElement(Chunk.NEWLINE);
        right.addElement(new Paragraph("Nenshkrimi: ________________", lineFont));
        right.addElement(new Paragraph("Data: " + date, lineFont));

        table.addCell(left);
        table.addCell(right);
        doc.add(table);
    }

    private static String nvl(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private static class ContractPageEvent extends PdfPageEventHelper {
        private final Font headerFont = FontFactory.getFont(
                FontFactory.HELVETICA, 9, new BaseColor(120, 130, 150));

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();

            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                    new Phrase("Centrix Solutions SH.P.K - Kontrate Pune", headerFont),
                    document.leftMargin(), document.top() + 10, 0);

            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                    new Phrase("Faqja " + writer.getPageNumber(), headerFont),
                    document.right(), document.top() + 10, 0);
        }
    }
}
