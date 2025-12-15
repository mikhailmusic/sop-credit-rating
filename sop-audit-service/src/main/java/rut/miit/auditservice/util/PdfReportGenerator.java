package rut.miit.auditservice.util;

import org.openpdf.text.*;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import rut.miit.auditservice.dto.*;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
public class PdfReportGenerator {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private static final Font TITLE_FONT = new Font(Font.HELVETICA, 18, Font.BOLD);
    private static final Font HEADING_FONT = new Font(Font.HELVETICA, 12, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.HELVETICA, 10, Font.NORMAL);

    public byte[] generate(AuditStatisticDto statistics) throws DocumentException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (Document document = new Document(PageSize.A4, 40, 40, 40, 40)) {
            PdfWriter.getInstance(document, baos);
            document.open();

            document.addTitle("Audit Report");
            document.addAuthor("Audit Service");

            addHeader(document, statistics);

            addKeyMetrics(document, statistics);

            addCompactStatistics(document, statistics);
        }

        return baos.toByteArray();
    }

    private void addHeader(Document document, AuditStatisticDto statistics) throws DocumentException {
        Paragraph title = new Paragraph("СТАТИСТИКА ПО ЗАЯВКАМ", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(8);
        document.add(title);

        Paragraph period = new Paragraph(
                String.format("Период: %s - %s  |  Создан: %s",
                        statistics.dataRangeStart().format(DATE_FORMATTER),
                        statistics.dataRangeEnd().format(DATE_FORMATTER),
                        statistics.generatedAt().format(DATE_FORMATTER)),
                HEADING_FONT
        );
        period.setAlignment(Element.ALIGN_CENTER);
        period.setSpacingAfter(12);
        document.add(period);
    }

    private void addKeyMetrics(Document document, AuditStatisticDto statistics) throws DocumentException {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingAfter(15);

        addMetricCell(table, "Заявки", String.valueOf(statistics.assessmentRequests().totalRequests()));
        addMetricCell(table, "Одобрено", String.valueOf(statistics.assessmentResponses().approvedCount()));
        addMetricCell(table, "Предложения", String.valueOf(statistics.offers().totalOffers()));
        addMetricCell(table, "Конверсия", String.format("%.1f%%", statistics.assessmentResponses().approvalRate()));

        document.add(table);
    }

    private void addMetricCell(PdfPTable table, String label, String value) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.NO_BORDER);
        Paragraph p = new Paragraph();
        p.add(new Chunk(label + "\n", HEADING_FONT));
        p.add(new Chunk(value, HEADING_FONT));
        p.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(p);

        table.addCell(cell);
    }

    private void addCompactStatistics(Document document, AuditStatisticDto statistics) throws DocumentException {
        addSectionTitle(document, "Статистика заявок и оценки");

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 2, 3, 2});
        table.setSpacingAfter(12);

        AssessmentRequestDto req = statistics.assessmentRequests();
        AssessmentResponseDto resp = statistics.assessmentResponses();

        addCompactRow(table, "Всего заявок", String.valueOf(req.totalRequests()),
                "Одобрено", String.valueOf(resp.approvedCount()));
        addCompactRow(table, "Средняя сумма", formatCurrency(req.averageRequestedAmount()),
                "Отклонено", String.valueOf(resp.rejectedCount()));
        addCompactRow(table, "Мин / Макс сумма",
                formatCurrency(req.minRequestedAmount()) + " / " + formatCurrency(req.maxRequestedAmount()),
                "Кредитный рейтинг", String.format("%.1f (%.1f-%.1f)",
                        resp.averageCreditScore(), resp.minCreditScore(), resp.maxCreditScore()));
        addCompactRow(table, "Средний срок", String.format("%.0f мес.", req.averageTermMonths()),
                "% одобрения", String.format("%.2f%%", resp.approvalRate()));
        addCompactRow(table, "Средний возраст", String.format("%.0f лет", req.averageAge()),
                "Средний доход", formatCurrency(req.averageAnnualIncome()));

        document.add(table);

        addCompactDistributions(document, statistics);

        addOfferStatisticsCompact(document, statistics.offers());
    }

    private void addCompactDistributions(Document document, AuditStatisticDto statistics) throws DocumentException {
        addSectionTitle(document, "Распределения");

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingAfter(12);

        addDistributionHeader(table, "Цель займа");
        addDistributionHeader(table, "Статус занятости");
        addDistributionHeader(table, "Уровень риска");
        addDistributionHeader(table, "Комментарий");

        addDistributionColumn(table, statistics.assessmentRequests().purposeDistribution());
        addDistributionColumn(table, statistics.assessmentRequests().employmentStatusDistribution());
        addDistributionColumn(table, statistics.assessmentResponses().riskLevelDistribution());
        addDistributionColumn(table, statistics.assessmentResponses().topRejectionReasons());

        document.add(table);
    }

    private void addDistributionHeader(PdfPTable table, String title) {
        PdfPCell cell = new PdfPCell(new Phrase(title, NORMAL_FONT));
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addDistributionColumn(PdfPTable table, Map<String, Long> distribution) {
        StringBuilder content = new StringBuilder();
        distribution.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> content.append(String.format("%s: %d\n", entry.getKey(), entry.getValue())));

        if (content.isEmpty()) {
            content.append("-");
        }

        PdfPCell cell = new PdfPCell(new Phrase(content.toString().trim(), NORMAL_FONT));
        cell.setPadding(8);
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        table.addCell(cell);
    }

    private void addOfferStatisticsCompact(Document document, OfferGeneratedDto stats) throws DocumentException {
        addSectionTitle(document, "Предложения");

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 2, 3, 2});
        table.setSpacingAfter(12);

        addCompactRow(table, "Всего предложений", String.valueOf(stats.totalOffers()),
                "Средняя ставка", String.format("%.2f%%", stats.averageAPR()));
        addCompactRow(table, "Средняя сумма", formatCurrency(stats.averageApprovedAmount()),
                "Диапазон ставки", String.format("%.2f%% - %.2f%%", stats.minAPR(), stats.maxAPR()));
        addCompactRow(table, "Мин / Макс сумма",
                formatCurrency(stats.minApprovedAmount()) + " / " + formatCurrency(stats.maxApprovedAmount()),
                "Средний платеж", formatCurrency(stats.averageMonthlyPayment()));
        addCompactRow(table, "Средний срок", String.format("%.0f мес.", stats.averageTermMonths()),
                "", "");

        document.add(table);
    }

    private void addCompactRow(PdfPTable table, String label1, String value1, String label2, String value2) {
        PdfPCell labelCell1 = new PdfPCell(new Phrase(label1, NORMAL_FONT));
        labelCell1.setPadding(6);
        table.addCell(labelCell1);

        PdfPCell valueCell1 = new PdfPCell(new Phrase(value1, NORMAL_FONT));
        valueCell1.setPadding(6);
        table.addCell(valueCell1);

        PdfPCell labelCell2 = new PdfPCell(new Phrase(label2, NORMAL_FONT));
        labelCell2.setPadding(6);
        table.addCell(labelCell2);

        PdfPCell valueCell2 = new PdfPCell(new Phrase(value2, NORMAL_FONT));
        valueCell2.setPadding(6);
        table.addCell(valueCell2);
    }

    private void addSectionTitle(Document document, String title) throws DocumentException {
        Paragraph section = new Paragraph(title, HEADING_FONT);
        section.setSpacingBefore(12);
        section.setSpacingAfter(8);
        document.add(section);
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.valueOf(1000)) < 0) {
            return String.format("₽%.0f", amount);
        }
        return String.format("₽%,d", amount.intValue());
    }
}