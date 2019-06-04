package ecdb;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
	JTable currentResult;
	private DatabaseConnectionService dbcs = DatabaseConnectionService.getService();
	
	public TablePanel() {
		setLayout(new FlowLayout());
		combine = new JButton("�ϲ�");
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
			
			currentResult = new JTable(model) {
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
			
			JScrollPane jp = new JScrollPane(currentResult, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
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
			int col = currentResult.getModel().getColumnCount();
			int row = currentResult.getModel().getRowCount();
			String[] r = new String[row];
			for(int i = 0; i < row; i++) {
				if((boolean) currentResult.getModel().getValueAt(i, col - 1)) {
					Object data = currentResult.getModel().getValueAt(i, col - 2);
					r[i] = (String) data;
				}
			}
			combineData(r);
			combine.setEnabled(false);
		}

		private void combineData(String[] r) {
			for(String num : r) {
				String query = 
						"DECLARE @Temp TABLE (\r\n" + 
						"	[���] [varchar](8000) NULL,\r\n" + 
						"	[�˵����] [varchar](8000) NULL,\r\n" + 
						"	[�ռ�ʡ] [varchar](8000) NULL,\r\n" + 
						"	[����] [float] NULL,\r\n" + 
						"	[�Ʒ�����] [float] NULL,\r\n" + 
						"	[��λ�ܼ�] [float] NULL,\r\n" + 
						"	[��ݹ�˾] [varchar](8000) NULL,\r\n" + 
						"	[�·�] [varchar](8000) NULL,\r\n" + 
						"	[��Ӧ������] [varchar](8000) NULL,\r\n" + 
						"	[��Ӧ����] [varchar](8000) NULL,\r\n" + 
						"	[��ע] [varchar](8000) NULL\r\n" + 
						")\r\n" + 
						"\r\n" + 
						"DECLARE @Result TABLE (\r\n" + 
						"	[���] [varchar](8000) NULL,\r\n" + 
						"	[�˵����] [varchar](8000) NULL,\r\n" + 
						"	[�ռ�ʡ] [varchar](8000) NULL,\r\n" + 
						"	[����] [float] NULL,\r\n" + 
						"	[�Ʒ�����] [float] NULL,\r\n" + 
						"	[��λ�ܼ�] [float] NULL,\r\n" + 
						"	[��ݹ�˾] [varchar](8000) NULL,\r\n" + 
						"	[�·�] [varchar](8000) NULL,\r\n" + 
						"	[��Ӧ������] [varchar](8000) NULL,\r\n" + 
						"	[��Ӧ����] [varchar](8000) NULL,\r\n" + 
						"	[��ע] [varchar](8000) NULL\r\n" + 
						")\r\n" + 
						"\r\n" + 
						"INSERT INTO @Temp SELECT * FROM Sheet1 WHERE ��Ӧ������ = '"
						+ num
						+ "'\r\n" + 
						"\r\n" + 
						"INSERT INTO @Result\r\n" + 
						"SELECT\r\n" + 
						"STUFF((SELECT DISTINCT '/' + ���\r\n" + 
						"		FROM @Temp AS b\r\n" + 
						"		WHERE b.��Ӧ������ = a.��Ӧ������\r\n" + 
						"		FOR XML PATH ('')),1,1,'') AS ���,\r\n" + 
						"MIN(�˵����) AS �˵����,\r\n" + 
						"STUFF((SELECT DISTINCT '/' + �ռ�ʡ\r\n" + 
						"		FROM @Temp AS b\r\n" + 
						"		WHERE b.��Ӧ������ = a.��Ӧ������\r\n" + 
						"		FOR XML PATH ('')),1,1,'') AS �ռ�ʡ,\r\n" + 
						"SUM(����) AS ����,\r\n" + 
						"SUM(�Ʒ�����) AS �Ʒ�����,\r\n" + 
						"SUM(��λ�ܼ�) AS ��λ�ܼ�,\r\n" + 
						"STUFF((SELECT DISTINCT '/' + ��ݹ�˾\r\n" + 
						"		FROM @Temp AS b\r\n" + 
						"		WHERE b.��Ӧ������ = a.��Ӧ������\r\n" + 
						"		FOR XML PATH ('')),1,1,'') AS ��ݹ�˾,\r\n" + 
						"STUFF((SELECT DISTINCT '/' + �·�\r\n" + 
						"		FROM @Temp AS b\r\n" + 
						"		WHERE b.��Ӧ������ = a.��Ӧ������\r\n" + 
						"		FOR XML PATH ('')),1,1,'') AS �·�,\r\n" + 
						"��Ӧ������,\r\n" + 
						"MIN(��Ӧ����) AS ��Ӧ����,\r\n" + 
						"STUFF((SELECT DISTINCT '/' + ��ע\r\n" + 
						"		FROM @Temp AS b\r\n" + 
						"		WHERE b.��Ӧ������ = a.��Ӧ������ AND ��ע != '��'\r\n" + 
						"		FOR XML PATH ('')) +\r\n" + 
						"		(SELECT DISTINCT '/' + �˵����\r\n" + 
						"		FROM @Temp AS b\r\n" + 
						"		WHERE b.��Ӧ������ = a.��Ӧ������\r\n" + 
						"		FOR XML PATH ('')),1,1,'')\r\n" + 
						"AS ��ע\r\n" + 
						"FROM @Temp AS a\r\n" + 
						"GROUP BY ��Ӧ������\r\n" + 
						"\r\n" + 
						"DELETE FROM Sheet1 WHERE ��Ӧ������ = '"
						+ num
						+ "'\r\n" + 
						"\r\n" + 
						"INSERT INTO Sheet1 Select * FROM @Result";
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
		}

	}

}
