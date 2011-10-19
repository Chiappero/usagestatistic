package UsageStatisticServer;

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
		checkIfBaseIsOpen();
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
				if (e.getMessage().contains("Tablela \"LOG\" nie istnieje"))
				{
					createTables();
					try {
						conn.createStatement().execute(sql);
						return true;
					} catch (SQLException e1) 
					{
						return false;
					}
				}	
				
				
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
        catch (SQLException e) 
        {
			
		}

	}
	
	public void closeDatabase()
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
	
	private void checkIfBaseIsOpen()
	{
		try
		{
			if (conn==null || conn.isClosed())
			{
				openDatabase();
			}
		} catch (SQLException e)
		{
			
		}
	}

}
