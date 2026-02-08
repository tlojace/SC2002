package services;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ExcelReaderWriter {

	/* Writing (Saving) */
	public static void write(List<List<String>> data, String filePath) throws IOException {
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Sheet1");

		int rowNum = 0;
		for (List<String> rowData : data) {
			Row row = sheet.createRow(rowNum++);
			int cellNum = 0;
			for (String cellData : rowData) {
				Cell cell = row.createCell(cellNum++);
				cell.setCellValue(cellData);
			}
		}

		FileOutputStream fos = new FileOutputStream(filePath);
		workbook.write(fos);
		fos.close();
		workbook.close();
	}

	/* Reading */
	public static List<List<String>> read(String filePath) throws IOException {
        List<List<String>> data = new ArrayList<>();

        FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            Iterator<Cell> cellIterator = row.cellIterator();
            List<String> rowData = new ArrayList<>();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                rowData.add(getCellValueAsString(cell));
            }
            data.add(rowData);
        }

        workbook.close();
        fis.close();

        return data;
    }

	/* Helper Method to Handle Different Cell Types */
	private static String getCellValueAsString(Cell cell) {
		if (cell == null || cell.getCellType() == CellType.BLANK) {
			return ("Empty");
		}
		switch (cell.getCellType()) {
			case STRING:
				return cell.getStringCellValue();
			case NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					// Use SimpleDateFormat to format date and time as needed
					SimpleDateFormat dateFormat = new SimpleDateFormat("d-MMM-yyyy h:mm:ss a");
					return dateFormat.format(cell.getDateCellValue());
				} else {
					// Avoid scientific notation for large numbers
					return String.format("%.0f", cell.getNumericCellValue());
				}
			case BOOLEAN:
				return Boolean.toString(cell.getBooleanCellValue());
			case FORMULA:
				return cell.getCellFormula();
			default:
				return "";
		}
	}

}

