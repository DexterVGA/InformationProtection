import java.util.Scanner;

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
        int k = m; // m = k = sqrt(p) (+ 1) ?
        if (m * k <= p) {
            System.err.println("Need another mk!");
        }

        // (a^i * y) mod p
        int[] arrM = new int[m];
        for (int i = 0; i < m; i++) {
            arrM[i] = ((int)Math.pow(a, i) * y) % p;
        }

        // a^j mod p
        int[] arrK = new int[k];
        for (int i = 1; i <= k; i++) {
            arrK[i - 1] = (int)Math.pow(a, i * m) % p;
        }

        //TODO Сортируем массивы с сохранением исходных индексов
        //TODO Находим одинаковые элементы
        //TODO x = i * m - j
    }

    public static void main(String[] args) {

    }
}
