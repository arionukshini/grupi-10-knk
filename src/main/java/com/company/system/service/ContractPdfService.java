package com.company.system.service;

import com.company.system.model.Contract;
import com.company.system.model.Employee;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

public class ContractPdfService {

    private static final BaseColor COLOR_DARK   = new BaseColor(15, 40, 80);   // Navy
    private static final BaseColor COLOR_ACCENT = new BaseColor(59, 130, 246); // Blue
    private static final BaseColor COLOR_LIGHT  = new BaseColor(240, 245, 255);
    private static final BaseColor COLOR_LINE   = new BaseColor(200, 215, 240);

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static byte[] generateContractPdf(Employee employee, Contract contract)
            throws Exception {

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
        Paragraph title = new Paragraph("KONTRATË PUNE", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);

        doc.add(Chunk.NEWLINE);
        doc.add(Chunk.NEWLINE);

        addHorizontalLine(doc, COLOR_ACCENT, 2);

        doc.add(Chunk.NEWLINE);

        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA, 12, COLOR_DARK);
        Paragraph nemes = new Paragraph("Në mes:", labelFont);
        nemes.setAlignment(Element.ALIGN_LEFT);
        doc.add(nemes);

        doc.add(Chunk.NEWLINE);

        addCoverBlock(doc, "Centrix Solutions SH.P.K",
                "Rr. Lidhjes së Prizrenit, Nr. 15, 10000, Prishtinë, Kosovë\n" +
                        "Numri i biznesit: 920453217\n" +
                        "I përfaqësuar sipas autorizimit nga:\n" +
                        "Erion Cana – Director HR and Administration",
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
        String dateStr = "Data e nënshkrimit: " +
                (contract.getStartDate() != null
                        ? contract.getStartDate().toLocalDate().format(DATE_FMT)
                        : "-");
        Paragraph datePar = new Paragraph(dateStr, dateFont);
        datePar.setAlignment(Element.ALIGN_CENTER);
        doc.add(datePar);
    }

    private static void addCoverBlock(Document doc, String name, String detail,
                                      boolean isCompany) throws DocumentException {
        Font nameFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14,
                isCompany ? COLOR_DARK : COLOR_ACCENT);
        Font detFont   = FontFactory.getFont(FontFactory.HELVETICA, 11, COLOR_DARK);

        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(85);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell cell = new PdfPCell();
        cell.setBorderColor(isCompany ? COLOR_DARK : COLOR_ACCENT);
        cell.setBorderWidth(1.5f);
        cell.setPadding(14);
        cell.setBackgroundColor(isCompany ? COLOR_LIGHT : BaseColor.WHITE);

        Paragraph namePar = new Paragraph(name, nameFont);
        Paragraph detPar  = new Paragraph("\n" + detail, detFont);
        detPar.setLeading(16);

