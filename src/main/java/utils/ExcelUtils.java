package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.util.concurrent.locks.ReentrantLock;

public class ExcelUtils implements AutoCloseable {

    private static final Logger logger = LogManager.getLogger(ExcelUtils.class);

    private final String path;
    private final ReentrantLock lock = new ReentrantLock();

    private XSSFWorkbook workbook;

    /**
     * Loads (or creates) the Excel file for manipulation.
     */
    public ExcelUtils(String path) {
        this.path = path;

        File file = new File(path);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(path)) {
                workbook = new XSSFWorkbook(fis);
                logger.info("Loaded existing Excel file: {}", path);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load Excel file: " + path, e);
            }
        } else {
            workbook = new XSSFWorkbook();
            logger.info("Created new Excel workbook in memory.");
        }
    }

    /**
     * Save the workbook to disk.
     */
    public void save() {
        lock.lock();
        try (FileOutputStream fos = new FileOutputStream(path)) {
            workbook.write(fos);
            logger.info("Excel file saved: {}", path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save Excel file: " + path, e);
        } finally {
            lock.unlock();
        }
    }

    public int getRowCount(String sheetName) {
        Sheet sheet = getOrCreateSheet(sheetName);
        int rowCount = sheet.getLastRowNum();
        logger.info("Row count in sheet '{}': {}", sheetName, rowCount);
        return rowCount;
    }

    public int getCellCount(String sheetName, int rownum) {
        Sheet sheet = getOrCreateSheet(sheetName);
        Row row = sheet.getRow(rownum);
        int count = (row != null) ? row.getLastCellNum() : 0;
        logger.info("Cell count in sheet '{}', row {}: {}", sheetName, rownum, count);
        return count;
    }

    public String getCellData(String sheetName, int rownum, int colnum) {
        Sheet sheet = getOrCreateSheet(sheetName);
        Row row = sheet.getRow(rownum);
        if (row == null) return "";
        Cell cell = row.getCell(colnum);
        if (cell == null) return "";

        DataFormatter formatter = new DataFormatter();
        String data = formatter.formatCellValue(cell);
        logger.info("Read cell data [{}] from sheet '{}', row {}, col {}", data, sheetName, rownum, colnum);
        return data;
    }

    public void setCellData(String sheetName, int rownum, int colnum, String data) {
        lock.lock();
        try {
            Sheet sheet = getOrCreateSheet(sheetName);
            Row row = sheet.getRow(rownum);
            if (row == null) {
                row = sheet.createRow(rownum);
            }
            Cell cell = row.createCell(colnum);
            cell.setCellValue(data);

            logger.info("Wrote cell data [{}] in sheet '{}', row {}, col {}", data, sheetName, rownum, colnum);
        } finally {
            lock.unlock();
        }
    }

    public void fillCellColor(String sheetName, int rownum, int colnum, IndexedColors color) {
        lock.lock();
        try {
            Sheet sheet = getOrCreateSheet(sheetName);
            Row row = sheet.getRow(rownum);
            if (row == null) {
                row = sheet.createRow(rownum);
            }
            Cell cell = row.getCell(colnum);
            if (cell == null) {
                cell = row.createCell(colnum);
            }

            CellStyle style = workbook.createCellStyle();
            style.setFillForegroundColor(color.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cell.setCellStyle(style);

            logger.info("Filled cell color [{}] in sheet '{}', row {}, col {}", color, sheetName, rownum, colnum);
        } finally {
            lock.unlock();
        }
    }

    public void fillGreenColor(String sheetName, int rownum, int colnum) {
        fillCellColor(sheetName, rownum, colnum, IndexedColors.GREEN);
    }

    public void fillRedColor(String sheetName, int rownum, int colnum) {
        fillCellColor(sheetName, rownum, colnum, IndexedColors.RED);
    }

    public void fillYellowColor(String sheetName, int rownum, int colnum) {
        fillCellColor(sheetName, rownum, colnum, IndexedColors.YELLOW);
    }

    public void fillBlueColor(String sheetName, int rownum, int colnum) {
        fillCellColor(sheetName, rownum, colnum, IndexedColors.AQUA);
    }

    public void fillOrangeColor(String sheetName, int rownum, int colnum) {
        fillCellColor(sheetName, rownum, colnum, IndexedColors.ORANGE);
    }

    private Sheet getOrCreateSheet(String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            sheet = workbook.createSheet(sheetName);
            logger.info("Created new sheet: {}", sheetName);
        }
        return sheet;
    }

    /**
     * Closes the workbook. Always call this when finished!
     */
    @Override
    public void close() {
        if (workbook != null) {
            try {
                workbook.close();
                logger.info("Workbook closed for file: {}", path);
            } catch (IOException e) {
                logger.error("Failed to close workbook for file: {}", path, e);
            }
        }
    }
}
