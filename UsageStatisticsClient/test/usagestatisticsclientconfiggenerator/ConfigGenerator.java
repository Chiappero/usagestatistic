package usagestatisticsclientconfiggenerator;

import java.io.File;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

import junitx.util.PrivateAccessor;


public class ConfigGenerator
{
public static void createConfigFile(String fileName, String serverURL, String user, String password, String tool) throws Throwable
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
		String pass=(String) PrivateAccessor.invoke(Ciphers.class, "sha256", new Class[] {String.class}, new String[] {password});
		new Ciphers().writeCiphered(new File(fileName), "serverURL= "+ serverURL +" user= "+user+" password= "+pass+" tool= "+tool+" debug= on");
	} catch (InvalidKeyException e) {
	} catch (InvalidAlgorithmParameterException e) {
	} catch (NoSuchAlgorithmException e) {
	} catch (NoSuchPaddingException e) {
	}
}

public static String config(String serverURL, String user, String password, String tool) throws Throwable
{
		String pass=(String) PrivateAccessor.invoke(Ciphers.class, "sha256", new Class[] {String.class}, new String[] {password});
		return "serverURL = "+serverURL+"\nuser = "+user+"\npassword = "+pass+"\ntool = "+tool+"\ndebug = on";
}


}
