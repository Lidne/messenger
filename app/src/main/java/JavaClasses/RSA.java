package JavaClasses;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSA {
    private Context context;
    private KeyPairGenerator generator;
    private KeyPair keys;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    public RSA(Context context, boolean generate) throws NoSuchAlgorithmException {
        this.context = context;
        this.generator = KeyPairGenerator.getInstance("RSA");
        if (generate) {
            this.generator.initialize(2048);
            this.keys = generator.generateKeyPair();
            this.privateKey = keys.getPrivate();
            this.publicKey = keys.getPublic();
        }
    }

    public RSA(Context context, PublicKey publicKey, PrivateKey privateKey) throws NoSuchAlgorithmException {
        this.context = context;
        this.generator = KeyPairGenerator.getInstance("RSA");
        this.keys = new KeyPair(publicKey, privateKey);
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public void writePublic(String chat) {
        try {
            File path = new File(context.getFilesDir() + "/" + chat);
            if (!path.exists()) path.mkdirs();
            File public_file = new File(context.getFilesDir() + "/" + chat + "/public.key");
            if (!public_file.exists()) {
                if (!public_file.createNewFile()) return;
            }
            FileOutputStream public_file_out = new FileOutputStream(public_file);
            public_file_out.write(this.publicKey.getEncoded());
            public_file_out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writePrivate(String chat) {
        try {
            File path = new File(context.getFilesDir() + "/" + chat);
            if (!path.exists()) path.mkdirs();
            File private_file = new File(context.getFilesDir() + "/" + chat + "private.key");
            if (!private_file.exists()) {
                if (!private_file.createNewFile()) return;
            }
            FileOutputStream private_file_out = new FileOutputStream(private_file);
            private_file_out.write(this.privateKey.getEncoded());
            private_file_out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeKeys(String chat) {
        writePublic(chat);
        writePrivate(chat);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void readPublic(String chat) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        File publicKeyFile = new File(context.getFilesDir() + "/" + chat + "public.key");
        byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        this.publicKey = keyFactory.generatePublic(publicKeySpec);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void readPrivate(String chat) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        File privateKeyFile = new File(context.getFilesDir() + "/" + chat + "private.key");
        byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        this.privateKey = keyFactory.generatePrivate(privateKeySpec);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void readKeys(String chat) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        readPublic(chat);
        readPrivate(chat);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String encrypt(String msg) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException {
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, this.publicKey);

        byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedMsgBytes = encryptCipher.doFinal(msgBytes);
        return Base64.getEncoder().encodeToString(encryptedMsgBytes);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String decrypt(String encodedMsg) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher decryptCipher = Cipher.getInstance("RSA");
        decryptCipher.init(Cipher.DECRYPT_MODE, this.privateKey);
        byte[] encryptedMsgBytes = Base64.getDecoder().decode(encodedMsg);
        byte[] decryptedMessageBytes = decryptCipher.doFinal(encryptedMsgBytes);
        return new String(decryptedMessageBytes, StandardCharsets.UTF_8);
    }

    public KeyPair getKeys() {
        return keys;
    }

    public void setKeys(KeyPair keys) {
        this.keys = keys;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getStrPublicKey() {
        return Base64.getEncoder().encodeToString(this.publicKey.getEncoded());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey));
        this.publicKey = keyFactory.generatePublic(publicKeySpec);
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getStrPrivateKey() {
        return Base64.getEncoder().encodeToString(this.privateKey.getEncoded());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setPrivateKey(String privateKey
    ) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
        this.privateKey = keyFactory.generatePrivate(privateKeySpec);
    }
}
