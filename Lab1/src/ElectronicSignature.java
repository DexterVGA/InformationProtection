import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ElectronicSignature {
    public static class RSAPair {
        public byte[] h;
        public long[] s;

        public RSAPair(byte[] h, long[] s) {
            this.h = h;
            this.s = s;
        }
    }

    public static void RSA(byte[] message) {
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
            return;
        }
        long c = CryptographicLibrary.generalizedEuclidAlgorithm(Fi, d)[2]; // Инверсия cd mod Fi = 1
        if (c < 0) c += Fi;

        // PUBLIC: N, d
        // PUBLIC key: d
        // PRIVATE key: c

        // Alice

        final MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA3-256"); // returns 32 bytes array
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return;
        }
        byte[] hA = digest.digest(message);
        int[] intHA = byteArrToIntArr(hA); // 1 < h < P
        //System.out.println("SIZE: " + hA.length);

        long[] s = new long[intHA.length];
        for (int i = 0; i < s.length; i++) {
            s[i] = CryptographicLibrary.fastExponentiationModulo(intHA[i], c, N);
        }
        //message[0] = 77;
        //return new RSAPair(message, s);

        // Alice -> Bob <m, s>

        // Bob

        byte[] hB = digest.digest(message);
        int[] intHB = byteArrToIntArr(hB); // 1 < h < P
        long[] e = new long[s.length];
        for (int i = 0; i < s.length; i++) {
            e[i] = CryptographicLibrary.fastExponentiationModulo(s[i], d, N);
        }

        for (int i = 0; i < s.length; i++) {
            if (e[i] != intHB[i]) {
                System.err.println("RSA: Invalid signature!");
                return;
            }
        }
        System.out.println("RSA: Valid signature.");
    }

    public static int[] byteArrToIntArr(byte[] bArr) {
        int[] iArr = new int[bArr.length];
        for (int i = 0; i < iArr.length; i++) {
            iArr[i] = bArr[i] + 129;
        }
        return iArr;
    }

    public static byte[] fileToByteArray(String filename) {
        List<Byte> resultList = new ArrayList<>();
        try (FileInputStream fileInputStream = new FileInputStream(filename)) {
            int currentByte;
            while ((currentByte = fileInputStream.read()) != -1) {
                resultList.add((byte) currentByte);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(resultList);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bos.toByteArray();
    }

    public static void Elgamal(byte[] message) {
        long[] arr = CryptographicLibrary.generateGeneralData();
        long P = arr[0]; // Безопасное простое число
        long g = arr[1]; // Первообразный корень по модулю P
        long x = ThreadLocalRandom.current().nextLong(2, P); // 1 < x < P
        long y = CryptographicLibrary.fastExponentiationModulo(g, x, P);

        // PUBLIC: P, g, y

        // Alice

        final MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA3-256"); // returns 32 bytes array
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return;
        }
        byte[] hA = digest.digest(message);
        int [] intHA = byteArrToIntArr(hA);
        long k = ThreadLocalRandom.current().nextLong(1, P / 4);
        while (CryptographicLibrary.generalizedEuclidAlgorithm(++k, P - 1)[0] != 1);
        if (k >= P - 1) {
            System.err.println("Не получилось сгенерировать число k.");
            return;
        }
        long r = CryptographicLibrary.fastExponentiationModulo(g, k, P);
        long[] u = new long[intHA.length];
        for (int i = 0; i < u.length; i++) {
            u[i] = (intHA[i] - x * r) % (P - 1);
        }

        long inverseK = CryptographicLibrary.generalizedEuclidAlgorithm(P - 1, k)[2]; // kk^-1 mod (P - 1) = 1
        long[] s = new long[u.length];
        for (int i = 0; i < s.length; i++) {
            s[i] = (inverseK * u[i]) % (P - 1);
        }

        // Alice -> Bob <m, r, s>

        byte[] hB = digest.digest(message);
        int[] intHB = byteArrToIntArr(hB);
        long[] leftArr = new long[s.length];
        for (int i = 0; i < leftArr.length; i++) {
            leftArr[i] = (CryptographicLibrary.fastExponentiationModulo(y, r, P) * CryptographicLibrary.fastExponentiationModulo(r, s[i], P)) % P;
        }

        long[] rightArr = new long[intHB.length];
        for (int i = 0; i < rightArr.length; i++) {
            rightArr[i] = CryptographicLibrary.fastExponentiationModulo(g, intHB[i], P);
        }

        for (int i = 0; i < intHB.length; i++) {
            if (leftArr[i] != rightArr[i]) {
                System.err.println("Elgamal: Invalid signature!");
                return;
            }
        }
        System.out.println("Elgamal: Valid signature.");
    }

    public static void GOST() {
        long Q = 1L << 16; // 16 bit
        while (!CryptographicLibrary.isPrime(++Q)) ;

        long P;
        for (int i = 1; i <= 15; i++) {
            long num = i * Q + 1;
            if (CryptographicLibrary.isPrime(num) && num >= (1 << 30) && num < Integer.MAX_VALUE) {
                P = num;
                break;
            }
        }
        P = 1L << 30; // 31 bit
        while (!CryptographicLibrary.isPrime(++P)) ;



    }

    public static void main(String[] args) {
        String inFileName = "R://Hello.txt";
        //String outFileName = "R://Hello2.txt";
        byte[] fileInBytes = fileToByteArray(inFileName);
        ElectronicSignature.RSA(fileInBytes);
        Elgamal(fileInBytes);


        /*StringBuilder stringBuilder = new StringBuilder();
        for (char currentChar : passedMessage) {
            stringBuilder.append(currentChar);
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(outFileName)) {
            byte[] buffer = stringBuilder.toString().getBytes();
            fileOutputStream.write(buffer, 0, buffer.length);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }*/
    }
}
