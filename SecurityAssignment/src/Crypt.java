import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class Crypt {
    
    private SecretKey secretkey; 
    
    public Crypt(String keyFileName) throws NoSuchAlgorithmException, IOException, ClassNotFoundException 
    {
        // search for key with provided filename. if none exists, generate one and save to disk.
    	File keyFile = new File(keyFileName);
    	
    	if (keyFile.exists()) {
			
    		FileInputStream fileStream = new FileInputStream(keyFileName);
			ObjectInputStream inputStream = new ObjectInputStream(fileStream);
			
			// Read the object
			SecretKey key = (SecretKey)inputStream.readObject();
			inputStream.close();
			
			// set key
			this.setSecretkey(key);
			
    	} else {
    		
			generateKey();
			FileOutputStream fileStream = new FileOutputStream(keyFileName);
			ObjectOutputStream objStream = new ObjectOutputStream(fileStream);
			
			// write object to output stream i.e. write the key to the file
			objStream.writeObject(this.getSecretkey());
			objStream.close();
    	}
    }
    
    
    /**
	* Step 1. Generate a DES key using KeyGenerator 
    */
    
    public void generateKey() throws NoSuchAlgorithmException 
    {
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        this.setSecretkey(keyGen.generateKey());        
    }
    
    // load encryption key from file. 
    public void loadKey(String keyFileName) throws IOException, ClassNotFoundException {
    	File keyFile = new File(keyFileName);
    	if (keyFile.exists()) {
    		FileInputStream fileStream = new FileInputStream(keyFileName);
    		ObjectInputStream inputStream = new ObjectInputStream(fileStream);
    		// Read the object
    		SecretKey key = (SecretKey)inputStream.readObject();
    		inputStream.close();
    		// set key
    		this.setSecretkey(key);
    	} else {
    		throw new IOException("File not found");
    	}
    }
    public void loadKey(File keyFile) throws IOException, ClassNotFoundException {
		if (keyFile.exists()) {
			FileInputStream fileStream = new FileInputStream(keyFile.getAbsolutePath());
			ObjectInputStream inputStream = new ObjectInputStream(fileStream);
			// Read the object
			SecretKey key = (SecretKey)inputStream.readObject();
			inputStream.close();
			// set key
			this.setSecretkey(key);
		} else {
			throw new FileNotFoundException("File not found");
		}
    }
    
    public void saveKey(String keyFileName) throws IOException {
    	FileOutputStream fileStream = new FileOutputStream(keyFileName);
    	ObjectOutputStream objStream = new ObjectOutputStream(fileStream);
    	// write key to output stream
    	objStream.writeObject(this.getSecretkey());
    	objStream.close();
    }
    
    public void saveEncrypted(String keyFileName, byte[] encryptedText) throws IOException {
    	FileOutputStream fileStream = new FileOutputStream(keyFileName);
    	ObjectOutputStream objStream = new ObjectOutputStream(fileStream);
    	// write object to output stream
    	objStream.writeObject(encryptedText);
    	objStream.close();
    	
    }
    
    public byte[] encrypt (String strDataToEncrypt) throws 
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, 
            InvalidAlgorithmParameterException, IllegalBlockSizeException, 
            BadPaddingException
    {
        Cipher desCipher = Cipher.getInstance("DES"); // Must specify the mode explicitly as most JCE providers default to ECB mode!!
        desCipher.init(Cipher.ENCRYPT_MODE, this.getSecretkey());
        byte[] byteDataToEncrypt = strDataToEncrypt.getBytes();
        byte[] byteCipherText = desCipher.doFinal(byteDataToEncrypt);       
        return byteCipherText;
    }
    
//    public String decrypt (byte[] strCipherText) throws 
//    NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, 
//    InvalidAlgorithmParameterException, IllegalBlockSizeException, 
//    BadPaddingException
//    {        
//    	Cipher desCipher = Cipher.getInstance("DES"); // Must specify the mode explicitly as most JCE providers default to ECB mode!!				
//    	desCipher.init(Cipher.DECRYPT_MODE, this.getSecretkey());        
//    	byte[] byteDecryptedText = desCipher.doFinal(strCipherText);        
//    	return new String(byteDecryptedText);
//    }   
//    
    public String decrypt (byte[] strCipherText) throws 
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, 
            InvalidAlgorithmParameterException, IllegalBlockSizeException, 
            BadPaddingException
    {        
        Cipher desCipher = Cipher.getInstance("DES"); // Must specify the mode explicitly as most JCE providers default to ECB mode!!				
        desCipher.init(Cipher.DECRYPT_MODE, this.getSecretkey());        
        byte[] byteDecryptedText = desCipher.doFinal(strCipherText);        
        return new String(byteDecryptedText);
    }   

    public SecretKey getSecretkey() {
        return secretkey;
    }

    public void setSecretkey(SecretKey secretkey) {
        this.secretkey = secretkey;
    }
}
