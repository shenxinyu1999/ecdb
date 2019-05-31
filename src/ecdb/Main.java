package ecdb;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Main {
	
	static GUI gui;
	
	public static void main(String[] args) {
		System.out.println("HELLO");
		
		gui = new GUI();
		
		connect();
	}

	private static void connect() {
		Properties dbProps = new Properties();
		try {
			dbProps.load(new FileReader(new File("app.properties")));
		} catch (IOException e) {
			gui.displayError("error reading property file");
		}
	}
}
