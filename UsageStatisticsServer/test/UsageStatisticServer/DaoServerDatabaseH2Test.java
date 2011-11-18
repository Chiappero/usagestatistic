package UsageStatisticServer;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import junitx.util.PrivateAccessor;

import org.junit.Assert;
import org.junit.Test;

public class DaoServerDatabaseH2Test {

	DaoServerDatabaseH2 dao = new DaoServerDatabaseH2();
	
	@Test
	public void AT72_Empty_server_database() throws SQLException, InterruptedException, NoSuchFieldException
	{
		usunWszystkieLogi();
		Assert.assertTrue(dao.getFunctionalities().isEmpty());
		Assert.assertTrue(dao.getTools().isEmpty());
		Assert.assertTrue(dao.getUsers().isEmpty());
		Assert.assertTrue(dao.getAllLogs().isEmpty());
		//TODO na poziomie widoku ma zwracac "ze nie ma logow zadnych"
	}
	
	public static void dropTable(DaoServerDatabaseH2 dao) throws NoSuchFieldException, SQLException
	{
		String sql="DROP TABLE Log";
		Connection conn=(Connection) PrivateAccessor.getField(dao, "conn");
		conn.createStatement().execute(sql);
	}
	
	@Test
	public void AT73_Invalid_readout_from_server_database() throws SQLException, InterruptedException, NoSuchFieldException
	{
		dao.closeDatabase();
		Assert.assertNotNull(dao.getAllLogs());
		
		usunWszystkieLogi();
		saveTemporaryData(10);
		PrivateAccessor.setField(dao, "conn", null);
		Assert.assertNotNull(dao.getAllLogs());
		Assert.assertEquals(dao.getLogsAmount(),10);
		
		
		dao.closeDatabase();
		Assert.assertNotNull(dao.getLogsWithWhereClausure(new LogFilter(null, null, null, null, null), new LinkedList<String>(), 1, 1));
		
		usunWszystkieLogi();
		saveTemporaryData(10);
		PrivateAccessor.setField(dao, "conn", null);
		Assert.assertNotNull(dao.getLogsWithWhereClausure(new LogFilter(null, null, null, null, null), new LinkedList<String>(), 1, 1));
		Assert.assertEquals(dao.getLogsAmount(),10);
		
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
	public void AT71_Proper_show_data() throws SQLException, NoSuchFieldException
	{	
		usunWszystkieLogi();
		saveTemporaryData(25);
		dao.saveLog(new LogInformation(new GregorianCalendar().getTime(),"SELENIUM","SELENIUM2","SELENIUM3","SELENIUM4"));
		ArrayList<LogInformation> list1=dao.getAllLogs();
		Assert.assertEquals(list1.size(), 26);
		Assert.assertEquals(list1.get(0).getFunctionality(), "test");
		Assert.assertEquals(list1.get(0).getTool(), "test");
		Assert.assertEquals(list1.get(0).getUser(), "test");
		Assert.assertEquals(list1.get(0).getParameters(), "test");
		list1=dao.getAllLogs(-1,100);
		Assert.assertEquals(list1.size(), 26);
		list1=dao.getAllLogs(-1,-1);
		Assert.assertEquals(list1.size(), 0);
		list1=dao.getAllLogs(3,-1);
		Assert.assertEquals(list1.size(), 0);
		list1=dao.getAllLogs(26,1);
		Assert.assertEquals(list1.size(), 1);	
		Assert.assertEquals(list1.get(0).getFunctionality(), "SELENIUM");	

		LinkedList<String> orderby=new LinkedList<String>();
		orderby.add("functionality");
		list1=dao.getAllLogsSorted(orderby);
		Assert.assertEquals(list1.get(0).getFunctionality(), "SELENIUM");
		for (int i=1;i<list1.size();i++)
		Assert.assertEquals(list1.get(i).getFunctionality(), "test");
		orderby.add("user");
		dao.saveLog(new LogInformation(new GregorianCalendar().getTime(),"SELENIUM","SELENIUM1","SELENIUM3","SELENIUM4"));
		list1=dao.getAllLogsSorted(orderby);
		Assert.assertEquals(list1.get(0).getFunctionality(), "SELENIUM");
		Assert.assertEquals(list1.get(0).getUser(), "SELENIUM1");
		Assert.assertEquals(list1.get(1).getFunctionality(), "SELENIUM");
		Assert.assertEquals(list1.get(1).getUser(), "SELENIUM2");
		
		
		
		
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
	list2=dao.getLogsWithWhereClausure(new LogFilter(new java.util.Date(new GregorianCalendar().getTimeInMillis()+10), null, null, null, null));
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

	
	@Test
	public void AT82_Proper_show_agregated_data() throws SQLException, NoSuchFieldException
	{
		usunWszystkieLogi();
		saveTemporaryData(25);
		dao.saveLog(new LogInformation(new GregorianCalendar().getTime(),"SELENIUM","SEL2","SELENIUM3","SELENIUM4"));
		dao.saveLog(new LogInformation(new java.util.Date(new GregorianCalendar().getTimeInMillis()-60000),"SELA","SEL2","SELA","SEL4"));
		dao.saveLog(new LogInformation(new java.util.Date(new GregorianCalendar().getTimeInMillis()+60000),"SELB","SEL2","SEL3","SEL4"));
		ArrayList<String> groupby=new ArrayList<String>();
		groupby.add("functionality");
		ArrayList<Pair<LogInformation, Integer>> list2=dao.agregate(groupby);
		Assert.assertEquals(4, list2.size());
		groupby=new ArrayList<String>();
		groupby.add("parameters");
		list2=dao.agregate(groupby);
		Assert.assertEquals(3, list2.size());
		groupby.add("functionality");
		list2=dao.agregate(groupby);
		Assert.assertEquals(4, list2.size());
		list2=dao.agregate(groupby,3,2);
		Assert.assertEquals(2, list2.size());
		Assert.assertEquals("SELENIUM", list2.get(0).getLewy().getFunctionality());
		Assert.assertEquals("test", list2.get(1).getLewy().getFunctionality());
		Assert.assertEquals(null, list2.get(0).getLewy().getUser());
		groupby=new ArrayList<String>();
		groupby.add("user");
		list2=dao.agregateOverTime(groupby, 30000);
		Assert.assertEquals(4, list2.size());	
		for (int i=1;i<list2.size();i++)
		Assert.assertTrue(list2.get(i).getLewy().getDateTime().getTime()-list2.get(i-1).getLewy().getDateTime().getTime()==30000);
		dao.saveLog(new LogInformation(new GregorianCalendar().getTime(),"SELENIUM","SELENIUM2","SELA","SELENIUM4"));
		dao.saveLog(new LogInformation(new java.util.Date(new GregorianCalendar().getTimeInMillis()),"SELA","SEL2","SELA","SEL4"));
		dao.saveLog(new LogInformation(new java.util.Date(new GregorianCalendar().getTimeInMillis()),"function","SEL2","SELA","SEL4"));	
		ArrayList<String> fun=dao.getFunctionalities("SELA");
		Assert.assertEquals(3,fun.size());
		Assert.assertEquals("function", fun.get(2));		
		fun=dao.getFunctionalities("SELZ");
		Assert.assertEquals(0,fun.size());
		
		
		usunWszystkieLogi();
		saveTemporaryData(25);
		dao.saveLog(new LogInformation(new GregorianCalendar().getTime(),"SELENIUM","SEL2","SELENIUM3","SELENIUM4"));
		dao.saveLog(new LogInformation(new java.util.Date(new GregorianCalendar().getTimeInMillis()-60000),"SELA","SEL2","SELA","SEL4"));
		dao.saveLog(new LogInformation(new java.util.Date(new GregorianCalendar().getTimeInMillis()+60000),"SELB","SEL2","SEL3","SEL4"));
		dao.saveLog(new LogInformation(new GregorianCalendar().getTime(),"SELENIUM","SELENIUM2","SELA","SELENIUM4"));
		dao.saveLog(new LogInformation(new java.util.Date(new GregorianCalendar().getTimeInMillis()),"SELA","SEL2","SELA","SEL4"));
		dao.saveLog(new LogInformation(new java.util.Date(new GregorianCalendar().getTimeInMillis()),"function","SEL2","SELA","SEL4"));		
		ArrayList<String> func=dao.getFunctionalities("SELA");
		Assert.assertEquals(3, func.size());
		ArrayList<StandardFilter> res=dao.getFunctionalities("SELA", null, null);
		Assert.assertEquals(func.size(), res.size());
		for (int i=0;i<func.size();i++)
			Assert.assertEquals(func.get(i),res.get(i).getFunctionality());
		Assert.assertEquals(2,res.get(0).getCount());
		Assert.assertEquals(1,res.get(1).getCount());
		Assert.assertEquals(1,res.get(2).getCount());
		dao.saveLog(new LogInformation(new java.util.Date(new GregorianCalendar().getTimeInMillis()+1000*3600*24),"SEL1","SEL2","SELA","SEL4"));
		dao.saveLog(new LogInformation(new java.util.Date(new GregorianCalendar().getTimeInMillis()+2*1000*3600*24),"SEL2","SEL2","SELA","SEL4"));
		dao.saveLog(new LogInformation(new java.util.Date(new GregorianCalendar().getTimeInMillis()+3*1000*3600*24),"SEL3","SEL2","SELA","SEL4"));
		dao.saveLog(new LogInformation(new java.util.Date(new GregorianCalendar().getTimeInMillis()-1000*3600*24),"SEL-1","SEL2","SELA","SEL4"));
		dao.saveLog(new LogInformation(new java.util.Date(new GregorianCalendar().getTimeInMillis()-2*1000*3600*24),"SEL-2","SEL2","SELA","SEL4"));
		dao.saveLog(new LogInformation(new java.util.Date(new GregorianCalendar().getTimeInMillis()-3*1000*3600*24),"SEL-3","SEL2","SELA","SEL4"));
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String today=sdf.format(Calendar.getInstance().getTime());
		String next=sdf.format(new java.util.Date(Calendar.getInstance().getTimeInMillis()+2*1000*3600*24));
		res=dao.getFunctionalities("SELA", today, next);
		Assert.assertEquals(5, res.size());		
		res=dao.getFunctionalities("SELA", null, next);
		Assert.assertEquals(8, res.size());
		res=dao.getFunctionalities("SELA", today, null);
		Assert.assertEquals(6, res.size());	
	
	}
	
	@Test
	public void A11_3_Proper_cache_of_credentials_on_server() throws SQLException, NoSuchFieldException
	{	
		String u,p;
		dao.addUserClient("user", EncryptInstance.SHA256("user"));
		dao.isValidCredential("user", EncryptInstance.SHA256("user"));
		u=(String) PrivateAccessor.getField(dao, "user");
		p=(String) PrivateAccessor.getField(dao,"pass");	
		Assert.assertEquals(u,"user");
		Assert.assertEquals(p,EncryptInstance.SHA256("user"));
		dao.isValidCredential("user", EncryptInstance.SHA256("fakepass"));
		u=(String) PrivateAccessor.getField(dao, "user");
		p=(String) PrivateAccessor.getField(dao,"pass");
		Assert.assertEquals(u,"user");
		Assert.assertEquals(p,EncryptInstance.SHA256("user"));
		dao.addUserClient("uuser", EncryptInstance.SHA256("upass"));
		dao.isValidCredential("uuser", EncryptInstance.SHA256("upass"));
		u=(String) PrivateAccessor.getField(dao, "user");
		p=(String) PrivateAccessor.getField(dao,"pass");
		Assert.assertEquals(u,"uuser");
		Assert.assertEquals(p,EncryptInstance.SHA256("upass"));
		
	}

}
