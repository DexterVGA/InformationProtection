import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class CryptographicLibrary {
    /**
     * a^x mod p = y
     */
    public static long fastExponentiationModulo(int a, int x, int p) {
        int n = log2(x) + 1; // count of iterations

        String binaryString = Integer.toString(x, 2); // приводим число в двоичную форму
        int[] binaryArr = new int[n];
        for (int i = 0; i < binaryArr.length; i++) {
            binaryArr[i] = Integer.parseInt(binaryString.substring(n-i-1, n-i)); // записываем в массив в обратном порядке
        }

        int[] array = new int[n];
        array[0] = a;
        int answer = (int) Math.pow(array[0], binaryArr[0]);
        for (int i = 1; i < array.length; i++) { // в цикле прохидимся по алгоритму
            array[i] = (int) Math.pow(array[i - 1], 2) % p;
            answer *= (int) Math.pow(array[i], binaryArr[i]); // сразу же перемножаем числа для ответа
        }

        return answer % p;
    }

    private static int log2(int x) {
        return (int) (Math.log(x) / Math.log(2));
    }

    /**
     * ax + by = gcd(a, b)
     */
    public static long generalizedEuclidAlgorithm(int a, int b) {
        int[] arrU = new int[3];
        int[] arrV = new int[3];
        int[] arrBuffer = new int[3];
        arrayInitialisation(a, b, arrU, arrV);
        int q;

        while (arrV[0] != 0) {
            q = arrU[0] / arrV[0];
            //System.out.println("q: " + q);
            arrBuffer[0] = arrU[0] % arrV[0];
            arrBuffer[1] = arrU[1] - q * arrV[1];
            arrBuffer[2] = arrU[2] - q * arrV[2];
            arrU = Arrays.copyOf(arrV, arrV.length);
            arrV = Arrays.copyOf(arrBuffer, arrBuffer.length);
            //System.out.println(Arrays.toString(arrU));
            //System.out.println(Arrays.toString(arrV));
            //System.out.println();
        }

        //System.out.println("Answer is: " + arrU[0] + "\nx = " + arrU[1] + "\ny = " + arrU[2]);

        return arrU[0];
    }

    private static void arrayInitialisation(int a, int b, int[] arrU, int[] arrV) {
        arrU[0] = a;
        arrU[1] = 1;
        arrU[2] = 0;

        arrV[0] = b;
        arrV[1] = 0;
        arrV[2] = 1;
    }

    // **********************************************************************************
    public static void diffieHellman() { //TODO data type
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

    /**
     * a^x mod p = y
     * Find X
     * Input: a, p, y
     */
    public static void babyStepGiantStep() { //TODO data type
        Scanner in = new Scanner(System.in);
        System.out.print("Enter number \"a\": ");
        int a = in.nextInt();
        System.out.print("Enter number \"p\": ");
        int p = in.nextInt();
        System.out.print("Enter number \"y\": ");
        int y = in.nextInt();
        in.close();

        int m = (int)Math.sqrt(p) + 1; // mk > p
        int k = m; // Можно оставить только переменную М для оптимизации
        if (m * k <= p) {
            System.err.println("Need another mk!");
        }

        // (a^j * y) mod p
        // 0 <= j <= m - 1
        int[] rowM = new int[m];
        for (int j = 0; j < m; j++) {
            rowM[j] = ((int)Math.pow(a, j) * y) % p;
        }

        // a^im mod p
        // 1 <= i <= k
        int[] rowK = new int[k];
        for (int i = 1; i <= k; i++) {
            // rowK[i - 1] = (int)Math.pow(a, i * m) % p; // rowK is shifted 1 element to the left TODO use our method
            rowK[i - 1] = FastExponentiation.exponentiation(a, i * m, p);
        }

        // Ищем одинаковые элементы
        // a^im = a^j * y
        int answer = -1;
        Map<Integer, Integer> mapM = new TreeMap<>();
        for (int j = 0; j < m; j++) {
            mapM.put(rowM[j], j); // rowM starts with (a^0 * y), rowK starts with (a^1m)
        }
        // 0 <= j < m
        // 1 <= i <= k
        for (int i = 0; i < k; i++) {
            if (mapM.containsKey(rowK[i])) {
                answer = (i + 1) * m - mapM.get(rowK[i]); // x = i * m - j
                System.out.println("j: " + mapM.get(rowK[i]) + ", i: " + (i + 1));
                break;
            }
        }

        //Answer: x = i * m - j
        if (answer == -1) {
            System.err.println("Cannot find a answer!");
            return;
        }
        System.out.println("Answer: " + answer);
    }

    public static void main(String[] args) {

    }
}
