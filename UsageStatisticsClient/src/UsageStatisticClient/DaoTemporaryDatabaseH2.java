package UsageStatisticClient;

import java.sql.*;




final class DaoTemporaryDatabaseH2 implements DaoTemporaryDatabaseInterface
{

	Connection conn=null;
	
	DaoTemporaryDatabaseH2()
	{
		openDatabase();
	}
	
	@Override
	public boolean saveLog(LogInformation log) 
	{	
		checkIfBaseIsOpen();
		if (log==null)
		{
			return false;
		}
		
		if (log.getParameters()==null)
		{
			log.setParameters(""); //Baza H2 zapisuje nulla jako String "null"
		}
		if (!LogInformation.validateLog(log))
		{
			return false;
		}
		java.sql.Timestamp sqlTimestamp =new java.sql.Timestamp(log.getDate().getTime());
		String sql="INSERT INTO Log " +
				"(timestamp, functionality , user , tool ,parameters) " +
				"values(\'"+sqlTimestamp+"\', \'"+log.getFunctionality()+"\', \'"+log.getUser()+"\', \'"+log.getTool()+"\', \'"+log.getParameters()+"\')";
		try
		{
			conn.createStatement().execute(sql);
		} catch (SQLException e)
		{
			resetDatabase();
			try
			{
				conn.createStatement().execute(sql);
			} catch (SQLException e1)
			{
				return false;
			}
		}
		catch (Exception e)
		{
			return false;
		}
			
		return true;
	}

	@Override
	public synchronized void clearFirstLog() throws SQLException 
	{	
		checkIfBaseIsOpen();
		try
		{
		if (isEmpty())
		{
			throw new SQLException();
		}
		String sql="SELECT TOP 1 * FROM Log";
		ResultSet rs=conn.createStatement().executeQuery(sql);
		rs.first();
		int index=rs.getInt("id");
		sql="DELETE FROM Log WHERE id="+index;
		conn.createStatement().executeUpdate(sql);
		}
		catch (SQLException e)
		{
			throw e;
		}
		
		catch (Exception e)
		{
			throw new SQLException();
		}
		
	}

	@Override
	public LogInformation getFirstLog() throws SQLException 
	{	
		checkIfBaseIsOpen();
		if (isEmpty())return null;
		String sql="SELECT TOP 1 * FROM Log";
		ResultSet rs = null;
		try
		{
		rs=conn.createStatement().executeQuery(sql);
		}
		catch (SQLException e)
		{
			if (e.getMessage().contains("Tablela \"LOG\" nie istnieje"))
			{
				createTables();
				return null; //dopiero co utworzono tabele, loga nie ma
			}			
		}
		rs.first();
		LogInformation logInformation = new LogInformation(rs.getTimestamp("timestamp"),rs.getString("functionality"),rs.getString("user"),rs.getString("tool"),rs.getString("parameters"));
		if (!LogInformation.validateLog(logInformation))
			return null;
		return logInformation;

	}

	@Override
	public boolean isEmpty() throws SQLException 
	{	
			checkIfBaseIsOpen();
			String sql="SELECT COUNT(*) FROM Log";
			try
			{
			ResultSet rs=conn.createStatement().executeQuery(sql);
			rs.first();
			return rs.getString(1).equals("0");
			}
			catch (SQLException e)
			{
				if (e.getMessage().contains("Tablela \"LOG\" nie istnieje"))
				{
					createTables();
					return true;
				}
				else throw e;
				
			}			
	}

	@Override
	public int getLogsAmount() throws SQLException 
	{	
		checkIfBaseIsOpen();
		String sql="SELECT COUNT(*) FROM Log";
		try
		{
		ResultSet rs=conn.createStatement().executeQuery(sql);
		rs.first();
		return Integer.parseInt(rs.getString(1));
		}
		catch (SQLException e)
		{
			if (e.getMessage().contains("Tablela \"LOG\" nie istnieje"))
			{
				createTables();
				return 0; //utworzono tabele, liczba logow = 0				
			}
			else throw e;
			
		}
	}
	
	@Override
	public void openDatabase()
	{
        try {
			Class.forName("org.h2.Driver");
	        conn= DriverManager.getConnection("jdbc:h2:~/db", "user", "");
	        createTables();
		} catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		}
        catch (SQLException e) {
		}
        
        catch (Exception e)
        {
        }

	}
	
	@Override
	public void closeDatabase()
	{

			try {
				if (conn!=null)
				{
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}	
	
	private void createTables()
	{
		try {
			

			String query="CREATE TABLE IF NOT EXISTS Log (id int NOT NULL AUTO_INCREMENT, timestamp timestamp, functionality varchar(50), user varchar(50), tool varchar(50),parameters varchar(200))";
			conn.createStatement().execute(query);
		} catch (SQLException e) {
		}
	}
	
	
	
	private void recreateTable() throws SQLException
	{
		try
		{
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException e)
		{
		}
        conn= DriverManager.getConnection("jdbc:h2:~/db", "user", "");
        String query="DROP TABLE IF EXISTS Log"; 
        conn.createStatement().execute(query);
        createTables();
	}

	@Override
	public void resetDatabase()
	{
			try
			{
				if (conn==null || conn.isClosed())
				{
					recreateTable();
				} 
				else
				{
						String query="DROP TABLE IF EXISTS Log"; 
				        conn.createStatement().execute(query);
				        createTables();
				}
			} catch (SQLException e)
			{
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
			resetDatabase();
		}
	}
		

}
