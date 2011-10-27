package UsageStatisticServer;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Test;

public class DaoServerDatabaseH2Test {

	@Test
	public void test() throws SQLException 
	{
		
		DaoServerDatabaseH2 dao=new DaoServerDatabaseH2();
		dao.saveLog(new LogInformation(new GregorianCalendar().getTime(), "function1", "user3", "tool5", "params=fege"));
		Assert.assertFalse(dao.isEmpty());
//		
//		System.out.println("\nusers:");
//		ArrayList<String> users=dao.getUsers();
//		for (String u: users)
//		{
//			System.out.println(u);
//		}
//
//		System.out.println("\ntools:");
//		ArrayList<String> tools=dao.getTools();
//		for (String t: tools)
//		{
//			System.out.println(t);
//		}		
//		
//		System.out.println("\nfunctions:");
//		ArrayList<String> funct=dao.getFunctionalities();
//		for (String f: funct)
//		{
//			System.out.println(f);
//		}
		
		ArrayList<LogInformation> logs=dao.getAllLogs();
		for (LogInformation l: logs)
		{
			System.out.println(l);
		}	
//		logs=dao.getAllLogs(5,3);
//		System.out.println("\n");
//		for (LogInformation l: logs)
//		{
//			System.out.println(l);
//		}			
		ArrayList<String> arr=new ArrayList<String>();
		arr.add("function2");
		logs=dao.getLogsWithWhereClausure(null, null, arr, null, null);
		System.out.println(logs.size());
		logs=dao.getLogsWithWhereClausure(null, null, null, null, null);
		System.out.println(logs.size());
		logs=dao.getLogsWithWhereClausure(null, Calendar.getInstance().getTime(), null, null, null);
		System.out.println(logs.size());		
		logs=dao.getLogsWithWhereClausure(Calendar.getInstance().getTime(), Calendar.getInstance().getTime(), null, null, null);
		System.out.println(logs.size());			
		ArrayList<String> a1=new ArrayList<String>();
		a1.add("function1");
		a1.add("function2");
		ArrayList<String> a2=new ArrayList<String>();
		a2.add("tool2");
		a2.add("tool5");
		ArrayList<String> a3=new ArrayList<String>();
		a3.add("user2");
		a3.add("user3");
		logs=dao.getAllLogs(1,10);
		System.out.println(logs.size());
		logs=dao.getLogsWithWhereClausure(null, Calendar.getInstance().getTime(), a1, a3, a2,2,10);
		for (LogInformation l: logs)
		{
			System.out.println(l);
		}		
		
	}
	
	@Test
	public void agregateTest() throws SQLException
	{
		DaoServerDatabaseH2 dao=new DaoServerDatabaseH2();
		Assert.assertFalse(dao.isEmpty());
		ArrayList<String> groupby=new ArrayList<String>();
		groupby.add("user");
	//	groupby.add("functionality");
		ArrayList<Pair<LogInformation,Integer>> list=dao.agregate(groupby);
		Assert.assertFalse(list.isEmpty());
		for (Pair<LogInformation,Integer> p: list)
		{
			System.out.println(p.getLewy().toString()+" "+p.getPrawy());
		}
		System.out.println();
		groupby.add("functionality");
		list=dao.agregate(groupby);
		Assert.assertFalse(list.isEmpty());
		for (Pair<LogInformation,Integer> p: list)
		{
			System.out.println(p.getLewy().toString()+" "+p.getPrawy());
		}		
	}

}
