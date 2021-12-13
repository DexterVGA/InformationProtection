package rgz;

import java.util.concurrent.ThreadLocalRandom;
import lab.CryptographicLibrary;

public class Client {
    private Server server;
    private long N;
    private long S;
    private long R;

    public Client(Server someServer) {
        this.server = someServer;
        this.N = server.N;
    }

    public long generatePrivateKey() {
        this.S = ThreadLocalRandom.current().nextLong(1, N);
        long V = CryptographicLibrary.fastExponentiationModulo(S, 2, N);

        return V;
    }

    public long giveCertificate() {
        this.R = ThreadLocalRandom.current().nextLong(1, N);
        long X = CryptographicLibrary.fastExponentiationModulo(R, 2, N);

        return X;
    }

    public long calculateAnswer(long C) {
        long Y;
        if (C == 0) {
            Y = R;
        } else {
            Y = (R * S) % N;
        }
        /*System.out.println("R = " + R);
        System.out.println("S = " + S);*/

        return Y;
    }
}
