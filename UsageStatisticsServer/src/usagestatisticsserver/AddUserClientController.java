package usagestatisticsserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller 
class AddUserClientController
{
	private static final String MESSAGE = "message";
	private static final String SOME_FIELDS_EMPTY = "Nie wypelniono wszystkich pol";
	private static final String PASSWORD_ENCRYPTION_ERROR = "Blad przy szyfrowaniu hasla";
	private static final String PROPER_ADD_USER = "Udalo sie dodac uzytkownika";
	private static final String ERROR_WITH_CONNECTION_TO_DATABASE = "Blad z polaczeniem do bazy danych";
	private static final String INVALID_READOUT_PARAMETERS ="Zle odczytano parametry";
	
	private static final String ADD_USER_CLIENT_SLASH ="/addUserClient";
	private static final String ADD_USER_CLIENT = "addUserClient";
	
	private static final String COMMAND = "command";
	@Autowired
	private DaoServerDatabaseH2 dao;
	
	@RequestMapping(value = ADD_USER_CLIENT_SLASH, method = RequestMethod.GET)
	protected ModelAndView addUserClientForm() 
	{	
		ModelAndView modelAndView = new ModelAndView(ADD_USER_CLIENT);
		modelAndView.addObject(COMMAND,new UserClient());
		return modelAndView;
	}
	
	@RequestMapping(value = ADD_USER_CLIENT_SLASH, method = RequestMethod.POST)
	protected ModelAndView addUserClientPost(@ModelAttribute("UserClient")UserClient userClient) 
	{	
		ModelAndView modelAndView = new ModelAndView(MESSAGE);
		if (userClient!=null)
		{
			String user = userClient.getUser();
			String password = userClient.getPassword();
			
			
			if (user!=null&&password!=null)
			{		
					if (user.isEmpty()||password.isEmpty())
					{
						modelAndView.addObject(MESSAGE,SOME_FIELDS_EMPTY);
						return modelAndView;
					}
					password = EncryptInstance.sha256(password);
					if (password==null)
					{
						modelAndView.addObject(MESSAGE,PASSWORD_ENCRYPTION_ERROR);
						return modelAndView;
					}
					boolean done = dao.addUserClient(user, password);
					
					if (done)
					{
					modelAndView.addObject(MESSAGE,PROPER_ADD_USER);
					}
					else
					{
					modelAndView.addObject(MESSAGE,ERROR_WITH_CONNECTION_TO_DATABASE);
					}
					return modelAndView;	
				
			}
		}
		
		modelAndView.addObject(MESSAGE,INVALID_READOUT_PARAMETERS);
		return modelAndView;
				
	}
	
}
