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
				"(SELECT 运单编号 FROM 原始数据 GROUP BY 运单编号,备注 HAVING COUNT(运单编号) > 1)";
		
		ResultSet rs = null;
		try {
			PreparedStatement stmt = c.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery();
		} catch (SQLException e) {
			Main.gui.displayMessage(e.getMessage());
		}

		return rs;
	}
	
}
