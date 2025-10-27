package org.example.tests;

import org.example.sorting.BubbleSort;
import org.example.sorting.InsertionSort;
import org.example.sorting.async.MergeSort;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class InteractiveSortTester {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("    Тестирование алгоритмов сортировки");
        System.out.print("Введите количество чисел для сортировки: ");
        int arraySize = scanner.nextInt();
        scanner.close();

        if (arraySize <= 0) {
            System.out.println("Размер массива должна быть положительным числом!");
            return;
        }

        List<Integer> testData = generateRandomArray(arraySize);
        System.out.println("\nСгенерирован массив из " + arraySize + " элементов");

        testBubbleSort(new ArrayList<>(testData));
        testInsertionSort(new ArrayList<>(testData));
        testMergeSort(new ArrayList<>(testData));
    }

    private static List<Integer> generateRandomArray(int size) {
        List<Integer> array = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            array.add(random.nextInt(10000));
        }
        return array;
    }

    private static void testBubbleSort(List<Integer> data) {
        System.out.println("\n    Bubble Sort");
        if (data.size() > 10000) {
            System.out.println("Пропуск: Bubble Sort слишком медленный для больших массивов");
            return;
        }

        BubbleSort sorter = new BubbleSort();
        int[] array = data.stream().mapToInt(i -> i).toArray();

        long startTime = System.currentTimeMillis();
        sorter.sort(array);
        long endTime = System.currentTimeMillis();

        System.out.println("Время выполнения: " + (endTime - startTime) + " мс");
        System.out.println("Проверка сортировки: " + (isSorted(array) ? "УСПЕХ" : "ОШИБКА"));
    }

    private static void testInsertionSort(List<Integer> data) {
        System.out.println("\n    Insertion Sort");
        if (data.size() > 50000) {
            System.out.println("Пропуск: Insertion Sort слишком медленный для очень больших массивов");
            return;
        }

        InsertionSort sorter = new InsertionSort();
        int[] array = data.stream().mapToInt(i -> i).toArray();

        long startTime = System.currentTimeMillis();
        sorter.sort(array);
        long endTime = System.currentTimeMillis();

        System.out.println("Время выполнения: " + (endTime - startTime) + " мс");
        System.out.println("Проверка сортировки: " + (isSorted(array) ? "УСПЕХ" : "ОШИБКА"));
    }

    private static void testMergeSort(List<Integer> data) {
        System.out.println("\n    Merge Sort (многопоточный)");

        try (MergeSort<Integer> sorter = new MergeSort<>(4)) {
            long startTime = System.currentTimeMillis();

            CompletableFuture<List<Integer>> future = sorter.sort(data);
            List<Integer> result = future.get(1, TimeUnit.MINUTES);

            long endTime = System.currentTimeMillis();

            System.out.println("Время выполнения: " + (endTime - startTime) + " мс");
            System.out.println("Проверка сортировки: " + (isSorted(result) ? "УСПЕХ" : "ОШИБКА"));
        } catch (Exception e) {
            System.out.println("Ошибка при сортировке: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testSyncVersions(List<Integer> data) {
        System.out.println("\n=== Тестирование синхронных версий ===");

        testMergeSortSync(new ArrayList<>(data));
    }

    private static void testMergeSortSync(List<Integer> data) {
        System.out.println("\n    Merge Sort (синхронный)");

        try (MergeSort<Integer> sorter = new MergeSort<>(4)) {
            long startTime = System.currentTimeMillis();

            List<Integer> result = sorter.sortAsync(data, Comparator.naturalOrder());

            long endTime = System.currentTimeMillis();

            System.out.println("Время выполнения: " + (endTime - startTime) + " мс");
            System.out.println("Проверка сортировки: " + (isSorted(result) ? "УСПЕХ" : "ОШИБКА"));
        } catch (Exception e) {
            System.out.println("Ошибка при сортировке: " + e.getMessage());
        }
    }

    private static boolean isSorted(int[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] > array[i + 1]) {
                return false;
            }
        }
        return true;
    }

    private static boolean isSorted(List<Integer> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i) > list.get(i + 1)) {
                return false;
            }
        }
        return true;
    }
}