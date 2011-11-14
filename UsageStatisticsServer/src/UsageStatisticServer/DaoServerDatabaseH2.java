package UsageStatisticServer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;





public class DaoServerDatabaseH2
{
	private Connection conn=null;
	private String user=null,pass=null;
	
	
	
	public DaoServerDatabaseH2()
	{
		openDatabase();
	}
	
	public boolean addUserClient(final String username, final String password)
	{	
			checkIfBaseIsOpen();
			String sql = "INSERT INTO Credentials (username, password) VALUES ('"+username+"', '"+password+"')";
			
			try
			{
				conn.createStatement().execute(sql);
				 return true;
				
			} catch (SQLException e)
			{
				if (e.getMessage().contains("Tablela \"LOG\" nie istnieje"))
				{
					createTables();
					try
					{
						conn.createStatement().execute(sql);
						return true;
					} catch (SQLException e1)
					{
						//COS ZLEGO
						return false; //
					}
				} else //juz byl taki rekord
				{
				try
				{

					sql = "UPDATE Credentials SET password='"+password+"' WHERE username='"+username+"'";
					conn.createStatement().execute(sql);
					return true;
				} catch (SQLException e2)
				{
					return false;
				}
				}
			}
	}
	
	public boolean isValidCredential(final String user, final String password) throws SQLException
	{
		checkIfBaseIsOpen();
		if (this.user!=null&&!this.user.isEmpty()&&this.user.equals(user)
		  &&this.pass!=null&&!this.pass.isEmpty()&&this.pass.equals(user))
			return true;
		String sql="SELECT username, password FROM Credentials WHERE username='"+user+"' AND password='"+password+"'";
		
		try
		{
			ResultSet rs=conn.createStatement().executeQuery(sql);
			if (rs.first())
			{
				this.user=user;
				this.pass=password;
				return true;
			}
			return false;
		}
		catch (SQLException e)
		{
			if (e.getMessage().contains("Tablela \"CREDENTIALS\" nie istnieje"))
			{
				createTables();
				return false;
			}	
			else throw e;
		}		
	}
	
	
	public boolean saveLog(final LogInformation log) 
	{
		checkIfBaseIsOpen();
		if (log!=null&&LogInformation.validateLog(log) && conn!=null)
		{
		java.sql.Timestamp sqlTimestamp =new java.sql.Timestamp(log.getDateTime().getTime());
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
				try {
					conn.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
	}	
	
	private void createTables()
	{
		String query="CREATE TABLE IF NOT EXISTS Log (id int NOT NULL AUTO_INCREMENT, timestamp timestamp, functionality varchar(50), user varchar(50), tool varchar(50),parameters varchar(200))";
		try {
			if(conn!=null){
				conn.createStatement().execute(query);
			}		
		} catch (SQLException e) {
			try {
				conn.createStatement().execute(query);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		
		query="CREATE TABLE IF NOT EXISTS Credentials (username varchar(50) PRIMARY KEY, password varchar(64))";
		try {
			if(conn!=null){
				conn.createStatement().execute(query);
			}		
		} catch (SQLException e) {
			try {
				conn.createStatement().execute(query);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
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
			e.printStackTrace();
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
					somethingVeryBad();
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
				somethingVeryBad();
				throw e;
			}
			else throw e;
			
		}
	}
	

	public ArrayList<LogInformation> getAllLogs() throws SQLException 
	{	
		return getAllLogsSorted(null);
	}
	
	public ArrayList<LogInformation> getAllLogs(final int from, final int count) throws SQLException 
	{	
		return getAllLogsSorted(null, from, count);

	}	
	
	private ArrayList<LogInformation> getLogsWithWhereClausure(String whereclausure, LinkedList<String> orderby) throws SQLException 
	{	
		checkIfBaseIsOpen();
		if (isEmpty())return new ArrayList<LogInformation>();
		String sql="SELECT * FROM Log";
		if (whereclausure!=null&&!whereclausure.isEmpty())sql+=" WHERE "+whereclausure;
		sql+=getOrderByString(orderby);
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
				somethingVeryBad();
				throw e;
			}	
			//TODO czy tu rozpatrujemy zly format zapytania skoro to bedzie prywatne?
			else throw e;
		}

	}		
	
	private ArrayList<LogInformation> getLogsWithWhereClausure(final String whereclausure,final LinkedList<String> orderby,int from,final int count) throws SQLException 
	{	
		checkIfBaseIsOpen();
		if (isEmpty())return new ArrayList<LogInformation>();
		if (from<1)from=1;
		if (count<1)return new ArrayList<LogInformation>();
		String sql="SELECT * FROM Log";
		if (whereclausure!=null&&!whereclausure.isEmpty())sql+=" WHERE "+whereclausure;
		sql+=getOrderByString(orderby);
		sql+=" LIMIT "+count+" OFFSET "+(from-1); //TODO a co jezeli from=1
		ResultSet rs = null;

		try
		{
		rs=conn.createStatement().executeQuery(sql);
		
		
		
		//return getLogsFromResultSet(rs,from,count);
		return getLogsFromResultSet(rs);
		}
		catch (SQLException e)
		{
			if (e.getMessage().contains("Tablela \"LOG\" nie istnieje"))
			{
				//TODO cos bardzo zlego powinno tu byc
				somethingVeryBad();
				throw e;
			}	
			//TODO czy tu rozpatrujemy zly format zapytania skoro to bedzie prywatne?
			else throw e;
		}

	}		
	
	
	
	public  ArrayList<LogInformation> getLogsWithWhereClausure
	(LogFilter filter, int from, int count) throws SQLException
	 {
		return getLogsWithWhereClausure(filter, null, from,count);
	 }
	
	public  ArrayList<LogInformation> getLogsWithWhereClausure
	(LogFilter filter, LinkedList<String>orderby) throws SQLException
	 {
		return getLogsWithWhereClausure(filter, orderby, -1,-1);
	 }
	
	
	private String getOneFilterString(String column,ArrayList<String> filter)
	{
		StringBuffer fil=new StringBuffer("");
		if (filter!=null&&!filter.isEmpty())
		{
			fil.append("(");
			for (String f: filter)
			{
				fil.append(" "+column+"=\'"+f+"\' OR");
			}
			fil.delete(fil.length()-3, fil.length());
			fil.append(") AND");			
		}
		return fil.toString();
	}
	
	
	
	public  ArrayList<LogInformation> getLogsWithWhereClausure
			(LogFilter filter, LinkedList<String> orderby, int from, int count) throws SQLException
	{
		StringBuffer where=new StringBuffer("");
		if (filter.getDatefrom()!=null)
		{
			java.sql.Timestamp sqlTimestamp =new java.sql.Timestamp(filter.getDatefrom().getTime());
			where.append(" timestamp>=\'"+sqlTimestamp+"\' AND");
		}
		if (filter.getDatebefore()!=null)
		{
			java.sql.Timestamp sqlTimestamp =new java.sql.Timestamp(filter.getDatebefore().getTime());
			where.append(" timestamp<=\'"+sqlTimestamp+"\' AND");
		}	
		
		where.append(getOneFilterString("functionality",filter.getFunctionality()));		
		where.append(getOneFilterString("user",filter.getUser()));	
		where.append(getOneFilterString("tool", filter.getTool()));

		if (!where.toString().isEmpty())
			where.delete(where.length()-4, where.length());
		String clausure=where.toString();
		if (from!=-1||count!=-1)
			return getLogsWithWhereClausure(clausure,orderby,from,count);
		else return getLogsWithWhereClausure(clausure,orderby);
		
}
	
	public  ArrayList<LogInformation> getLogsWithWhereClausure
	(LogFilter filter) throws SQLException
	 {
		return getLogsWithWhereClausure
		(filter, null,-1,-1);
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
	
	private String getOrderByString(List<String> orderby)
	{
		String sql="";
		if (orderby!=null&&!orderby.isEmpty())
		{
			sql+=" ORDER BY ";
			for (String s:orderby)
				sql+=(s+", ");
			sql=sql.substring(0, sql.length()-2);
				
		}
		return sql;
	}

	private String getColumnsString(ArrayList<String> columns)
	{
		String sql="";
		if (columns!=null&&!columns.isEmpty())
		{
			sql+="";
			for (String s:columns)
				sql+=(s+", ");
			sql=sql.substring(0, sql.length()-2);
				
		}
		return sql;
	}
	
	
	public ArrayList<LogInformation> getAllLogsSorted(LinkedList<String> orderby) throws SQLException
	{
		
		checkIfBaseIsOpen();
		if (isEmpty())return new ArrayList<LogInformation>();
		String sql="SELECT * FROM Log";
		sql+=getOrderByString(orderby);
		
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
				somethingVeryBad();
				throw e;
			}	
			else throw e;
		}		
	}
	
	public ArrayList<LogInformation> getAllLogsSorted(LinkedList<String> orderby, int from, int count) throws SQLException
	{
		checkIfBaseIsOpen();
		if (isEmpty())return new ArrayList<LogInformation>();
		if (from<1)from=1;
		if (count<1)return new ArrayList<LogInformation>();
		String sql="SELECT * FROM Log";
		sql+=getOrderByString(orderby);
		sql+=" LIMIT "+count+" OFFSET "+(from-1);
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
				somethingVeryBad();
				throw e;
			}	
			else throw e;
		}
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
		if(!rs.first())return values;
		while (!rs.isAfterLast())
		{
			values.add(rs.getString(column));
			rs.next();
		}

