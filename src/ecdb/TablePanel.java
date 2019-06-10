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
	private DatabaseConnectionService dbcs = DatabaseConnectionService.getService();
	
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
	
	private class CombineListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			int col = before.getModel().getColumnCount();
			int row = before.getModel().getRowCount();
			String[] r = new String[row];
			for(int i = 0; i < row; i++) {
				if((boolean) before.getModel().getValueAt(i, col - 1)) {
					Object data = before.getModel().getValueAt(i, col - 2);
					r[i] = (String) data;
				}
			}
			combineData(r);
		}

		private void combineData(String[] r) {
			for(String num : r) {
				String query = 
						"DECLARE @Temp TABLE (\r\n" + 
						"	[编号] [varchar](8000) NULL,\r\n" + 
						"	[运单编号] [varchar](8000) NULL,\r\n" + 
						"	[收件省] [varchar](8000) NULL,\r\n" + 
						"	[重量] [float] NULL,\r\n" + 
						"	[计费重量] [float] NULL,\r\n" + 
						"	[单位总价] [float] NULL,\r\n" + 
						"	[快递公司] [varchar](8000) NULL,\r\n" + 
						"	[月份] [varchar](8000) NULL,\r\n" + 
						"	[对应订单号] [varchar](8000) NULL,\r\n" + 
						"	[对应店铺] [varchar](8000) NULL,\r\n" + 
						"	[备注] [varchar](8000) NULL\r\n" + 
						")\r\n" + 
						"\r\n" + 
						"DECLARE @Result TABLE (\r\n" + 
						"	[编号] [varchar](8000) NULL,\r\n" + 
						"	[运单编号] [varchar](8000) NULL,\r\n" + 
						"	[收件省] [varchar](8000) NULL,\r\n" + 
						"	[重量] [float] NULL,\r\n" + 
						"	[计费重量] [float] NULL,\r\n" + 
						"	[单位总价] [float] NULL,\r\n" + 
						"	[快递公司] [varchar](8000) NULL,\r\n" + 
						"	[月份] [varchar](8000) NULL,\r\n" + 
						"	[对应订单号] [varchar](8000) NULL,\r\n" + 
						"	[对应店铺] [varchar](8000) NULL,\r\n" + 
						"	[备注] [varchar](8000) NULL\r\n" + 
						")\r\n" + 
						"\r\n" + 
						"INSERT INTO @Temp SELECT * FROM Sheet1 WHERE 对应订单号 = '"
						+ num
						+ "'\r\n" + 
						"\r\n" + 
						"INSERT INTO @Result\r\n" + 
						"SELECT\r\n" + 
						"STUFF((SELECT DISTINCT '/' + 编号\r\n" + 
						"		FROM @Temp AS b\r\n" + 
						"		WHERE b.对应订单号 = a.对应订单号\r\n" + 
						"		FOR XML PATH ('')),1,1,'') AS 编号,\r\n" + 
						"MIN(运单编号) AS 运单编号,\r\n" + 
						"STUFF((SELECT DISTINCT '/' + 收件省\r\n" + 
						"		FROM @Temp AS b\r\n" + 
						"		WHERE b.对应订单号 = a.对应订单号\r\n" + 
						"		FOR XML PATH ('')),1,1,'') AS 收件省,\r\n" + 
						"SUM(重量) AS 重量,\r\n" + 
						"SUM(计费重量) AS 计费重量,\r\n" + 
						"SUM(单位总价) AS 单位总价,\r\n" + 
						"STUFF((SELECT DISTINCT '/' + 快递公司\r\n" + 
						"		FROM @Temp AS b\r\n" + 
						"		WHERE b.对应订单号 = a.对应订单号\r\n" + 
						"		FOR XML PATH ('')),1,1,'') AS 快递公司,\r\n" + 
						"STUFF((SELECT DISTINCT '/' + 月份\r\n" + 
						"		FROM @Temp AS b\r\n" + 
						"		WHERE b.对应订单号 = a.对应订单号\r\n" + 
						"		FOR XML PATH ('')),1,1,'') AS 月份,\r\n" + 
						"对应订单号,\r\n" + 
						"MIN(对应店铺) AS 对应店铺,\r\n" + 
						"STUFF((SELECT DISTINCT '/' + 备注\r\n" + 
						"		FROM @Temp AS b\r\n" + 
						"		WHERE b.对应订单号 = a.对应订单号 AND 备注 != '无'\r\n" + 
						"		FOR XML PATH ('')) +\r\n" + 
						"		(SELECT DISTINCT '/' + 运单编号\r\n" + 
						"		FROM @Temp AS b\r\n" + 
						"		WHERE b.对应订单号 = a.对应订单号\r\n" + 
						"		FOR XML PATH ('')),1,1,'')\r\n" + 
						"AS 备注\r\n" + 
						"FROM @Temp AS a\r\n" + 
						"GROUP BY 对应订单号\r\n" + 
						"\r\n" + 
						"DELETE FROM Sheet1 WHERE 对应订单号 = '"
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
	
	public void rAll() {
		removeAll();
	}

}
