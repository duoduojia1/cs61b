package deque;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedListDeque<T> implements Deque<T>{
    private static class Node<N> {
        private final N item;
        private Node<N> pre;
        private Node<N> next;
        Node(N item, Node<N> pre, Node<N> next) {
            this.item = item;
            this.pre = pre;
            this.next = next;
        }

        @Override
        public String toString() {
            if(item == null){
                return "null";
            }
            return item.toString();
        }
    }
    private Node<T> head;
    private Node<T> tail;
    private int size;
    LinkedListDeque() {
        head = new Node<T>(null,null,null);
        tail = new Node<T>(null,null,null);
        size = 0;
    }
    @Override
    public void addFirst(T item) {
        // 这里已经把newnode的前后节点设置好了
        Node<T> newNode = new Node<>(item, head, head.next);

        if (size == 0) {
            // First element: set both head.next and tail to newNode
            head.next = newNode;
            tail = newNode;
        } else {
            // Insert before current first
            head.next.pre = newNode;
            head.next = newNode;
        }
        size++;
    }

    @Override
    public void addLast(T item) {
        /**
         * size != 0:
         * T->next
         * tail->next = T
         * T->pre = tail
         * tail = T
         * size ++
         */
        if( size == 0 ) {
            addFirst(item);
        }
        else{
            Node<T> new_node = new Node<T>(item,tail,tail.next);
            tail.next = new_node;
            tail = new_node;
            size++;
        }
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Iterator<T> iterator = iterator();
        while(iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }

    @Override
    public T removeFirst() {
        if( size == 0 ) {
            return null;
        }
        Node<T> firstnode = head.next;
        T removedItem = firstnode.item;
        if(size == 1) {
            head.next = null;
            tail = null;
        }
        else {
            head.next = firstnode.next;
            head.next.pre = head;
        }
        firstnode.next = null;
        firstnode.pre = null;
        size--;
        return removedItem;
    }

    @Override
    public T removeLast() {
        if( size == 0 ) {
            return null;
        }
        T removedItem = tail.item;
        if( size == 1) {
            head.next = null;
            tail = null;
        }
        else{
            tail = tail.pre;
            tail.next = null;
        }
        size--;
        return removedItem;
    }

    @Override
    public T get(int index) {
        Node<T> cur = head.next;
        for(int i = 0; i < size; i++) {
            if(index == i ) {
                return cur.item;
            }
            cur = cur.next;
        }
        return null;
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }
    private class LinkedListDequeIterator implements Iterator<T> {

        private  Node<T> cur;
        LinkedListDequeIterator() {
            cur = head.next;
        }
        @Override
        public boolean hasNext() {
            return cur != null;
        }

        @Override
        public T next() {
            if(!hasNext()) {
                throw new NoSuchElementException();
            }
            T item = cur.item;
            cur =cur.next;
            return item;
        }
    }
}
