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
				"(SELECT �˵���� FROM ԭʼ���� GROUP BY �˵���� HAVING COUNT(�˵����) =2) \r\n" + 
				"ORDER BY �˵����";
		
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
				"	[���] [int] NULL,\r\n" + 
				"	[�˵����] [varchar](8000) NULL,\r\n" + 
				"	[�ռ�ʡ] [varchar](8000) NULL,\r\n" + 
				"	[����] [float] NULL,\r\n" + 
				"	[�Ʒ�����] [float] NULL,\r\n" + 
				"	[��λ�ܼ�] [float] NULL,\r\n" + 
				"	[��ݹ�˾] [varchar](8000) NULL,\r\n" + 
				"	[�·�] [int] NULL,\r\n" + 
				"	[��ע] [varchar](8000) NULL\r\n" + 
				") \r\n" + 
				"\r\n" + 
				"INSERT INTO @Temp SELECT ԭʼ����.* \r\n" + 
				"FROM ԭʼ���� JOIN \r\n" + 
				"(SELECT MAX(�·�) AS �·�, �˵���� FROM ԭʼ���� GROUP BY �˵���� HAVING COUNT(�˵����) =2) AS k\r\n" + 
				"ON k.�·� = ԭʼ����.�·� AND k.�˵���� = ԭʼ����.�˵����\r\n" + 
				"\r\n" + 
				"UPDATE ԭʼ����\r\n" + 
				"SET ��ע = '�ظ�'\r\n" + 
				"FROM @Temp AS a\r\n" + 
				"WHERE ԭʼ����.��� = a.��� AND\r\n" + 
				"	ԭʼ����.�˵���� = a.�˵���� AND\r\n" + 
				"	ԭʼ����.�ռ�ʡ = a.�ռ�ʡ AND\r\n" + 
				"	ԭʼ����.���� = a.���� AND\r\n" + 
				"	ԭʼ����.�Ʒ����� = a.�Ʒ����� AND\r\n" + 
				"	ԭʼ����.��λ�ܼ� = a.��λ�ܼ� AND\r\n" + 
				"	ԭʼ����.��ݹ�˾ = a.��ݹ�˾ AND\r\n" + 
				"	ԭʼ����.�·� = a.�·� AND\r\n" + 
				"	ԭʼ����.��ע = a.��ע\r\n" + 
				"\r\n" + 
				"DELETE FROM @Temp\r\n" + 
				"\r\n" + 
				"INSERT INTO @Temp SELECT ԭʼ����.* \r\n" + 
				"FROM ԭʼ���� JOIN \r\n" + 
				"(SELECT MAX(���) AS ���, �˵���� FROM ԭʼ���� GROUP BY �˵����,�·� HAVING COUNT(�˵����) =2) AS k\r\n" + 
				"ON k.��� = ԭʼ����.��� AND k.�˵���� = ԭʼ����.�˵����\r\n" + 
				"\r\n" + 
				"UPDATE ԭʼ����\r\n" + 
				"SET ��ע = 'ͬ���ظ�'\r\n" + 
				"FROM @Temp AS a\r\n" + 
				"WHERE ԭʼ����.��� = a.��� AND\r\n" + 
				"	ԭʼ����.�˵���� = a.�˵���� AND\r\n" + 
				"	ԭʼ����.�ռ�ʡ = a.�ռ�ʡ AND\r\n" + 
				"	ԭʼ����.���� = a.���� AND\r\n" + 
				"	ԭʼ����.�Ʒ����� = a.�Ʒ����� AND\r\n" + 
				"	ԭʼ����.��λ�ܼ� = a.��λ�ܼ� AND\r\n" + 
				"	ԭʼ����.��ݹ�˾ = a.��ݹ�˾ AND\r\n" + 
				"	ԭʼ����.�·� = a.�·� AND\r\n" + 
				"	ԭʼ����.��ע = a.��ע";
		
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

	
	public void findOrderNum(int count) {
		Connection c = dbcs.getConnection();
		String query = "SELECT * INTO ��� FROM ԭʼ���� WHERE ��ע=''\r\n" + 
				"\r\n" + 
				"ALTER TABLE ���\r\n" + 
				"ADD \r\n" + 
				"��Ӧ������ varchar(8000),\r\n" + 
				"��Ӧ���� varchar(8000)\r\n" + 
				"\r\n" + 
				"UPDATE ���\r\n" + 
				"SET ��Ӧ������ = ''\r\n" + 
				"WHERE ��Ӧ������ IS NULL\r\n" + 
				"\r\n" + 
				"UPDATE ���\r\n" + 
				"SET ��Ӧ���� = ''\r\n" + 
				"WHERE ��Ӧ���� IS NULL\r\n";
		for(int i = 1; i<count; i++) {
			String s = "��Ӧ����" + Integer.toString(i);
			query = query + 
					"UPDATE t2\r\n" + 
					"SET t2.��Ӧ������ = t1.������\r\n" + 
					"FROM (SELECT * FROM "
					+ s
					+ " WHERE �����浥�� IN (SELECT �����浥�� FROM "
					+ s
					+ " GROUP BY �����浥�� HAVING COUNT(�����浥��) = 1)) AS t1\r\n" + 
					"JOIN ��� t2 on t1.�����浥�� = t2.�˵����\r\n" + 
					"WHERE ��Ӧ������ = ''";
		}
		
				
		
		ResultSet rs = null;
		try {
			PreparedStatement stmt = c.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery();
		} catch (SQLException e) {
			Main.gui.displayMessage(e.getMessage());
		}
	}

	public ResultSet markAfterDuplicate() {
		Connection c = dbcs.getConnection();
		String query = "SELECT * \r\n" + 
				"FROM ԭʼ����\r\n" + 
				"WHERE �˵���� IN \r\n" + 
				"(SELECT �˵���� FROM ԭʼ���� GROUP BY �˵���� HAVING COUNT(�˵����) >2) \r\n" + 
				"ORDER BY �˵����";
		
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
