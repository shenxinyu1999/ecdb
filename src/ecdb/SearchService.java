package ecdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class SearchService {

	private DatabaseConnectionService dbcs = DatabaseConnectionService.getService();

	public SearchService() {

	}

	public void searchDuplicate() {
		Connection c = dbcs.getConnection();
		String query = "SELECT *FROM ���\r\n" + 
				"WHERE ��Ӧ������ IN\r\n" + 
				"(\r\n" + 
				"SELECT ��Ӧ������ FROM ��� GROUP BY ��Ӧ������ HAVING COUNT(��Ӧ������) > 1\r\n" + 
				") AND ��Ӧ������ LIKE 'CK%'\r\n" + 
				"ORDER BY ��Ӧ������";
		
		ResultSet rs = null;
		
		try {
			PreparedStatement stmt = c.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery();
			combine(rs);
		} catch (SQLException e) {
			Main.gui.displayMessage(e.getMessage());
		}

	}

	private void combine(ResultSet rs) {
		Object[][] data = null;
		Object[] columnNames;
		int size = 0;
		
		if (rs == null) {
			return;
		}
			
		ResultSetMetaData meta;
		
		try {
			meta = rs.getMetaData();
			columnNames = new Object[meta.getColumnCount()];
			
			size = 0;
			if (rs.last()) {
				size = rs.getRow();
			}
			
			rs.beforeFirst();
			data = new Object[size][meta.getColumnCount()];

			for (int col = 0; col < meta.getColumnCount(); col++) {
				columnNames[col] = meta.getColumnLabel(col+1);
			}
			
			for (int row = 0; rs.next(); row++) {
				for (int col = 0; col < meta.getColumnCount(); col++) {
					data[row][col] = rs.getObject(col+1);
				}
			}
			
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	public void doMarkDup() {
		Connection c = dbcs.getConnection();
		String query = "DECLARE @Temp TABLE (  \r\n" + "	[���] [int] NULL,\r\n"
				+ "	[�˵����] [varchar](8000) NULL,\r\n" + "	[�ռ�ʡ] [varchar](8000) NULL,\r\n"
				+ "	[����] [float] NULL,\r\n" + "	[�Ʒ�����] [float] NULL,\r\n" + "	[��λ�ܼ�] [float] NULL,\r\n"
				+ "	[��ݹ�˾] [varchar](8000) NULL,\r\n" + "	[�·�] [int] NULL,\r\n" + "	[��ע] [varchar](8000) NULL\r\n"
				+ ") \r\n" + "\r\n" + "INSERT INTO @Temp SELECT ԭʼ����.* \r\n" + "FROM ԭʼ���� JOIN \r\n"
				+ "(SELECT MAX(�·�) AS �·�, �˵���� FROM ԭʼ���� GROUP BY �˵���� HAVING COUNT(�˵����) =2) AS k\r\n"
				+ "ON k.�·� = ԭʼ����.�·� AND k.�˵���� = ԭʼ����.�˵����\r\n" + "\r\n" + "UPDATE ԭʼ����\r\n" + "SET ��ע = '�ظ�'\r\n"
				+ "FROM @Temp AS a\r\n" + "WHERE ԭʼ����.��� = a.��� AND\r\n" + "	ԭʼ����.�˵���� = a.�˵���� AND\r\n"
				+ "	ԭʼ����.�ռ�ʡ = a.�ռ�ʡ AND\r\n" + "	ԭʼ����.���� = a.���� AND\r\n" + "	ԭʼ����.�Ʒ����� = a.�Ʒ����� AND\r\n"
				+ "	ԭʼ����.��λ�ܼ� = a.��λ�ܼ� AND\r\n" + "	ԭʼ����.��ݹ�˾ = a.��ݹ�˾ AND\r\n" + "	ԭʼ����.�·� = a.�·� AND\r\n"
				+ "	ԭʼ����.��ע = a.��ע\r\n" + "\r\n" + "DELETE FROM @Temp\r\n" + "\r\n"
				+ "INSERT INTO @Temp SELECT ԭʼ����.* \r\n" + "FROM ԭʼ���� JOIN \r\n"
				+ "(SELECT MAX(���) AS ���, �˵���� FROM ԭʼ���� GROUP BY �˵����,�·� HAVING COUNT(�˵����) =2) AS k\r\n"
				+ "ON k.��� = ԭʼ����.��� AND k.�˵���� = ԭʼ����.�˵����\r\n" + "\r\n" + "UPDATE ԭʼ����\r\n" + "SET ��ע = 'ͬ���ظ�'\r\n"
				+ "FROM @Temp AS a\r\n" + "WHERE ԭʼ����.��� = a.��� AND\r\n" + "	ԭʼ����.�˵���� = a.�˵���� AND\r\n"
				+ "	ԭʼ����.�ռ�ʡ = a.�ռ�ʡ AND\r\n" + "	ԭʼ����.���� = a.���� AND\r\n" + "	ԭʼ����.�Ʒ����� = a.�Ʒ����� AND\r\n"
				+ "	ԭʼ����.��λ�ܼ� = a.��λ�ܼ� AND\r\n" + "	ԭʼ����.��ݹ�˾ = a.��ݹ�˾ AND\r\n" + "	ԭʼ����.�·� = a.�·� AND\r\n"
				+ "	ԭʼ����.��ע = a.��ע";

		try {
			PreparedStatement stmt = c.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.executeQuery();
		} catch (SQLException e) {
			if (!e.getMessage().equals("The statement did not return a result set.")) {
				Main.gui.displayMessage(e.getMessage());
			}
		}
	}

	public ResultSet showDuplicateMoreThenTwo() {
		Connection c = dbcs.getConnection();
		String query = "SELECT * \r\n" + "FROM ԭʼ����\r\n" + "WHERE �˵���� IN \r\n"
				+ "(SELECT �˵���� FROM ԭʼ���� GROUP BY �˵���� HAVING COUNT(�˵����) >2) \r\n" + "ORDER BY �˵����";

		ResultSet rs = null;
		try {
			PreparedStatement stmt = c.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery();
		} catch (SQLException e) {
			Main.gui.displayMessage(e.getMessage());
		}

		return rs;
	}

	public void findOrderNum(int count) {
		Connection c = dbcs.getConnection();
		String query = "SELECT * INTO ��� FROM ԭʼ���� WHERE ��ע=''\r\n" + "\r\n" + "ALTER TABLE ���\r\n" + "ADD \r\n"
				+ "��Ӧ������ varchar(8000),\r\n" + "��Ӧ���� varchar(8000)\r\n" + "\r\n" + "UPDATE ���\r\n"
				+ "SET ��Ӧ������ = ''\r\n" + "WHERE ��Ӧ������ IS NULL\r\n" + "\r\n" + "UPDATE ���\r\n" + "SET ��Ӧ���� = ''\r\n"
				+ "WHERE ��Ӧ���� IS NULL\r\n";
		for (int i = 1; i < count - 1; i++) {
			String s = "��Ӧ����" + Integer.toString(i);
			query = query + "UPDATE t2\r\n" + "SET t2.��Ӧ������ = t1.������, t2.��Ӧ���� = t1.��˾\r\n" + "FROM (SELECT * FROM " + s
					+ " WHERE �����浥�� IN (SELECT �����浥�� FROM " + s + " GROUP BY �����浥�� HAVING COUNT(�����浥��) = 1)) AS t1\r\n"
					+ "JOIN ��� t2 on t1.�����浥�� = t2.�˵����\r\n" + "WHERE ��Ӧ������ = ''";
		}

		try {
			PreparedStatement stmt = c.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.executeQuery();
		} catch (SQLException e) {
			if (!e.getMessage().equals("The statement did not return a result set.")) {
				Main.gui.displayMessage(e.getMessage());
			}
		}
	}

	public ResultSet showOrderNumMoreThenOne(int count) {
		Connection c = dbcs.getConnection();
		String query = "DECLARE @temp TABLE(\r\n" + 
				"	[���] [int] NULL,\r\n" + 
				"	[�˵����] [varchar](8000) NULL,\r\n" + 
				"	[�ռ�ʡ] [varchar](8000) NULL,\r\n" + 
				"	[����] [float] NULL,\r\n" + 
				"	[�Ʒ�����] [float] NULL,\r\n" + 
				"	[��λ�ܼ�] [float] NULL,\r\n" + 
				"	[��ݹ�˾] [varchar](8000) NULL,\r\n" + 
				"	[�·�] [int] NULL,\r\n" + 
				"	[��ע] [varchar](8000) NULL,\r\n" + 
				"	[��Ӧ������] [varchar](8000) NULL,\r\n" + 
				"	[��Ӧ����] [varchar](8000) NULL\r\n" + 
				")\r\n";
		
		for (int i = 1; i < count-1; i++) {
			String s = "��Ӧ����" + Integer.toString(i);
			query = query + "INSERT INTO @temp\r\n" + 
					"SELECT * FROM ��� WHERE �˵���� IN\r\n" + 
					"(\r\n" + 
					"SELECT �����浥�� FROM "
					+ s
					+ "\r\n" + 
					"GROUP BY �����浥�� HAVING COUNT(�����浥��) > 1\r\n" + 
					") AND ��Ӧ������ = ''";
		}
		
		query = query + "SELECT * FROM @temp";
		
		ResultSet rs = null;
		try {
			PreparedStatement stmt = c.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery();
		} catch (SQLException e) {
			Main.gui.displayMessage(e.getMessage());
		}

		return rs;
	}

	public ResultSet showCorrespondOrderNumMoreThenOne(int count) {
		Connection c = dbcs.getConnection();
		String query = "DECLARE @dy TABLE(\r\n" + 
				"	[������] [varchar](8000) NULL,\r\n" + 
				"	[�����浥��] [varchar](8000) NULL,\r\n" + 
				"	[��ע] [varchar](8000) NULL,\r\n" + 
				"	[��˾] [varchar](8000) NULL\r\n" + 
				")";
		
		for (int i = 1; i < count-1; i++) {
			String s = "��Ӧ����" + Integer.toString(i);
			query = query + "INSERT INTO @dy\r\n" + 
					"SELECT * FROM "
					+ s
					+ "\r\n" + 
					"WHERE �����浥�� IN \r\n" + 
					"(\r\n" + 
					"SELECT �˵���� FROM ��� WHERE �˵���� IN\r\n" + 
					"(\r\n" + 
					"SELECT �����浥�� FROM "
					+ s
					+ "\r\n" + 
					"GROUP BY �����浥�� HAVING COUNT(�����浥��) > 1\r\n" + 
					") AND ��Ӧ������ = ''\r\n" + 
					")";
		}
		
		query = query + "SELECT * FROM @dy";
		
		ResultSet rs = null;
		try {
			PreparedStatement stmt = c.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery();
		} catch (SQLException e) {
			Main.gui.displayMessage(e.getMessage());
		}

		return rs;
	}

	public void splitOrderNum() {
		Connection c = dbcs.getConnection();
		String query = "SELECT * FROM ��� WHERE ��Ӧ������ LIKE '%,%' AND ��Ӧ������ NOT LIKE '%[^0123456789_,]%' AND ��Ӧ������ != ''";
		
		ResultSet rs = null;
		try {
			PreparedStatement stmt = c.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery();
			split(rs);
		} catch (SQLException e) {
			if (!e.getMessage().equals("The statement did not return a result set.")) {
				Main.gui.displayMessage(e.getMessage());
			}
		}
	}

	private void split(ResultSet rs) {
		Object[][] data = null;
		Object[] columnNames;
		int size = 0;
		
		if (rs == null) {
			return;
		}
			
		ResultSetMetaData meta;
		
		try {
			meta = rs.getMetaData();
			columnNames = new Object[meta.getColumnCount()];
			
			size = 0;
			if (rs.last()) {
				size = rs.getRow();
			}
			
			rs.beforeFirst();
			data = new Object[size][meta.getColumnCount()];

			for (int col = 0; col < meta.getColumnCount(); col++) {
				columnNames[col] = meta.getColumnLabel(col+1);
			}
			
			for (int row = 0; rs.next(); row++) {
				for (int col = 0; col < meta.getColumnCount(); col++) {
					data[row][col] = rs.getObject(col+1);
				}
			}
			
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		Connection c = dbcs.getConnection();
		String query = "DELETE FROM ��� WHERE ��Ӧ������ LIKE '%,%' AND ��Ӧ������ NOT LIKE '%[^0123456789_,]%' AND ��Ӧ������ != ''";
		
		try {
			PreparedStatement stmt = c.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.executeQuery();
		} catch (SQLException e) {
			if (!e.getMessage().equals("The statement did not return a result set.")) {
				System.out.println(query);
				Main.gui.displayMessage(e.getMessage());
			}
		}
		
		for(int row = 0; row < size; row++) {
			String originalOrderNum = (String) data[row][9];
			String[] orderNums = originalOrderNum.split(",");
			for(int i = 0; i < orderNums.length; i++) {
				Object[] thisRow = new Object[11];
				thisRow[0] = data[row][0];
				thisRow[1] = data[row][1];
				thisRow[2] = data[row][2];
				if(i == 0) {
					thisRow[3] = data[row][3];
					thisRow[4] = data[row][4];
					thisRow[5] = data[row][5];
				}else {
					thisRow[3] = 0.0;
					thisRow[4] = 0.0;
					thisRow[5] = 0.0;
				}
				
				thisRow[6] = data[row][6];
				thisRow[7] = data[row][7];
				thisRow[8] = data[row][8];
				thisRow[9] = orderNums[i];
				thisRow[10] = data[row][10];
				
				String query2 = "INSERT INTO ��� VALUES ("
						+ Integer.toString((int)thisRow[0])
						+ ", '"
						+ (String) thisRow[1]
						+ "', '"
						+ (String) thisRow[2]
						+ "', "
						+ Double.toString((double)thisRow[3])
						+ ","
						+ Double.toString((double)thisRow[4])
						+ ","
						+ Double.toString((double)thisRow[5])
						+ ",'"
						+ (String) thisRow[6]
						+ "',"
						+ Integer.toString((int)thisRow[7])
						+ ",'"
						+ (String) thisRow[8]
						+ "','"
						+ (String) thisRow[9]
						+ "','"
						+ (String) thisRow[10]
						+ "')";
				
				try {
					PreparedStatement stmt = c.prepareStatement(query2, ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
					stmt.executeQuery();
				} catch (SQLException e) {
					if (!e.getMessage().equals("The statement did not return a result set.")) {
						Main.gui.displayMessage(e.getMessage());
					}
				}
			}
		}		
	}

	
	public void findslash16(int count) {
		Connection c = dbcs.getConnection();
		String query = "UPDATE t2 SET t2.��Ӧ������ = t1.������\r\n" + 
				"FROM \r\n" + 
				"(SELECT * FROM "
				+ "��Ӧ����"
				+ Integer.toString(count-2)
				+ ") AS t1\r\n" + 
				"JOIN ��� t2 ON t1.��ע = t2.��Ӧ������\r\n" + 
				"WHERE ��Ӧ������ LIKE '%[_]16[_]%'";
		
		try {
			PreparedStatement stmt = c.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.executeQuery();
		} catch (SQLException e) {
			if (!e.getMessage().equals("The statement did not return a result set.")) {
				System.out.println(query);
				Main.gui.displayMessage(e.getMessage());
			}
		}

		Main.gui.displayMessage("split finish");
	}

	
	public void findCK() {
		Connection c = dbcs.getConnection();
		String query = "UPDATE t2  SET t2.��Ӧ������ = t1.ƾ֤���\r\n" + 
				" FROM\r\n" + 
				"(SELECT * FROM  ϵͳ) AS t1\r\n" + 
				"JOIN ��� t2 on t1.�Է������� = t2.��Ӧ������\r\n" + 
				"WHERE ��Ӧ������ NOT LIKE '%[^0123456789]%' AND ��Ӧ������ != ''";
		
		try {
			PreparedStatement stmt = c.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.executeQuery();
		} catch (SQLException e) {
			if (!e.getMessage().equals("The statement did not return a result set.")) {
				System.out.println(query);
				Main.gui.displayMessage(e.getMessage());
			}
		}

		Main.gui.displayMessage("CK finish");
	}
}	
