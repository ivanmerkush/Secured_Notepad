package edu.bsu.ivanmerkush.security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;

public class SecurityService {
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private SecretKey sessionKey;
    private Cipher cipherRSA;
    private Cipher decryptionCipher;
    private Cipher encryptionCipher;

    private final static byte[] iv = { 1, 2, 3, 4, 5, 6, 7, 8};
    private static final IvParameterSpec ivspec = new IvParameterSpec(iv);

    public SecurityService() {
        Security.addProvider(new BouncyCastleProvider());
    }
    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setSecretKey(byte[] encodedKey) throws BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, InvalidAlgorithmParameterException {
        byte[] decodedKey = cipherRSA.doFinal(encodedKey);
        sessionKey = new SecretKeySpec(decodedKey, "IDEA");
        decryptionCipher = Cipher.getInstance("IDEA/CFB/NoPadding", "BC");
        decryptionCipher.init(Cipher.DECRYPT_MODE, sessionKey, ivspec);
        encryptionCipher = Cipher.getInstance("IDEA/CFB/NoPadding", "BC");
        encryptionCipher.init(Cipher.ENCRYPT_MODE, sessionKey, ivspec);
    }

    public boolean isKeyGenerated() {
        return sessionKey != null;
    }

    public void generateRSA() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
        cipherRSA = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipherRSA.init(Cipher.DECRYPT_MODE, privateKey);

    }

    public byte[] encodeText(byte[] text) throws BadPaddingException, IllegalBlockSizeException {
        return encryptionCipher.doFinal(text);
    }
    public byte[] decodeText(byte[] text) throws BadPaddingException, IllegalBlockSizeException {
        return decryptionCipher.doFinal(text);
    }

}
