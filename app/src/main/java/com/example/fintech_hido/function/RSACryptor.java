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
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
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
    private static final String CIPHER_ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

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
                    initAndroidM(context.getPackageName());
                } else {
                    // API Level 19 이상 (킷캣)
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

            /* for encrypt / decrypt */
//            keyPairGenerator.initialize(new KeyGenParameterSpec.Builder(packageName, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT )
//                    .setAlgorithmParameterSpec(new RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4))
//                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
//                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
//                    .setDigests(KeyProperties.DIGEST_SHA512, KeyProperties.DIGEST_SHA384, KeyProperties.DIGEST_SHA256)
//                    .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
//                    .setUserAuthenticationRequired(false)
//                    .build());

            // signature
            keyPairGenerator.initialize(
                    new KeyGenParameterSpec.Builder(
                            packageName,
                            KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                            .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                            .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                                    //KeyProperties.SIGNATURE_PADDING_RSA_PSS)
                            .build());

            keyPairGenerator.generateKeyPair();
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
     * PublicKey String 으로 반환
     * @return publicKey
     */
    public String getPublicKeyStr() {
        byte[] publicKeyBytes = ((KeyStore.PrivateKeyEntry) keyEntry).getCertificate().getPublicKey().getEncoded();
        String publicKey = new String(Base64.encode(publicKeyBytes,Base64.DEFAULT));

        return publicKey;
    }

    /**
     *  PublicKey 반환
     * @param packageName
     * @return
     */
    public PublicKey getPublicKey(String packageName)  {
        try {
            KeyStore keyStore = KeyStore.getInstance(keyStoreName);
            keyStore.load(null);
            PublicKey publicKey = keyStore.getCertificate(packageName).getPublicKey();
            return publicKey;
        } catch (IOException | CertificateException | NoSuchAlgorithmException |  KeyStoreException e) {
            Log.d(TAG, "public key get fail : " + e);
            return null;
        }
    }

    /**
     *  PrivateKey 반환
     * @param packageName
     * @return
     */
    public PrivateKey getPrivateKey(String packageName) {
        try {
            KeyStore keyStore = KeyStore.getInstance(keyStoreName);
            keyStore.load(null);
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(packageName, null);
            return privateKey;
        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException | UnrecoverableKeyException e) {
            Log.d(TAG, "private key get fail : " + e);
            return null;
        }
    }


    public byte[] getDigitalSignature(String packageName, String text) {
        try{
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(getPrivateKey(packageName));
            //"SHA256withECDSA");

            byte[] data = text.getBytes("UTF-8");
            signature.update(data);

            byte[] signatureBytes = signature.sign();
            return signatureBytes;
        } catch (NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException | SignatureException e) {
            Log.e(TAG, "error in digital signature" + e);
            return null;
        }
    }

    public boolean verifySignature(String packageName, byte[] signature, String original){
        try{
            //byte[] signatureBytes = signature.getBytes("UTF-8");
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(getPublicKey(packageName));

            byte[] data = original.getBytes("UTF-8");
            sig.update(data);

            boolean result = sig.verify(signature);
            Log.e(TAG, "signatureString result :" + result);
            return result;
        }catch(Exception e){
            e.printStackTrace();
            Log.e(TAG,"error for verify:" + e.getMessage());
            return false;
        }
    }


    /**
     * 암호화 (privateKey) 테스트 (privateKey 와 publicKey가 같은 쌍인지 확인)
     * @param plain
     * @return encryptedString
     */
    public String encryptTest(String plain) {
        String encryptedString = plain;
        try {
            byte[] bytes = plain.getBytes("UTF-8");

            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, ((KeyStore.PrivateKeyEntry) keyEntry).getCertificate().getPublicKey());
            Log.d(TAG, "Encrypt Text: " + plain);
            byte[] encryptedBytes = cipher.doFinal(bytes);


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

/*
소희님 기존 코드
            byte[] base64Bytes = encryptedString.getBytes("UTF-8");
            byte[] decryptedBytes = Base64.decode(base64Bytes, Base64.DEFAULT);
            decryptedString = new String(cipher.doFinal(decryptedBytes));
 */

            byte[] byteEncrypted = java.util.Base64.getDecoder().decode(encryptedString.getBytes());
            byte[] bytePlain = cipher.doFinal(byteEncrypted);
            decryptedString = new String(bytePlain, "utf-8");


            Log.d(TAG, "Decrypted Text: "  + decryptedString);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | UnsupportedEncodingException | IllegalBlockSizeException e) {
            Log.e(TAG, "Decrypt fail", e);
        }
        return decryptedString;
    }
}