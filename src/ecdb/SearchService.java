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
		String query = "SELECT ��Ӧ������ FROM Sheet1 GROUP BY ��Ӧ������ HAVING COUNT(��Ӧ������) > 1";
		
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
				"FROM ԭʼ����\r\n" + 
				"WHERE �˵���� IN \r\n" + 
				"(SELECT �˵���� FROM ԭʼ���� GROUP BY �˵����,��ע HAVING COUNT(�˵����) > 1)";
		
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