		return values;
		}
		catch (SQLException e)
		{
			if (e.getMessage().contains("Tablela \"LOG\" nie istnieje"))
			{
				//TODO cos bardzo zlego powinno tu byc
				somethingVeryBad();
				throw e;
				
			}	
			else throw e;
		}
	}
	
	public ArrayList<Pair<LogInformation,Integer>> agregate(ArrayList<String> groupby) throws SQLException
	{
		return agregate(groupby, -1,-1);
	
	}
	
	public ArrayList<Pair<LogInformation,Integer>> agregate(ArrayList<String> groupby, int from, int count) throws SQLException
	{
		ArrayList<Pair<LogInformation,Integer>> values=new ArrayList<Pair<LogInformation,Integer>>();
		checkIfBaseIsOpen();
		if (from!=-1||count!=-1)
		{
			if (from<1)from=1;
			if (count<1)return new ArrayList<Pair<LogInformation,Integer>>();
		}
		
		if (isEmpty())return values;
		String columns=getColumnsString(groupby);
		String sql="SELECT "+columns+", COUNT(*) AS cnt FROM Log";
		sql+=" GROUP BY "+columns;
		sql+=getOrderByString(groupby);
		if (from!=-1||count!=-1)
		sql+=" LIMIT "+count+" OFFSET "+(from-1);
		ResultSet rs = null;
		
		try
		{
		rs=conn.createStatement().executeQuery(sql);


		return agregate(rs,groupby);
		}
		catch (SQLException e)
		{
			if (e.getMessage().contains("Tablela \"LOG\" nie istnieje"))
			{
				//TODO cos bardzo zlego powinno tu byc
				somethingVeryBad();
				throw e;
			}	
			else throw e;
		}		
	}

	public ArrayList<Pair<LogInformation,Integer>> agregateOverTime(ArrayList<String> groupby, int timeinterval) throws SQLException
	{
		return agregateOverTime(groupby,timeinterval,-1,-1);
	}
	
