package ecdb;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Main {
	
	static GUI gui;
	
	public static void main(String[] args) {
		gui = new GUI();
		
		connect();
	}

	private static void connect() {
		gui.startConnecting();
		Properties dbProps = new Properties();
		try {
			dbProps.load(new FileReader(new File("database.properties")));
		} catch (IOException e) {
			gui.displayMessage("error reading property file");
		}
		
		DatabaseConnectionService dbcs = DatabaseConnectionService.getService();

		dbcs.setDatabaseInfo(dbProps.getProperty("serverName"), dbProps.getProperty("databaseName"));

		if (!dbcs.connect(dbProps.getProperty("serverUsername"), dbProps.getProperty("serverPassword"))) {
			gui.displayMessage("ERROR: Connection service failed");
			return;
		}
		gui.titleSetName();
	}
}
