package org.example.sorting;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MergeSortNumericField<T> implements AutoCloseable {
    private final ExecutorService executor;
    private final int PARALLEL_THRESHOLD = 1000;
    private final String numericFieldName;

    public MergeSortNumericField(int threadCount, String numericFieldName) {
        this.executor = Executors.newFixedThreadPool(threadCount);
        this.numericFieldName = numericFieldName;
    }

    public CompletableFuture<List<T>> sort(List<T> list) {
        return sortWithEvenOddLogic(list);
    }

    private CompletableFuture<List<T>> standardSort(List<T> list) {
        if (list.size() > PARALLEL_THRESHOLD) {
            return parallelMergeSort(list);
        } else {
            return CompletableFuture.supplyAsync(() -> sequentialMergeSort(list), executor);
        }
    }

    public CompletableFuture<List<T>> sortWithEvenOddLogic(List<T> list) {
        List<T> evenElements = new ArrayList<>();
        List<Integer> evenIndices = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            T elem = list.get(i);
            if (isEven(elem)) {
                evenElements.add(elem);
                evenIndices.add(i);
            }
        }
        return standardSort(evenElements).thenApply(sortedEvens -> {
            List<T> result = new ArrayList<>(list);

            for (int i = 0; i < evenIndices.size(); i++) {
                int originalIndex = evenIndices.get(i);
                result.set(originalIndex, sortedEvens.get(i));
            }

            return result;
        });
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
            if (compare(left.get(i), right.get(j)) <= 0) {
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

    private int compare(T a, T b) {
        long valueA = getNumericValue(a);
        long valueB = getNumericValue(b);
        return Long.compare(valueA, valueB);
    }

    private boolean isEven(T obj) {
        try {
            Field field = obj.getClass().getDeclaredField(numericFieldName);
            field.setAccessible(true);
            Number value = (Number) field.get(obj);
            return value.longValue() % 2 == 0;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка доступа к полю: " + numericFieldName, e);
        }
    }

    private long getNumericValue(T obj) {
        try {
            Field field = obj.getClass().getDeclaredField(numericFieldName);
            field.setAccessible(true);
            Number value = (Number) field.get(obj);
            return value.longValue();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка доступа к полю: " + numericFieldName, e);
        }
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

    public int getThreadPoolSize() {
        return ((java.util.concurrent.ThreadPoolExecutor) executor).getPoolSize();
    }

    public boolean isShutdown() {
        return executor.isShutdown();
    }

    public List<T> sortSync(List<T> list) throws Exception {
        return sort(list).get();
    }

    public List<T> sortSync(List<T> list, long timeout, TimeUnit unit) throws Exception {
        return sort(list).get(timeout, unit);
    }
}

