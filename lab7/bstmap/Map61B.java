package bstmap;

import java.util.Set;

/* Your implementation BSTMap should implement this interface. To do so,
 * append "implements Map61B<K,V>" to the end of your "public class..."
 * declaration, though you can use other formal type parameters if you'd like.
 */
/* 你的 BSTMap 实现应当实现这个接口。
 * 为此，请在你的类定义中添加 "implements Map61B<K,V>"，
 * 例如："public class BSTMap<K, V> implements Map61B<K, V>"。
 * 不过你也可以使用其他的类型参数名。
 */
public interface Map61B<K, V> extends Iterable<K> {

    /** Removes all of the mappings from this map. */
    /** 移除该 Map 中的所有键值对。*/
    void clear();

    /* Returns true if this map contains a mapping for the specified key. */
    /* 如果该 Map 中包含指定键的映射，返回 true。 */
    boolean containsKey(K key);

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    /* 返回指定键对应的值，如果该 Map 中不包含该键的映射，则返回 null。 */
    V get(K key);

    /* Returns the number of key-value mappings in this map. */
    int size();

    /* Associates the specified value with the specified key in this map. */
    void put(K key, V value);

    /* Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException. */
    /* 返回包含所有键的 Set 视图。不要求在 Lab 7 中实现。
     * 如果你不实现这个方法，应抛出 UnsupportedOperationException 异常。
     */
    Set<K> keySet();

    /* Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException. */
    /* 如果存在，移除指定键对应的映射。
     * 不要求在 Lab 7 中实现。如果你不实现这个方法，应抛出 UnsupportedOperationException 异常。
     */
    V remove(K key);

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    /* 仅当该键当前映射到指定值时，移除这个键对应的映射。
     * 不要求在 Lab 7 中实现。如果你不实现这个方法，应抛出 UnsupportedOperationException 异常。
     */
    V remove(K key, V value);

}
