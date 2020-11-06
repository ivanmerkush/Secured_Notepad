package edu.bsu.ivanmerkush.security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
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
        System.out.println(Arrays.toString(Security.getProviders()));

    }

    public boolean isKeyGenerated() {
        return sessionKey != null;
    }

    public byte[] generateSessionKey() throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, NoSuchProviderException, InvalidAlgorithmParameterException {
        SecureRandom secureRandom = new SecureRandom();
        byte[] keyBytes = new byte[16];
        secureRandom.nextBytes(keyBytes);
        sessionKey = new SecretKeySpec(keyBytes, "IDEA");
        System.out.println(Base64.getEncoder().encodeToString(sessionKey.getEncoded()));
        encryptionCipher = Cipher.getInstance("IDEA/CFB/NoPadding", "BC");
        encryptionCipher.init(Cipher.ENCRYPT_MODE, sessionKey, ivspec);
        decryptionCipher = Cipher.getInstance("IDEA/CFB/NoPadding", "BC");
        decryptionCipher.init(Cipher.DECRYPT_MODE, sessionKey, ivspec);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE,publicKey);
        return cipher.doFinal(sessionKey.getEncoded());
    }

    public void setPublicKey(String strKey) throws InvalidKeySpecException, NoSuchProviderException, NoSuchAlgorithmException {
        byte[] bytes  = Base64.getDecoder().decode(strKey);
        KeyFactory factory = KeyFactory.getInstance("RSA", "BC");
        this.publicKey = factory.generatePublic(new X509EncodedKeySpec(bytes));

    }

    public byte[] encodeText(byte[] text) throws BadPaddingException, IllegalBlockSizeException {
        return encryptionCipher.doFinal(text);
    }

    public byte[] decodeText(byte[] text) throws BadPaddingException, IllegalBlockSizeException {
        return decryptionCipher.doFinal(text);
    }
}