        cell.addElement(namePar);
        cell.addElement(detPar);
        table.addCell(cell);
        doc.add(table);
    }


    private static void addContractBody(Document doc, Employee employee, Contract contract)
            throws DocumentException {

        String empName     = employee.getFirstName() + " " + employee.getLastName();
        String position    = nvl(employee.getPosition());
        String baseSalary  = String.format("%.2f", employee.getBaseSalary());
        String startDate   = contract.getStartDate() != null
                ? contract.getStartDate().toLocalDate().format(DATE_FMT) : "-";
        String endDate     = contract.getEndDate() != null
                ? contract.getEndDate().toLocalDate().format(DATE_FMT) : "-";
        String contractType = nvl(contract.getContractType());
        int workHours = contractType.equalsIgnoreCase("Part-Time") ? 84 : 168;

        Font h1 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, COLOR_DARK);
        Paragraph mainTitle = new Paragraph("KONTRATË PUNE", h1);
        mainTitle.setAlignment(Element.ALIGN_CENTER);
        mainTitle.setSpacingAfter(4);
        doc.add(mainTitle);

        addHorizontalLine(doc, COLOR_ACCENT, 2);
        doc.add(Chunk.NEWLINE);

        addSection(doc, "1. Pozita dhe përgjegjësitë");
        addClause(doc, "1.1", "Punonjësi angazhohet në pozitën **" + position + "**. " +
                "Detyrat dhe përgjegjësitë e vendit të punës do të përcaktohen me një aneks të veçantë, " +
                "e cila është pjesë përbërëse e kësaj kontrate.");
        addClause(doc, "1.2", "Punonjësi është i obliguar që të kryej të gjitha detyrat të cilat mund " +
                "të i kërkohen nga punëdhënësi lidhur me pozitën e tij.");
        addClause(doc, "1.3", "Punonjësi është i obliguar që të veproj në përputhje me të gjitha aktet " +
                "e brendshme të kompanisë.");

        addSection(doc, "2. Kohëzgjatja");
        addClause(doc, "2.1", "Punonjësi do t'i kryejë detyrat e saktësuara në këtë kontratë për kohë " +
                "të caktuar dhe me orar të pjesëshem dhe në ankesin e veçantë, duke filluar nga **" +
                startDate + "** deri më **" + endDate + "**.");
        addClause(doc, "2.2", "Në rast se punonjësi nuk e fillon punën në ditën e caktuar sipas kësaj " +
                "kontrate, do të konsiderohet se nuk ka themeluar marrëdhënie pune, përveç nëse është " +
                "penguar të fillojë punën për shkaqe të arsyeshme.");
        addClause(doc, "2.3", "Puna provuese e punonjësit zgjatë gjashtë (6) muaj. Gjatë periudhës " +
                "provuese të punës, punëdhënësi dhe punonjësi, mund ta ndërpresin marrëdhënien e punës, " +
                "me njoftim paraprak prej shtatë (7) ditësh.");

        addSection(doc, "3. Paga dhe benefitet financiare");
        addClause(doc, "3.1", "Punonjësit i caktohet paga bazë për punën të cilën e kryen për " +
                "punëdhënësin, në lartësi prej **" + baseSalary + " Euro** bruto që realizohet në " +
                "mënyrë periodike për çdo muaj. Kjo pagë korrespondon me punën në kohëzgjatje prej " +
                workHours + " orë në muaj.");
        addClause(doc, "3.2", "Paga shtesë, bonuset si dhe komisionet që mund t'i shtohen pagës bazë " +
                "të punonjësit në bazë të performancës, do të rregullohen me anë të Rregullores së Brendshme.");
        addClause(doc, "3.3", "Punëdhënësi do t'i përmbush obligimet tatimore dhe kontributet pensionale " +
                "për punonjësin në përputhje me legjislacionin në fuqi.");
        addClause(doc, "3.4", "Paga do t'i paguhet punonjësit në fund të muajit përkatës dhe më së " +
                "largu deri më datën 10 të muajit vijues.");

        addSection(doc, "4. Orari i punës");
        addClause(doc, "4.1", "Punonjësi themelon marrëdhënie pune me orar të pjesëshem prej " +
                workHours + " orë në muaj (apo 20 orë/për javë).");
        addClause(doc, "4.2", "Për shkak të natyrës specifike të punës, punonjësi mund të angazhohet " +
                "në punë edhe gjatë ditëve të vikendit, për të cilat do të kompenzohet në përputhje me " +
                "këtë kontratë të punës dhe në përputhje me Ligjin e Punës.");
        addClause(doc, "4.3", "Në fillim të çdo jave, punëdhënësi do të bëjë të ditur orarin dhe orët " +
                "e punës për punonjësin për javën vijuese.");

        addSection(doc, "5. Konfidencialiteti");
        addClause(doc, "5.1", "Punonjësi është i detyruar të ruaj konfidencialitetin e të gjitha " +
                "informacioneve të kompanisë, klientëve dhe partnerëve gjatë dhe pas përfundimit " +
                "të marrëdhënies së punës.");
        addClause(doc, "5.2", "Shkelja e detyrimit të konfidencialitetit do të konsiderohet shkelje " +
                "e rëndë e detyrave të punës dhe mund të rezultojë me ndërprerje të menjëhershme " +
                "të kontratës.");

        addSection(doc, "6. Pushimet dhe lejet");
        addClause(doc, "6.1", "Punonjësi ka të drejtë në pushim vjetor të paguar sipas legjislacionit " +
                "në fuqi të Republikës së Kosovës.");
        addClause(doc, "6.2", "Pushimi vjetor do të planifikohet me marrëveshje mes punëdhënësit " +
                "dhe punonjësit, duke marrë parasysh nevojat e punës.");

        addSection(doc, "7. Ndërprerja e kontratës");
        addClause(doc, "7.1", "Kjo kontratë mund të ndërpritet me marrëveshje të ndërsjelltë, " +
                "me njoftim paraprak prej 30 ditësh ose me shkaqe të arsyeshme sipas Ligjit të Punës.");
        addClause(doc, "7.2", "Punëdhënësi rezervon të drejtën e ndërprerjes së menjëhershme të " +
                "kontratës në rast të shkeljes së rëndë të detyrave të punës.");

        doc.add(Chunk.NEWLINE);
        doc.add(Chunk.NEWLINE);
        addHorizontalLine(doc, COLOR_LINE, 1);
        doc.add(Chunk.NEWLINE);

        addSignatureSection(doc, empName, startDate);

        doc.add(Chunk.NEWLINE);
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 9,
                new BaseColor(120, 130, 150));
        Paragraph footer = new Paragraph("Centrix Solutions Headquarters – Rr. Lidhjes së Prizrenit, Nr. 15, 10000, Prishtinë, Kosovë",
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
        Font numFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, COLOR_ACCENT);
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
        Font lineFont  = FontFactory.getFont(FontFactory.HELVETICA, 11, COLOR_DARK);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(20);

        PdfPCell left = new PdfPCell();
        left.setBorder(Rectangle.NO_BORDER);
        left.addElement(new Paragraph("Punëdhënësi:", labelFont));
        left.addElement(new Paragraph("Centrix Solutions SH.P.K", lineFont));
        left.addElement(new Paragraph("Amir Brajshori", lineFont));
        left.addElement(Chunk.NEWLINE);
        left.addElement(new Paragraph("Nënshkrimi: ________________", lineFont));
        left.addElement(new Paragraph("Data: " + date, lineFont));

        PdfPCell right = new PdfPCell();
        right.setBorder(Rectangle.NO_BORDER);
        right.addElement(new Paragraph("Punonjësi:", labelFont));
        right.addElement(new Paragraph(empName, lineFont));
        right.addElement(Chunk.NEWLINE);
        right.addElement(new Paragraph("Nënshkrimi: ________________", lineFont));
        right.addElement(new Paragraph("Data: " + date, lineFont));

        table.addCell(left);
        table.addCell(right);
        doc.add(table);
    }

    private static String nvl(String v) {
        return v == null || v.isBlank() ? "-" : v;
    }


    static class ContractPageEvent extends PdfPageEventHelper {
        private final Font headerFont = FontFactory.getFont(
                FontFactory.HELVETICA, 9, new BaseColor(120, 130, 150));

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();

            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                    new Phrase("Centrix Solutions SH.P.K – Kontratë Pune", headerFont),
                    document.leftMargin(), document.top() + 10, 0);

            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                    new Phrase("Faqja " + writer.getPageNumber(), headerFont),
                    document.right(), document.top() + 10, 0);
        }
    }
}
