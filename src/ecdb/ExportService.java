package ecdb;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExportService {

	private DatabaseConnectionService dbcs = DatabaseConnectionService.getService();
	private int sheetNum = 10;

	public ExportService() {

	}

	public void exportFile(Object[][] data, Object[][] data2) {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("有CK");
		XSSFSheet sheet2 = wb.createSheet("无CK");
		
		int rownum = 0;

		for (int i = 0; i<data.length ; i++) {
			Row row = sheet.createRow(rownum++);
			Object[] currentRow = data[i];
            int cellnum = 0;
            for (Object obj : currentRow)
            {
               Cell cell = row.createCell(cellnum++);
               if(obj instanceof String)
                    cell.setCellValue((String)obj);
                else if(obj instanceof Integer)
                    cell.setCellValue((Integer)obj);
            }
		}
		rownum = 0;
		for (int i = 0; i<data2.length ; i++) {
			Row row = sheet2.createRow(rownum++);
			Object[] currentRow = data2[i];
            int cellnum = 0;
            for (Object obj : currentRow)
            {
               Cell cell = row.createCell(cellnum++);
               if(obj instanceof String)
                    cell.setCellValue((String)obj);
                else if(obj instanceof Integer)
                    cell.setCellValue((Integer)obj);
            }
		}
		try
        {
            FileOutputStream out = new FileOutputStream(new File("结果.xlsx"));
            wb.write(out);
            out.close();
            System.out.println("howtodoinjava_demo.xlsx written successfully on disk.");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
	}

	public void removeTables() {
		String query = "DROP TABLE 原始数据 DROP TABLE 结果 DROP TABLE 系统 ";
		for (int i = 1; i < sheetNum - 1; i++) {
			query = query + "DROP TABLE 对应数据" + Integer.toString(i) + " ";
		}
		Connection c = dbcs.getConnection();

		try {
			PreparedStatement stmt = c.prepareStatement(query);
			stmt.executeQuery();
		} catch (SQLException e) {
			if (!e.getMessage().equals("The statement did not return a result set.")) {
				System.out.println(query);
				Main.gui.displayMessage(e.getMessage());
			}
		}
	}

}