//unchecked
	public ArrayList<Pair<LogInformation,Integer>> agregateOverTime(ArrayList<String> groupby, int timeinterval, int from, int count) throws SQLException
	{
		boolean flag=true;
		for (int i=0;i<groupby.size()&&flag;i++)
		{
			if (groupby.get(i).equals("timestamp"))
			{
				groupby.remove(i);
				flag=false;
			}
		}
		groupby.add(groupby.size(), "timestamp");
		
		ArrayList<Pair<LogInformation,Integer>> array=agregate(groupby, from, count);
		if (array==null||array.size()<2)return new ArrayList<Pair<LogInformation,Integer>>();
		long time=array.get(0).getLewy().getDateTime().getTime();
		long timeround=(time/timeinterval)*timeinterval;
		LogInformation log=array.get(0).getLewy();
		log.setDateTime(new java.util.Date(timeround));
		array.get(0).setLewy(log);
		for (int i=1;i<array.size();)
		{
			if (compareType(array.get(i).getLewy(),array.get(i-1).getLewy())&&array.get(i).getLewy().getDateTime().getTime()<=timeround+timeinterval)
			{
				array.get(i-1).setPrawy(array.get(i-1).getPrawy()+1);
				array.remove(i);
			}
			else
			{
				timeround+=timeinterval;
				log=array.get(i).getLewy();
				log.setDateTime(new java.util.Date(timeround));
				array.get(i).setLewy(log);
				i++;
				
			}
		}
		return array;
		
	}
	
	
	private boolean compareType(LogInformation lewy, LogInformation lewy2) 
	{
		try
		{
		return 
				((lewy.getFunctionality()==null&&lewy2.getFunctionality()==null)||lewy.getFunctionality().equals(lewy2.getFunctionality()))
			  &&((lewy.getTool()==null&&lewy2.getTool()==null)||lewy.getTool().equals(lewy2.getTool()))
			  &&((lewy.getUser()==null&&lewy2.getUser()==null)||lewy.getUser().equals(lewy2.getUser()))
			  &&((lewy.getParameters()==null&&lewy2.getParameters()==null)||(lewy.getParameters().equals(lewy2.getParameters())));
		}
		catch (NullPointerException e)
		{
			return false;
		}
	}


	private ArrayList<Pair<LogInformation,Integer>> agregate(ResultSet rs, ArrayList<String> groupby) throws SQLException 
	{
		ArrayList<Pair<LogInformation,Integer>> pairlist=new ArrayList<Pair<LogInformation,Integer>>();
		if (!rs.first())return pairlist;
		do
		{
			int count=rs.getInt("cnt");
			String[] str=new String[4];
			java.util.Date date = null;
			for (String s:groupby)
			{
				if (s.equals("functionality"))
					str[0]=rs.getString(s);
				else if (s.equals("user"))
					str[1]=rs.getString(s);
				else if (s.equals("tool"))
					str[2]=rs.getString(s);
				else if (s.equals("parameters"))
					str[3]=rs.getString(s);
				else if (s.equals("timestamp"))
					date=rs.getTimestamp(s);
			}
		LogInformation logInformation = new LogInformation(date,str[0],str[1],str[2],str[3]);
		pairlist.add(new Pair<LogInformation,Integer>(logInformation,count));
		rs.next();
		}
		while (!rs.isAfterLast());
		return pairlist;
	}
	
	private void somethingVeryBad() throws SQLException
	{
		closeDatabase();
		openDatabase();
		if(conn!=null){
			String sql = "SELECT COUNT(*) FROM RDB$RELATIONS WHERE RDB$RELATION_NAME = 'LOG'";
				ResultSet rs=conn.createStatement().executeQuery(sql);
				rs.first();
			
				if(rs.getString(1).equals("0")){
					//TODO przywrocenie kopii zapasowej
				}
		}

		
	}
	
	public ArrayList<String> getFunctionalities(String tool) throws SQLException
	{
		ArrayList<String> values=new ArrayList<String>();
		checkIfBaseIsOpen();
		if (isEmpty())return values;
		String sql="SELECT DISTINCT functionality FROM Log WHERE tool=\'"+tool+"\' ORDER BY functionality";
		ResultSet rs = null;

		try
		{
		rs=conn.createStatement().executeQuery(sql);
		if(!rs.first())return values;
		
		while (!rs.isAfterLast())
		{
			values.add(rs.getString("functionality"));
			rs.next();
		}
		
		return values;
		}
		catch (SQLException e)
		{
			if (e.getMessage().contains("Tablela \"LOG\" nie istnieje"))
			{
				//TODO cos bardzo zlego powinno tu byc
				somethingVeryBad();
				throw e;
				
			}	
			else throw e;
		}		
	}
	

	
	public ArrayList<StandardFilter> getFunctionalities(String tool, String datefrom, String dateto) throws SQLException
	{
		ArrayList<StandardFilter> values=new ArrayList<StandardFilter>();
		checkIfBaseIsOpen();
		if (isEmpty())return values;
		
		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		java.sql.Timestamp sqlfrom=null,sqlto=null;
		
		
		try {
			if (datefrom!=null&&!datefrom.isEmpty())
				sqlfrom =new java.sql.Timestamp(sdf.parse(datefrom).getTime());
			if (dateto!=null&&!dateto.isEmpty())
			sqlto =new java.sql.Timestamp(sdf.parse(dateto).getTime()+1000*3600*24);
		} catch (ParseException e1) {
			return values;
		}
		
		
		String sql="SELECT DISTINCT functionality, timestamp, COUNT(*) AS cnt FROM Log WHERE ";
		if (sqlfrom!=null)  sql+=("timestamp>=\'"+sqlfrom+"\' AND ");
		if (sqlto!=null)	sql+=("timestamp<\'"+sqlto+"\' AND ");
		sql+=("tool=\'"+tool+"\' GROUP BY functionality, timestamp ORDER BY functionality, timestamp");
		ResultSet rs = null;

		try
		{
		rs=conn.createStatement().executeQuery(sql);
		if(!rs.first())return values;
		java.util.Date start=rs.getTimestamp("timestamp");		
		int i=0;
		values.add(new StandardFilter(rs.getString("functionality"),sdf.format(start),rs.getInt("cnt")));
		rs.next();

		while (!rs.isAfterLast())
		{
			if(rs.getString("functionality").equals(values.get(i).getFunctionality())&&sdf.format(rs.getTimestamp("timestamp")).equals(values.get(i).getDate()))
					{
						values.get(i).setCount(values.get(i).getCount()+1);
						
					}
			else
			{
				values.add(new StandardFilter(rs.getString("functionality"),sdf.format(rs.getTimestamp("timestamp")),rs.getInt("cnt")));
				i++;
			}
			rs.next();
		}
		
		return values;
		}
		catch (SQLException e)
		{
			if (e.getMessage().contains("Tablela \"LOG\" nie istnieje"))
			{
				//TODO cos bardzo zlego powinno tu byc
				somethingVeryBad();
				throw e;
				
			}	
			else throw e;
		}		
	}	
	
	
	

}
