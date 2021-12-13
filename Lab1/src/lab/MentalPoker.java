package lab;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MentalPoker {

    public static void PlayTheGame(int cardsCount, int playersCount) {
        cardsCount -= 5; // Cards on table
        if (cardsCount / 2 < playersCount) {
            System.err.println("So many players for this cards count!");
            return;
        }
        long[] cards = new long[cardsCount];
        for (int i = 0; i < cardsCount; i++) cards[i] = i + 2;
        System.out.println("Начальная колода карт: " +  Arrays.toString(cards));

        long P = CryptographicLibrary.generateGeneralData()[0];
        System.out.println("P: " + P);

        long[][] players = new long[playersCount][4]; // C, D, K1, K2
        for (int i = 0; i < playersCount; i++) {
            long C = ThreadLocalRandom.current().nextLong(1000) + 1; // Взаимнопростое с Р - 1
            while (CryptographicLibrary.generalizedEuclidAlgorithm(++C, P - 1)[0] != 1) ;
            long D = CryptographicLibrary.generalizedEuclidAlgorithm(P - 1, C)[2]; // Инверсия = C * D mod P - 1 = 1 // m(-k) + cd = gcd(m, c) // Ищем число d
            if (D < 0) D += P - 1;
            if (C * D % (P - 1) != 1) {
                System.err.println("Не получилось сгенерировать C и D");
                return;
            }
            players[i][0] = C;
            players[i][1] = D;
        }
        System.out.println("\nКаждый игрок генерирует секретный C и открытый D ключи...");
        for (int i = 0; i < playersCount; i++)
            System.out.println("Игрок #" + i + " сгенерировал параметры C, D: " + players[i][0] + ", " + players[i][1]);

        // Мешаем и кодируем
        System.out.println("\nКаждый игрок зашифровывает и перемешивает всю колоду...");
        for (int i = 0; i < playersCount; i++) {
            shuffleArray(cards);
            for (int j = 0; j < cardsCount; j++) {
                cards[j] = CryptographicLibrary.fastExponentiationModulo(cards[j], players[i][0], P);
            }
            System.out.println("Колода после перемешивания игроком #" + i + ": " + Arrays.toString(cards));
        }

        // Раздаём карты
        System.out.println("\nРаздача карт...");
        int counter = 0;
        for (int i = 0; i < playersCount; i++) {
            players[i][2] = cards[counter++];
            players[i][3] = cards[counter++];
            System.out.println("Игрок #" + i + " получил 2 зашифрованные карты: " + players[i][2] + " & " + players[i][3]);
        }

        // Декодируем карты
        System.out.println("\nКаждый игрок расшифровывает свои карты...");
        for (int i = 0; i < playersCount; i++) {
            for (int j = 0; j < playersCount; j++) {
                if (j != i) {
                    players[i][2] = CryptographicLibrary.fastExponentiationModulo(players[i][2], players[j][1], P);
                    players[i][3] = CryptographicLibrary.fastExponentiationModulo(players[i][3], players[j][1], P);
                }
            }
            players[i][2] = CryptographicLibrary.fastExponentiationModulo(players[i][2], players[i][1], P);
            players[i][3] = CryptographicLibrary.fastExponentiationModulo(players[i][3], players[i][1], P);
            System.out.println("Игрок #" + i + " расшивровал 2 карты: " + players[i][2] + " & " + players[i][3]);
        }
    }

    public static void shuffleArray(long[] arr) {
        Random rnd = ThreadLocalRandom.current();
        for (int i = arr.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            long temp = arr[index];
            arr[index] = arr[i];
            arr[i] = temp;
        }
    }

    public static void main(String[] args) {
        PlayTheGame(52, 3);
    }
}
