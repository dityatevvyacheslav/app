package org.example.sorting.async;

import java.util.*;
import java.util.concurrent.*;

public class MergeSort<T extends Comparable<? super T>> implements GenericSort<T>, AutoCloseable {

    private static final int PARALLEL_THRESHOLD = 5000;
    private final ExecutorService executor;

    public MergeSort(int threadCount) {
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
        return CompletableFuture.supplyAsync(() -> parallelMergeSort(list), executor);
    }

    private List<T> parallelMergeSort(List<T> list) {
        if (list.size() <= 1) return new ArrayList<>(list);
        if (list.size() < PARALLEL_THRESHOLD) return sequentialMergeSort(list);

        int mid = list.size() / 2;
        List<T> left = new ArrayList<>(list.subList(0, mid));
        List<T> right = new ArrayList<>(list.subList(mid, list.size()));

        CompletableFuture<List<T>> leftFuture =
                CompletableFuture.supplyAsync(() -> parallelMergeSort(left), executor);
        CompletableFuture<List<T>> rightFuture =
                CompletableFuture.supplyAsync(() -> parallelMergeSort(right), executor);

        return leftFuture.thenCombine(rightFuture, this::merge).join();
    }

    private List<T> sequentialMergeSort(List<T> list) {
        if (list.size() <= 1) return new ArrayList<>(list);

        int mid = list.size() / 2;
        List<T> left = sequentialMergeSort(list.subList(0, mid));
        List<T> right = sequentialMergeSort(list.subList(mid, list.size()));

        return merge(left, right);
    }

    private List<T> merge(List<T> left, List<T> right) {
        List<T> merged = new ArrayList<>(left.size() + right.size());
        int i = 0, j = 0;

        while (i < left.size() && j < right.size()) {
            if (left.get(i).compareTo(right.get(j)) <= 0) merged.add(left.get(i++));
            else merged.add(right.get(j++));
        }

        while (i < left.size()) merged.add(left.get(i++));
        while (j < right.size()) merged.add(right.get(j++));

        return merged;
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