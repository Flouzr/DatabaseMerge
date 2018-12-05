package dbUtil;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

class ParseExcel {

    ArrayList excelText(String workbook) throws IOException {

        int rowLength = 0;

        if (workbook.contains("NEW")) {
            rowLength = 11;
        }else if (workbook.contains("USED")){
            rowLength = 10;
        }

        // Rows from row 8 (actually 9) and below
        StringBuilder sbLotInfo = new StringBuilder();

        InputStream input = new FileInputStream(workbook);
        Workbook wb = new XSSFWorkbook(input);

        ArrayList<String> lotInfo = new ArrayList<>();

        // should never be 2 pages so take the first
        Sheet sheet = wb.getSheetAt(0);

        for (Row row : sheet) {
            int cellCount = 0;

            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                // Set all cells to type string for easier handling
                // otherwise use a which statement
                cell.setCellType(CellType.STRING);

                // Start taking from 2nd column in and 8th row down
                if (cell.getColumnIndex() >= 2 && cell.getRowIndex() >= 8) {
                    if (cellCount < rowLength){
                        sbLotInfo.append(cell.getStringCellValue()).append(" ");
                    }else {
                        sbLotInfo.append(cell.getStringCellValue()).append("\n");
                        lotInfo.add(sbLotInfo.toString());
                        sbLotInfo.setLength(0);
                    }
                    cellCount++;
                }
            }
        }
        return lotInfo;
    }
}