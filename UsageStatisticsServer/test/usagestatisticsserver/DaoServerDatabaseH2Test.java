package usagestatisticsserver;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.Random;

import junitx.util.PrivateAccessor;

import org.junit.Assert;
import org.junit.Test;

import usagestatisticsserver.DaoServerDatabaseH2;
import usagestatisticsserver.EncryptInstance;
import usagestatisticsserver.LogInformation;
import usagestatisticsserver.StandardFilter;

public class DaoServerDatabaseH2Test {

	DaoServerDatabaseH2 dao = new DaoServerDatabaseH2();
	
	@Test
	public void AT72_Empty_server_database() throws SQLException, InterruptedException, NoSuchFieldException
	{
		usunWszystkieLogi();
		Assert.assertTrue(dao.getFunctionalities().isEmpty());
		Assert.assertTrue(dao.getTools().isEmpty());
		Assert.assertTrue(dao.getUsers().isEmpty());
		Assert.assertTrue(dao.isEmpty());
		Assert.assertTrue(dao.getUsers("tool").isEmpty());
		Assert.assertTrue(dao.getFunctionalities("tool").isEmpty());
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
		closeDatabase();
		Assert.assertNotNull(dao.getAllLogs());
		
		usunWszystkieLogi();
		saveTemporaryData(10);
		PrivateAccessor.setField(dao, "conn", null);
		Assert.assertNotNull(dao.getAllLogs());
		Assert.assertEquals(dao.getLogsAmount(),10);
		
		usunWszystkieLogi();
		saveTemporaryData(10);
		PrivateAccessor.setField(dao, "conn", null);
		Assert.assertNotNull(dao.getAllLogs());
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
		log.setParameters("test"+i);
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
		for (int i=0;i<25;i++)
		{
			Assert.assertEquals(list1.get(i).getFunctionality(), "test");
			Assert.assertEquals(list1.get(i).getTool(), "test");
			Assert.assertEquals(list1.get(i).getUser(), "test");
			Assert.assertEquals(list1.get(i).getParameters(), "test"+i);
		}
		Assert.assertEquals(list1.get(25).getFunctionality(), "SELENIUM");
		Assert.assertEquals(list1.get(25).getTool(), "SELENIUM3");
		Assert.assertEquals(list1.get(25).getUser(), "SELENIUM2");
		Assert.assertEquals(list1.get(25).getParameters(), "SELENIUM4");	
		ArrayList<String> funcpertool=dao.getFunctionalities("notexist");
		Assert.assertTrue(funcpertool.isEmpty());
		dao.saveLog(new LogInformation(new GregorianCalendar().getTime(),"SELENIUM","user3","test","SELENIUM4"));
		funcpertool=dao.getFunctionalities("test");
		Assert.assertEquals(2,funcpertool.size());
		funcpertool=dao.getUsers("notexist");
		Assert.assertTrue(funcpertool.isEmpty());
		funcpertool=dao.getUsers("test");
		Assert.assertEquals(2,funcpertool.size());		
		
		
		
		
	}
	
	
	@Test
	public void AT81_Proper_show_filtered_data() throws SQLException, NoSuchFieldException
	{
		usunWszystkieLogi();
		saveTemporaryData(25);
		dao.saveLog(new LogInformation(new GregorianCalendar().getTime(),"SELENIUM","SEL2","SELENIUM3","SELENIUM4"));
		dao.saveLog(new LogInformation(new java.util.Date(new GregorianCalendar().getTimeInMillis()-60000),"SELA","SEL2","SELA","SEL4"));
		dao.saveLog(new LogInformation(new java.util.Date(new GregorianCalendar().getTimeInMillis()+60000),"SELB","SEL2","SELA","SEL4"));
		ArrayList<String> uu=dao.getUsers();
		ArrayList<String> ff=dao.getFunctionalities();
		ff.remove(2);
		String[] users=toArray(uu);
		String[] func=toArray(ff);
		ArrayList<StandardFilter> list=dao.getLogsFromDatabase(func,users,"SELA",null,null,false);
		Assert.assertEquals(1, list.size());
		uu.remove(1);
		users=toArray(uu);
		list=dao.getLogsFromDatabase(func,users,"SELA",null,null,false);
		Assert.assertEquals(0, list.size());
		dao.saveLog(new LogInformation(new java.util.Date(new GregorianCalendar().getTimeInMillis()),"SELC","SEL2","SELA","SEL4"));
		dao.saveLog(new LogInformation(new java.util.Date(new GregorianCalendar().getTimeInMillis()+1000*3600*24),"SELC","SEL2","SELA","SEL4"));
		dao.saveLog(new LogInformation(new java.util.Date(new GregorianCalendar().getTimeInMillis()+1000*3600*24),"SELC","SEL2","SELA","SEL4"));
		dao.saveLog(new LogInformation(new java.util.Date(new GregorianCalendar().getTimeInMillis()-1000*3600*24),"SELC","SEL2","SELA","SEL4"));
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		users=toArray(dao.getUsers());
		func=toArray(dao.getFunctionalities());		
		list=dao.getLogsFromDatabase(func,users,"SELA",sdf.format(Calendar.getInstance().getTime()),null,false);
		Assert.assertEquals(3, list.size());
		Assert.assertEquals(3, list.get(2).getCount());
		Assert.assertEquals("SELC", list.get(2).getFunctionality());
		Assert.assertEquals(null, list.get(2).getParameters());
		list=dao.getLogsFromDatabase(func,users,"SELA",sdf.format(Calendar.getInstance().getTime()),null,true);
		Assert.assertEquals("SEL4", list.get(2).getParameters());
		list=dao.getLogsFromDatabase(func,users,"SELA",null,sdf.format(Calendar.getInstance().getTime()),false);
		Assert.assertEquals(2, list.get(2).getCount());	
		list=dao.getLogsFromDatabase(func,users,"SELA",sdf.format(Calendar.getInstance().getTime()),sdf.format(Calendar.getInstance().getTime()),false);
		Assert.assertEquals(1, list.get(2).getCount());	
		list=dao.getLogsFromDatabase(func,users,"SELA","bad date",sdf.format(Calendar.getInstance().getTime()),false);
		Assert.assertEquals(0, list.size());
	}

