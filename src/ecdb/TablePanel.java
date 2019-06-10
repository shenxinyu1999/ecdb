package ecdb;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class TablePanel extends JPanel {

	JTable before;
	JTable after;
	
	public TablePanel() {
		setLayout(new FlowLayout());
	}

	public void displayResultSet(ResultSet rs, int i) {
		Object[][] data;
		Object[] columnNames;
		
		if (rs == null) {
			return;
		}
			
		ResultSetMetaData meta;
		
		try {
			meta = rs.getMetaData();
			columnNames = new Object[meta.getColumnCount()];
			
			int size = 0;
			if (rs.last()) {
				size = rs.getRow();
			}
			
			rs.beforeFirst();
			data = new Object[size][meta.getColumnCount()];

			for (int col = 1; col <= meta.getColumnCount(); col++) {
				columnNames[col - 1] = meta.getColumnLabel(col);
			}
			
			for (int row = 1; rs.next(); row++) {
				for (int col = 1; col <= meta.getColumnCount(); col++) {
					data[row - 1][col - 1] = rs.getObject(col);
				}
			}
			
			rs.beforeFirst();
			
			DefaultTableModel model = new DefaultTableModel(data, columnNames);
			
			if(i == 0) {
				before = new JTable(model);
			}else {
				after = new JTable(model);
			}
			
			JScrollPane jp = new JScrollPane(i == 0? before:after, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			
			add(jp);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void rAll() {
		removeAll();
	}

}
