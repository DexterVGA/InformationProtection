import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class BabyStepGiantStep {
    /**
     * a^x mod p = y
     * Find X
     * Input: a, p, y
     */
    public static void meth1() {
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
            rowK[i - 1] = (int)Math.pow(a, i * m) % p; // rowK is shifted 1 element to the left TODO use our method
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
        meth1();
    }
}
