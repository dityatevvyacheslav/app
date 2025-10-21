package org.example.sorting;

public class BubbleSort {
    public void sort(int[] array){
        if (array == null || array.length <= 1){
            return;
        }

        int n = array.length;
        boolean swapped;

        for (int i = 0; i < n - 1; i++){
            swapped = false;
            for (int j = 0; j < n - i - 1; j++){
                if (array[j] > array[j + 1]){
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    swapped = true;
                }
            }
            if (!swapped){
                break;
            }
        }
    }
}
