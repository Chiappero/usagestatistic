package usagestatisticsclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

class Ciphers{
	
	private final Cipher cipher;
	private final SecretKeySpec key;
	private static final String UTF8 = "UTF8";
	private static final int CIPHER_128 = 128;
	private static final int BYTE10 = 10;
	private static final int BYTE9 = 9;
	private static final int BYTE8 = 8;
	private static final int BYTE4 = 4;
	private static final int OxOF = 0x0F;
	private static final String ALGORITHM = "AES";
	private static final String BLOCK_MODE = "CBC";
	private static final String PADDING = "PKCS5Padding";
	private static final Charset CHARSET = Charset.forName("UTF-8");
	private static final String SHA256 = "SHA-256";
	private static final IvParameterSpec CBC_SALT = new IvParameterSpec(new byte[] { 7, 34, 56, 78, 90, 87, 65, 43, 12, 34, 56, 78, -123, 87, 65, 43 });
	private static final byte[] BYTE_KEY =
	{ 4, -91, -30, -45, 28, -98, -80, -17, 100, 30, 28, -26, 94, 80, 69, 56, 111, 73, -44, -89, 72, -98, -105, 97, -27, 0, -19, 124, 73, 49, 120, 17 };


	public Ciphers() throws NoSuchAlgorithmException, NoSuchPaddingException //public for test only
	{
		key = generateKey();
		cipher = Cipher.getInstance(ALGORITHM + "/" + BLOCK_MODE + "/" + PADDING);
	}

	private SecretKeySpec generateKey() throws NoSuchAlgorithmException
	{
		final KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM);
		kg.init(CIPHER_128);
		final SecretKey secretKey = kg.generateKey();
		final byte[] keyAsBytes = secretKey.getEncoded();
		for (int i = 0; i < keyAsBytes.length; i++)
		{
			keyAsBytes[i] = BYTE_KEY[i];
		}
		return new SecretKeySpec(keyAsBytes, ALGORITHM);
	}

	final String readCiphered(final File file) throws InvalidKeyException, IOException, InvalidAlgorithmParameterException
	{
		cipher.init(Cipher.DECRYPT_MODE, key, CBC_SALT);
		final CipherInputStream cin = new CipherInputStream(new FileInputStream(file), cipher);
		final int messageLengthInBytes = (cin.read() << BYTE8) | cin.read();
		final byte[] reconstitutedBytes;
		if (messageLengthInBytes >= 0)
		{
			reconstitutedBytes = new byte[messageLengthInBytes];
		} else
		{
			reconstitutedBytes = new byte[0];
		}

		int bytesReadSoFar = 0;
		int bytesRemaining = messageLengthInBytes;
		while (bytesRemaining > 0)
		{
			final int bytesThisChunk = cin.read(reconstitutedBytes, bytesReadSoFar, bytesRemaining);
			if (bytesThisChunk == 0)
			{
				throw new IOException(file.toString() + " corrupted.");
			}
			bytesReadSoFar += bytesThisChunk;
			bytesRemaining -= bytesThisChunk;
		}
		cin.close();
		return new String(reconstitutedBytes, CHARSET);
	}

	private static MessageDigest md;
	static
	{
		try
		{
			md = MessageDigest.getInstance(SHA256);
		} catch (NoSuchAlgorithmException e)
		{
		}
	}

	private static String convertToHex(final byte[] data)
	{
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++)
		{
			int halfbyte = (data[i] >>> BYTE4) & OxOF;
			int twoHalfs = 0;
			do
			{
				if ((0 <= halfbyte) && (halfbyte <= BYTE9))
				{
					buf.append((char) ('0' + halfbyte));
				} else
				{
					buf.append((char) ('a' + (halfbyte - BYTE10)));
				}
				halfbyte = data[i] & OxOF;
			} while (twoHalfs++ < 1);
		}
		return buf.toString();
	}

	/**
	 * @param text
	 *            - text do zaszyfrowania
	 * @return null jezeli nie powiod³o sie szyfrowanie
	 */
	static String sha256(final String text)
	{
		if (text == null || md == null)
		{
			return null;
		}
		try
		{
			md.update(text.getBytes(UTF8), 0, text.length());
		} catch (UnsupportedEncodingException e)
		{
		}
		return convertToHex(md.digest());
	}
}
