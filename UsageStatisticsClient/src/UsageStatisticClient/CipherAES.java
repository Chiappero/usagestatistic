package UsageStatisticClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CipherAES {

	 private  final String ALGORITHM = "AES";
	    private  final String BLOCK_MODE = "CBC";
	    private  final String PADDING = "PKCS5Padding";
	    private  final Charset CHARSET = Charset.forName( "UTF-8" );
	    private  final IvParameterSpec CBC_SALT = new IvParameterSpec(
	            new byte[] { 7, 34, 56, 78, 90, 87, 65, 43,
	                    12, 34, 56, 78, -123, 87, 65, 43 } );
	    private final byte[] BYTE_KEY = {4, -91, -30, -45, 28, -98, -80, -17, 100, 30, 28, -26, 94, 80, 69, 56, 111, 73, -44, -89, 72, -98, -105, 97, -27, 0, -19, 124, 73, 49, 120, 17};
	    final Cipher cipher;
	    SecretKeySpec key;
	    
	    
	    public CipherAES() throws NoSuchAlgorithmException, NoSuchPaddingException{
	        key = generateKey();
	        cipher = Cipher.getInstance( ALGORITHM + "/" + BLOCK_MODE + "/" + PADDING );
	    }
	    
	    private  SecretKeySpec generateKey()
	            throws NoSuchAlgorithmException
	        {
	        final KeyGenerator kg = KeyGenerator.getInstance( ALGORITHM );
	        kg.init( 128 );
	        final SecretKey secretKey = kg.generateKey();
	        final byte[] keyAsBytes = secretKey.getEncoded();
	        for(int i=0; i<keyAsBytes.length; i++){
	            keyAsBytes[i] = BYTE_KEY[i];           
	        }
	        return new SecretKeySpec( keyAsBytes, ALGORITHM );
	        }
	    
	    public  void writeCiphered(File file, String plainText )
	            throws InvalidKeyException, IOException, InvalidAlgorithmParameterException
	        {
	        cipher.init( Cipher.ENCRYPT_MODE, key, CBC_SALT );
	        final CipherOutputStream cout = new CipherOutputStream( new FileOutputStream( file ), cipher );
	        final byte[] plainTextBytes = plainText.getBytes( CHARSET );
	        cout.write( plainTextBytes.length >>> 8 );
	        cout.write( plainTextBytes.length & 0xff );
	        cout.write( plainTextBytes );
	        cout.close();
	        }
	    
	    public String readCiphered(File file )
	            throws InvalidKeyException, IOException, InvalidAlgorithmParameterException
	        {
	        cipher.init( Cipher.DECRYPT_MODE, key, CBC_SALT );
	        final CipherInputStream cin = new CipherInputStream( new FileInputStream( file ), cipher );
	        final int messageLengthInBytes = ( cin.read() << 8 ) | cin.read();
	        final byte[] reconstitutedBytes = new byte[ messageLengthInBytes>=0?messageLengthInBytes:0 ];
	        int bytesReadSoFar = 0;
	        int bytesRemaining = messageLengthInBytes;
	        while ( bytesRemaining > 0 )
	            {
	            final int bytesThisChunk = cin.read( reconstitutedBytes, bytesReadSoFar, bytesRemaining );
	            if ( bytesThisChunk == 0 )
	                {
	                throw new IOException( file.toString() + " corrupted." );
	                }
	            bytesReadSoFar += bytesThisChunk;
	            bytesRemaining -= bytesThisChunk;
	            }
	        cin.close();
	        return new String( reconstitutedBytes, CHARSET );
	        }
}
