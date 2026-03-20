package com.example.invoicing;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.example.constants.Constants;
import com.example.pojo.Customer;
import com.example.pojo.Item;
import com.example.pojo.Order;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PdfMaker {

    // --- Layout ---
    private static final float MARGIN_L = 50f;
    private static final float MARGIN_R = 50f;
    private static final float MARGIN_T = 50f;
    private static final float MARGIN_B = 50f;
    private static final float LEADING  = 14f;

    // Fonts for PDFBox 3.x
    private static final PDFont FONT = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private static final PDFont FONT_BOLD = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

    private static final float FS_NORMAL = 10f;
    private static final float FS_LABEL  = 10f;
    private static final float FS_H1     = 20f;
    private static final float FS_H2     = 12f;

    // Table column widths
    private static final float COL_NAME = 250f;
    private static final float COL_QTY = 50f;
    private static final float COL_UNIT = 80f;
    private static final float COL_LINE = 90f;

    private final Locale locale;
    private final NumberFormat moneyFmt;
    private final DateTimeFormatter dateTimeFmt;

    private PDFont bodyFont = FONT;
    private PDFont boldFont = FONT_BOLD;

    public PdfMaker(Locale locale) {
        this.locale = locale != null ? locale : Locale.forLanguageTag("nl-NL");
        this.moneyFmt = NumberFormat.getCurrencyInstance(this.locale);
        this.dateTimeFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    }

    /**
     * Main entry point to generate the invoice.
     */
    public void generate(Path logoPath, Customer customer, Order order, Path outputFile) throws IOException {
        try (PDDocument doc = new PDDocument()) {

            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {

                float y = page.getMediaBox().getHeight() - MARGIN_T;

                // Header
                y = drawHeader(doc, cs, page, logoPath, y);

                // Customer + Order info
                y -= 10;
                y = drawCustomerAndOrder(cs, page, customer, order, y);

                // Items table
                y -= 20;
                DrawContext drawContext = new DrawContext(doc, page, cs, y);
                y = drawItemsTable(drawContext, order.getItems());

                float afterTableY = y - 10;

                // Ensure space for totals & notes
                float needed = (LEADING * 3) + (LEADING * 3) + 40;
                if (afterTableY < (MARGIN_B + needed)) {
                    cs.close();
                    page = new PDPage(PDRectangle.A4);
                    doc.addPage(page);
                    try (PDPageContentStream cs2 = new PDPageContentStream(doc, page)) {
                        y = page.getMediaBox().getHeight() - MARGIN_T;
                        drawTotalsAndNotes(cs2, page, order, y);
                    }
                } else {
                    drawTotalsAndNotes(cs, page, order, afterTableY);
                }
            }

            if (outputFile.getParent() != null)
                Files.createDirectories(outputFile.getParent());

            doc.save(outputFile.toFile());
        }
    }

    // ---------------------------------------------------------------
    // HEADER
    // ---------------------------------------------------------------
    private float drawHeader(PDDocument doc, PDPageContentStream cs, PDPage page, Path logoPath, float y) throws IOException {
        float x = MARGIN_L;

        // Company name
        setFont(cs, boldFont, FS_H1);
        drawText(cs, x, y, Constants.COMPANY_NAME != null ? Constants.COMPANY_NAME : "Company");
        y -= (LEADING + 2);

        // Invoice title
        setFont(cs, boldFont, FS_H2);
        drawText(cs, x, y, "Invoice");
        y -= (LEADING + 4);

        // Logo (right side)
        if (logoPath != null && Files.exists(logoPath)) {
            BufferedImage img = ImageIO.read(logoPath.toFile());
            if (img != null) {
                PDImageXObject logo = LosslessFactory.createFromImage(doc, img);
                float maxW = 120f;
                float scale = Math.min(1f, maxW / logo.getWidth());
                float w = logo.getWidth() * scale;
                float h = logo.getHeight() * scale;
                float lx = page.getMediaBox().getWidth() - MARGIN_R - w;
                float ly = page.getMediaBox().getHeight() - MARGIN_T - h + 8;
                cs.drawImage(logo, lx, ly, w, h);
            }
        }

        return y;
    }

    // ---------------------------------------------------------------
    // CUSTOMER + ORDER INFO
    // ---------------------------------------------------------------
    private float drawCustomerAndOrder(PDPageContentStream cs, PDPage page, Customer customer, Order order, float y) throws IOException {
        float leftX = MARGIN_L;
        //float rightX = page.getMediaBox().getWidth() / 2 + 10;

        // LEFT: Customer
        setFont(cs, boldFont, FS_H2);
        drawText(cs, leftX, y, "Customer");
        y -= LEADING;

        setFont(cs, bodyFont, FS_NORMAL);
        y = drawLabelValue(cs, leftX, y, "Name",
                safe(customer.getFirstname()) + " " + safe(customer.getLastName()));
        y = drawLabelValue(cs, leftX, y, "Address",
                safe(customer.getAddress()));
        y = drawLabelValue(cs, leftX, y, "Zip/City",
                safe(customer.getZipCode()) + " " + safe(customer.getCity()));
        y = drawLabelValue(cs, leftX, y, "Email", safe(customer.getEmail()));
        y = drawLabelValue(cs, leftX, y, "Phone", safe(customer.getPhoneNumber()));

        // RIGHT: Order
        //float yRight = y + 5 * LEADING;
        float yOrder = y - 10;

        setFont(cs, boldFont, FS_H2);
        drawText(cs, leftX, yOrder, "Order");
        yOrder -= LEADING;

        setFont(cs, bodyFont, FS_NORMAL);
        yOrder = drawLabelValue(cs, leftX, yOrder, "Order #",
                String.valueOf(order.getId()));

        Timestamp tPlaced = order.getOrderPlacedTimestamp();
        Timestamp tPickup = order.getPickupTime();

        yOrder = drawLabelValue(cs, leftX, yOrder, "Order placed",
                tPlaced != null ? dateTimeFmt.format(tPlaced.toLocalDateTime()) : "");

        yOrder = drawLabelValue(cs, leftX, yOrder, "Pickup time",
                tPickup != null ? dateTimeFmt.format(tPickup.toLocalDateTime()) : "");

        return Math.min(y, yOrder);
    }

    // ---------------------------------------------------------------
    // ITEMS TABLE
    // ---------------------------------------------------------------
    private float drawItemsTable(DrawContext drawContext, List<Item> items) throws IOException {

        PDPageContentStream cs = drawContext.cs;
        PDPage page = drawContext.page;

        float x = MARGIN_L;
        float y = drawContext.y;

        // Header
        setFont(cs, boldFont, FS_NORMAL);
        drawText(cs, x, y, "Item");
        drawText(cs, x + COL_NAME + 10, y, "Qty");
        drawText(cs, x + COL_NAME + COL_QTY + 20, y, "Unit Price");
        drawText(cs, x + COL_NAME + COL_QTY + COL_UNIT + 30, y, "Subtotal");

        y -= (LEADING + 4);

        cs.moveTo(x, y);
        cs.lineTo(x + COL_NAME + COL_QTY + COL_UNIT + COL_LINE + 30, y);
        cs.stroke();

        y -= 20;

        setFont(cs, bodyFont, FS_NORMAL);

        for (Item item : items) {

            String itemName = safe(item.getFullDisplayName());
            List<String> nameLines = splitToWidth(itemName, bodyFont, FS_NORMAL, page, COL_NAME);

            float rowHeight = Math.max(LEADING, nameLines.size() * LEADING);
            float minY = MARGIN_B + 120;

            // Page break?
            if (y - rowHeight < minY) {
                cs.close();
                drawContext.page = new PDPage(PDRectangle.A4);
                drawContext.doc.addPage(drawContext.page);
                drawContext.cs = new PDPageContentStream(drawContext.doc, drawContext.page);
                cs = drawContext.cs;
                page = drawContext.page;
                y = page.getMediaBox().getHeight() - MARGIN_T;

                // Re-draw header
                setFont(cs, boldFont, FS_NORMAL);
                drawText(cs, x, y, "Item");
                drawText(cs, x + COL_NAME + 10, y, "Qty");
                drawText(cs, x + COL_NAME + COL_QTY + 20, y, "Unit Price");
                drawText(cs, x + COL_NAME + COL_QTY + COL_UNIT + 30, y, "Subtotal");

                y -= (LEADING + 4);

                cs.moveTo(x, y);
                cs.lineTo(x + COL_NAME + COL_QTY + COL_UNIT + COL_LINE + 30, y);
                cs.stroke();
                y -= 8;

                setFont(cs, bodyFont, FS_NORMAL);
            }

            // Name (multi-line)
            float textY = y;
            for (String line : nameLines) {
                drawText(cs, x, textY, line);
                textY -= LEADING;
            }

            // Qty
            Integer qty = item.getQuantity();
            drawText(cs, x + COL_NAME + 10, y, qty != null ? qty.toString() : "0");

            // Unit price
            BigDecimal unit = new BigDecimal(safe(item.getUnitPrice()));
            drawText(cs, x + COL_NAME + COL_QTY + 20, y, moneyFmt.format(unit));

            // Subtotal
            BigDecimal subtotal = unit.multiply(BigDecimal.valueOf(qty != null ? qty : 0));
            drawText(cs, x + COL_NAME + COL_QTY + COL_UNIT + 30, y, moneyFmt.format(subtotal));

            y -= (rowHeight + 6);
        }

        return y;
    }

    // ---------------------------------------------------------------
    // TOTALS + NOTES
    // ---------------------------------------------------------------
    private void drawTotalsAndNotes(
            PDPageContentStream cs,
            PDPage page,
            Order order,
            float y
    ) throws IOException {

        float rightX = page.getMediaBox().getWidth() - MARGIN_R - 230;

        setFont(cs, boldFont, FS_NORMAL);
        drawText(cs, rightX, y, "Subtotal:");
        drawText(cs, rightX + 120, y, moneyFmt.format(order.getSubTotalCost()));
        y -= LEADING;

        setFont(cs, bodyFont, FS_NORMAL);
        drawText(cs, rightX, y, "VAT:");
        drawText(cs, rightX + 120, y, moneyFmt.format(order.getTotalVAT()));
        y -= LEADING;

        setFont(cs, boldFont, FS_NORMAL + 1);
        drawText(cs, rightX, y, "Total:");
        drawText(cs, rightX + 120, y, moneyFmt.format(order.getTotalCost()));
        y -= (LEADING + 10);

        // Notes
        if (safe(order.getOrderNotes()).isEmpty())
            return;

        setFont(cs, boldFont, FS_H2);
        drawText(cs, MARGIN_L, y, "Order Notes");
        y -= (LEADING + 2);

        setFont(cs, bodyFont, FS_NORMAL);

        float maxW = page.getMediaBox().getWidth() - MARGIN_L - MARGIN_R;
        List<String> lines = splitToWidth(order.getOrderNotes(), bodyFont, FS_NORMAL, page, maxW);
        for (String line : lines) {
            drawText(cs, MARGIN_L, y, line);
            y -= LEADING;
            if (y < MARGIN_B) break;
        }
    }

    // ---------------------------------------------------------------
    // HELPERS
    // ---------------------------------------------------------------
    private static class DrawContext {
        PDDocument doc;
        PDPage page;
        PDPageContentStream cs;
        float y;

        DrawContext(PDDocument d, PDPage p, PDPageContentStream c, float y) {
            this.doc = d;
            this.page = p;
            this.cs = c;
            this.y = y;
        }
    }

    private static void setFont(PDPageContentStream cs, PDFont font, float size) throws IOException {
        cs.setFont(font, size);
    }

    private static void drawText(PDPageContentStream cs, float x, float y, String t) throws IOException {
        cs.beginText();
        cs.newLineAtOffset(x, y);
        cs.showText(t != null ? t : "");
        cs.endText();
    }

    private float drawLabelValue(PDPageContentStream cs, float x, float y, String label, String value)
            throws IOException {
        setFont(cs, boldFont, FS_LABEL);
        drawText(cs, x, y, label + ":");
        setFont(cs, bodyFont, FS_NORMAL);
        drawText(cs, x + 80, y, value != null ? value : "");
        return y - LEADING;
    }

    private static List<String> splitToWidth(
            String text,
            PDFont font,
            float fs,
            PDPage page,
            float maxWidth
    ) throws IOException {

        List<String> lines = new ArrayList<>();
        if (text == null || text.isBlank()) {
            lines.add("");
            return lines;
        }

        String[] words = text.trim().split("\\s+");
        StringBuilder line = new StringBuilder();

        for (String w : words) {
            String test = line.length() == 0 ? w : line + " " + w;
            float width = (font.getStringWidth(test) / 1000f) * fs;
            if (width <= maxWidth) {
                line = new StringBuilder(test);
            } else {
                if (line.length() > 0) lines.add(line.toString());
                if ((font.getStringWidth(w) / 1000f) * fs > maxWidth) {
                    lines.addAll(hardWrapWord(w, font, fs, maxWidth));
                    line = new StringBuilder();
                } else {
                    line = new StringBuilder(w);
                }
            }
        }

        if (line.length() > 0) lines.add(line.toString());
        return lines;
    }

    private static List<String> hardWrapWord(
            String word,
            PDFont font,
            float fs,
            float maxWidth
    ) throws IOException {
        List<String> parts = new ArrayList<>();
        StringBuilder part = new StringBuilder();

        for (char c : word.toCharArray()) {
            String test = part.toString() + c;
            float w = (font.getStringWidth(test) / 1000f) * fs;
            if (w <= maxWidth) {
                part.append(c);
            } else {
                parts.add(part.toString());
                part = new StringBuilder(String.valueOf(c));
            }
        }

        if (part.length() > 0) parts.add(part.toString());
        return parts;
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }
}