package org.example.sorting.async;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class RadixSort<T extends Comparable<? super T>> implements GenericSort<T>, AutoCloseable {

    private final ExecutorService executor;

    public RadixSort(int threadCount) {
        this.executor = Executors.newFixedThreadPool(threadCount);
    }

    @Override
    public List<T> sortAsync(List<T> list, Comparator<T> comparator) {
        List<T> copy = new ArrayList<>(list);
        copy.sort(comparator);
        return copy;
    }

    @Override
    public CompletableFuture<List<T>> sort(List<T> list) {
        return CompletableFuture.supplyAsync(() -> {
            if (list.isEmpty()) return new ArrayList<>(list);
            if (!(list.get(0) instanceof Integer))
                throw new IllegalArgumentException("RadixSort поддерживает только Integer");

            List<Integer> numbers = list.stream()
                    .map(e -> (Integer) e)
                    .collect(Collectors.toList());

            int max = Collections.max(numbers);
            for (int exp = 1; max / exp > 0; exp *= 10)
                countingSort(numbers, exp);

            @SuppressWarnings("unchecked")
            List<T> result = (List<T>) new ArrayList<>(numbers);
            return result;
        }, executor);
    }

    private void countingSort(List<Integer> numbers, int exp) {
        int n = numbers.size();
        int[] output = new int[n];
        int[] count = new int[10];
        Arrays.fill(count, 0);

        for (int num : numbers)
            count[(num / exp) % 10]++;

        for (int i = 1; i < 10; i++)
            count[i] += count[i - 1];

        for (int i = n - 1; i >= 0; i--) {
            int num = numbers.get(i);
            output[count[(num / exp) % 10] - 1] = num;
            count[(num / exp) % 10]--;
        }

        for (int i = 0; i < n; i++)
            numbers.set(i, output[i]);
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

    @Override
    public void sort(List<T> list, Comparator<T> comparator) {
        list.sort(comparator);
    }
}