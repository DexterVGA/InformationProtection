package archive;

import java.math.BigInteger;

public class DiffieHellman {
    public static void main(String[] args) {
        int[] arr = generateGeneralData();
        int P = arr[0];
        int g = arr[1];

        int keyXa = 1 + (int) (Math.random() * (P - 1));
        int keyXb = 1 + (int) (Math.random() * (P - 1)); // 1 <= X < p
        int keyYa = FastExponentiation.exponentiation(g, keyXa, P);
        int keyYb = FastExponentiation.exponentiation(g, keyXb, P);

        int keyZab = FastExponentiation.exponentiation(keyYb, keyXa, P);
        int keyZba = FastExponentiation.exponentiation(keyYa, keyXb, P);

        if(keyZab != keyZba) {
            System.err.println("Error in calculation");
        }

        System.out.println("Answer: " + keyZab);
    }

    // Miller – Rabin algorithm
    public static boolean isPrime(int number) {
        BigInteger bigInt = BigInteger.valueOf(number);
        return bigInt.isProbablePrime(100);
    }

    /**
     * @returns Pair consist of number P and number g
     */
    public static int[] generateGeneralData() {
        // Specify min value of Q
        int Q = 13; // Q - Prime
        int P; // P = 2Q + 1
        // Specify min value of g
        int g = 3; // (1 < g < P − 1) && (g^Q mod P != 1)

        boolean isPrimeP = false;
        while (!isPrimeP) {
            isPrimeP = isPrime(2 * Q + 1);
            if (!isPrimeP) {
                boolean isPrimeQ = isPrime(++Q);
                while (!isPrimeQ) {
                    isPrimeQ = isPrime(++Q);
                }
            }
        }
        P = 2 * Q + 1;
        System.out.println("P && Q = " + P + ", " + Q);

        while (g < P - 1) {
            if (FastExponentiation.exponentiation(g, Q, P) != 1) {
                break;
            }
            g++;
        }
        if (g == P - 1) {
            System.err.println("Exceptional situation!!!");
        }
        System.out.println("g = " + g);

        return new int[] {P, g};
    }
}
