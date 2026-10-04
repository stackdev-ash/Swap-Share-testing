package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelReporter {
    private static final String FILE = TestConfig.EXCEL_FILE;
    private static final String SHEET = "Test Results";
    
    public static synchronized void saveResult(
            String testCaseId,
            String scenario,
            String expected,
            String actual,
            String status) {

        File file = new File(FILE);
        File parent = file.getParentFile();

        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        Workbook workbook = null;

        try {
            if (file.exists()) {
                try (FileInputStream input = new FileInputStream(file)) {
                    workbook = new XSSFWorkbook(input);
                }
            } else {
                workbook = new XSSFWorkbook();
            }

            Sheet sheet = workbook.getSheet(SHEET);

            if (sheet == null) {
                sheet = workbook.createSheet(SHEET);
                createHeader(sheet);
            }
            int nextRow = sheet.getLastRowNum() + 1;

            Row row = sheet.createRow(nextRow);

            row.createCell(0).setCellValue(testCaseId);
            row.createCell(1).setCellValue(scenario);
            row.createCell(2).setCellValue(expected);
            row.createCell(3).setCellValue(actual);
            row.createCell(4).setCellValue(status);
            row.createCell(5).setCellValue(
                    java.time.LocalDateTime.now().toString()
            );

            try (FileOutputStream output = new FileOutputStream(file)) {
                workbook.write(output);
            }

            workbook.close();

        } catch (IOException e) {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException ignored) {
                }
            }

            throw new RuntimeException(
                    "Failed to write Excel report.", e
            );
        }
    }

    private static void createHeader(Sheet sheet) {
        Row header = sheet.createRow(0);

        String[] headers = {
                "Test Case ID",
                "Test Scenario",
                "Expected Result",
                "Actual Result",
                "Status",
                "Execution Date"
        };

        for (int i = 0; i < headers.length; i++) {
            header.createCell(i).setCellValue(headers[i]);
        }
    }
}
