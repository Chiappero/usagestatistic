package UsageStatisticServer;

import java.io.UnsupportedEncodingException; 
import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException; 

public class EncryptInstance
{		static MessageDigest md;
		static
		{
		try
		{
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e)
		{
		}
		}
	    private static String convertToHex(final byte[] data) { 
	        StringBuffer buf = new StringBuffer();
	        for (int i = 0; i < data.length; i++) { 
	            int halfbyte = (data[i] >>> 4) & 0x0F;
	            int two_halfs = 0;
	            do { 
	                if ((0 <= halfbyte) && (halfbyte <= 9)) 
	                    buf.append((char) ('0' + halfbyte));
	                else 
	                    buf.append((char) ('a' + (halfbyte - 10)));
	                halfbyte = data[i] & 0x0F;
	            } while(two_halfs++ < 1);
	        } 
	        return buf.toString();
	    } 
	 
	    
	    /**
	     * @param text - text do zaszyfrowania
	     * @return null jezeli nie powiod³o sie szyfrowanie
	     */
	    public static String SHA256(final String text) 
	    { 
	    if (text==null||md==null)
	    {
	    	return null;
	    }
			try
			{
				md.update(text.getBytes("UTF8"), 0, text.length());
			} catch (UnsupportedEncodingException e)
			{
			}
	    return convertToHex(md.digest());
	    } 
} 

