package ch.arons.jbench.io.crypto;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class CryptoSpeedTest {
    private static final String FILE_NAME = "testFile.dat";
    private static final String ENCRYPTED_FILE_NAME = "encryptedFile.dat";
    private static final String DECRYPTED_FILE_NAME = "decryptedFile.dat";
    private static final int FILE_SIZE_MB = 200; // 200 MB
    private static final String ALG_CRYPT = "AES"; 
    
    
    private static void printHelp() {
        System.err.println("jbench cr");
        System.err.println("options:");
        System.err.println(" filesize=200 (mb)");
    }
    
    public static void main(String[] args) {
        
        int filesize = FILE_SIZE_MB;
        
        for (int i = 0; i < args.length; i++) {
            if ( "-h".equals(args[i]) || "--help".equals(args[i]) ) {
                printHelp();
                System.exit(0);
            }
            
            if ( args[i].startsWith("filesize=") ) {
                filesize = Integer.parseInt( args[i].replace("filesize=", "") );
            } 
            
        }
        
        // Step 1: Generate a 200MB file
        long startTime = System.currentTimeMillis();
        try {
            generateTestFile(FILE_NAME, filesize);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("Time to generate 1G file: " + elapsedTime + " ms");
        
        // Step 2: Calculate SHA-256 of the file
        startTime = System.currentTimeMillis();
        String sha256;
        try {
            sha256 = calculateSHA256(FILE_NAME);
            elapsedTime = System.currentTimeMillis() - startTime;
            System.out.println("SHA-256: " + sha256);
            System.out.println("Time to calculate SHA-256: " + elapsedTime + " ms");
        } catch (NoSuchAlgorithmException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
        
        SecretKey secretKey;
        try {
            secretKey = generateSecretKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to generate key.");
        }
        
        // Step 3: Encrypt the file
        try {
            startTime = System.currentTimeMillis();
            encryptFile(FILE_NAME, ENCRYPTED_FILE_NAME, secretKey);
            elapsedTime = System.currentTimeMillis() - startTime;
            System.out.println("Time to encrypt the file: " + elapsedTime + " ms");
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       
        
        // Step 4: Decrypt the file
        try {
            startTime = System.currentTimeMillis();
            decryptFile(ENCRYPTED_FILE_NAME, DECRYPTED_FILE_NAME, secretKey);
            elapsedTime = System.currentTimeMillis() - startTime;
            System.out.println("Time to decrypt the file: " + elapsedTime + " ms");
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
                | BadPaddingException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    // Generate a file with dummy data of the specified size in MB
    private static void generateTestFile(String fileName, int sizeMB) throws FileNotFoundException, IOException {
        byte[] data = new byte[1024 * 1024]; // 1MB buffer
        Arrays.fill(data, (byte) 0xFF); // Fill with dummy data
        
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            for (int i = 0; i < sizeMB; i++) {
                fos.write(data);
            }
        }
    }
    
    // Calculate the SHA-256 hash of the file
    private static String calculateSHA256(String fileName) throws NoSuchAlgorithmException, FileNotFoundException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (FileInputStream fis = new FileInputStream(new File(fileName))) {
            byte[] byteArray = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesRead);
            }
        }
        byte[] hashBytes = digest.digest();
        
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    // Generate a secret  key for encryption
    private static SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALG_CRYPT);
        keyGen.init(256); // AES-256
        return keyGen.generateKey();
    }
    
    // Encrypt the file using encryption
    private static void encryptFile(String inputFile, String outputFile, SecretKey secretKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(ALG_CRYPT);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        
        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            byte[] inputBytes = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(inputBytes)) != -1) {
                byte[] outputBytes = cipher.update(inputBytes, 0, bytesRead);
                if (outputBytes != null) {
                    fos.write(outputBytes);
                }
            }
            byte[] outputBytes = cipher.doFinal();
            if (outputBytes != null) {
                fos.write(outputBytes);
            }
        }
    }
    
    // Decrypt the encrypted file using  decryption
    private static void decryptFile(String inputFile, String outputFile, SecretKey secretKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(ALG_CRYPT);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        
        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            byte[] inputBytes = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(inputBytes)) != -1) {
                byte[] outputBytes = cipher.update(inputBytes, 0, bytesRead);
                if (outputBytes != null) {
                    fos.write(outputBytes);
                }
            }
            byte[] outputBytes = cipher.doFinal();
            if (outputBytes != null) {
                fos.write(outputBytes);
            }
        }
    }
}
