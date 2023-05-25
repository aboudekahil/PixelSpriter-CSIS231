package com.abdelkader.utils;

import java.util.ArrayDeque;

public class FixedSizeStack<T> {

    private final ArrayDeque<T> deque;
    private final int           maxSize;

    public FixedSizeStack(int maxSize) {
        deque = new ArrayDeque<>(maxSize);
        this.maxSize = maxSize;
    }

    public void push(T element) {
        if (deque.size() == maxSize) {
            deque.removeFirst(); // remove the oldest element
        }
        deque.addLast(element);
    }

    public T pop() {
        if(!deque.isEmpty())
            return deque.removeLast();
        return null;
    }

    public T peek() {
        return deque.getLast();
    }

    public int size() {
        return deque.size();
    }

    public void clear() {
        deque.clear();
    }
}
