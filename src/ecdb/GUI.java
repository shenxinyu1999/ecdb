package ecdb;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.ResultSet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GUI {

	JFrame frame;
	JPanel body;
	JPanel importButtons;
	TablePanel table;
	ImportService importService;
	SearchService searchService;
	ExportService exportService;
	int count = 0;

	public GUI() {
		frame = new JFrame();
		frame.setLayout(new BorderLayout());
		body = new JPanel(new FlowLayout());
		importButtons = new JPanel(new FlowLayout());
		table = new TablePanel();
		frame.add(body, BorderLayout.NORTH);
		frame.add(importButtons, BorderLayout.WEST);
		frame.add(table, BorderLayout.CENTER);
		frame.setMinimumSize(new Dimension(1920, 1080));
		frame.setTitle("Easy Creative");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		importService = new ImportService();
		searchService = new SearchService();
		exportService = new ExportService();
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
		JButton markDup = new JButton("标记重复"); //仅能标记出现两次的（多于两次的显示出来）
		JButton findOrderNum = new JButton("找寻对应订单号");
		JButton splitOrderNum = new JButton("分离对应订单号");
		JButton findCKNum = new JButton("对应CK号");
		JButton duplicateButton = new JButton("合并相同订单号");
		JButton exportButton = new JButton("导出");
		JButton exitButton = new JButton("退出");

		body.add(importButton);
		body.add(markDup);
		body.add(findOrderNum);
		body.add(splitOrderNum);
		body.add(findCKNum);
		body.add(duplicateButton);
		body.add(exportButton);
		body.add(exitButton);

		importButton.addActionListener(new ImportListener());
		markDup.addActionListener(new MarkDupListener());
		findOrderNum.addActionListener(new FindOrderNumListener());
		splitOrderNum.addActionListener(new SplitOrderNumListener());
		findCKNum.addActionListener(new CKListener());
		duplicateButton.addActionListener(new duplicateListener());
		exportButton.addActionListener(new ExportListener());
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

	private class ImportListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			FileDialog dialog = new FileDialog(frame, "Select File to Open");
			dialog.setMode(FileDialog.LOAD);
			dialog.setVisible(true);
			String directory = dialog.getDirectory();
			String file = dialog.getFile();
			importService.importFile(new File(directory + file));
		}
	}

	private class duplicateListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			searchService.searchDuplicate();
			displayMessage("duplicate finish");
		}

	}
	
	private class MarkDupListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			searchService.doMarkDup();
			ResultSet rs = searchService.showDuplicateMoreThenTwo();
			displayResultSet(rs, 0);
		}

	}
	
	private class FindOrderNumListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			searchService.findOrderNum(count);
			ResultSet rs = searchService.showOrderNumMoreThenOne(count);
			ResultSet rs2 = searchService.showCorrespondOrderNumMoreThenOne(count);
			searchService.findAndCombine(rs, rs2);
			System.out.println("hello?");
			searchService.findInComment(count);
			System.out.println("hello!!");
			table.rAll();
			displayMessage("Find finish");
		}

	}
	
	private class SplitOrderNumListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			searchService.findslash16(count);
			searchService.splitOrderNum();
			displayMessage("Split finish");
		}

	}
	
	private class CKListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			searchService.findCK();
			displayMessage("CK finish");
		}

	}
	
	private class ExportListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Object[][] data = searchService.getData();
			Object[][] data2 = searchService.getData2();
			exportService.exportFile(data, data2);
		}

	}

	private void displayResultSet(ResultSet rs, int i) {
		table.displayResultSet(rs, i);
		frame.pack();
	}
}
