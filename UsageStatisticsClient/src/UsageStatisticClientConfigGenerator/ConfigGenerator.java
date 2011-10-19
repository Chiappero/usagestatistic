package UsageStatisticClientConfigGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ConfigGenerator
{
public static void createConfigFile(String fileName, String serverURL, String user, String password) throws IOException
{
	 File f = new File(fileName);
	 PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(f)));
	 if (serverURL!=null) out.println("serverURL = "+serverURL);
	 if (user!=null)out.println("user = "+user);
	 if (password!=null)out.println("password = "+password);
	 out.close();
}
}
