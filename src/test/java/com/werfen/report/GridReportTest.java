package com.werfen.report;

import com.werfen.report.model.*;
import com.werfen.report.service.GridReportService;
import com.werfen.report.service.template.BaseXlsxReportTemplateBuilder;
import net.sf.jasperreports.engine.JRException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GridReportTest {
    public static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Test
    public void generateGridPdfReport() {
        try {
            GridReportService gridReportService = new GridReportService();
            File file = gridReportService.build(this.getConfiguration("grid_report"), this.getData(), ReportFormat.PDF);
            file.createNewFile();
        } catch (JRException | IOException e) {
            e.printStackTrace();
        }

        try (PDDocument original = PDDocument.load(new File("grid_report_golden.pdf"));
             PDDocument generated = PDDocument.load(new File("grid_report.pdf"))) {
                PDFTextStripper textStripper = new PDFTextStripper();
                assertEquals(textStripper.getText(original), textStripper.getText(generated));
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void generateGridXlsxReport() {
        try {
            GridReportService gridReportService = new GridReportService();
            File file = gridReportService.build(this.getConfiguration("grid_report"), this.getData(), ReportFormat.EXCEL);
            file.createNewFile();
        } catch (JRException | IOException e) {
            e.printStackTrace();
        }
    }

    private GridReportData getData() {
        return GridReportData.builder()
                .row(GridReportRow.builder().value(ReportField.builder().name("col1").value("val1").build())
                        .build()
                ).build();
    }

    private GridReportConfiguration getConfiguration(String fileName) throws IOException {
         return GridReportConfiguration.builder()
                .outputFilePath(fileName)
                .headerConfiguration(this.buildHeaderConfiguration())
                .footerConfiguration(this.buildReportFooterConfiguration())
                .gridColumnConfiguration(GridColumnConfiguration.builder().name("col1").width(GridReportColumnWidth.COLUMN_WIDTH_3).translation("col2").build())
                .build();
    }

    private ReportHeaderConfiguration buildHeaderConfiguration() {
        return ReportHeaderConfiguration.builder()
                .title("Grid report")
                .field1(ReportField.builder().name("Lab name").value("Name").build())
                .field2(ReportField.builder().name("Second").value("Another").build())
                .field3(ReportField.builder().name("Third").value("Another one").build())
                .field4(ReportField.builder().name("Fourth").value("Last one").build())
                .build();
    }

    private ReportFooterConfiguration buildReportFooterConfiguration() {
        return ReportFooterConfiguration.builder()
                .createdAt(ZonedDateTime.of(2021,12,1,10,1,1,1, ZoneId.systemDefault()).toOffsetDateTime().format(DateTimeFormatter.ofPattern(DATE_FORMAT)))
                .additionalField1(ReportField.builder().name("User").value("My self").build())
                .additionalField2(ReportField.builder().name("Second").value("Another").build())
                .build();
    }
}
