package crypto;

/**
 * Created by miguel on 12/10/15.
 */

import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author JavaDigest
 * Based on example from
 * https://javadigest.wordpress.com/2012/08/26/rsa-encryption-example/
 * http://janiths-codes.blogspot.com/2009/11/how-to-convert-publickey-as-string-and.html
 * http://www.java2s.com/Tutorial/Java/0490__Security/SimpleDigitalSignatureExample.htm
 */
public class RSA {

    /**
     * String to hold name of the encryption algorithm.
     */
    public static final String ALGORITHM = "RSA";



    /**
     * Generate key which contains a pair of private and public key using 1024
     * bytes. Store the set of keys in Prvate.key and Public.key files.
     *
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static KeyPair generateKey() throws  Exception{

            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
            keyGen.initialize(1024);
            final KeyPair key = keyGen.generateKeyPair();
            return key;

    }

    public static String publicKeyAsString(PublicKey publicKey){
        byte[] array = publicKey.getEncoded();
        return  Base64.getEncoder().encodeToString(array);
    }

    public static PublicKey stringAsPublicKey(String publicKey) throws Exception{

        byte[] asBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(asBytes);
        KeyFactory keyFact = KeyFactory.getInstance("RSA");
        return keyFact.generatePublic(x509KeySpec);
    }

    /**
     * Encrypt the plain text using public key.
     *
     * @param text
     *          : original plain text
     * @param key
     *          :The public key
     * @return Encrypted text
     * @throws Exception
     */
    public static String encrypt(String text, PublicKey key) {
        byte[] cipherText = null;
        try {
            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            // encrypt the plain text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(text.getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Base64.getEncoder().encodeToString(cipherText);
    }

    /**
     * Decrypt text using private key.
     *
     * @param text
     *          :encrypted text
     * @param key
     *          :The private key
     * @return plain text
     * @throws Exception
     */
    public static String decrypt(String text, PrivateKey key) {
        byte[] dectyptedText = null;
        try {
            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance(ALGORITHM);

            // decrypt the text using the private key
            cipher.init(Cipher.DECRYPT_MODE, key);
            dectyptedText = cipher.doFinal(Base64.getDecoder().decode(text));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new String(dectyptedText);
    }

    public static boolean checkSignature(String signature, String data, PublicKey pk) throws Exception{
        Signature sig = Signature.getInstance("MD5WithRSA");
        sig.initVerify(pk);
        sig.update(data.getBytes("UTF8"));
        return sig.verify(Base64.getDecoder().decode(signature));
    }



    /**
     * Test the EncryptionUtil
     */
    public static void main(String[] args) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);
            KeyPair keyPair = kpg.genKeyPair();

            byte[] data = "test".getBytes("UTF8");

            Signature sig = Signature.getInstance("MD5WithRSA");
            sig.initSign(keyPair.getPrivate());
            sig.update(data);

            byte[] signatureBytes = sig.sign();
            System.out.println("Singature:" + new BASE64Encoder().encode(signatureBytes));

            sig.initVerify(keyPair.getPublic());
            sig.update(data);

            sig.verify(signatureBytes);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}