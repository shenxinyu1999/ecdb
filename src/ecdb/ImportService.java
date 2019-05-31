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
		} catch (IOException e) {
			Main.gui.displayMessage("ERROR when reading file");
		}
	}

	private void addKey(String[] key) {
		String query = "CREATE TABLE Sheet1 (\r\n" + 
				"    " + key[0] + " int,\r\n" + 
				"    " + key[1] + " varchar(20),\r\n" + 
				"    " + key[2] + " float,\r\n" + 
				"    " + key[3] + " varchar(20),\r\n" + 
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
		String query = "INSERT INTO Sheet1 VALUES (" + data[0] + ",'" + data[1] + "'," + data[2] + ",'" + data[3] + "')";
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