	public String[] toArray(ArrayList<String> list)
	{
		String[] array=new String[list.size()];
		for (int i=0;i<list.size();i++)
			array[i]=new String(list.get(i));
		return array;
	}
	
	
	
	@Test
	public void AT82_Proper_show_agregated_data() throws SQLException, NoSuchFieldException
	{
		
		usunWszystkieLogi();
		saveTemporaryData(25);
		dao.saveLog(new LogInformation(new GregorianCalendar().getTime(),"SELENIUM","SEL2","SELENIUM3","SELENIUM4"));
		dao.saveLog(new LogInformation(new java.util.Date(new GregorianCalendar().getTimeInMillis()-60000),"SELA","SEL2","SELA","SEL4"));
		dao.saveLog(new LogInformation(new java.util.Date(new GregorianCalendar().getTimeInMillis()+60000),"SELB","SEL2","SELA","SEL4"));


		String[] users=toArray(dao.getUsers());
		String[] func=toArray(dao.getFunctionalities());
	
		ArrayList<StandardFilter> list=dao.getLogsFromDatabase(func,users,"tool",null,null,false);
		Assert.assertEquals(0, list.size());
		list=dao.getLogsFromDatabase(func,users,"SELENIUM3",null,null,false);
		Assert.assertEquals(1, list.size());
		list=dao.getLogsFromDatabase(func,users,"SELA",null,null,false);
		Assert.assertEquals(2, list.size());
		list=dao.getLogsFromDatabase(func,users,"test",null,null,false);
		Assert.assertEquals(1, list.size());	
		Assert.assertEquals(25,list.get(0).getCount());
		list=dao.getLogsFromDatabase(func,users,"test",null,null,true);
		Assert.assertEquals(25, list.size());
		for (int i=0;i<25;i++)
			Assert.assertEquals(1,list.get(0).getCount());
		users=new String[0];
		list=dao.getLogsFromDatabase(func,users,"test",null,null,false);
		Assert.assertEquals(0, list.size());
		func=null;
		list=dao.getLogsFromDatabase(func,users,"test",null,null,false);
		Assert.assertEquals(0, list.size());		
	}
	
	@Test
	public void AT185_Proper_adduser() throws SQLException, NoSuchFieldException
	{	
		
		dao.addUserClient("user", EncryptInstance.sha256("user"));
		Assert.assertTrue(dao.isValidCredential("user", EncryptInstance.sha256("user")));
		dao.addUserClient("user", EncryptInstance.sha256("password"));
		Assert.assertFalse(dao.isValidCredential("user", EncryptInstance.sha256("user")));
		Assert.assertTrue(dao.isValidCredential("user", EncryptInstance.sha256("password")));
		dao.addUserClient("user", EncryptInstance.sha256("user"));
		Random rand=new Random();
		int x=rand.nextInt()%1000000;
		dao.addUserClient("user"+x, EncryptInstance.sha256("user"));
		Assert.assertTrue(dao.isValidCredential("user", EncryptInstance.sha256("user")));
		Assert.assertTrue(dao.isValidCredential("user"+x, EncryptInstance.sha256("user")));
		
		Assert.assertNull(EncryptInstance.sha256(null));
	}
		
	@Test
	public void AT82_Proper_cache_of_credentials_on_server() throws SQLException, NoSuchFieldException
	{	
		String u,p;
		dao.isValidCredential("user", EncryptInstance.sha256("user"));
		u=(String) PrivateAccessor.getField(dao, "user");
		p=(String) PrivateAccessor.getField(dao,"pass");	
		Assert.assertEquals(u,"user");
		Assert.assertEquals(p,EncryptInstance.sha256("user"));
		dao.isValidCredential("user", EncryptInstance.sha256("fakepass"));
		u=(String) PrivateAccessor.getField(dao, "user");
		p=(String) PrivateAccessor.getField(dao,"pass");
		Assert.assertEquals(u,"user");
		Assert.assertEquals(p,EncryptInstance.sha256("user"));
		dao.addUserClient("uuser", EncryptInstance.sha256("upass"));
		dao.isValidCredential("uuser", EncryptInstance.sha256("upass"));
		dao.isValidCredential("uuser", EncryptInstance.sha256("upass"));
		u=(String) PrivateAccessor.getField(dao, "user");
		p=(String) PrivateAccessor.getField(dao,"pass");
		Assert.assertEquals(u,"uuser");
		Assert.assertEquals(p,EncryptInstance.sha256("upass"));
		
	}


	public void closeDatabase() throws NoSuchFieldException
	{
		Connection conn=(Connection)PrivateAccessor.getField(dao, "conn");

			try {
				if (conn!=null)
				{
					conn.close();
				}
			} catch (SQLException e) {
				try {
					conn.close();
				} catch (SQLException e1) {
				}
			}

	}
	
}
