package org.example.sorting;

public class InsertionSort<T extends Comparable<? super T>> {
    public void sort(T[] array) {
        if (array == null || array.length <= 1) {
            return;
        }
        int n = array.length;

        for (int i = 1; i < n; i++) {
            T key = array[i];
            int j = i - 1;

            while (j >= 0 && array[j].compareTo(key) > 0) {
                array[j + 1] = array[j];
                j = j - 1;
            }
            array[j + 1] = key;

        }
    }

    public void sort(int[] array) {
        if (array == null || array.length <= 1) {
            return;
        }
        int n = array.length;

        for (int i = 1; i < n; i++) {
            int key = array[i];
            int j = i - 1;

            while (j >= 0 && array[j] > key) {
                array[j + 1] = array[j];
                j = j - 1;
            }
            array[j + 1] = key;

        }
    }
}
