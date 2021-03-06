package usagestatisticsserver;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DaoServerDatabaseH2
{
	private Connection conn = null;
	private String user = null, pass = null;
	private static final String DEBUGLOGFILE = "errorlog.txt";
	private static final int MSTODAY = 86400000;

	public DaoServerDatabaseH2()
	{
		openDatabase();
	}

	public final boolean addUserClient(final String username, final String password)
	{
		checkIfBaseIsOpen();
		String sql = "INSERT INTO Credentials (username, password) VALUES ('" + username + "', '" + password + "')";
		user=null;
		pass=null;
		try
		{
			conn.createStatement().execute(sql);
			return true;

		}
		catch (SQLException e)
		{
			try
			{
				sql = "UPDATE Credentials SET password='" + password + "' WHERE username='" + username + "'";
				conn.createStatement().execute(sql);
				return true;
			}
			catch (SQLException e2)
			{
				return false;
			}
		}
	}

	public final boolean isValidCredential(final String user, final String password)
	{
		checkIfBaseIsOpen();
		if (this.user != null && !this.user.isEmpty() && this.user.equals(user) && this.pass != null && !this.pass.isEmpty() && this.pass.equals(password))
		{
			return true;
		}
		String sql = "SELECT username, password FROM Credentials WHERE username='" + user + "' AND password='" + password + "'";
		try
		{
			ResultSet rs = conn.createStatement().executeQuery(sql);
			if (rs.first())
			{
				this.user = user;
				this.pass = password;
				return true;
			}
			return false;
		}
		catch (SQLException e)
		{
			return false;
		}
	}

	public final boolean saveLog(final LogInformation log)
	{
		checkIfBaseIsOpen();
		if (log != null && LogInformation.validateLog(log) && conn != null)
		{
			java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(log.getDateTime().getTime());
			String sql = "INSERT INTO Log " + "(timestamp, functionality , user , tool ,parameters) " + "values(\'" + sqlTimestamp + "\', \'" + log.getFunctionality() + "\', \'" + log.getUser() + "\', \'" + log.getTool() + "\', \'"
					+ log.getParameters() + "\')";
			try
			{
				conn.createStatement().execute(sql);
				return true;
			}
			catch (SQLException e)
			{
				return false;
			}
		}
		return false;

	}

	public final void openDatabase()
	{
		try
		{
			Class.forName("org.h2.Driver");
			conn = DriverManager.getConnection("jdbc:h2:~/server/serverdb", "user", "");
			createTables();
		}
		catch (ClassNotFoundException e)
		{
		}
		catch (SQLException e)
		{
			logerror(e);
		}

	}

	private void logerror(SQLException e)
	{
		BufferedWriter out;
		try
		{
			out = new BufferedWriter(new FileWriter(DEBUGLOGFILE, true));
			out.write(Calendar.getInstance().getTime() + ": " + e.getMessage() + "\n");
			out.close();
		}
		catch (IOException e1)
		{
		}

	}

	private void createTables()
	{
		try
		{
			String query = "CREATE TABLE IF NOT EXISTS Log (id int NOT NULL AUTO_INCREMENT, timestamp timestamp, functionality varchar(50), user varchar(50), tool varchar(50),parameters varchar(200))";

			if (conn != null)
			{
				conn.createStatement().execute(query);
			}

			query = "CREATE TABLE IF NOT EXISTS Credentials (username varchar(50) PRIMARY KEY, password varchar(64))";
			if (conn != null)
			{
				conn.createStatement().execute(query);
			}
		}
		catch (SQLException e)
		{
			logerror(e);
		}

	}

	private void checkIfBaseIsOpen()
	{
		try
		{
			if (conn == null || conn.isClosed())
			{
				openDatabase();

			}
			createTables();
		}
		catch (SQLException e)
		{
		}
	}

	public final boolean isEmpty() throws SQLException
	{
		checkIfBaseIsOpen();
		String sql = "SELECT COUNT(*) FROM Log";
		try
		{
			ResultSet rs = conn.createStatement().executeQuery(sql);
			rs.first();
			return rs.getString(1).equals("0");
		}
		catch (SQLException e)
		{
			throw e;

		}
	}

	public final int getLogsAmount() throws SQLException
	{
		checkIfBaseIsOpen();
		String sql = "SELECT COUNT(*) FROM Log";
		try
		{
			ResultSet rs = conn.createStatement().executeQuery(sql);
			rs.first();
			return Integer.parseInt(rs.getString(1));
		}
		catch (SQLException e)
		{
			throw e;
		}
	}

	public final ArrayList<LogInformation> getAllLogs() throws SQLException
	{
		checkIfBaseIsOpen();
		if (isEmpty())
		{
			return new ArrayList<LogInformation>();
		}
		String sql = "SELECT * FROM Log";

		ResultSet rs = null;
		rs = conn.createStatement().executeQuery(sql);
		return getLogsFromResultSet(rs);

	}

	private ArrayList<LogInformation> getLogsFromResultSet(ResultSet rs) throws SQLException
	{
		ArrayList<LogInformation> loglist = new ArrayList<LogInformation>();
		if(!rs.first())
		{
			return loglist;
		}
		do
		{
			LogInformation logInformation = new LogInformation(rs.getTimestamp("timestamp"), rs.getString("functionality"), rs.getString("user"), rs.getString("tool"), rs.getString("parameters"));
			if (LogInformation.validateLog(logInformation))
			{
				loglist.add(logInformation);
			}
			rs.next();
		} while (!rs.isAfterLast());
		return loglist;
	}

	public final ArrayList<String> getUsers() throws SQLException
	{
		return getColumn("user");
	}

	public final ArrayList<String> getTools() throws SQLException
	{
		return getColumn("tool");
	}

	public final ArrayList<String> getFunctionalities() throws SQLException
	{
		return getColumn("functionality");
	}

	private ArrayList<String> getColumn(String column) throws SQLException
	{
		ArrayList<String> values = new ArrayList<String>();
		checkIfBaseIsOpen();
		if (isEmpty())
		{
			return values;
		}
		String sql = "SELECT DISTINCT " + column + " FROM Log";
		ResultSet rs = null;
		rs = conn.createStatement().executeQuery(sql);
		if (!rs.first())
		{
			return values;
		}
		while (!rs.isAfterLast())
		{
			values.add(rs.getString(column));
			rs.next();
		}

		return values;

	}

	public final ArrayList<String> getFunctionalities(String tool) throws SQLException
	{
		ArrayList<String> values = new ArrayList<String>();
		checkIfBaseIsOpen();
		if (isEmpty())
		{
			return values;
		}
		String sql = "SELECT DISTINCT functionality FROM Log WHERE tool=\'" + tool + "\' ORDER BY functionality";
		ResultSet rs = null;

		rs = conn.createStatement().executeQuery(sql);
		if (!rs.first())
		{
			return values;
		}

		while (!rs.isAfterLast())
		{
			values.add(rs.getString("functionality"));
			rs.next();
		}

		return values;

	}

	public final ArrayList<String> getUsers(String tool) throws SQLException
	{
		ArrayList<String> values = new ArrayList<String>();
		checkIfBaseIsOpen();
		if (isEmpty())
		{
			return values;
		}
		String sql = "SELECT DISTINCT user FROM Log WHERE tool=\'" + tool + "\' ORDER BY user";
		ResultSet rs = null;
		rs = conn.createStatement().executeQuery(sql);
		if (!rs.first())
		{
			return values;
		}

		while (!rs.isAfterLast())
		{
			values.add(rs.getString("user"));
			rs.next();
		}

		return values;
	}

	private String andAndOrStatement(String name, String[] values)
	{
		String statement="";
		for (int i = 0; i < values.length; i++)
		{
			statement+=name+"= '"+values[i]+"' ";
			if (i + 1 < values.length)
			{
				statement += "OR ";
			} else{
				statement+=") AND ";
			}
		}
		return statement;
	}
	
	
	
	
	public final ArrayList<StandardFilter> getLogsFromDatabase(String[] functionalities, String[] users, String tool, String datefrom, String dateto, boolean param) throws SQLException
	{
		ArrayList<StandardFilter> values = new ArrayList<StandardFilter>();
		checkIfBaseIsOpen();
		if (isEmpty() || functionalities==null||functionalities.length == 0 || users==null||users.length == 0)
		{
			return values;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		java.sql.Timestamp sqlfrom = null, sqlto = null;

		try
		{
			if (datefrom != null && !datefrom.isEmpty())
			{
				sqlfrom = new java.sql.Timestamp(sdf.parse(datefrom).getTime());
			}
			if (dateto != null && !dateto.isEmpty())
			{
				sqlto = new java.sql.Timestamp(sdf.parse(dateto).getTime() + MSTODAY);
			}
		}
		catch (ParseException e1)
		{
			return values;
		}

		String cols = "functionality";
		if (param)
		{
			cols += ", parameters";
		}
		String sql = "SELECT DISTINCT " + cols + ", COUNT(*) AS cnt FROM Log WHERE (";
		sql+=(andAndOrStatement("functionality",functionalities)+"(");
		sql+=(andAndOrStatement("user",users));
		if (sqlfrom != null){
			sql += ("timestamp>=\'" + sqlfrom + "\' AND ");
		}
		if (sqlto != null){
			sql += ("timestamp<\'" + sqlto + "\' AND ");
		}

		sql += ("tool=\'" + tool + "\' GROUP BY " + cols + " ORDER BY " + cols + ";");
		ResultSet rs = null;

		rs = conn.createStatement().executeQuery(sql);
		if (!rs.first()){
			return values;
		}
		values.add(new StandardFilter(rs.getString("functionality"), rs.getInt("cnt"), (param ? rs.getString("parameters") : null)));
		rs.next();
		while (!rs.isAfterLast())
		{
			values.add(new StandardFilter(rs.getString("functionality"), rs.getInt("cnt"), (param ? rs.getString("parameters") : null)));
			rs.next();
		}
		return values;
	}

}
