package usagestatisticsclient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.h2.tools.Server;




final class DaoTemporaryDatabaseH2 implements DaoTemporaryDatabaseInterface
{
	private final String user="localdb";
	private final String pass="localpass";
	private Server server=null;
	private Connection conn=null;
	
	DaoTemporaryDatabaseH2()
	{
		openDatabase();
	}
	
	@Override
	public boolean saveLog(final LogInformation log) 
	{	
		checkIfBaseIsOpen();
		if (log==null || conn==null)
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
		java.sql.Timestamp sqlTimestamp =new java.sql.Timestamp(log.getDateTime().getTime());
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
		return true;
		
	}

	@Override
	public synchronized void clearFirstLog() throws SQLException 
	{	
		checkIfBaseIsOpen();
		
		if (isEmpty() || conn==null)
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

	@Override
	public LogInformation getFirstLog() throws SQLException 
	{	
		checkIfBaseIsOpen();
		if (isEmpty() || conn==null){
		
			return null;
		}
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
			
				//return null; //dopiero co utworzono tabele, loga nie ma
			}
			return null;
		}
		rs.first();
		LogInformation logInformation = new LogInformation(rs.getTimestamp("timestamp"),rs.getString("functionality"),rs.getString("user"),rs.getString("tool"),rs.getString("parameters"));
		if (!LogInformation.validateLog(logInformation)){
		
			return null;
		}
	
		return logInformation;

	}

	@Override
	public boolean isEmpty() throws SQLException 
	{	
			checkIfBaseIsOpen();
			if(conn==null){
			
				return false;
			}
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
				else{
			
					throw e;
				}
				
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
			else{

				throw e;
			}
			
		}
	}
	
	@Override
	public void openDatabase() 
	{
		
		
        try {
        	if (server==null||server.getStatus().equals("Not started"))
        	{
        		server = Server.createTcpServer(new String[] { "-tcpAllowOthers" }).start();
        	}
			Class.forName("org.h2.Driver");
	        conn= DriverManager.getConnection("jdbc:h2:tcp://localhost/db", user,pass);
	        createTables();
		} 
        	catch (ClassNotFoundException e) 
		{
		}
        catch (SQLException e) {
	        try {
	        conn= DriverManager.getConnection("jdbc:h2:tcp://localhost/db", user,pass);
	        createTables();
			} catch (SQLException e1) {
			}
	        
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
				if (server!=null&&!server.getStatus().equals("Not started"))
				{
					server.shutdown();
					server.stop();
				}
			} catch (SQLException e) {
				try {
					conn.close();
				} catch (SQLException e1) {
				}
			}
	}	
	
	private void createTables()
	{
		checkIfBaseIsOpen();
		String query="CREATE TABLE IF NOT EXISTS Log (id int NOT NULL AUTO_INCREMENT, timestamp timestamp, functionality varchar(50), user varchar(50), tool varchar(50),parameters varchar(200))";
		
				
			if(conn!=null){
				try
				{
					conn.createStatement().execute(query);
				} catch (SQLException e)
				{
				}
			
			
		
	}
		
		query="CREATE TABLE IF NOT EXISTS Credentials (username varchar(50) PRIMARY KEY, password varchar(50))";
		try {
			if(conn!=null){
				conn.createStatement().execute(query);
			}		
		} catch (SQLException e) {
			try {
				conn.createStatement().execute(query);
			} catch (SQLException e1) {
			}
		}

	}
	
	
	
	private void recreateTable() throws SQLException
	{

        	if (server==null||server.getStatus().equals("Not started"))
        	{
        		server = Server.createTcpServer(new String[] { "-tcpAllowOthers" }).start();
        	}
			try {
				Class.forName("org.h2.Driver");
		        conn= DriverManager.getConnection("jdbc:h2:tcp://localhost/db", user,pass);
		        if(conn!=null){
		        	String query="DROP TABLE IF EXISTS Log";
		        	conn.createStatement().execute(query);
		        	createTables();
		        }
		        else{
		        	throw new SQLException(UsageStatisticException.ERROR_WITH_CONNECTION_TO_LOCAL_DATABASE);
		        	}
			} catch (ClassNotFoundException e) {

			}


      
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
			} 
			catch (SQLException e)
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
	
	public boolean isOpen() throws SQLException{
		return !conn.isClosed();
	}

	@Override
	public Date getOldestLogDate()
	{
		checkIfBaseIsOpen();
		String sql="SELECT TOP 1 timestamp FROM Log"; //TODO test trzeba napisac czy napewno zwraca najstarszy rekord a nie tylko 'pierwszy'
		try
		{
		ResultSet rs=conn.createStatement().executeQuery(sql);
		if (!rs.first())
		{
			return null;
		}
		return rs.getTimestamp("timestamp");
		}
		catch (SQLException e)
		{
			if (e.getMessage().contains("Tablela \"LOG\" nie istnieje"))
			{
				createTables();
					
			}
			
			return null;			
		}
		
	}
	
	public List<LogInformation> getAllLogs()
	{
		
		checkIfBaseIsOpen();
		try
		{
			if (isEmpty())
			{
				return new ArrayList<LogInformation>();
			}
		} catch (SQLException e1)
		{
			return new ArrayList<LogInformation>();
		}
		String sql="SELECT * FROM Log";
		final ResultSet rs;
		try
		{
		rs=conn.createStatement().executeQuery(sql);
		return getLogsFromResultSet(rs);
		}
		catch (SQLException e)
		{
			if (e.getMessage().contains("Tablela \"LOG\" nie istnieje"))
			{
				createTables();
			}	
			return new ArrayList<LogInformation>();
		}		
	}
	
	private ArrayList<LogInformation> getLogsFromResultSet(ResultSet rs) throws SQLException {
		ArrayList<LogInformation> loglist=new ArrayList<LogInformation>();
		if (!rs.first())
		{
			return new ArrayList<LogInformation>();
		}
		
		do
		{
		LogInformation logInformation = new LogInformation(rs.getTimestamp("timestamp"),rs.getString("functionality"),rs.getString("user"),rs.getString("tool"),rs.getString("parameters"));
		if (LogInformation.validateLog(logInformation))
		{
			loglist.add(logInformation);
		}
		rs.next();
		}
		while (!rs.isAfterLast());
		return loglist;
	}
	
	
		

}
