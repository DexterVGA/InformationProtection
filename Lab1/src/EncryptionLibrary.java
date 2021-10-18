import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class EncryptionLibrary {

    public static void inputEncryptionShamirData() {
        //String inFileName = "R://1.jpg";
        String inFileName = "R://temp.txt";
        String outFileName = "R://2.txt";
        List<Character> resultOfEncryption = new ArrayList<>();

        try (FileInputStream fileInputStream = new FileInputStream(inFileName)) {
            //System.out.printf("File size: %d bytes \n", fileInputStream.available());
            int currentByte; // 0-255
            while ((currentByte = fileInputStream.read()) != -1) {
                resultOfEncryption.add((char) encryptionShamir(currentByte));
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

    public static long encryptionElgamal(long message) { // message < P
        long[] arr = CryptographicLibrary.generateGeneralData();
        long P = arr[0]; // Безопасное простое число
        long g = arr[1]; // Первообразный корень по модулю P
        long x = ThreadLocalRandom.current().nextLong(2, P); // 1 < x < P
        long y = CryptographicLibrary.fastExponentiationModulo(g, x, P);
        long k = ThreadLocalRandom.current().nextLong(2, P - 1); // 1 < k < P - 1
        long a = CryptographicLibrary.fastExponentiationModulo(g, k, P);
        long b = message * (CryptographicLibrary.fastExponentiationModulo(y, k, P)) % P;
        return b * (CryptographicLibrary.fastExponentiationModulo(a, P - 1 - x, P)) % P;
    }

    public static long encryptionRSA(long message) { // message < N
        long P = ThreadLocalRandom.current().
                nextLong(2 << 6, Integer.MAX_VALUE >> 17); // P is big prime number
        while (!CryptographicLibrary.isPrime(++P)) ;
        long Q = ThreadLocalRandom.current().
                nextLong(2 << 6, Integer.MAX_VALUE >> 17); // Q is big prime number
        while (!CryptographicLibrary.isPrime(++Q) || Q == P) ;

        long N = P * Q;
        long Fi = (P - 1) * (Q - 1);
        long d = ThreadLocalRandom.current().nextLong(11, Integer.MAX_VALUE >> 16);
        while (CryptographicLibrary.generalizedEuclidAlgorithm(++d, Fi)[0] != 1 && d < Fi) ;
        if (d == Fi) {
            System.err.println("Не получилось сгенерировать число d.");
            return -1;
        }
        long c = CryptographicLibrary.generalizedEuclidAlgorithm(Fi, d)[2]; // Инверсия cd mod Fi = 1
        if (c < 0) c += Fi;

        long e = CryptographicLibrary.fastExponentiationModulo(message, d, N);
        return CryptographicLibrary.fastExponentiationModulo(e, c, N);
    }

    public static void encryptionVernam() {

    }

    public static void main(String[] args) {
        //encryptionShamir();
        //inputEncryptionShamirData();
        //System.out.println(encryptionElgamal(2));
        System.out.println(encryptionRSA(12345));
    }
}
