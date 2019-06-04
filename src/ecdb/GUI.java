package ecdb;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.ResultSet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GUI {

	JFrame frame;
	JPanel body;
	TablePanel table;
	ImportService importService;
	SearchService searchService;

	public GUI() {
		frame = new JFrame();
		frame.setLayout(new BorderLayout());
		body = new JPanel(new FlowLayout());
		table = new TablePanel();
		frame.add(body,BorderLayout.NORTH);
		frame.add(table,BorderLayout.CENTER);
		frame.setMinimumSize(new Dimension(1920, 1080));
		frame.setTitle("Easy Creative");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		importService = new ImportService();
		searchService = new SearchService();
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
		JButton importButton = new JButton("导入数据");
		JButton duplicateButton = new JButton("查看重复订单号");
		JButton exitButton = new JButton("退出");

		importButton.setPreferredSize(new Dimension(200, 100));
		duplicateButton.setPreferredSize(new Dimension(200, 100));
		exitButton.setPreferredSize(new Dimension(200, 100));

		body.add(importButton);
		body.add(duplicateButton);
		body.add(exitButton);

		importButton.addActionListener(new importListener());
		duplicateButton.addActionListener(new duplicateListener());
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

	private class duplicateListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			ResultSet rs = searchService.searchDuplicate();
			displayResultSet(rs);
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

	private void displayResultSet(ResultSet rs) {
		table.displayResultSet(rs);
		frame.pack();
	}
}
