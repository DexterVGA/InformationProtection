package archive;

import java.util.Arrays;
import java.util.Scanner;

public class EuclidAlgorithm {
    /**
     * ax + by = gcd(a, b)
     */
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Enter number \"a\": ");
        int a = in.nextInt();
        System.out.print("Enter number \"b\": ");
        int b = in.nextInt();

        int answer = euclid(a, b);

        System.out.println("Answer: " + answer);
    }

    public static int euclid(int a, int b) {
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

    public static void arrayInitialisation(int a, int b, int[] arrU, int[] arrV) {
        arrU[0] = a;
        arrU[1] = 1;
        arrU[2] = 0;

        arrV[0] = b;
        arrV[1] = 0;
        arrV[2] = 1;
    }
}
