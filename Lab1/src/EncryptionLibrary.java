import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class EncryptionLibrary {

    public static void inputEncryptionShamirData() {
        //String inFileName = "R://2.gif";
        String inFileName = "R://temp.txt";
        String outFileName = "R://newTemp";
        List<Character> resultOfEncryption = new ArrayList<>();

        try (FileInputStream fileInputStream = new FileInputStream(inFileName)) {
            //System.out.printf("File size: %d bytes \n", fileInputStream.available());
            int currentByte; // 0-255
            while ((currentByte = fileInputStream.read()) != -1) {
                resultOfEncryption.add((char)encryptionShamir(currentByte));
                //System.out.println((char)currentByte);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (char currentChar : resultOfEncryption) {
            stringBuilder.append(currentChar);
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(outFileName)) {
            byte[] buffer = stringBuilder.toString().getBytes();
            fileOutputStream.write(buffer, 0, buffer.length);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static long encryptionShamir(long message) {
        long P = ThreadLocalRandom.current().
                nextLong(Integer.MAX_VALUE >> 16, Integer.MAX_VALUE >> 2); // P is big prime number
        while (!CryptographicLibrary.isPrime(++P)) ;
        //System.out.println("P: " + P);

        // CA * DA mod (P - 1) == 1
        long CA = ThreadLocalRandom.current().nextLong(1000); // Взаимнопростое с Р - 1
        while (CryptographicLibrary.generalizedEuclidAlgorithm(++CA, P - 1)[0] != 1) ;
        long DA = CryptographicLibrary.generalizedEuclidAlgorithm(P - 1, CA)[2]; // Инверсия = CA * DA mod P - 1 = 1 // m(-k) + cd = gcd(m, c) // Ищем число d
        if (DA < 0) DA += P - 1;
        if (CA * DA % (P - 1) != 1) {
            System.err.println("Не получилось сгенерировать CA & DA");
            return -1;
        }

        // CB * DB mod (P - 1) == 1
        long CB = ThreadLocalRandom.current().nextLong(1000);
        while (CryptographicLibrary.generalizedEuclidAlgorithm(++CB, P - 1)[0] != 1) ;
        long DB = CryptographicLibrary.generalizedEuclidAlgorithm(P - 1, CB)[2];
        if (DB < 0) DB += P - 1;
        if (CB * DB % (P - 1) != 1) {
            System.err.println("Не получилось сгенерировать CB & DB");
            return -1;
        }
        //System.out.println(CA + " : " + DA);
        //System.out.println(CB + " : " + DB);

        //long m = ThreadLocalRandom.current().nextLong(P); // m < P
        // Когда будем делать файл, будем разбивать по 1 Байту или по P - 1 бит

        long x1 = CryptographicLibrary.fastExponentiationModulo(message, CA, P);
        long x2 = CryptographicLibrary.fastExponentiationModulo(x1, CB, P);
        long x3 = CryptographicLibrary.fastExponentiationModulo(x2, DA, P);
        long x4 = CryptographicLibrary.fastExponentiationModulo(x3, DB, P);

        //System.out.println("Source: " + message);
        //System.out.println("Result: " + x4);
        return x4;
    }

    public static void encryptionElgamal() {

    }

    public static void encryptionRSA() {

    }

    public static void encryptionVernam() {

    }

    public static void main(String[] args) {
        //encryptionShamir();
        inputEncryptionShamirData();


    }
}
