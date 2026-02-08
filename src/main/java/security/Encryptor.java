package security;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Encryptor {

    // PRIVATE KEY DO NOT CHANGE!!
	private static final String GENERATED_KEY_FOR_PROJECT = "k18s6v4AKg412Ymnfiq/nQ==";
	
	private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    private Encryptor () {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // Encrypt the input string using the provided key
    public static String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, getKeyFromString(GENERATED_KEY_FOR_PROJECT));
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes); // Convert to Base64 string for storage
    }

    // Decrypt the encrypted string using the provided key
    public static String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, getKeyFromString(GENERATED_KEY_FOR_PROJECT));
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }

    // Generate a SecretKey from a given key string
    public static SecretKey getKeyFromString(String key) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(key);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
    }

    // Convert SecretKey to String (for storing the key securely)
    public static String keyToString(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
    
    // Uncomment below code to generate an AES key
//    //Generate a new AES key (you could store this securely and reuse it)
//    public static SecretKey generateKey(int n) throws Exception {
//        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
//        keyGenerator.init(n); // Specify the key size (128, 192, or 256 bits)
//        return keyGenerator.generateKey();
//    }

//    public static void main(String[] args) {
//        try {
//            // Generate a new AES key
//            SecretKey key = generateKey(128); // AES with 128-bit key
//
//            // Convert the key to a string for storage (if needed)
//            String keyString = keyToString(key);
//            System.out.println("Secret Key: " + keyString);
//
//            // Encrypt a sample message
//            String originalMessage = "Hello, World!";
//            String encryptedMessage = encrypt(originalMessage, key);
//            System.out.println("Encrypted Message: " + encryptedMessage);
//
//            // Decrypt the message
//            String decryptedMessage = decrypt(encryptedMessage, key);
//            System.out.println("Decrypted Message: " + decryptedMessage);
//
//            // Alternatively, you can recreate the SecretKey from the string key
//            SecretKey recreatedKey = getKeyFromString(keyString);
//            String decryptedWithRecreatedKey = decrypt(encryptedMessage, recreatedKey);
//            System.out.println("Decrypted with recreated key: " + decryptedWithRecreatedKey);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
