package UsageStatisticServer;

import java.sql.*;
import java.util.ArrayList;




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
			System.out.println(e.getMessage());
		}
        catch (SQLException e) 
        {
        	//TODO Baza otwarta przez inny proces
			System.out.println(e.getMessage());
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
					//TODO cos bardzo zlego powinno tu byc
					throw e;

				}
				else throw e;
				
			}			
	}
	
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
				//TODO cos bardzo zlego powinno tu byc	
				throw e;
			}
			else throw e;
			
		}
	}
	

	public ArrayList<LogInformation> getAllLogs() throws SQLException 
	{	
		checkIfBaseIsOpen();
		if (isEmpty())return new ArrayList<LogInformation>();
		String sql="SELECT * FROM Log";
		ResultSet rs = null;
		try
		{
		rs=conn.createStatement().executeQuery(sql);
		return getLogsFromResultSet(rs);
		}
		catch (SQLException e)
		{
			if (e.getMessage().contains("Tablela \"LOG\" nie istnieje"))
			{
				//TODO cos bardzo zlego powinno tu byc
				throw e;

			}	
			else throw e;
		}

	}
	
	public ArrayList<LogInformation> getAllLogs(int from, int count) throws SQLException 
	{	
		checkIfBaseIsOpen();
		if (isEmpty())return new ArrayList<LogInformation>();
		if (from<0)from=0;
		if (count<1)return new ArrayList<LogInformation>();
		String sql="SELECT TOP "+(from+count)+" * FROM Log";
		ResultSet rs = null;

		try
		{
		rs=conn.createStatement().executeQuery(sql);


		return getLogsFromResultSet(rs,from,count);
		}
		catch (SQLException e)
		{
			if (e.getMessage().contains("Tablela \"LOG\" nie istnieje"))
			{
				//TODO cos bardzo zlego powinno tu byc
				throw e;
			}	
			else throw e;
		}

	}	
	
	private ArrayList<LogInformation> getLogsWithWhereClausure(String whereclausure) throws SQLException 
	{	
		checkIfBaseIsOpen();
		if (isEmpty())return new ArrayList<LogInformation>();
		String sql="SELECT * FROM Log";
		if (whereclausure!=null&&!whereclausure.isEmpty())sql+=" WHERE "+whereclausure;
		ResultSet rs = null;

		try
		{
		rs=conn.createStatement().executeQuery(sql);

		return getLogsFromResultSet(rs);
		}
		catch (SQLException e)
		{
			if (e.getMessage().contains("Tablela \"LOG\" nie istnieje"))
			{
				//TODO cos bardzo zlego powinno tu byc
				throw e;
			}	
			//TODO czy tu rozpatrujemy zly format zapytania skoro to bedzie prywatne?
			else throw e;
		}

	}		
	
	private ArrayList<LogInformation> getLogsWithWhereClausure(String whereclausure,int from, int count) throws SQLException 
	{	
		checkIfBaseIsOpen();
		if (isEmpty())return new ArrayList<LogInformation>();
		if (from<0)from=0;
		if (count<1)return new ArrayList<LogInformation>();
		String sql="SELECT TOP "+(from+count)+" * FROM Log";
		if (whereclausure!=null&&!whereclausure.isEmpty())sql+=" WHERE "+whereclausure;
		ResultSet rs = null;

		try
		{
		rs=conn.createStatement().executeQuery(sql);
		
		
		
		return getLogsFromResultSet(rs,from,count);
		}
		catch (SQLException e)
		{
			if (e.getMessage().contains("Tablela \"LOG\" nie istnieje"))
			{
				//TODO cos bardzo zlego powinno tu byc
				throw e;
			}	
			//TODO czy tu rozpatrujemy zly format zapytania skoro to bedzie prywatne?
			else throw e;
		}

	}		
	
	public  ArrayList<LogInformation> getLogsWithWhereClausure
			(java.util.Date datefrom, java.util.Date datebefore, ArrayList<String> functionality,
			 ArrayList<String> user, ArrayList<String> tool, int from, int count) throws SQLException
	{
		StringBuffer where=new StringBuffer("");
		if (datefrom!=null)
		{
			java.sql.Timestamp sqlTimestamp =new java.sql.Timestamp(datefrom.getTime());
			where.append(" timestamp>=\'"+sqlTimestamp+"\' AND");
		}
		if (datebefore!=null)
		{
			java.sql.Timestamp sqlTimestamp =new java.sql.Timestamp(datebefore.getTime());
			where.append(" timestamp<=\'"+sqlTimestamp+"\' AND");
		}	
		if (functionality!=null)
			for (String f: functionality)
			{
				where.append(" functionality=\'"+f+"\' AND");
			}
		if (user!=null)
			for (String u: user)
			{
				where.append(" user=\'"+u+"\' AND");
			}		
		if (tool!=null)
			for (String t: tool)
			{
				where.append(" tool=\'"+t+"\' AND");
			}
		if (!where.toString().isEmpty())
			where.delete(where.length()-4, where.length());
		String clausure=where.toString();
		if (from!=-1&&count!=-1)
			return getLogsWithWhereClausure(clausure,from,count);
		else return getLogsWithWhereClausure(clausure);
		
}
	
	public  ArrayList<LogInformation> getLogsWithWhereClausure
	(java.util.Date datefrom, java.util.Date date, ArrayList<String> functionality,
	 ArrayList<String> user, ArrayList<String> tool) throws SQLException
	 {
		return getLogsWithWhereClausure
		(datefrom,date,functionality,user,tool, -1,-1);
	 }
	
	
	
	
	
	


	private ArrayList<LogInformation> getLogsFromResultSet(ResultSet rs, int from, int count) throws SQLException {
		ArrayList<LogInformation> loglist=new ArrayList<LogInformation>();

		if (!rs.absolute(from))return new ArrayList<LogInformation>();
		from=0;
		do
		{
		LogInformation logInformation = new LogInformation(rs.getTimestamp("timestamp"),rs.getString("functionality"),rs.getString("user"),rs.getString("tool"),rs.getString("parameters"));
		if (LogInformation.validateLog(logInformation))
		{
			loglist.add(logInformation);
			from++;
		}
		rs.next();
		
		}
		while (!rs.isAfterLast()&&from<count);
		return loglist;
	}


	private ArrayList<LogInformation> getLogsFromResultSet(ResultSet rs) throws SQLException {
		ArrayList<LogInformation> loglist=new ArrayList<LogInformation>();
		if (!rs.first())return new ArrayList<LogInformation>();
		do
		{
		LogInformation logInformation = new LogInformation(rs.getTimestamp("timestamp"),rs.getString("functionality"),rs.getString("user"),rs.getString("tool"),rs.getString("parameters"));
		if (LogInformation.validateLog(logInformation))
			loglist.add(logInformation);
		rs.next();
		}
		while (!rs.isAfterLast());
		return loglist;
	}
	
	public ArrayList<String> getUsers() throws SQLException
	{
		return getColumn("user");
	}
	
	public ArrayList<String> getTools() throws SQLException
	{
		return getColumn("tool");
	}	
	public ArrayList<String> getFunctionalities() throws SQLException
	{
		return getColumn("functionality");
	}
	
	
	private ArrayList<String> getColumn(String column) throws SQLException
	{
		ArrayList<String> values=new ArrayList<String>();
		checkIfBaseIsOpen();
		if (isEmpty())return values;
		String sql="SELECT DISTINCT "+column+" FROM Log";
		ResultSet rs = null;

		try
		{
		rs=conn.createStatement().executeQuery(sql);
		rs.first();
		do
		{
			values.add(rs.getString(column));
			rs.next();
		}
		while (!rs.isAfterLast());
		return values;
		}
		catch (SQLException e)
		{
			if (e.getMessage().contains("Tablela \"LOG\" nie istnieje"))
			{
				//TODO cos bardzo zlego powinno tu byc
				throw e;
			}	
			else throw e;
		}
	}
	
	

}
