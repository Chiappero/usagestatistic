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
		dao.saveLog(new LogInformation(new GregorianCalendar().getTime(), "function1", "user2", "tool4", "params=fege"));
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
		ArrayList<String> a2=new ArrayList<String>();
		a2.add("tool4");
		ArrayList<String> a3=new ArrayList<String>();
		a3.add("user2");
		logs=dao.getLogsWithWhereClausure(null, null,a1,a3,a2);
		System.out.println(logs.size());
		
	}

}
