package usagestatisticsclient;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.junit.Assert;
import org.junit.Test;

import usagestatisticsclient.Ciphers;
import usagestatisticsclient.UsageLogger;
import usagestatisticsclient.UsageStatistic;
import usagestatisticsclientconfiggenerator.ConfigGenerator;

public class CiphersTest 
{

	@Test
	public void AT10_1_AND_AT10_2_Proper_create_user_config_AND_Proper_decrypt_user_config() throws Throwable 
	{
		Ciphers cipher=new Ciphers();
		usagestatisticsclientconfiggenerator.Ciphers testcipher=new usagestatisticsclientconfiggenerator.Ciphers();
		File f=new File("cipher.test");
		File f2=new File("cipher2.test");
		testcipher.writeCiphered(f, "line1\nline2");
		cipher=new Ciphers();
		String answer=cipher.readCiphered(f);
		Assert.assertEquals("line1\nline2", answer);
		f.delete();
		f=new File("cipher.test");
		answer=ConfigGenerator.config("localhost:8080/UsageStatisticServer", "user", "password", "tool");
		testcipher.writeCiphered(f, answer);
		String s2=cipher.readCiphered(f);
		Assert.assertEquals("serverURL = localhost:8080/UsageStatisticServer\nuser = user\npassword = "+Ciphers.sha256("password")+"\ntool = tool\ndebug = on", s2);
		testcipher.writeCiphered(f2, answer);
		BufferedReader br=new BufferedReader(new FileReader(f));
		BufferedReader br2=new BufferedReader(new FileReader(f2));
		while ((s2=br.readLine())!=null)
			Assert.assertEquals(s2,br2.readLine());
		br.close();
		br2.close();
		ConfigGenerator.createConfigFile(f.getName(), "localhost:8080/UsageStatisticServer", "user", "password", "tool");
		answer=cipher.readCiphered(f);
		Assert.assertEquals("serverURL= localhost:8080/UsageStatisticServer user= user password= "+Ciphers.sha256("password")+" tool= tool debug= on", answer);
		ConfigGenerator.createConfigFile("client-config.cfg", "localhost:8080/UsageStatisticServer", "user", "user", "tool");
		UsageLogger us=UsageStatistic.getInstance();
		Assert.assertTrue(us instanceof UsageStatistic);
		f.delete();
		f2.delete();
		
	}
	
	@Test
	public void AT103_Proper_Encrypt_Password_When_Creating_User_Config()
	{
		String sha=Ciphers.sha256("pasword123?\n");
		String sha2=Ciphers.sha256("pasword123?\n");
		Assert.assertEquals(sha,sha2);
		sha2=Ciphers.sha256("passrord123?\n ");
		Assert.assertNotSame(sha,sha2);
		sha2=Ciphers.sha256("passrord124?\n");
		Assert.assertNotSame(sha,sha2);
	}

}
