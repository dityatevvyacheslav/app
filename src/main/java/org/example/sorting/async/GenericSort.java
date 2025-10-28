package org.example.sorting.async;

import org.example.sorting.SortStrategy;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GenericSort<T extends Comparable<? super T>> extends SortStrategy<T> {
    List<T> sortAsync(List<T> list, Comparator<T> comparator);

    CompletableFuture<List<T>> sort(List<T> list);

}