package usagestatisticsserver;

import junit.framework.Assert;
import junitx.util.PrivateAccessor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:WebContent/WEB-INF/dispatcher-servlet.xml"})
public class AddUserClientControllerTest
{
	
	String MESSAGE;
	String INVALID_READOUT_PARAMETERS;
	String COMMAND;
	String SOME_FIELDS_EMPTY;
	String PROPER_ADD_USER;
	
	@Autowired
	private AddUserClientController addUserClientController;
	

	@Before
	public void setUp() throws Exception
	{
	MESSAGE = 	(String) PrivateAccessor.getField(AddUserClientController.class, "MESSAGE");
	INVALID_READOUT_PARAMETERS = (String) PrivateAccessor.getField(AddUserClientController.class, "INVALID_READOUT_PARAMETERS");
	COMMAND = (String) PrivateAccessor.getField(AddUserClientController.class, "COMMAND");
	SOME_FIELDS_EMPTY = (String) PrivateAccessor.getField(AddUserClientController.class, "SOME_FIELDS_EMPTY");
	PROPER_ADD_USER = (String) PrivateAccessor.getField(AddUserClientController.class, "PROPER_ADD_USER");
	}

	@Test
	public void testAddUserClientForm()
	{
		Assert.assertTrue(addUserClientController.addUserClientForm().getModel().get(COMMAND) instanceof UserClient);
	}

	@Test
	public void testAddUserClientPost()
	{
		Assert.assertTrue(addUserClientController.addUserClientPost(null).getModel().get(MESSAGE).equals(INVALID_READOUT_PARAMETERS));
		UserClient userClient = new UserClient();
		userClient.setPassword("");
		userClient.setUser("");
		Assert.assertTrue(addUserClientController.addUserClientPost(userClient).getModel().get(MESSAGE).equals(SOME_FIELDS_EMPTY));
		userClient.setUser("test");
		Assert.assertTrue(addUserClientController.addUserClientPost(userClient).getModel().get(MESSAGE).equals(SOME_FIELDS_EMPTY));
		userClient.setUser("");
		userClient.setPassword("test");
		Assert.assertTrue(addUserClientController.addUserClientPost(userClient).getModel().get(MESSAGE).equals(SOME_FIELDS_EMPTY));
		userClient.setPassword("test");
		userClient.setUser("test");
		Assert.assertTrue(addUserClientController.addUserClientPost(userClient).getModel().get(MESSAGE).equals(PROPER_ADD_USER));
		
		userClient.setUser(null);
		Assert.assertTrue(addUserClientController.addUserClientPost(userClient).getModel().get(MESSAGE).equals(INVALID_READOUT_PARAMETERS));
		userClient.setPassword(null);
		Assert.assertTrue(addUserClientController.addUserClientPost(userClient).getModel().get(MESSAGE).equals(INVALID_READOUT_PARAMETERS));
		userClient.setUser("test");
		Assert.assertTrue(addUserClientController.addUserClientPost(userClient).getModel().get(MESSAGE).equals(INVALID_READOUT_PARAMETERS));
		
	}

}
