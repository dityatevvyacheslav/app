package org.example.searching;

import java.util.Comparator;
import java.util.List;

public class BinarySearch<T> {

    public int search(List<T> list, T target, Comparator<T> comparator) {
        int low = 0;
        int high = list.size() - 1;

        while (low <= high) {
            int mid = (low + high) / 2;
            T midValue = list.get(mid);
            int cmp = comparator.compare(midValue, target);

            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return -1;
    }
}