package UsageStatisticClient;

import java.sql.*;




class DaoTemporaryDatabaseH2 implements DaoTemporaryDatabaseInterface
{

	Connection conn=null;
	
	DaoTemporaryDatabaseH2()
	{
		openDatabase();
	}
	
	@Override
	public boolean saveLog(LogInformation log) 
	{
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
			return false;
		}
		catch (Exception e)
		{
			return false;
		}
			
		return true;
	}

	@Override
	public synchronized boolean clearFirstLog() throws SQLException 
	{
		if (isEmpty())return false;
		String sql="SELECT TOP 1 * FROM Log";
		ResultSet rs=conn.createStatement().executeQuery(sql);
		rs.first();
		int index=rs.getInt("id");
		sql="DELETE FROM Log WHERE id="+index;
		conn.createStatement().executeUpdate(sql);
		return true;
	}

	@Override
	public LogInformation getFirstLog() throws SQLException 
	{
		String sql="SELECT TOP 1 * FROM Log";
		ResultSet rs=conn.createStatement().executeQuery(sql);
		if (isEmpty())return null;
		rs.first();
		LogInformation logInformation = new LogInformation(rs.getTimestamp("timestamp"),rs.getString("functionality"),rs.getString("user"),rs.getString("tool"),rs.getString("parameters"));
		if (!LogInformation.validateLog(logInformation))
			return null;
		return logInformation;

	}

	@Override
	public boolean isEmpty() throws SQLException 
	{

			String sql="SELECT COUNT(*) FROM Log";
			
			ResultSet rs=conn.createStatement().executeQuery(sql);
			rs.first();
			return rs.getString(1).equals("0");
	}

	@Override
	public int getLogsAmount() throws SQLException 
	{
		String sql="SELECT COUNT(*) FROM Log";
		ResultSet rs=conn.createStatement().executeQuery(sql);
		rs.first();
		return Integer.parseInt(rs.getString(1));
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
	public void closeDatabase() //TODO jak zamknac baze?
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

}
