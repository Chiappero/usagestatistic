package UsageStatisticClientConfigGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

import UsageStatisticClient.Ciphers;

public class ConfigGenerator
{
public static void createConfigFile(String fileName, String serverURL, String user, String password, String tool) throws IOException
{
	 /*File f = new File(fileName);
	 PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(f)));
	 if (serverURL!=null) out.println("serverURL = "+serverURL);
	 if (user!=null)out.println("user = "+user);
	 if (password!=null)out.println("password = "+password);
	 if (tool!=null)out.println("tool = "+tool);
	 out.println("debug = on");
	 out.close();*/
	try {
		new Ciphers().writeCiphered(new File(fileName), "serverURL= "+ serverURL +" user= "+user+" password= "+Ciphers.SHA256(password)+" tool= "+tool+" debug= on");
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

public static String config(String serverURL, String user, String password, String tool)
{
		return "serverURL = "+serverURL+"\nuser = "+user+"\npassword = "+Ciphers.SHA256(password)+"\ntool = "+tool+"\ndebug = on";
}


}
