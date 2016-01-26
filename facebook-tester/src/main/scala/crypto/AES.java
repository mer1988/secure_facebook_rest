package crypto;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;


/**
 * Created by miguel on 12/10/15.
 * based on example from
 * http://javapapers.com/java/java-symmetric-aes-encryption-decryption-using-jce/
 */


public class AES {
    Cipher cipher;

//    public static void main(String[] args) throws Exception {
//
//        SecretKey secretKey = obtainSecretKey();
//
//
//        String plainText = "AES Symmetric Encryption Decryption";
//        System.out.println("Plain Text Before Encryption: " + plainText);
//
//        String encryptedText = encrypt(plainText, secretKey);
//        System.out.println("Encrypted Text After Encryption: " + encryptedText);
//
//        String decryptedText = decrypt(encryptedText, secretKey);
//        System.out.println("Decrypted Text After Decryption: " + decryptedText);
//    }

    public  SecretKey obtainSecretKey() throws  Exception{
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        return keyGenerator.generateKey();
    }

    public  String encrypt(String plainText, SecretKey secretKey) throws Exception {
        cipher = Cipher.getInstance("AES");
        byte[] plainTextByte = plainText.getBytes();
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedByte = cipher.doFinal(plainTextByte);
        Base64.Encoder encoder = Base64.getEncoder();
        String encryptedText = encoder.encodeToString(encryptedByte);
        return encryptedText;
    }

    public  String decrypt(String encryptedText, SecretKey secretKey)
            throws Exception {
        cipher = Cipher.getInstance("AES");
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] encryptedTextByte = decoder.decode(encryptedText);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
        String decryptedText = new String(decryptedByte);
        return decryptedText;
    }

    public  String keyToString(SecretKey key){
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(key.getEncoded());
    }

    public  SecretKey stringToKey(String key){
        byte[] decodedKey = Base64.getDecoder().decode(key);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }
}