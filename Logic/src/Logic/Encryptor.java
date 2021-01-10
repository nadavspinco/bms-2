package Logic;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Encryptor {
    private static final String SECRET_KEY = "ENCRYPTOR_KEY";
    private static final SecretKeySpec secretKey;
    private static Cipher rc4;

    static {
        byte[] my_key = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        secretKey = new SecretKeySpec(my_key, "RC4");
        try {
            rc4 = Cipher.getInstance("RC4"); // create the instance of the cipher
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(String plaintext) {
        try {
            rc4.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] plaintextBytes = plaintext.getBytes();
            byte[] ciphertextBytes = rc4.doFinal(plaintextBytes);
            return Base64.getEncoder().encodeToString(ciphertextBytes);
        }
        catch (BadPaddingException e){
            e.printStackTrace();
            return plaintext;
        }
        catch (IllegalBlockSizeException e){
            e.printStackTrace();
            return plaintext;
        }
        catch (InvalidKeyException e){
            e.printStackTrace();
            return plaintext;
        }
    }

    public static String decrypt(String encryptedText) {
        try {
            byte[] ciphertextBytes = Base64.getDecoder().decode(encryptedText);
            rc4.init(Cipher.DECRYPT_MODE, secretKey, rc4.getParameters());
            byte[] byteDecryptedText = rc4.doFinal(ciphertextBytes);
            return new String(byteDecryptedText);
        }
        catch (InvalidKeyException e){
            e.printStackTrace();
            return encryptedText;
        }
        catch (InvalidAlgorithmParameterException e){
            e.printStackTrace();
            return encryptedText;
        }
        catch (IllegalBlockSizeException e){
            e.printStackTrace();
            return encryptedText;
        }
        catch (BadPaddingException e){
            e.printStackTrace();
            return encryptedText;
        }
    }
}