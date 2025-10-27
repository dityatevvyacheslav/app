package org.example.sorting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RadixSort implements GenericSort<Integer>, AutoCloseable {
    private final ExecutorService executor;

    public RadixSort(int threadCount) {
        this.executor = Executors.newFixedThreadPool(threadCount);
    }

    @Override
    public CompletableFuture<List<Integer>> sort(List<Integer> list) {
        return CompletableFuture.supplyAsync(() -> radixSort(list), executor);
    }

    private List<Integer> radixSort(List<Integer> list) {
        if (list.isEmpty()) return new ArrayList<>(list);
        int max = list.stream().max(Integer::compareTo).get();
        Integer[] array = list.toArray(new Integer[0]);

        int maxDigits = String.valueOf(max).length();

        for (int digit = 0; digit < maxDigits; digit++) {
            countSort(array, digit);
        }
        return Arrays.asList(array);
    }

    private void countSort(Integer[] array, int digit) {
        int n = array.length;
        Integer[] output = new Integer[n];
        int[] count = new int[10];
        Arrays.fill(count, 0);

        int exp = (int) Math.pow(10, digit);

        for (int i = 0; i < n; i++) {
            int index = (array[i] / exp) % 10;
            count[index]++;
        }

        for (int i = 1; i < 10; i++) {
            count[i] += count[i - 1];
        }

        for (int i = n - 1; i >= 0; i--) {
            int index = (array[i] / exp) % 10;
            output[count[index] - 1] = array[i];
            count[index]--;
        }
        System.arraycopy(output, 0, array, 0, n);
    }

    @Override
    public void close() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
