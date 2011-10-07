package UsageStatisticClient;

import java.sql.Date;
import java.util.GregorianCalendar;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.statements.Fail;

public class DaoTemporaryDatabaseH2Test
{

	final DaoTemporaryDatabaseInterface dao = new DaoTemporaryDatabaseSerialization();

	@Before
	public void usunWszystkieLogi()
	{
		while (!dao.isEmpty())
		{
			dao.clearFirstLog();
		}
	}

	@Test
	public void AT31_Proper_save()
	{	
		final LogInformation log = new LogInformation();
		log.setDate(new GregorianCalendar().getTime());
		log.setFunctionality("funkcjonalnosc");
		log.setParameters("parametry");
		log.setTool("tool");
		log.setUser("user");
		for (int i=0;i<10;i++)
		{
		dao.saveLog(log);
		}
		Assert.assertEquals(10, dao.getLogsAmount());
	}

	@Test
	public void AT32_Save_many_logs_at_one_time()
	{
		tempSaveManyLogsAtOneTime(10, 15, 40);
		usunWszystkieLogi();
		tempSaveManyLogsAtOneTime(10, 15, 20);
		usunWszystkieLogi();
		tempSaveManyLogsAtOneTime(10, 1, 0);
	}
	
	private void tempSaveManyLogsAtOneTime(final int stalaLiczbaWatkow, final int stalaLiczbaRekordow, final int stalaLiczbaCzasuDoSpania)
	{	
		final LogInformation log = new LogInformation();
		log.setDate(new GregorianCalendar().getTime());
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		Assert.assertEquals(stalaLiczbaRekordow*stalaLiczbaWatkow, dao.getLogsAmount());
	}

}
