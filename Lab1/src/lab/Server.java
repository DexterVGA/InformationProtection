package lab;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ThreadLocalRandom;

public class Server {
    public long N;
    public long D;
    private long C;
    private boolean isBlankPassed = false;

    public void generateGeneralData() {
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

        this.N = N;
        this.D = d;
        this.C = c;
    }

    // Task 5
    public long[] makeSignature(long[] inverseH) {
        isBlankPassed = true;
        long[] inverseS = new long[inverseH.length];
        for (int i = 0; i < inverseS.length; i++) {
            inverseS[i] = CryptographicLibrary.fastExponentiationModulo(inverseH[i], this.C, this.N);
        }
        System.out.println("Сервер помечает, что выдал бюллетень и вычисляет s с чертой и отправляет пользователю...");
        return inverseS;
    }

    // Task 7
    public void validateBlank(long n, long[] s) {
        System.out.println("Сервер проверяет корректность бюллетеня...");
        final MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA3-256"); // returns 32 bytes array
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return;
        }
        byte[] h = digest.digest(longToBytes(n));
        int[] intH = byteArrToIntArr(h); // 1 < h < N
        //intH[0] = 777;

        for (int i = 0; i < intH.length; i++) {
            long temp = CryptographicLibrary.fastExponentiationModulo(s[i], this.D, this.N);
            if (intH[i] != temp) {
                System.err.println("Некорректный бюллетень!");
                return;
            }
        }
        System.out.println("Корректный бюллетень.\nПроголосовали за вариант №" + (n >> 32));
    }

    public byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    public static int[] byteArrToIntArr(byte[] bArr) {
        int[] iArr = new int[bArr.length];
        for (int i = 0; i < iArr.length; i++) {
            iArr[i] = bArr[i] + 129;
        }
        return iArr;
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.generateGeneralData();

        Client client = new Client(server);
        long[] inverseH = client.makeVote();
        long[] inverseS = server.makeSignature(inverseH);
        long[] s = client.calculateSignature(inverseS);
        server.validateBlank(client.NUMBER, s);
    }
}
