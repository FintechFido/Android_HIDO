package com.example.fintech_hido.function;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.Calendar;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.x500.X500Principal;

public class RSACryptor {
    private static final String TAG = "RSACryptor";
    private static final String keyStoreName = "AndroidKeyStore";

    private KeyStore.Entry keyEntry;


    // 비대칭 암호화(공개키) 알고리즘 호출 상수
    private static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";

    // Singleton
    private RSACryptor() {
    }

    private static class RSACryptorHolder {
        static final RSACryptor INSTANCE = new RSACryptor();
    }

    public static RSACryptor getInstance() {
        return RSACryptorHolder.INSTANCE;
    }

    public void init(Context context) {
        try {
            //AndroidKeyStore 정확하게 기입
            KeyStore keyStore = KeyStore.getInstance(keyStoreName);
            keyStore.load(null);

            if(!keyStore.containsAlias(context.getPackageName())){
                // KeyStore에 패키지 네임이 등록되어 있지 않을 때 실행
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // API Level 23 이상 (마쉬멜로우)
                    System.out.println("HERE 11");
                    initAndroidM(context.getPackageName());
                } else {
                    // API Level 19 이상 (킷캣)
                    System.out.println("HERE 22");
                    initAndroidK(context);
                }
            }
            keyEntry = keyStore.getEntry(context.getPackageName(), null);


        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | UnrecoverableEntryException e) {
            Log.e(TAG, "init fail", e);
        }
    }

    /**
     * 키 생성
     * @param packageName
     * @return
     */
    private void initAndroidM(String packageName) {
        try{
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, keyStoreName);

            keyPairGenerator.initialize(new KeyGenParameterSpec.Builder(packageName, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT )
                    .setAlgorithmParameterSpec(new RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4))
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                    .setDigests(KeyProperties.DIGEST_SHA512, KeyProperties.DIGEST_SHA384, KeyProperties.DIGEST_SHA256)
                    .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                    .setUserAuthenticationRequired(false)
                    .build());


            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            Log.d(TAG, "RSA init M");
        } catch (GeneralSecurityException e){
            Log.e(TAG, "알고리즘 지원하지 않는 디바이스", e);
            /**
             * TODO
             * alert 추가
             */
        }
    }

    /**
     *  키 생성
     * @param context
     * @return
     */
    private void initAndroidK(Context context) {
        try{
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            end.add(Calendar.YEAR, 25);

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", keyStoreName);

            keyPairGenerator.initialize(new KeyPairGeneratorSpec.Builder(context)
                    .setKeySize(2048)
                    .setAlias(context.getPackageName())
                    .setSubject(new X500Principal("CN=myKey"))
                    .setSerialNumber(BigInteger.ONE)
                    .setStartDate(start.getTime())
                    .setEndDate(end.getTime())
                    .build());

            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            Log.d(TAG, "RSA init K");
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            Log.e(TAG, "알고리즘 지원하지 않는 디바이스", e);
        }
    }

    /**
     *
     * @return publicKey
     */
    public String getPublicKey() {

        byte[] publicKeyBytes = ((KeyStore.PrivateKeyEntry) keyEntry).getCertificate().getPublicKey().getEncoded();
        String publicKey = new String(Base64.encode(publicKeyBytes,Base64.DEFAULT));

        return publicKey;
    }

/*
    public String encrypt_subin_test(String text) {
        String encryptedString = text;
        byte[] s;
        try {
            byte[] bytes = text.getBytes("UTF-8");
            Signature signature = Signature.getInstance("NONEwithRSA");
            signature.initSign(privateKey);
            //signature.initSign(((KeyStore.PrivateKeyEntry) keyEntry).getPrivateKey());
            signature.update(bytes);
            s = signature.sign();
            System.out.println("SIGN RESULT : "+s.toString());
            return s.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
        try {
            byte[] bytes = text.getBytes("UTF-8");

            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, ((KeyStore.PrivateKeyEntry) keyEntry).getPrivateKey());
            byte[] encryptedBytes = cipher.doFinal(bytes);
            encryptedString = new String(Base64.encode(encryptedBytes, Base64.DEFAULT));
        } catch (UnsupportedEncodingException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            Log.e(TAG, "Encrypt fail", e);
        }


        return "";
    }


 */


    /**
     * 암호화 (privateKey) 테스트 (privateKey 와 publicKey가 같은 쌍인지 확인)
     * @param plain
     * @return encryptedString
     */
    public String encryptTest(String plain) {
        String encryptedString = plain;
        try {
            byte[] bytes = plain.getBytes("UTF-8");
/*
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, ((KeyStore.PrivateKeyEntry) keyEntry).getCertificate().getPublicKey());
            Log.d(TAG, "Encrypt Text: " + plain);
            byte[] encryptedBytes = cipher.doFinal(bytes);

 */
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, ((KeyStore.PrivateKeyEntry)keyEntry).getPrivateKey());
            byte[] encryptedBytes = cipher.doFinal(bytes);
            encryptedString = new String(Base64.encode(encryptedBytes, Base64.DEFAULT));

        } catch (UnsupportedEncodingException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            Log.e(TAG, "Encrypt fail", e);
        }
        return encryptedString;
    }

    /**
     * 복호화 (publicKey) 테스트 (privateKey 와 publicKey가 같은 쌍인지 확인)
     * @param encryptedString
     * @return decryptedString
     */
    public String decryptTest(String encryptedString) {
        String decryptedString = encryptedString;
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, ((KeyStore.PrivateKeyEntry) keyEntry).getPrivateKey());
            byte[] base64Bytes = encryptedString.getBytes("UTF-8");
            byte[] decryptedBytes = Base64.decode(base64Bytes, Base64.DEFAULT);

            decryptedString = new String(cipher.doFinal(decryptedBytes));
            Log.d(TAG, "Decrypted Text: "  + decryptedString);
            } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | UnsupportedEncodingException | IllegalBlockSizeException e) {
                Log.e(TAG, "Decrypt fail", e);
            }
        return decryptedString;
    }
}
