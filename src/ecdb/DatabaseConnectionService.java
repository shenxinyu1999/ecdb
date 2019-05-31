package ecdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionService {
	private static DatabaseConnectionService unique;
	private final String SampleURL = "jdbc:sqlserver://${dbServer};databaseName=${dbName};user=${user};password={${pass}}";

	private Connection connection = null;

	private String databaseName;
	private String serverName;

	private DatabaseConnectionService() { }
	
	public void setDatabaseInfo(String server,String db) {
		this.databaseName = db;
		this.serverName = server;
	}
	
	public static DatabaseConnectionService getService() {
		if (unique == null)
			unique = new DatabaseConnectionService();
		return unique;
	}

	public boolean connect(String user, String pass) {
		String connStr = SampleURL;
		connStr = connStr.replace("${dbServer}", serverName);
		connStr = connStr.replace("${dbName}", databaseName);
		connStr = connStr.replace("${user}", user);
		connStr = connStr.replace("${pass}", pass);

		try {
			connection = DriverManager.getConnection(connStr);
		} catch (SQLException e) {
			Main.gui.displayMessage(e.getMessage());
			return false;
		}
		
		return true;
	}

	public Connection getConnection() {
		return this.connection;
	}

	public void closeConnection() {
		try {
			if (connection != null && !connection.isClosed()) {
				this.connection.rollback();
				this.connection.close();
			}
		} catch (SQLException e) {
			Main.gui.displayMessage(e.getMessage());
		}
	}
}
