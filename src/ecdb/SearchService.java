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
		String query = "SELECT *FROM 结果\r\n" + 
				"WHERE 对应订单号 IN\r\n" + 
				"(\r\n" + 
				"SELECT 对应订单号 FROM 结果 GROUP BY 对应订单号 HAVING COUNT(对应订单号) > 1\r\n" + 
				") AND 对应订单号 LIKE 'CK%'\r\n" + 
				"ORDER BY 对应订单号";
		
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
		
		
		
		
		
		
		
		ArrayList<String[]> result = new ArrayList<>();		
		
		ArrayList<String> allYunDan = new ArrayList<>();
		ArrayList<String> allShengShi = new ArrayList<>();
		ArrayList<String> allKuaiDi = new ArrayList<>();
		ArrayList<String> allBeiZhu = new ArrayList<>();
		
		String bianHao = Integer.toString((int) data[0][0]);
		allYunDan.add((String) data[0][1]);
		allShengShi.add((String) data[0][2]);
		double zhongLiang = (double) data[0][3];
		double jiFeiZhongLiang = (double) data[0][4];
		double danWeiZongJia = (double) data[0][5];
		allKuaiDi.add((String) data[0][6]);
		String yueFen = Integer.toString((int) data[0][7]);
		allBeiZhu.add((String) data[0][8]);
		String dianPu = (String) data[0][10];
		
		String currentNum = (String) data[0][9];
		int index = 1;
		while (index < data.length) {
			while(index < data.length && currentNum.equals((String) data[index][9])) {
				String nYunDan = (String) data[index][1];
				String nShengShi = (String) data[index][2];
				double nZhongLiang = (double) data[index][3];
				double nJiFeiZhongLiang = (double) data[index][4];
				double nDanWeiZongJia = (double) data[index][5];
				String nKuaiDi = (String) data[index][6];
				String nYueFen = Integer.toString((int) data[index][7]);
				String nBeiZhu = (String) data[index][8];
				String nDianPu = (String) data[index][10];
				if(!allYunDan.contains(nYunDan)) {
					allYunDan.add(nYunDan);
				}
				if(!allShengShi.contains(nShengShi)) {
					allShengShi.add(nShengShi);
				}
				zhongLiang = zhongLiang + nZhongLiang;
				jiFeiZhongLiang = jiFeiZhongLiang + nJiFeiZhongLiang;
				danWeiZongJia = danWeiZongJia + nDanWeiZongJia;
				if(!allKuaiDi.contains(nKuaiDi)) {
					allKuaiDi.add(nKuaiDi);
				}
				if(nYueFen.compareTo(yueFen) < 0) {
					yueFen = nYueFen;
				}
				if(!allBeiZhu.contains(nBeiZhu)){
					allBeiZhu.add(nBeiZhu);
				}
				if(nDianPu.compareTo(dianPu) < 0) {
					dianPu = nDianPu;
				}
				index++;
			}
			
			if(index >= data.length) {
				break;
			}
			String[] row = new String[11];
			row[0] = bianHao;
			row[1] = allYunDan.get(0);
			row[2] = allShengShi.get(0);
			for (int i = 1; i < allShengShi.size();i++) {
				if(!allShengShi.get(i).equals("")) {
					row[2] = row[2] + "/" + allShengShi.get(i);
				}
			}
			row[3] = Double.toString(zhongLiang);
			row[4] = Double.toString(jiFeiZhongLiang);
			row[5] = Double.toString(danWeiZongJia);
			row[6] = allKuaiDi.get(0);
			for (int i = 1; i < allKuaiDi.size();i++) {
				if(!allKuaiDi.get(i).equals("")) {
					row[6] = row[6] + "/" + allKuaiDi.get(i);
				}
			}
			row[7] = yueFen;
			row[8] = allBeiZhu.get(0);
			for (int i = 1; i < allBeiZhu.size();i++) {
				if(!allBeiZhu.get(i).equals("")) {
					row[8] = row[8] + "/" + allBeiZhu.get(i);
				}
			}
			for (int i = 1; i < allYunDan.size();i++) {
				if(!allYunDan.get(i).equals("")) {
					if(row[8].equals("")) {
						row[8] = row[8] + allYunDan.get(i);
					}else {
						row[8] = row[8] + "/" + allYunDan.get(i);
					}
				}
			}
			row[9] = currentNum;
			row[10] = dianPu;
			
			result.add(row);
			allBeiZhu.removeAll(allBeiZhu);
			allKuaiDi.removeAll(allKuaiDi);
			allShengShi.removeAll(allShengShi);
			yueFen = Integer.toString((int) data[index][7]);
			allYunDan.removeAll(allYunDan);
			zhongLiang = 0.0;
			jiFeiZhongLiang = 0.0;
			danWeiZongJia = 0.0;
			bianHao = Integer.toString((int) data[index][0]);
			dianPu = (String) data[index][10];
			currentNum = (String) data[index][9];
		}
		
		Connection c = dbcs.getConnection();
		String deleteQuery = "DELETE FROM 结果 WHERE 对应订单号 IN (  \r\n" + 
				"				SELECT 对应订单号 FROM 结果 GROUP BY 对应订单号 HAVING COUNT(对应订单号) > 1  \r\n" + 
				"				) AND 对应订单号 LIKE 'CK%'";
		
		try {
			PreparedStatement stmt = c.prepareStatement(deleteQuery, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.executeQuery();
		} catch (SQLException e) {
			if (!e.getMessage().equals("The statement did not return a result set.")) {
				Main.gui.displayMessage(e.getMessage());
			}
		}
		for(String[] r:result) {

			
			String query = "INSERT INTO 结果 VALUES ("
					+ r[0]
					+ ", '"
					+ r[1]
					+ "', '"
					+ r[2]
					+ "', "
					+ r[3]
					+ ","
					+ r[4]
					+ ","
					+ r[5]
					+ ",'"
					+ r[6]
					+ "',"
					+ r[7]
					+ ",'"
					+ r[8]
					+ "','"
					+ r[9]
					+ "','"
					+ r[10]
					+ "')";
			
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
		
	}

	public void doMarkDup() {
		Connection c = dbcs.getConnection();
		String query = "DECLARE @Temp TABLE (  \r\n" + "	[编号] [int] NULL,\r\n"
				+ "	[运单编号] [varchar](8000) NULL,\r\n" + "	[收件省] [varchar](8000) NULL,\r\n"
				+ "	[重量] [float] NULL,\r\n" + "	[计费重量] [float] NULL,\r\n" + "	[单位总价] [float] NULL,\r\n"
				+ "	[快递公司] [varchar](8000) NULL,\r\n" + "	[月份] [int] NULL,\r\n" + "	[备注] [varchar](8000) NULL\r\n"
				+ ") \r\n" + "\r\n" + "INSERT INTO @Temp SELECT 原始数据.* \r\n" + "FROM 原始数据 JOIN \r\n"
				+ "(SELECT MAX(月份) AS 月份, 运单编号 FROM 原始数据 GROUP BY 运单编号 HAVING COUNT(运单编号) =2) AS k\r\n"
				+ "ON k.月份 = 原始数据.月份 AND k.运单编号 = 原始数据.运单编号\r\n" + "\r\n" + "UPDATE 原始数据\r\n" + "SET 备注 = '重复'\r\n"
				+ "FROM @Temp AS a\r\n" + "WHERE 原始数据.编号 = a.编号 AND\r\n" + "	原始数据.运单编号 = a.运单编号 AND\r\n"
				+ "	原始数据.收件省 = a.收件省 AND\r\n" + "	原始数据.重量 = a.重量 AND\r\n" + "	原始数据.计费重量 = a.计费重量 AND\r\n"
				+ "	原始数据.单位总价 = a.单位总价 AND\r\n" + "	原始数据.快递公司 = a.快递公司 AND\r\n" + "	原始数据.月份 = a.月份 AND\r\n"
				+ "	原始数据.备注 = a.备注\r\n" + "\r\n" + "DELETE FROM @Temp\r\n" + "\r\n"
				+ "INSERT INTO @Temp SELECT 原始数据.* \r\n" + "FROM 原始数据 JOIN \r\n"
				+ "(SELECT MAX(编号) AS 编号, 运单编号 FROM 原始数据 GROUP BY 运单编号,月份 HAVING COUNT(运单编号) =2) AS k\r\n"
				+ "ON k.编号 = 原始数据.编号 AND k.运单编号 = 原始数据.运单编号\r\n" + "\r\n" + "UPDATE 原始数据\r\n" + "SET 备注 = '同月重复'\r\n"
				+ "FROM @Temp AS a\r\n" + "WHERE 原始数据.编号 = a.编号 AND\r\n" + "	原始数据.运单编号 = a.运单编号 AND\r\n"
				+ "	原始数据.收件省 = a.收件省 AND\r\n" + "	原始数据.重量 = a.重量 AND\r\n" + "	原始数据.计费重量 = a.计费重量 AND\r\n"
				+ "	原始数据.单位总价 = a.单位总价 AND\r\n" + "	原始数据.快递公司 = a.快递公司 AND\r\n" + "	原始数据.月份 = a.月份 AND\r\n"
				+ "	原始数据.备注 = a.备注";

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
		String query = "SELECT * \r\n" + "FROM 原始数据\r\n" + "WHERE 运单编号 IN \r\n"
				+ "(SELECT 运单编号 FROM 原始数据 GROUP BY 运单编号 HAVING COUNT(运单编号) >2) \r\n" + "ORDER BY 运单编号";

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
		String query = "SELECT * INTO 结果 FROM 原始数据 WHERE 备注=''\r\n" + "\r\n" + "ALTER TABLE 结果\r\n" + "ADD \r\n"
				+ "对应订单号 varchar(8000),\r\n" + "对应店铺 varchar(8000)\r\n" + "\r\n" + "UPDATE 结果\r\n"
				+ "SET 对应订单号 = ''\r\n" + "WHERE 对应订单号 IS NULL\r\n" + "\r\n" + "UPDATE 结果\r\n" + "SET 对应店铺 = ''\r\n"
				+ "WHERE 对应店铺 IS NULL\r\n";
		for (int i = 1; i < count - 1; i++) {
			String s = "对应数据" + Integer.toString(i);
			query = query + "UPDATE t2\r\n" + "SET t2.对应订单号 = t1.订单号, t2.对应店铺 = t1.公司\r\n" + "FROM (SELECT * FROM " + s
					+ " WHERE 电子面单号 IN (SELECT 电子面单号 FROM " + s + " GROUP BY 电子面单号 HAVING COUNT(电子面单号) = 1)) AS t1\r\n"
					+ "JOIN 结果 t2 on t1.电子面单号 = t2.运单编号\r\n" + "WHERE 对应订单号 = ''";
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
				"	[编号] [int] NULL,\r\n" + 
				"	[运单编号] [varchar](8000) NULL,\r\n" + 
				"	[收件省] [varchar](8000) NULL,\r\n" + 
				"	[重量] [float] NULL,\r\n" + 
				"	[计费重量] [float] NULL,\r\n" + 
				"	[单位总价] [float] NULL,\r\n" + 
				"	[快递公司] [varchar](8000) NULL,\r\n" + 
				"	[月份] [int] NULL,\r\n" + 
				"	[备注] [varchar](8000) NULL,\r\n" + 
				"	[对应订单号] [varchar](8000) NULL,\r\n" + 
				"	[对应店铺] [varchar](8000) NULL\r\n" + 
				")\r\n";
		
		for (int i = 1; i < count-1; i++) {
			String s = "对应数据" + Integer.toString(i);
			query = query + "INSERT INTO @temp\r\n" + 
					"SELECT * FROM 结果 WHERE 运单编号 IN\r\n" + 
					"(\r\n" + 
					"SELECT 电子面单号 FROM "
					+ s
					+ "\r\n" + 
					"GROUP BY 电子面单号 HAVING COUNT(电子面单号) > 1\r\n" + 
					") AND 对应订单号 = ''";
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

	public void findAndCombine(ResultSet rs, ResultSet rs2) {
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
		
		Object[][] data2 = null;
		Object[] columnNames2;
		int size2 = 0;
		
		if (rs2 == null) {
			return;
		}
			
		ResultSetMetaData meta2;
		
		try {
			meta2 = rs2.getMetaData();
			columnNames2 = new Object[meta2.getColumnCount()];
			
			size2 = 0;
			if (rs2.last()) {
				size2 = rs2.getRow();
			}
			
			rs2.beforeFirst();
			data2 = new Object[size2][meta2.getColumnCount()];

			for (int col2 = 0; col2 < meta2.getColumnCount(); col2++) {
				columnNames2[col2] = meta2.getColumnLabel(col2+1);
			}
			
			for (int row2 = 0; rs2.next(); row2++) {
				for (int col2 = 0; col2 < meta2.getColumnCount(); col2++) {
					data2[row2][col2] = rs2.getObject(col2+1);
				}
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		for(int i = 0; i < data.length ; i++) {
			String currentNum = (String) data[i][1];
			ArrayList<String> num = new ArrayList<>();
			for(int j = 0 ; j < data2.length ; j++) {
				if(currentNum.equals((String) data2[j][1]) && !((String) data2[j][0]).equals("")) {
					num.add((String) data2[j][0]);
				}
			}
			String tNum = num.get(0);
			for(int k = 1; k < num.size() ; k++) {
				tNum = tNum + "," + num.get(k);
			}
			Connection c = dbcs.getConnection();
			String query = "UPDATE 结果 SET 对应订单号 = '"
					+ tNum
					+ "'\r\n" + 
					"WHERE 运单编号 = '"
					+ currentNum
					+ "'";
			
			ResultSet r = null;
			try {
				PreparedStatement stmt = c.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				r = stmt.executeQuery();
			} catch (SQLException e) {
				if (!e.getMessage().equals("The statement did not return a result set.")) {
					System.out.println(query);
					Main.gui.displayMessage(e.getMessage());
				}
			}
			
		}		
	}

	public ResultSet showCorrespondOrderNumMoreThenOne(int count) {
		Connection c = dbcs.getConnection();
		String query = "DECLARE @dy TABLE(\r\n" + 
				"	[订单号] [varchar](8000) NULL,\r\n" + 
				"	[电子面单号] [varchar](8000) NULL,\r\n" + 
				"	[备注] [varchar](8000) NULL,\r\n" + 
				"	[公司] [varchar](8000) NULL\r\n" + 
				")";
		
		for (int i = 1; i < count-1; i++) {
			String s = "对应数据" + Integer.toString(i);
			query = query + "INSERT INTO @dy\r\n" + 
					"SELECT * FROM "
					+ s
					+ "\r\n" + 
					"WHERE 电子面单号 IN \r\n" + 
					"(\r\n" + 
					"SELECT 运单编号 FROM 结果 WHERE 运单编号 IN\r\n" + 
					"(\r\n" + 
					"SELECT 电子面单号 FROM "
					+ s
					+ "\r\n" + 
					"GROUP BY 电子面单号 HAVING COUNT(电子面单号) > 1\r\n" + 
					") AND 对应订单号 = ''\r\n" + 
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
		String query = "SELECT * FROM 结果 WHERE 对应订单号 LIKE '%,%' AND 对应订单号 NOT LIKE '%[^0123456789_,]%' AND 对应订单号 != ''";
		
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
		String query = "DELETE FROM 结果 WHERE 对应订单号 LIKE '%,%' AND 对应订单号 NOT LIKE '%[^0123456789_,]%' AND 对应订单号 != ''";
		
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
				
				String query2 = "INSERT INTO 结果 VALUES ("
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
		String query = "UPDATE t2 SET t2.对应订单号 = t1.订单号\r\n" + 
				"FROM \r\n" + 
				"(SELECT * FROM "
				+ "对应数据"
				+ Integer.toString(count-2)
				+ ") AS t1\r\n" + 
				"JOIN 结果 t2 ON t1.备注 = t2.对应订单号\r\n" + 
				"WHERE 对应订单号 LIKE '%[_]16[_]%'";
		
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
	}

	
	public void findCK() {
		Connection c = dbcs.getConnection();
		String query = "UPDATE t2  SET t2.对应订单号 = t1.凭证编号\r\n" + 
				" FROM\r\n" + 
				"(SELECT * FROM  系统) AS t1\r\n" + 
				"JOIN 结果 t2 on t1.对方订单号 = t2.对应订单号\r\n" + 
				"WHERE 对应订单号 NOT LIKE '%[^0123456789]%' AND 对应订单号 != ''";
		
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
	}

	public void findInComment(int count) {
		Connection c = dbcs.getConnection();
		String query = "";
		for (int i = 1; i < count-1; i++) {
			String s = "对应数据" + Integer.toString(i);
			query = query +  "UPDATE t2  SET t2.对应订单号 = t1.订单号\r\n" + 
					"FROM\r\n" + 
					"(SELECT * FROM  "
					+ s
					+ " WHERE 备注 != '') AS t1\r\n" + 
					"JOIN (SELECT * FROM 结果 WHERE 对应订单号 = '') AS t2 on t1.备注 LIKE CONCAT('%', t2.运单编号 , '%') \r\n" + 
					"";
		
		}
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
	}

	public Object[][] getData() {
		Connection c = dbcs.getConnection();
		String query = "SELECT * FROM 结果 WHERE 对应订单号 LIKE 'CK%'";
		
		ResultSet rs = null;
		try {
			PreparedStatement stmt = c.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery();
		} catch (SQLException e) {
			if (!e.getMessage().equals("The statement did not return a result set.")) {
				System.out.println(query);
				Main.gui.displayMessage(e.getMessage());
			}
		}
		
		Object[][] data = null;
		Object[] columnNames;
		int size = 0;
		
		if (rs == null) {
			return null;
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
		return data;
	}
	
	public Object[][] getData2() {
		Connection c = dbcs.getConnection();
		String query = "SELECT * FROM 结果 WHERE 对应订单号 NOT LIKE 'CK%'";
		
		ResultSet rs = null;
		try {
			PreparedStatement stmt = c.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery();
		} catch (SQLException e) {
			if (!e.getMessage().equals("The statement did not return a result set.")) {
				System.out.println(query);
				Main.gui.displayMessage(e.getMessage());
			}
		}
		
		Object[][] data = null;
		Object[] columnNames;
		int size = 0;
		
		if (rs == null) {
			return null;
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
		return data;
	}
}	
