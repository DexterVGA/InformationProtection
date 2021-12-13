package lab;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ThreadLocalRandom;

public class Client {

    private Server server;
    private long N;
    private long R;
    public long NUMBER;

    public Client(Server someServer) {
        this.server = someServer;
        this.N = server.N;
    }

    public long[] makeVote() {
        // Task 1
        int rnd = ThreadLocalRandom.current().nextInt(1, 1 << 15);
        int v = ThreadLocalRandom.current().nextInt(0, 3); // 0 1 2
        long n = rnd | ((long) v << 32); // first 32 bit = rnd; second 32 bit = v; v_rnd;
        this.NUMBER = n;
        //System.out.println(rnd);
        System.out.println("Пользователь выбрал вариант: " + v);
        //System.out.println(n);
        System.out.println("Сформировали число n...");

        // Task2
        long r = ThreadLocalRandom.current().nextLong(11, Integer.MAX_VALUE >> 16);
        while (CryptographicLibrary.generalizedEuclidAlgorithm(++r, N)[0] != 1) ;
        this.R = r;

        // Task 3
        final MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA3-256"); // returns 32 bytes array
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        byte[] h = digest.digest(longToBytes(n));
        int[] intH = byteArrToIntArr(h); // 1 < h < N
        System.out.println("Вычислили хэш-функцию от числа n...");

        //Task 4
        long[] inverseH = new long[intH.length];
        for (int i = 0; i < inverseH.length; i++) {
            inverseH[i] = (intH[i] * CryptographicLibrary.fastExponentiationModulo(r, server.D, N)) % N;
        }
        System.out.println("Передали по защищённому каналу от пользователя серверу хэш с чертой...");

        return inverseH;
    }

    // Task 6
    public long[] calculateSignature(long[] inverseS) {
        long inverseR = CryptographicLibrary.fastExponentiationModulo(R, -1, N);
        long[] s = new long[inverseS.length];
        for (int i = 0; i < s.length; i++) {
            s[i] = (inverseS[i] * inverseR) % N;
        }
        System.out.println("Пользователь вычислил подпись для своего бюллетеня...");
        System.out.println("Пользователь отправил на сервер подписанный бюллетень <n, s> по анонимному каналу связи...");
        return s;
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
}
