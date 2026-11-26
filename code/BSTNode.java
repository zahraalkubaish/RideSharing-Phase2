/** A node in the generic BST. */
public class BSTNode<K extends Comparable<K>, T> {
    K key;
    T data;
    BSTNode<K,T> left, right;
    public BSTNode(K key, T data) {
        this.key = key;
        this.data = data;
        left = right = null;
    }
}
