import java.util.concurrent.ThreadLocalRandom;

public class EncryptionLibrary {

    public static void encryptionShamir() {
        long P = ThreadLocalRandom.current().
                nextLong(Integer.MAX_VALUE >> 16, Integer.MAX_VALUE >> 2); // P is big prime number
        while (!CryptographicLibrary.isPrime(++P)) ;
        System.out.println("P: " + P);

        // CA * DA mod (P - 1) == 1
        long CA = ThreadLocalRandom.current().nextLong(1000); // Взаимнопростое с Р - 1
        while (CryptographicLibrary.generalizedEuclidAlgorithm(++CA, P - 1)[0] != 1);
        long DA = CryptographicLibrary.generalizedEuclidAlgorithm(P - 1, CA)[2]; // Инверсия = CA * DA mod P - 1 = 1 // m(-k) + cd = gcd(m, c) // Ищем число d
        if (DA < 0) DA += P - 1;
        if (CA * DA % (P - 1) != 1) {
            System.err.println("Не получилось сгенерировать CA & DA");
            return;
        }

        // CB * DB mod (P - 1) == 1
        long CB = ThreadLocalRandom.current().nextLong(1000);
        while (CryptographicLibrary.generalizedEuclidAlgorithm(++CB, P - 1)[0] != 1);
        long DB = CryptographicLibrary.generalizedEuclidAlgorithm(P - 1, CB)[2];
        if (DB < 0) DB += P - 1;
        if (CB * DB % (P - 1) != 1) {
            System.err.println("Не получилось сгенерировать CB & DB");
            return;
        }
        //System.out.println(CA + " : " + DA);
        //System.out.println(CB + " : " + DB);

        long m = ThreadLocalRandom.current().nextLong(P); // m < P
        // Когда будем делать файл, будем разбивать по 1 Байту или по P - 1 бит

        long x1 = CryptographicLibrary.fastExponentiationModulo(m, CA, P);
        long x2 = CryptographicLibrary.fastExponentiationModulo(x1, CB, P);
        long x3 = CryptographicLibrary.fastExponentiationModulo(x2, DA, P);
        long x4 = CryptographicLibrary.fastExponentiationModulo(x3, DB, P);

        System.out.println("Source: " + m);
        System.out.println("Result: " + x4);
    }

    public static void encryptionElgamal() {

    }

    public static void encryptionRSA() {

    }

    public static void encryptionVernam() {

    }

    public static void main(String[] args) {
        encryptionShamir();

    }
}
