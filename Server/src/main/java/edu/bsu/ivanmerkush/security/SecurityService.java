package edu.bsu.ivanmerkush.security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class SecurityService {
    private PublicKey publicKey;
    private Cipher encryptionCipher;
    private Cipher decryptionCipher;
    private SecretKey sessionKey;

    private final static byte[] iv = { 1, 2, 3, 4, 5, 6, 7, 8};
    private static final IvParameterSpec ivspec = new IvParameterSpec(iv);

    public SecurityService() {
        Security.addProvider(new BouncyCastleProvider());

    }

    public boolean isKeyGenerated() {
        return sessionKey != null;
    }

    public String generateSessionKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] keyBytes = new byte[16];
        secureRandom.nextBytes(keyBytes);
        sessionKey = new SecretKeySpec(keyBytes, "IDEA");
        try {
            encryptionCipher = Cipher.getInstance("IDEA/CFB/NoPadding", "BC");
            encryptionCipher.init(Cipher.ENCRYPT_MODE, sessionKey, ivspec);
            decryptionCipher = Cipher.getInstance("IDEA/CFB/NoPadding", "BC");
            decryptionCipher.init(Cipher.DECRYPT_MODE, sessionKey, ivspec);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE,publicKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(sessionKey.getEncoded()));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setPublicKey(String strKey) {
        byte[] bytes  = Base64.getDecoder().decode(strKey);
        KeyFactory factory;
        try {
            factory = KeyFactory.getInstance("RSA", "BC");
            this.publicKey = factory.generatePublic(new X509EncodedKeySpec(bytes));
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    public byte[] encodeText(byte[] text) {
        try {
            return encryptionCipher.doFinal(text);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] decodeText(byte[] text) {
        try {
            return decryptionCipher.doFinal(text);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String decodeText(String text) {
        try {
            return Base64.getEncoder().encodeToString(decryptionCipher.doFinal(Base64.getDecoder().decode(text)));
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }


}
