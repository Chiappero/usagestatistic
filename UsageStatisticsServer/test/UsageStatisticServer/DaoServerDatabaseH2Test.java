package UsageStatisticServer;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;

public class DaoServerDatabaseH2Test {

	@Test
	public void test() throws SQLException 
	{
		
		DaoServerDatabaseH2 dao=new DaoServerDatabaseH2();
		//TODO database is already open
		Assert.assertFalse(dao.isEmpty());
	}

}
