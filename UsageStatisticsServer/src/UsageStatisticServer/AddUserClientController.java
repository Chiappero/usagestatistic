package UsageStatisticServer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AddUserClientController
{	
	@Autowired
	DaoServerDatabaseH2 dao;
	
	@RequestMapping(value = "/addUserClient", method = RequestMethod.GET)
	protected ModelAndView addUserClientForm() 
	{	
		ModelAndView modelAndView = new ModelAndView("addUserClient");
		modelAndView.addObject("command",new UserClient());
		return modelAndView;
	}
	
	@RequestMapping(value = "/addUserClient", method = RequestMethod.POST)
	protected ModelAndView addUserClientPost(@ModelAttribute("UserClient")UserClient userClient, BindingResult result) 
	{	
		ModelAndView modelAndView = new ModelAndView("wiadomosc");
		if (userClient!=null)
		{
			String user = userClient.getUser();
			String password = userClient.getPassword();
			
			
			if (user!=null&&password!=null)
			{		
					if (user.isEmpty()||password.isEmpty())
					{
						modelAndView.addObject("message","Nie wypelniono wszystkich pol");
						return modelAndView;
					}
					password = EncryptInstance.SHA256(password);
					if (password==null)
					{
						modelAndView.addObject("message","Blad przy szyfrowaniu hasla");
						return modelAndView;
					}
					boolean done = dao.addUserClient(user, password);
					
					if (done)
					{
					modelAndView.addObject("message","Udalo sie dodac uzytkownika");
					}
					else
					{
					modelAndView.addObject("message","Blad z polaczeniem do bazy danych");
					}
					return modelAndView;	
				
			}
		}
		
		modelAndView.addObject("message","Zle odczytano parametry ");
		return modelAndView;
				
	}
	
}
