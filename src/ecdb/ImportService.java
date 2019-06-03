package ecdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ImportService {

	private DatabaseConnectionService dbcs = DatabaseConnectionService.getService();
	
	public ImportService() {
		
	}
	
	public void importFile(BufferedReader br){
		String st;
		try {
			st = br.readLine();
			String[] key = st.split(",");
			addKey(key);
			while ((st = br.readLine()) != null) {
				String[] data = st.split(",");
				addData(data);
			}
			Main.gui.displayMessage("导入完成");
		} catch (IOException e) {
			Main.gui.displayMessage("ERROR when reading file");
		}
	}

	private void addKey(String[] key) {
		String query = "CREATE TABLE Sheet1 (\r\n" + 
				"    " + key[0] + " int,\r\n" + 			//编号
				"    " + key[1] + " varchar(20),\r\n" + 	//运单编号
				"    " + key[2] + " varchar(20),\r\n" + 	//收件省
				"    " + key[3] + " float,\r\n" +			//重量
				"    " + key[4] + " float,\r\n" + 			//计费重量
				"    " + key[5] + " float,\r\n" + 			//单位总价
				"    " + key[6] + " varchar(20),\r\n" + 	//快递公司
				"    " + key[7] + " int,\r\n" + 			//月份
				"    " + key[8] + " varchar(50),\r\n" + 	//对应订单号
				"    " + key[9] + " varchar(20),\r\n" + 	//对应店铺
				"    " + key[10] + " TEXT,\r\n" + 	//备注
				")";

		Connection c = dbcs.getConnection();

		try {
			PreparedStatement stmt = c.prepareStatement(query);
			stmt.executeQuery();
		} catch (SQLException e) {
			if(!e.getMessage().equals("The statement did not return a result set.")) {
				Main.gui.displayMessage(e.getMessage());
			}
		}
	}
	
	private void addData(String[] data) {
		String query = "INSERT INTO Sheet1 VALUES (";
		query = query + 	  data[0] + 	  ",";
		query = query + "'" + data[1] + "'" + ",";
		query = query + "'" + data[2] + "'" + ",";
		query = query + 	  data[3] + 	  ",";
		query = query + 	  data[4] + 	  ",";
		query = query + 	  data[5] + 	  ",";
		query = query + "'" + data[6] + "'" + ",";
		query = query + 	  data[7] + 	  ",";
		query = query + "'" + data[8] + "'" + ",";
		query = query + "'" + data[9] + "'" + ",";
		query = query + "'" + data[10] + "'" + ")";
		Connection c = dbcs.getConnection();
		
		try {
			PreparedStatement stmt = c.prepareStatement(query);
			stmt.executeQuery();
		} catch (SQLException e) {
			if(!e.getMessage().equals("The statement did not return a result set.")) {
				System.out.println(query);
				Main.gui.displayMessage(e.getMessage());
			}
		}
	}

	public void removeTables() {
		String query = "DROP TABLE Sheet1";
		Connection c = dbcs.getConnection();
		
		try {
			PreparedStatement stmt = c.prepareStatement(query);
			stmt.executeQuery();
		} catch (SQLException e) {
			if(!e.getMessage().equals("The statement did not return a result set.")) {
				System.out.println(query);
				Main.gui.displayMessage(e.getMessage());
			}
		}
	}
}
