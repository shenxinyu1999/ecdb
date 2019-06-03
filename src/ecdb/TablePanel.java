package ecdb;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class TablePanel extends JPanel {

	JButton combine;

	public TablePanel() {
		setLayout(new FlowLayout());
		combine = new JButton("ºÏ²¢");
		combine.setPreferredSize(new Dimension(200, 100));
		combine.addActionListener(new CombineListener());
		combine.setEnabled(false);
	}

	public void displayResultSet(ResultSet rs) {
		Object[][] data;
		Object[] columnNames;
		
		if (rs == null) {
			return;
		}
			
		ResultSetMetaData meta;
		
		try {
			removeAll();
			
			meta = rs.getMetaData();
			columnNames = new Object[meta.getColumnCount() + 1];
			
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
			
			JTable t = new JTable(model) {
				@Override
				public TableCellRenderer getCellRenderer(int row, int column) {
					if (getValueAt(row, column) instanceof Boolean) {
						return super.getDefaultRenderer(Boolean.class);
					} else {
						return super.getCellRenderer(row, column);
					}
				}

				@Override
				public TableCellEditor getCellEditor(int row, int column) {
					if (getValueAt(row, column) instanceof Boolean) {
						return super.getDefaultEditor(Boolean.class);
					} else {
						return super.getCellEditor(row, column);
					}
				}
			};
			
			JScrollPane jp = new JScrollPane(t, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			
			add(jp);
			add(combine);
			combine.setEnabled(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private class CombineListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			combine.setEnabled(false);
		}

	}

}
