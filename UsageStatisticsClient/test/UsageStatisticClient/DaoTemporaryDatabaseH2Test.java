package UsageStatisticClient;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.GregorianCalendar;

import junit.framework.Assert;
import junitx.util.PrivateAccessor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.suppliers.TestedOn;
import org.junit.internal.runners.statements.Fail;

public class DaoTemporaryDatabaseH2Test
{

	final DaoTemporaryDatabaseInterface dao = new DaoTemporaryDatabaseH2();
	

	@Before
	public void usunWszystkieLogi() throws SQLException
	{	
		dao.openDatabase();
		while (!dao.isEmpty())
		{
			dao.clearFirstLog();
		}
	}
	
	@After
	public void poFakcie()
	{
		dao.closeDatabase();
	}

	@Test
	public void AT31_Proper_save() throws SQLException
	{	
		final LogInformation log = new LogInformation();
		java.util.Date date = new GregorianCalendar().getTime();
		for (int i=0;i<10;i++)
		{
		String stringLiczba = new Integer(i).toString();
		log.setDateTime(date);
		log.setFunctionality("funkcjonalnosc"+stringLiczba);
		log.setParameters("parametry"+stringLiczba);
		log.setTool("tool"+stringLiczba);
		log.setUser("user"+stringLiczba);
		dao.saveLog(log);
		}
		Assert.assertEquals(10, dao.getLogsAmount());
		for (int i=0;i<10;i++)
		{
		String stringLiczba = new Integer(i).toString();
		LogInformation firstLog = dao.getFirstLog();
		dao.clearFirstLog();
		log.setDateTime(date);
		log.setFunctionality("funkcjonalnosc"+stringLiczba);
		log.setParameters("parametry"+stringLiczba);
		log.setTool("tool"+stringLiczba);
		log.setUser("user"+stringLiczba);
		Assert.assertEquals(firstLog.getFunctionality(), log.getFunctionality());
		Assert.assertEquals(firstLog.getParameters(),log.getParameters());
		Assert.assertEquals(firstLog.getTool(),log.getTool());
		Assert.assertEquals(firstLog.getUser(),log.getUser());
		}
		
	}

	@Test
	public void AT32_Save_many_logs_at_one_time() throws SQLException
	{
		tempSaveManyLogsAtOneTime(10, 15, 40);
		usunWszystkieLogi();
		tempSaveManyLogsAtOneTime(10, 15, 20);
		usunWszystkieLogi();
		tempSaveManyLogsAtOneTime(10, 5, 3);
	}
	
