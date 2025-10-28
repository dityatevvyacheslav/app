package org.example.tests;

import org.example.sorting.SortContext;
import org.example.sorting.SortStrategy;
import org.example.searching.BinarySearch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class FeatureStrategyAndSearchTest {

    // --- Тесты для SortContext и SortStrategy ---

    private static class SimpleSort implements SortStrategy<Integer> {
        @Override
        public void sort(List<Integer> list, Comparator<Integer> comparator) {
            list.sort(comparator);
        }
    }

    @Test
    @DisplayName("Сортировка со стратегией — список отсортирован правильно")
    void testSortWithValidStrategy() {
        SortContext<Integer> context = new SortContext<>();
        context.setStrategy(new SimpleSort());

        List<Integer> numbers = Arrays.asList(5, 2, 9, 1);
        context.executeSort(numbers, Comparator.naturalOrder());

        assertEquals(Arrays.asList(1, 2, 5, 9), numbers);
    }

    @Test
    @DisplayName("Ошибка при попытке сортировки без выбранной стратегии")
    void testExecuteSortWithoutStrategyThrowsException() {
        SortContext<Integer> context = new SortContext<>();
        List<Integer> numbers = Arrays.asList(3, 2, 1);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> context.executeSort(numbers, Comparator.naturalOrder())
        );

        assertEquals("Сортировочная стратегия не выбрана!", exception.getMessage());
    }

    // --- Тесты для BinarySearch ---

    @Test
    @DisplayName("Бинарный поиск — элемент найден")
    void testBinarySearchFound() {
        List<Integer> arr = Arrays.asList(1, 3, 5, 7, 9);
        BinarySearch<Integer> bs = new BinarySearch<>();
        int result = bs.search(arr, 7, Comparator.naturalOrder());
        assertEquals(3, result);
    }

    @Test
    @DisplayName("Бинарный поиск — элемент не найден")
    void testBinarySearchNotFound() {
        List<Integer> arr = Arrays.asList(1, 3, 5, 7, 9);
        BinarySearch<Integer> bs = new BinarySearch<>();
        int result = bs.search(arr, 6, Comparator.naturalOrder());
        assertEquals(-1, result);
    }

    @Test
    @DisplayName("Бинарный поиск — пустой список")
    void testBinarySearchEmptyList() {
        List<Integer> arr = new ArrayList<>();
        BinarySearch<Integer> bs = new BinarySearch<>();
        int result = bs.search(arr, 1, Comparator.naturalOrder());
        assertEquals(-1, result);
    }
}