package server;

import java.sql.*;




public class DaoServerDatabaseH2
{
	
	public DaoServerDatabaseH2()
	{
		openDatabase();
	}
	
	Connection conn=null;
	
	public boolean saveLog(LogInformation log) 
	{
		
		if (log!=null&&LogInformation.validateLog(log))
		{
		java.sql.Timestamp sqlTimestamp =new java.sql.Timestamp(log.getDate().getTime());
		String sql="INSERT INTO Log " +
				"(timestamp, functionality , user , tool ,parameters) " +
				"values(\'"+sqlTimestamp+"\', \'"+log.getFunctionality()+"\', \'"+log.getUser()+"\', \'"+log.getTool()+"\', \'"+log.getParameters()+"\')";
			try
			{
				conn.createStatement().execute(sql);
			} catch (SQLException e)
			{
				return false;
			}
			
			catch (Exception e)
			{
				return false;
			}
			
			return true;
		}
		
		return false;
		
	}


	public void openDatabase()
	{
        try {
			Class.forName("org.h2.Driver");
	        conn= DriverManager.getConnection("jdbc:h2:~/server/serverdb", "user", "");
	        createTables();
		} catch (ClassNotFoundException e) 
		{
		}
        catch (SQLException e) {
        	// TODO CO JAK SIE NIE UDA
			
		}

	}
	
	public void closeDatabase() //TODO JAK ZAMKNAC baze?
	{

			try {
				if (conn!=null)
				{
					conn.close();
				}
			} catch (SQLException e) {
			}
	}	
	
	private void createTables()
	{
		try {
			

			String query="CREATE TABLE IF NOT EXISTS Log (id int NOT NULL AUTO_INCREMENT, timestamp timestamp, functionality varchar(50), user varchar(50), tool varchar(50),parameters varchar(200))";
			conn.createStatement().execute(query);
		} catch (SQLException e) {
			// TODO CO JAK SIE NIE UDA
		}
		
		
		
	}

}
