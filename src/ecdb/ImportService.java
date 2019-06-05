package ecdb;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ImportService {

	private DatabaseConnectionService dbcs = DatabaseConnectionService.getService();
	private int sheetNum = 10;

	public ImportService() {

	}

	public void importFile(File file) {
		XSSFWorkbook wb = null;
		InputStream fileToRead = null;

		try {
			fileToRead = new FileInputStream(file);
			wb = new XSSFWorkbook(fileToRead);
		} catch (Exception e) {
			Main.gui.displayMessage(e.getMessage());
			return;
		}

		sheetNum = wb.getNumberOfSheets();
		for (int index = 0; index < sheetNum; index++) {
			XSSFSheet sheet = wb.getSheetAt(index);
			Main.gui.frame.setTitle("importing " + sheet.getSheetName());
			XSSFRow row;
			XSSFCell cell;
			DataFormatter formatter = new DataFormatter();

			int rows = sheet.getPhysicalNumberOfRows();
			int cols = 0;

			if (index == 0) {
				cols = 9;

				for (int i = 0; i < rows; i++) {
					Main.gui.frame.setTitle("importing " + sheet.getSheetName() + " " + i + "/" + rows);
					row = sheet.getRow(i);

					int[] noAndMonth = new int[cols];
					double[] weightAndPrice = new double[cols];
					String[] information = new String[cols];

					for (int c = 0; c < cols; c++) {
						cell = row.getCell((short) c);
						if (i == 0) {
							information[c] = formatter.formatCellValue(cell);
						} else {

							if (cell == null) {
								continue;
							}
							switch (c) {
							case 0:
							case 7:
								noAndMonth[c] = (int) cell.getNumericCellValue();
								break;
							case 3:
							case 4:
							case 5:
								weightAndPrice[c] = cell.getNumericCellValue();
								break;
							case 1:
							case 2:
							case 6:
							case 8:
								information[c] = formatter.formatCellValue(cell);
								break;
							}
						}
					}
					if (i == 0) {
						addKey0(information); 
					} else {
						addData0(noAndMonth, weightAndPrice, information);
					}
				}
			} else {
				cols = 11;

				for (int i = 0; i < rows; i++) {
					Main.gui.frame.setTitle("importing " + sheet.getSheetName() + " " + i + "/" + rows);
					row = sheet.getRow(i);

					String[] information = new String[cols];

					for (int c = 0; c < cols; c++) {
						cell = row.getCell((short) c);
						if (cell == null) {
							continue;
						}
						information[c] = formatter.formatCellValue(cell);
					}
					if (i == 0) {
						addKey1(information, index);
					} else {
						addData1(information, index);
					}
				}
			}

		}

		try {
			wb.close();
		} catch (Exception e) {
			Main.gui.displayMessage(e.getMessage());
		}
		Main.gui.displayMessage("导入完成");
	}

	private void addData0(int[] noAndMonth, double[] weightAndPrice, String[] information) {
		for(int i = 0; i < 9; i++) {
			if (information[i] == null) {
				information[i] = "";
			}
		}
		String query = "INSERT INTO 原始数据 VALUES (";
		query = query + noAndMonth[0] + ",";
		query = query + "'" + information[1] + "'" + ",";
		query = query + "'" + information[2] + "'" + ",";
		query = query + weightAndPrice[3] + ",";
		query = query + weightAndPrice[4] + ",";
		query = query + weightAndPrice[5] + ",";
		query = query + "'" + information[6] + "'" + ",";
		query = query + "'" + noAndMonth[7] + "'" + ",";
		query = query + "'" + information[8] + "'" + ")";
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

	private void addData1(String[] information,int index) {
		for(int i = 0; i < information.length; i++) {
			if (information[i] == null || information[i].length() <= 0) {
				information[i] = "";
				continue;
			}
			if (information[i].charAt(0) == '\'') {
				information[i] = information[i].substring(1);
			}
		}
		String name = "对应数据" + Integer.toString(index);
		String query = "INSERT INTO "
				+ name
				+ " VALUES (";
		query = query + "'" + information[0] + "'" + ",";
		query = query + "'" + information[1] + "'" + ",";
		query = query + "'" + information[2] + "'" + ",";
		query = query + "'" + information[3] + "'" + ")";
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
	
	private void addKey0(String[] key) {
		String query = "CREATE TABLE 原始数据 (\r\n" + "    " + key[0] + " int,\r\n" + // 编号
				"    " + key[1] + " varchar(8000),\r\n" + // 运单编号
				"    " + key[2] + " varchar(8000),\r\n" + // 收件省
				"    " + key[3] + " float,\r\n" + // 重量
				"    " + key[4] + " float,\r\n" + // 计费重量
				"    " + key[5] + " float,\r\n" + // 单位总价
				"    " + key[6] + " varchar(8000),\r\n" + // 快递公司
				"    " + key[7] + " int,\r\n" + // 月份
				"    " + key[8] + " varchar(8000),\r\n" + // 备注
				")";

		Connection c = dbcs.getConnection();

		try {
			PreparedStatement stmt = c.prepareStatement(query);
			stmt.executeQuery();
		} catch (SQLException e) {
			if (!e.getMessage().equals("The statement did not return a result set.")) {
				Main.gui.displayMessage(e.getMessage());
			}
		}
	}
	
	private void addKey1(String[] key, int index) {
		String name = "对应数据" + Integer.toString(index);
		String query = "CREATE TABLE "
				+ name
				+ " (\r\n" + "    " + key[0] + " varchar(8000),\r\n" + // 订单号
				"    " + key[1] + " varchar(8000),\r\n" + // 电子面单号
				"    " + key[2] + " varchar(8000),\r\n" + // 备注
				"    " + key[3] + " varchar(8000),\r\n" + // 公司
				")";

		Connection c = dbcs.getConnection();

		try {
			PreparedStatement stmt = c.prepareStatement(query);
			stmt.executeQuery();
		} catch (SQLException e) {
			if (!e.getMessage().equals("The statement did not return a result set.")) {
				Main.gui.displayMessage(e.getMessage());
			}
		}
	}

	private void addKey(String[] key) {
		String query = "CREATE TABLE Sheet1 (\r\n" + "    " + key[0] + " varchar(8000),\r\n" + // 编号
				"    " + key[1] + " varchar(8000),\r\n" + // 运单编号
				"    " + key[2] + " varchar(8000),\r\n" + // 收件省
				"    " + key[3] + " float,\r\n" + // 重量
				"    " + key[4] + " float,\r\n" + // 计费重量
				"    " + key[5] + " float,\r\n" + // 单位总价
				"    " + key[6] + " varchar(20),\r\n" + // 快递公司
				"    " + key[7] + " varchar(8000),\r\n" + // 月份
				"    " + key[8] + " varchar(8000),\r\n" + // 对应订单号
				"    " + key[9] + " varchar(8000),\r\n" + // 对应店铺
				"    " + key[10] + " varchar(8000),\r\n" + // 备注
				")";

		Connection c = dbcs.getConnection();

		try {
			PreparedStatement stmt = c.prepareStatement(query);
			stmt.executeQuery();
		} catch (SQLException e) {
			if (!e.getMessage().equals("The statement did not return a result set.")) {
				Main.gui.displayMessage(e.getMessage());
			}
		}
	}

	private void addData(String[] data) {
		String query = "INSERT INTO Sheet1 VALUES (";
		query = query + "'" + data[0] + "'" + ",";
		query = query + "'" + data[1] + "'" + ",";
		query = query + "'" + data[2] + "'" + ",";
		query = query + data[3] + ",";
		query = query + data[4] + ",";
		query = query + data[5] + ",";
		query = query + "'" + data[6] + "'" + ",";
		query = query + "'" + data[7] + "'" + ",";
		query = query + "'" + data[8] + "'" + ",";
		query = query + "'" + data[9] + "'" + ",";
		query = query + "'" + data[10] + "'" + ")";
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

	public void removeTables() {
		String query = "DROP TABLE 原始数据 ";
		for(int i = 1; i<sheetNum; i++) {
			query = query + "DROP TABLE 对应数据"
					+ Integer.toString(i)
					+ " ";
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
