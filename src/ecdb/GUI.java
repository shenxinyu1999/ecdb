package ecdb;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class GUI {

	JFrame frame;
	JPanel body;
	JPanel table;
	ImportService importService;
	SearchService searchService;

	public GUI() {
		frame = new JFrame();
		frame.setLayout(new BorderLayout());
		body = new JPanel(new FlowLayout());
		table = new JPanel(new FlowLayout());
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
		JButton hebingButton = new JButton("合并");
		JButton duplicateButton = new JButton("查看重复订单号");
		JButton exitButton = new JButton("退出");

		importButton.setPreferredSize(new Dimension(200, 100));
		hebingButton.setPreferredSize(new Dimension(200, 100));
		duplicateButton.setPreferredSize(new Dimension(200, 100));
		exitButton.setPreferredSize(new Dimension(200, 100));

		body.add(importButton);
		body.add(hebingButton);
		body.add(duplicateButton);
		body.add(exitButton);

		importButton.addActionListener(new importListener());
		hebingButton.addActionListener(new hebingListener());
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

	private class hebingListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub

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

		Object[][] data;
		Object[] columnNames;
		if (rs == null)
			return;

		ResultSetMetaData meta;
		try {
			table.removeAll();
			meta = rs.getMetaData();
			columnNames = new Object[meta.getColumnCount()+1];
			int size = 0;
			if (rs.last()) {
				size = rs.getRow();
			}
			rs.beforeFirst();
			data = new Object[size][meta.getColumnCount() + 1];

			for (int col = 1; col <= meta.getColumnCount(); col++) {
				columnNames[col - 1] = meta.getColumnLabel(col);
			}
			columnNames[meta.getColumnCount()] = "check";
			for (int row = 1; rs.next(); row++) {
				for (int col = 1; col <= meta.getColumnCount(); col++) {
					data[row - 1][col - 1] = rs.getObject(col);
				}
				data[row - 1][meta.getColumnCount()] = false;
			}
			rs.beforeFirst();
			DefaultTableModel model = new DefaultTableModel(data, columnNames);
			JTable t = new JTable(model){
	            @Override
	            public TableCellRenderer getCellRenderer(int row, int column) {
	                if(getValueAt(row, column) instanceof Boolean) {
	                    return super.getDefaultRenderer(Boolean.class);
	                } else {
	                    return super.getCellRenderer(row, column);
	                }
	            }

	            @Override
	            public TableCellEditor getCellEditor(int row, int column) {
	                if(getValueAt(row, column) instanceof Boolean) {
	                    return super.getDefaultEditor(Boolean.class);
	                } else {
	                    return super.getCellEditor(row, column);
	                }
	            }
	        };
			JScrollPane jp = new JScrollPane(t, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			table.add(jp, BorderLayout.CENTER);
			frame.pack();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
