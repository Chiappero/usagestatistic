package usagestatisticsserver;

import java.io.UnsupportedEncodingException; 
import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException; 

class EncryptInstance
{		private static MessageDigest md;
		private static final int FOUR = 4;
		private static final int NINE = 9;
		private static final int TEN = 10;
		private static final int OxOF = 0x0F;
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
	            int halfbyte = (data[i] >>> FOUR) & OxOF;
	            int twoHalfs = 0;
	            do { 
	                if ((0 <= halfbyte) && (halfbyte <= NINE))
	                {
	                    buf.append((char) ('0' + halfbyte));
	                }
	                else
	                {
	                    buf.append((char) ('a' + (halfbyte - TEN)));
	                }
	                halfbyte = data[i] & OxOF;
	            } while(twoHalfs++ < 1);
	        } 
	        return buf.toString();
	    } 
	 
	    
	    /**
	     * @param text - text do zaszyfrowania
	     * @return null jezeli nie powiod³o sie szyfrowanie
	     */
	    static String sha256(final String text) 
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

