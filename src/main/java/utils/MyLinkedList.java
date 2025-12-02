package utils;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public class MyLinkedList<T> implements Iterable<T> {

    private class Node {

        T data;
        Node next;
        Node prev;

        Node(T data) {
            this.data = data;
        }
    }

    private Node head;
    private Node tail;
    private int size;

    public MyLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    // Convert List to MyLinkedList
    public static <E> MyLinkedList<E> fromJavaList(java.util.List<E> javaList) {
        MyLinkedList<E> newList = new MyLinkedList<>();
        for (E item : javaList) {
            newList.add(item);
        }
        return newList;
    }

    @SafeVarargs
    public static <E> MyLinkedList<E> of(E... elements) {
        MyLinkedList<E> list = new MyLinkedList<>();
        for (E element : elements) {
            list.add(element);
        }
        return list;
    }

    // #region Management Methods
    public void add(T element) {
        Node newNode = new Node(element);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        size++;
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }

    public boolean remove(T element) {
        Node current = head;
        while (current != null) {
            if (current.data.equals(element)) {
                unlink(current);
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        T data = current.data;
        unlink(current);
        return data;
    }

    private void unlink(Node node) {
        Node prev = node.prev;
        Node next = node.next;

        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
            node.prev = null;
        }

        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }

        node.data = null;
        size--;
    }

    public boolean contains(T element) {
        Node current = head;
        while (current != null) {
            if (current.data.equals(element)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        Node current = head;
        while (current != null) {
            Node next = current.next;
            current.data = null;
            current.next = null;
            current.prev = null;
            current = next;
        }
        head = tail = null;
        size = 0;
    }
    // #endregion Management Methods

    // #region Iterarion And Foreach Support
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T data = current.data;
                current = current.next;
                return data;
            }
        };
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        Node current = head;
        while (current != null) {
            action.accept(current.data);
            current = current.next;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Node current = head;
        while (current != null) {
            sb.append(current.data);
            if (current.next != null) {
                sb.append(", ");
            }
            current = current.next;
        }
        sb.append("]");
        return sb.toString();
    }
    // #endregion Iterarion And Foreach Support
}
