import java.util.Scanner;

public class FastExponentation {
    public static void main(String[] args) {
        int a, x, p;
        Scanner in = new Scanner(System.in);
        System.out.print("Input number a: ");
        a = in.nextInt();
        System.out.print("Input number x: ");
        x = in.nextInt();
        System.out.print("Input number p: ");
        p = in.nextInt();

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

        System.out.println("answer: " + answer % p);
    }

    public static int log2(int x) {
        return (int) (Math.log(x) / Math.log(2));
    }
}
