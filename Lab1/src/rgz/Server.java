package rgz;

import java.util.concurrent.ThreadLocalRandom;
import lab.CryptographicLibrary;

public class Server {
    public long N;
    public long V;
    private long X;

    public void generateGeneralData() {
        long P = ThreadLocalRandom.current().
                nextLong(2 << 6, Integer.MAX_VALUE >> 17); // P is big prime number
        while (!CryptographicLibrary.isPrime(++P)) ;
        long Q = ThreadLocalRandom.current().
                nextLong(2 << 6, Integer.MAX_VALUE >> 17); // Q is big prime number
        while (!CryptographicLibrary.isPrime(++Q) || Q == P) ;

        this.N = P * Q;
    }

    public void validate(Client client) {
        boolean isValidate = true;
        for (int i = 0; i < 40; i++) {
            X = client.giveCertificate();
            long C = ThreadLocalRandom.current().nextLong(0, 2);
//            System.out.println("C = " + C);
            long Y = client.calculateAnswer(C);
            /*System.out.println("Y = " + Y);
            System.out.println("X = " + X);
            System.out.println("V = " + V);
            System.out.println("C = " + C);
            System.out.println("N = " + N);*/
            long VC;
            if (C == 0) {
                VC = 1;
            } else {
                VC = V;
            }
            if((Y * Y) % N != (X * VC) % N) {
                /*System.out.println((Y * Y) % N);
                System.out.println((X * newV) % N);*/
                isValidate = false;
                break;
            }
        }
        if(isValidate) {
            System.out.println("Валидация пройдена успешно!");
        } else {
            System.out.println("Валидация не пройдена!");
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.generateGeneralData();
        Client client = new Client(server);
        server.V = client.generatePrivateKey();
        server.validate(client);
    }
}
