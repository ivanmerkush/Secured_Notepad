package edu.bsu.ivanmerkush.security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import sun.misc.BASE64Decoder;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

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

    public void setSecretKey(byte[] encodedKey) {
        try {
            byte[] decodedKey =  cipherRSA.doFinal(encodedKey);
            sessionKey = new SecretKeySpec(decodedKey, "IDEA");
            decryptionCipher = Cipher.getInstance("IDEA/CFB/NoPadding", "BC");
            decryptionCipher.init(Cipher.DECRYPT_MODE, sessionKey, ivspec);
            encryptionCipher = Cipher.getInstance("IDEA/CFB/NoPadding", "BC");
            encryptionCipher.init(Cipher.ENCRYPT_MODE, sessionKey, ivspec);
        } catch (IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | NoSuchProviderException e) {
            e.printStackTrace();
        }

    }

    public boolean isKeyGenerated() {
        return sessionKey != null;
    }

    public void generateRSA() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();
            cipherRSA = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipherRSA.init(Cipher.DECRYPT_MODE, privateKey);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public byte[] encodeText(byte[] text){
        if(isKeyGenerated()) {
            try {
                return encryptionCipher.doFinal(text);
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                e.printStackTrace();
            }
            return null;
        }
        else {
            return text;
        }
    }


    public byte[] decodeText(String text) {
        if(isKeyGenerated()) {
            try {
                return decryptionCipher.doFinal(Base64.getDecoder().decode(text));
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                e.printStackTrace();
            }
            return null;
        }
        else {
            return Base64.getDecoder().decode(text);
        }
    }

}
