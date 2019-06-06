package ecdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SearchService {

	private DatabaseConnectionService dbcs = DatabaseConnectionService.getService();

	public SearchService() {
		
	}

	public ResultSet searchDuplicate() {
		Connection c = dbcs.getConnection();
		String query = "SELECT 对应订单号 FROM Sheet1 GROUP BY 对应订单号 HAVING COUNT(对应订单号) > 1";
		
		ResultSet rs = null;
		try {
			PreparedStatement stmt = c.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery();
		} catch (SQLException e) {
			Main.gui.displayMessage(e.getMessage());
		}

		return rs;
	}

	public ResultSet markDuplicate() {
		Connection c = dbcs.getConnection();
		String query = "SELECT * \r\n" + 
				"FROM 原始数据\r\n" + 
				"WHERE 运单编号 IN \r\n" + 
				"(SELECT 运单编号 FROM 原始数据 GROUP BY 运单编号 HAVING COUNT(运单编号) > 1) \r\n" + 
				"ORDER BY 运单编号";
		
		ResultSet rs = null;
		try {
			PreparedStatement stmt = c.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery();
		} catch (SQLException e) {
			Main.gui.displayMessage(e.getMessage());
		}

		return rs;
	}

	public void doMarkDup() {
		Connection c = dbcs.getConnection();
		String query = "DECLARE @Temp TABLE (  \r\n" + 
				"	[编号] [int] NULL,\r\n" + 
				"	[运单编号] [varchar](8000) NULL,\r\n" + 
				"	[收件省] [varchar](8000) NULL,\r\n" + 
				"	[重量] [float] NULL,\r\n" + 
				"	[计费重量] [float] NULL,\r\n" + 
				"	[单位总价] [float] NULL,\r\n" + 
				"	[快递公司] [varchar](8000) NULL,\r\n" + 
				"	[月份] [int] NULL,\r\n" + 
				"	[备注] [varchar](8000) NULL\r\n" + 
				") \r\n" + 
				"\r\n" + 
				"INSERT INTO @Temp SELECT 原始数据.* \r\n" + 
				"FROM 原始数据 JOIN \r\n" + 
				"(SELECT MAX(月份) AS 月份, 运单编号 FROM 原始数据 GROUP BY 运单编号 HAVING COUNT(运单编号) > 1) AS k\r\n" + 
				"ON k.月份 = 原始数据.月份 AND k.运单编号 = 原始数据.运单编号\r\n" + 
				"\r\n" + 
				"UPDATE 原始数据\r\n" + 
				"SET 备注 = '重复'\r\n" + 
				"FROM @Temp AS a\r\n" + 
				"WHERE 原始数据.编号 = a.编号 AND\r\n" + 
				"	原始数据.运单编号 = a.运单编号 AND\r\n" + 
				"	原始数据.收件省 = a.收件省 AND\r\n" + 
				"	原始数据.重量 = a.重量 AND\r\n" + 
				"	原始数据.计费重量 = a.计费重量 AND\r\n" + 
				"	原始数据.单位总价 = a.单位总价 AND\r\n" + 
				"	原始数据.快递公司 = a.快递公司 AND\r\n" + 
				"	原始数据.月份 = a.月份 AND\r\n" + 
				"	原始数据.备注 = a.备注\r\n" + 
				"\r\n" + 
				"DELETE FROM @Temp\r\n" + 
				"\r\n" + 
				"INSERT INTO @Temp SELECT 原始数据.* \r\n" + 
				"FROM 原始数据 JOIN \r\n" + 
				"(SELECT MAX(编号) AS 编号, 运单编号 FROM 原始数据 GROUP BY 运单编号,月份 HAVING COUNT(运单编号) > 1) AS k\r\n" + 
				"ON k.编号 = 原始数据.编号 AND k.运单编号 = 原始数据.运单编号\r\n" + 
				"\r\n" + 
				"UPDATE 原始数据\r\n" + 
				"SET 备注 = '同月重复'\r\n" + 
				"FROM @Temp AS a\r\n" + 
				"WHERE 原始数据.编号 = a.编号 AND\r\n" + 
				"	原始数据.运单编号 = a.运单编号 AND\r\n" + 
				"	原始数据.收件省 = a.收件省 AND\r\n" + 
				"	原始数据.重量 = a.重量 AND\r\n" + 
				"	原始数据.计费重量 = a.计费重量 AND\r\n" + 
				"	原始数据.单位总价 = a.单位总价 AND\r\n" + 
				"	原始数据.快递公司 = a.快递公司 AND\r\n" + 
				"	原始数据.月份 = a.月份 AND\r\n" + 
				"	原始数据.备注 = a.备注";
		
		ResultSet rs = null;
		try {
			PreparedStatement stmt = c.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery();
		} catch (SQLException e) {
			if (!e.getMessage().equals("The statement did not return a result set.")) {
				Main.gui.displayMessage(e.getMessage());
			}
		}
	}
	
}
