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
    private static final SecretKeySpec secretKey;
    private static Cipher cipher;
    private static final String encryptor = "key";
    private static final String SECRET_KEY_STR  = "bms_key";


    static {
        byte[] my_key = SECRET_KEY_STR.getBytes(StandardCharsets.UTF_8);
        secretKey = new SecretKeySpec(my_key, encryptor);
        try {
            cipher = Cipher.getInstance(encryptor);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public static String decrypt(String encryptedText) {
        try {
            byte[] ciphertextBytes = Base64.getDecoder().decode(encryptedText);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, cipher.getParameters());
            byte[] byteDecryptedText = cipher.doFinal(ciphertextBytes);

            return new String(byteDecryptedText);
        }
        catch (InvalidKeyException | InvalidAlgorithmParameterException |
                IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return encryptedText;
        }
    }

    public static String encrypt(String plaintext) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] plaintextBytes = plaintext.getBytes();
            byte[] ciphertextBytes = cipher.doFinal(plaintextBytes);

            return Base64.getEncoder().encodeToString(ciphertextBytes);
        }
        catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            e.printStackTrace();
            return plaintext;
        }
    }
}