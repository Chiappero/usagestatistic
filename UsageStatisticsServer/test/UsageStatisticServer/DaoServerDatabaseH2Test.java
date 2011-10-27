package UsageStatisticServer;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import junitx.util.PrivateAccessor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DaoServerDatabaseH2Test {

	DaoServerDatabaseH2 dao = new DaoServerDatabaseH2();
	
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
		logs=dao.getLogsWithWhereClausure(new LogFilter(null, null, arr, null, null));
		System.out.println(logs.size());
		logs=dao.getLogsWithWhereClausure(new LogFilter(null, null, null, null, null));
		System.out.println(logs.size());
		logs=dao.getLogsWithWhereClausure(new LogFilter(null, Calendar.getInstance().getTime(), null, null, null));
		System.out.println(logs.size());		
		logs=dao.getLogsWithWhereClausure(new LogFilter(Calendar.getInstance().getTime(), Calendar.getInstance().getTime(), null, null, null));
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
		logs=dao.getLogsWithWhereClausure(new LogFilter(null, Calendar.getInstance().getTime(), a1, a3, a2),2,10);
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
	
	@Test
	public void AT75_Concurrent_read_and_save_to_server_database() throws SQLException, InterruptedException, NoSuchFieldException
	{
		usunWszystkieLogi();
		tempSaveAndLoadManyLogsAtOneTime(3, 1000, 10, 1, 10, 6000);
		
	}
	
	
	public void usunWszystkieLogi() throws SQLException, NoSuchFieldException
	{	
		String sql="DELETE FROM Log";
		Connection conn = (Connection) PrivateAccessor.getField(dao, "conn");
		conn.createStatement().executeUpdate(sql);
	}
	
	private void tempSaveAndLoadManyLogsAtOneTime(final int stalaLiczbaWatkowSave, final int stalaLiczbaRekordowSave, final int stalaLiczbaCzasuDoSpania, final int stalaLiczbaWatkowLoad, final int jednoczesneOdczytyWszystkichLogow, final int rekordyZapisaneOdRazu) throws SQLException, InterruptedException
	{	//nie usuwamy w tej wersji juz rekordow
		saveTemporaryData(rekordyZapisaneOdRazu); 
		
		
		final LogInformation log = new LogInformation();
		log.setDateTime(new GregorianCalendar().getTime());
		log.setFunctionality("funkcjonalnosc");
		log.setParameters("parametry");
		log.setTool("tool");
		log.setUser("user");
		Thread[] threadsSave = new Thread[stalaLiczbaWatkowSave];
		for (int j = 0; j < stalaLiczbaWatkowSave; j++)
		{
			threadsSave[j] = new Thread()
			{
				@Override
				public void run()
				{
					for (int k = 0; k < stalaLiczbaRekordowSave; k++)
					{
						boolean saveLog = dao.saveLog(log);
						if (!saveLog)
						{
							Assert.fail("nie umiem zapisac jednego loga");
						}
					}

				}
			};
		} 
		Thread[] threadsLoad = new Thread[stalaLiczbaWatkowLoad];
		for (int j = 0; j < stalaLiczbaWatkowLoad; j++)
		{
			threadsLoad[j] = new Thread()
			{
				@Override
				public void run()
				{
					for (int k = 0; k < jednoczesneOdczytyWszystkichLogow; k++)
					{
						try
						{
							dao.getAllLogs();
						} catch (SQLException e)
						{
							Assert.fail("nie umiem odczytac jednego loga");
						}
					}

				}
			};
		}
		for (int j = 0; j < stalaLiczbaWatkowLoad; j++)
		{
			threadsLoad[j].start();
			try
			{
				Thread.sleep(stalaLiczbaCzasuDoSpania);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			
		}
		
		
		
		for (int j = 0; j < stalaLiczbaWatkowSave; j++)
		{
			threadsSave[j].start();
			try
			{
				Thread.sleep(stalaLiczbaCzasuDoSpania);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			
		}
		
		for (int j = 0; j < stalaLiczbaWatkowLoad; j++)
		{
			threadsLoad[j].join(); //czeka na dokonczenie az wszystkie watki czytajace sie skoncza
		}
		for (int j = 0; j < stalaLiczbaWatkowSave; j++)
		{
			threadsSave[j].join(); //czeka na dokonczenie az wszystkie watki zapisujace sie skoncza
		}
		
		Assert.assertEquals(stalaLiczbaWatkowSave*stalaLiczbaRekordowSave+rekordyZapisaneOdRazu, dao.getLogsAmount());
	}
	
	private void saveTemporaryData(int amount)
	{
		LogInformation log = new LogInformation();
		for (int i=0;i<amount;i++)
		{
		log.setDateTime(new GregorianCalendar().getTime());
		log.setFunctionality("test");
		log.setParameters("test");
		log.setTool("test");
		log.setUser("test");
		dao.saveLog(log);
		}
		
	}
	
	@Test
	public void AT81_Proper_show_filtered_data() throws SQLException, NoSuchFieldException
	{
	usunWszystkieLogi();
	saveTemporaryData(25);
	dao.saveLog(new LogInformation(new GregorianCalendar().getTime(),"SELENIUM","SELENIUM2","SELENIUM3","SELENIUM4"));
	dao.saveLog(new LogInformation(new java.util.Date(new GregorianCalendar().getTimeInMillis()-60000),"SELA","SEL2","SELA","SEL4"));
	dao.saveLog(new LogInformation(new java.util.Date(new GregorianCalendar().getTimeInMillis()+60000),"SELB","SEL2","SEL3","SEL4"));
	
	//filter test
	ArrayList<LogInformation> list1=dao.getAllLogs();
	Assert.assertEquals(28, list1.size());
	ArrayList<LogInformation> list2=dao.getLogsWithWhereClausure(new LogFilter(null, null, null, null, null));
	Assert.assertEquals(list1.size(), list2.size());
	list2=dao.getLogsWithWhereClausure(new LogFilter( new GregorianCalendar().getTime(), null, null, null, null));
	Assert.assertEquals(1, list2.size());
	Assert.assertEquals("SELB", list2.get(0).getFunctionality());
	list2=dao.getLogsWithWhereClausure(new LogFilter(null, new GregorianCalendar().getTime(), null, null, null));
	Assert.assertEquals(list1.size(), list2.size()+1);
	list2=dao.getLogsWithWhereClausure(new LogFilter(new java.util.Date(new GregorianCalendar().getTimeInMillis()-30000), new java.util.Date(new GregorianCalendar().getTimeInMillis()+30000), null, null, null));
	Assert.assertEquals(list1.size(), list2.size()+2);
	list2=dao.getLogsWithWhereClausure(new LogFilter(null,new java.util.Date(new GregorianCalendar().getTimeInMillis()-30000), null, null, null));
	Assert.assertEquals(1, list2.size());
	Assert.assertEquals("SELA", list2.get(0).getFunctionality());	
	
	ArrayList<String> testlist=new ArrayList<String>();
	testlist.add("test");
	list2=dao.getLogsWithWhereClausure(new LogFilter(null,null,testlist , null, null));
	Assert.assertEquals(25, list2.size());
	list2=dao.getLogsWithWhereClausure(new LogFilter(null,null,null , testlist, null));
	Assert.assertEquals(25, list2.size());
	list2=dao.getLogsWithWhereClausure(new LogFilter(null,null,null , null, testlist));
	Assert.assertEquals(25, list2.size());
	list2=dao.getLogsWithWhereClausure(new LogFilter(null,null,testlist , testlist, testlist));
	Assert.assertEquals(25, list2.size());
	testlist.add("SELA");
	list2=dao.getLogsWithWhereClausure(new LogFilter(null,null,testlist , null, null));
	Assert.assertEquals(26, list2.size());
	list2=dao.getLogsWithWhereClausure(new LogFilter(null,null,null , testlist, null));
	Assert.assertEquals(25, list2.size());
	list2=dao.getLogsWithWhereClausure(new LogFilter(null,null,null , null, testlist));
	Assert.assertEquals(26, list2.size());
	list2=dao.getLogsWithWhereClausure(new LogFilter(null,null,testlist , testlist, testlist));
	Assert.assertEquals(25, list2.size());	
	list2=dao.getLogsWithWhereClausure(new LogFilter(null,null,testlist , null, testlist));
	Assert.assertEquals(26, list2.size());	
	list2=dao.getLogsWithWhereClausure(new LogFilter(new GregorianCalendar().getTime(),null,testlist , null, null));
	Assert.assertEquals(0, list2.size());		
	
	list2=dao.getLogsWithWhereClausure(new LogFilter(null,null,null,null,null),1,28);
	Assert.assertEquals(28, list2.size());
	list2=dao.getLogsWithWhereClausure(new LogFilter(null,null,null,null,null),1,29);
	Assert.assertEquals(28, list2.size());	
	list2=dao.getLogsWithWhereClausure(new LogFilter(null,null,null,null,null),1,27);
	Assert.assertEquals(27, list2.size());
	list2=dao.getLogsWithWhereClausure(new LogFilter(null,null,null,null,null),-1,28);
	Assert.assertEquals(28, list2.size());
	list2=dao.getLogsWithWhereClausure(new LogFilter(null,null,null,null,null),1,20);
	Assert.assertEquals(20, list2.size());
	list2=dao.getLogsWithWhereClausure(new LogFilter(null,null,null,null,null),21,10);
	Assert.assertEquals(8, list2.size());
	list2=dao.getLogsWithWhereClausure(new LogFilter(null,null,null,null,null),26,10);
	Assert.assertEquals(3, list2.size());
	Assert.assertEquals(list2.get(0).getFunctionality(), "SELENIUM");
	Assert.assertEquals(list2.get(1).getFunctionality(), "SELA");
	Assert.assertEquals(list2.get(2).getFunctionality(), "SELB");
	list2=dao.getLogsWithWhereClausure(new LogFilter(null,null,null,null,null),26,1);
	Assert.assertEquals(1, list2.size());	
	Assert.assertEquals(list2.get(0).getFunctionality(), "SELENIUM");
	list2=dao.getLogsWithWhereClausure(new LogFilter(null,null,null,null,null),1,0);
	Assert.assertEquals(0, list2.size());	
	list2=dao.getLogsWithWhereClausure(new LogFilter(null,null,null,null,null),1,-1);
	Assert.assertEquals(0, list2.size());	
	LinkedList<String> order=new LinkedList<String>();
	order.add("functionality");
	list2=dao.getLogsWithWhereClausure(new LogFilter(null,null,testlist,null,null),order,1,28);
	Assert.assertEquals( "SELA",list2.get(0).getFunctionality());
	Assert.assertEquals("SEL2",list2.get(0).getUser());
	Assert.assertEquals("test",list2.get(4).getFunctionality());	
	
	
	
	
	}
	
	

}
