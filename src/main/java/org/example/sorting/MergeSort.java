package org.example.sorting;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MergeSort<T extends Comparable<? super T>> implements GenericSort<T>, AutoCloseable {
    private final ExecutorService executor;
    private final int PARALLEL_THRESHOLD = 1000;

    public MergeSort(int threadCount) {
        this.executor = Executors.newFixedThreadPool(threadCount);
    }

    @Override
    public CompletableFuture<List<T>> sort(List<T> list) {
        if (list.size() > PARALLEL_THRESHOLD) {
            return parallelMergeSort(list);
        } else {
            return CompletableFuture.supplyAsync(() -> sequentialMergeSort(list), executor);
        }
    }

    private List<T> sequentialMergeSort(List<T> list) {
        if (list.size() <= 1) {
            return new ArrayList<>(list);
        }

        int mid = list.size() / 2;
        List<T> left = sequentialMergeSort(new ArrayList<>(list.subList(0, mid)));
        List<T> right = sequentialMergeSort(new ArrayList<>(list.subList(mid, list.size())));

        return merge(left, right);
    }

    private CompletableFuture<List<T>> parallelMergeSort(List<T> list) {
        if (list.size() <= 1) {
            return CompletableFuture.completedFuture(new ArrayList<>(list));
        }

        int mid = list.size() / 2;
        List<T> left = new ArrayList<>(list.subList(0, mid));
        List<T> right = new ArrayList<>(list.subList(mid, list.size()));

        CompletableFuture<List<T>> leftFuture = list.size() > PARALLEL_THRESHOLD / 2 ?
                parallelMergeSort(left) :
                CompletableFuture.supplyAsync(() -> sequentialMergeSort(left), executor);

        CompletableFuture<List<T>> rightFuture = list.size() > PARALLEL_THRESHOLD / 2 ?
                parallelMergeSort(right) :
                CompletableFuture.supplyAsync(() -> sequentialMergeSort(right), executor);

        return leftFuture.thenCombineAsync(rightFuture, this::merge, executor);
    }

    private List<T> merge(List<T> left, List<T> right) {
        List<T> merged = new ArrayList<>();
        int i = 0, j = 0;

        while (i < left.size() && j < right.size()) {
            if (left.get(i).compareTo(right.get(j)) <= 0) {
                merged.add(left.get(i++));
            } else {
                merged.add(right.get(j++));
            }
        }

        while (i < left.size()) {
            merged.add(left.get(i++));
        }

        while (j < right.size()) {
            merged.add(right.get(j++));
        }

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
}