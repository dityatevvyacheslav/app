package org.example.sorting;

import java.util.Comparator;
import java.util.List;

public interface SortStrategy<T> {
    void sort(List<T> list, Comparator<T> comparator);
}
