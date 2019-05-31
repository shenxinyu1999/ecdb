package ecdb;

import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GUI {

	JFrame frame;
	JPanel body;
	ImportService importService;
	
	public GUI() {
		frame = new JFrame();
		body = new JPanel(new FlowLayout());
		frame.add(body);
		frame.setMinimumSize(new Dimension(1920, 1080));
		frame.setTitle("Easy Creative");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		importService = new ImportService();
	}
	
	public void displayMessage(String msg) {
		JOptionPane.showMessageDialog(null, msg);
	}

	public void startConnecting() {
		frame.setTitle("Connecting...");
	}

	public void titleSetName() {
		frame.setTitle("Easy Creative");
	}

	public void login() {
		JButton importButton = new JButton("import");
		JButton exitButton = new JButton("exit");
		
		importButton.setPreferredSize(new Dimension(200, 100));
		exitButton.setPreferredSize(new Dimension(200, 100));
		
		body.add(importButton);
		body.add(exitButton);
		
		importButton.addActionListener(new importListener());
		exitButton.addActionListener(new ExitListener());
		body.revalidate();
	}
	
	private class ExitListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			importService.removeTables();
			DatabaseConnectionService.closeConnection();
			frame.dispose();
		}

	}
	
	private class importListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			FileDialog dialog = new FileDialog(frame, "Select File to Open");
		    dialog.setMode(FileDialog.LOAD);
		    dialog.setVisible(true);
		    String directory = dialog.getDirectory();
		    String file = dialog.getFile();
		    importFile(directory + file);
		}
	}
	
	private void importFile(String fileName) {
		File file = new File(fileName);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			displayMessage("ERROR Creating File Reader");
		}
		
		importService.importFile(br);		
	}
}
