package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V>{
    private class BSTNode {
        K key;
        V value;
        BSTNode left, right;

        public BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
    private BSTNode root;
    private int size;
    private BSTNode putHelper(BSTNode node, K key, V value) {
        if(node == null) {
            size++;
            return new BSTNode(key, value);
        }
        int cmp = key.compareTo(node.key);
        if(cmp < 0 ) {
            node.left = putHelper(node.left, key, value);
        }
        else if(cmp > 0) {
            node.right = putHelper(node.right, key, value);
        }
        else {
            node.value = value;
        }
        return node;
    }
    private BSTNode remove(BSTNode node, K key) {
        if(node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if(cmp < 0 ) {
            node.left = remove(node.left, key);
        }
        else if(cmp > 0) {
            node.right = remove(node.right, key);
        }
        else {
            if(node.left == null) {
                return node.right;
            }
            if(node.right == null) {
                return node.left;
            }
            BSTNode successor = findMin(node.right);
            node.key = successor.key;
            node.value = successor.value;
            node.right = remove(node.right, successor.key);
        }
        return node;
    }

    private BSTNode findMin(BSTNode node) {
        if(node.left != null) {
            return node.left;
        }
        return node;
    }
    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        BSTNode cur = root;
        while(cur != null) {
            int cmp = key.compareTo(cur.key);
            if(cmp < 0) {
                cur = cur.left;
            }
            else if(cmp > 0) {
                cur = cur.right;
            }
            else {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(K key) {
        BSTNode cur = root;
        while(cur != null) {
            int cmp = key.compareTo(cur.key);
            if(cmp < 0) {
                cur = cur.left;
            }
            else if(cmp > 0) {
                cur = cur.right;
            }
            else {
                return cur.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        root = putHelper(root, key, value);

    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException("keySet() is not required for Lab 7");
    }

    @Override
    public V remove(K key) {
        V value = get(key);
        if(value != null) {
            size--;
        }
        remove(root, key);
        return value;
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException("remove is not supported in Lab 7");
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException("iterator() is not required for Lab 7.");
    }
}