	private void tempSaveManyLogsAtOneTime(final int stalaLiczbaWatkow, final int stalaLiczbaRekordow, final int stalaLiczbaCzasuDoSpania) throws SQLException
	{	
		final LogInformation log = new LogInformation();
		log.setDateTime(new GregorianCalendar().getTime());
		log.setFunctionality("funkcjonalnosc");
		log.setParameters("parametry");
		log.setTool("tool");
		log.setUser("user");
		Thread[] threads = new Thread[stalaLiczbaWatkow];
		for (int j = 0; j < stalaLiczbaWatkow; j++)
		{
			threads[j] = new Thread()
			{
				@Override
				public void run()
				{
					for (int k = 0; k < stalaLiczbaRekordow; k++)
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
		for (int j = 0; j < stalaLiczbaWatkow; j++)
		{
			threads[j].start();
			try
			{
				Thread.sleep(stalaLiczbaCzasuDoSpania);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			
		}
		
		Assert.assertEquals(stalaLiczbaRekordow*stalaLiczbaWatkow, dao.getLogsAmount());
	}
	
	
	
	
	@Test
	public void AT35_Try_to_save_invalid_data() throws SQLException
	{
		LogInformation log = new LogInformation(null, "test", "test", "test", "test");
		Assert.assertFalse(dao.saveLog(log));
		//TODO a co gdy zly format daty
		log.setDateTime(new GregorianCalendar().getTime());
		log.setFunctionality(null); 
		Assert.assertFalse(dao.saveLog(log));
		log.setFunctionality("");
		Assert.assertFalse(dao.saveLog(log));
		log.setFunctionality("test");
		log.setParameters("");
		Assert.assertTrue(dao.saveLog(log));
		LogInformation firstLog = dao.getFirstLog();
		Assert.assertEquals(firstLog.getParameters(), "");
		log.setParameters(null);
		Assert.assertTrue(dao.saveLog(log));
		firstLog = dao.getFirstLog();
		Assert.assertEquals(firstLog.getParameters(), "");
		log.setTool(null);
		Assert.assertFalse(dao.saveLog(log));
		log.setTool("");
		Assert.assertFalse(dao.saveLog(log));
		log.setTool("test");
		log.setUser(null);
		Assert.assertFalse(dao.saveLog(log));
		log.setUser("");
		Assert.assertFalse(dao.saveLog(log));
		log=null;
		Assert.assertFalse(dao.saveLog(log));
	}
	
	@Test
	public void AT34_Cannot_save_to_local_database_or_database_corrupted() throws NoSuchFieldException, SQLException
	{
	while (!dao.isEmpty())
	{
		dao.clearFirstLog();
	}
	dao.saveLog(TestUtils.getExampleLog());
	dao.closeDatabase();
	Assert.assertTrue(dao.saveLog(TestUtils.getExampleLog()));
	Assert.assertEquals(dao.getLogsAmount(),2);
	Assert.assertFalse(dao.isEmpty());
	
	TestUtils.dropTable((DaoTemporaryDatabaseH2) dao);
	Assert.assertTrue(dao.saveLog(TestUtils.getExampleLog()));
	Assert.assertEquals(dao.getLogsAmount(),1);
	Assert.assertFalse(dao.isEmpty());
	
	while (!dao.isEmpty())
	{
		dao.clearFirstLog();
	}
	TestUtils.makeConnectionNull((DaoTemporaryDatabaseH2) dao);
	Assert.assertTrue(dao.saveLog(TestUtils.getExampleLog()));
	Assert.assertEquals(dao.getLogsAmount(),1);
	Assert.assertFalse(dao.isEmpty());
	}
	
	
	@Test
	public void AT27_Concurrent_read_and_write_to_local_database() throws SQLException, InterruptedException
	{
		tempSaveAndLoadManyLogsAtOneTime(1, 10, 10, 1, 400, 400);
		usunWszystkieLogi();
		tempSaveAndLoadManyLogsAtOneTime(1, 10, 10, 1, 400, 700);
		usunWszystkieLogi();
		tempSaveAndLoadManyLogsAtOneTime(10, 10, 10, 1, 500, 700);
		usunWszystkieLogi();
		tempSaveAndLoadManyLogsAtOneTime(10, 10, 10, 1, 700, 700);
		usunWszystkieLogi();
		tempSaveAndLoadManyLogsAtOneTime(0, 0, 40, 2, 200, 800);
		usunWszystkieLogi();
		tempSaveAndLoadManyLogsAtOneTime(10, 10, 30, 2, 200, 800);
		usunWszystkieLogi();
		tempSaveAndLoadManyLogsAtOneTime(10, 10, 10, 4, 200, 900);
	}
	
	
	private void tempSaveAndLoadManyLogsAtOneTime(final int stalaLiczbaWatkowSave, final int stalaLiczbaRekordowSave, final int stalaLiczbaCzasuDoSpania, final int stalaLiczbaWatkowLoad, final int stalaLiczbaRekordowLoad, final int rekordyZapisaneOdRazu) throws SQLException, InterruptedException
	{	
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
					for (int k = 0; k < stalaLiczbaRekordowLoad; k++)
					{
						try
						{
							dao.getFirstLog();
						} catch (SQLException e)
						{
							Assert.fail("nie umiem odczytac jednego loga");
						}
						try
						{
							dao.clearFirstLog();
						} catch (SQLException e)
						{
							Assert.fail("nie umiem usunac jednego loga");
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
		
		Assert.assertEquals(stalaLiczbaWatkowSave*stalaLiczbaRekordowSave+rekordyZapisaneOdRazu-stalaLiczbaWatkowLoad*stalaLiczbaRekordowLoad, dao.getLogsAmount());
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
	

	
	

}
