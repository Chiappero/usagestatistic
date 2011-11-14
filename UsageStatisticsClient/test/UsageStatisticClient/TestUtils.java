package UsageStatisticClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.GregorianCalendar;

import javax.crypto.NoSuchPaddingException;

import junitx.util.PrivateAccessor;
import UsageStatisticClientConfigGenerator.ConfigGenerator;

public class TestUtils
{
public static void removeAllLogsFromDao(UsageStatistic instance) throws NoSuchFieldException, SQLException
{
	
	DaoTemporaryDatabaseH2 dao = (DaoTemporaryDatabaseH2) PrivateAccessor.getField(instance, "dao");
	while (!dao.isEmpty())
	{
		dao.clearFirstLog();
	}
}


public static int getLogsAmmount(UsageStatistic instance) throws NoSuchFieldException, SQLException
{
	
	DaoTemporaryDatabaseH2 dao = (DaoTemporaryDatabaseH2) PrivateAccessor.getField(instance, "dao");
	return dao.getLogsAmount();
}

public static void addSomeLogsToDao(UsageStatistic instance, int amountRecord)
{	
	for (int i=0;i<amountRecord;i++)
	{
	instance.log("funkcjonalnosc", "i="+i); 
	}
}

public static DaoTemporaryDatabaseH2 getLocalDao(UsageStatistic instance) throws NoSuchFieldException
{
	return (DaoTemporaryDatabaseH2) PrivateAccessor.getField(instance, "dao");
}



public static void corruptFile(UsageStatistic instance) throws NoSuchFieldException, SQLException
{
	DaoTemporaryDatabaseH2 dao = (DaoTemporaryDatabaseH2) PrivateAccessor.getField(instance, "dao");
	dropTable(dao);
}

public static LogInformation getExampleLog()
{
	return new LogInformation(new GregorianCalendar().getTime(), "test", "user", "test", "test");
}

public static void dropTable(DaoTemporaryDatabaseH2 dao) throws NoSuchFieldException, SQLException
{
	String sql="DROP TABLE Log";
	dao.openDatabase();
	Connection conn=(Connection) PrivateAccessor.getField(dao, "conn");
	conn.createStatement().execute(sql);
	dao.closeDatabase();
}

public static void makeConnectionNull(DaoTemporaryDatabaseH2 dao) throws NoSuchFieldException
{
	PrivateAccessor.setField(dao, "conn", null);
}

public static void createExampleConfigFile() throws IOException
{
	//ConfigGenerator.createConfigFile("client-config.cfg", "http://localhost:8080/UsageStatisticsServer","matuszek","password", "tool");
	try {
		new Ciphers().writeCiphered(new File("client-config.cfg"), "serverURL= http://localhost:8080/UsageStatisticsServer user= user password= "+Ciphers.sha256("user")+" tool= tool debug= on");
	} catch (InvalidKeyException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InvalidAlgorithmParameterException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (NoSuchAlgorithmException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (NoSuchPaddingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}


public static boolean deleteDir(File dir) {
    if (dir.isDirectory()) {
        String[] children = dir.list();
        for (int i=0; i<children.length; i++) {
            boolean success = deleteDir(new File(dir, children[i]));
            if (!success) {
                return false;
            }
        }
    }
    return dir.delete();
}

public static String readLineFromDebugLog() throws IOException
{
	BufferedReader br = new BufferedReader(new FileReader("debuglog.txt"));
	String line=br.readLine();
	br.close();
	return line;
	
}

}