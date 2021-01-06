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
    private static final String ENCRYPTION_ALGORITHM = "RC4";
    private static final String SECRET_KEY = "YOUR_SECRET_KEY";
    private static final SecretKeySpec secretKey;
    private static Cipher rc4;

    static {
        byte[] my_key = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        secretKey = new SecretKeySpec(my_key, ENCRYPTION_ALGORITHM);
        try {
            rc4 = Cipher.getInstance(ENCRYPTION_ALGORITHM);
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
        } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
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
        } catch (InvalidKeyException | InvalidAlgorithmParameterException |
                IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return encryptedText;
        }
    }
}