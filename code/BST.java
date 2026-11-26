/** Generic binary search tree, following the iterative lecture implementation. */
public class BST<K extends Comparable<K>, T> implements IBST<K,T> {
    private BSTNode<K,T> root, current;
    private int size;
    public BST() { root = current = null; size = 0; }
    public boolean empty() { return root == null; }
    public int size() { return size; }
    public T retrieve() { return current.data; }
    public boolean findKey(K tkey) {
        BSTNode<K,T> p = root, q = root;
        if (empty()) return false;
        while (p != null) {
            q = p;
            int cmp = tkey.compareTo(p.key);
            if (cmp == 0) { current = p; return true; }
            else if (cmp < 0) p = p.left;
            else p = p.right;
        }
        current = q;
        return false;
    }
    public int nbKeyComp(K key) {
        BSTNode<K,T> p = root;
        int sum = 0;
        while (p != null) {
            sum++;
            int cmp = key.compareTo(p.key);
            if (cmp == 0) return sum;
            if (cmp < 0) p = p.left;
            else p = p.right;
        }
        return sum;
    }
    public boolean insert(K k, T val) {
        if (k == null) throw new IllegalArgumentException("Null key");
        BSTNode<K,T> p, q = current;
        if (findKey(k)) { current = q; return false; }
        p = new BSTNode<K,T>(k, val);
        if (empty()) { root = current = p; }
        else {
            if (k.compareTo(current.key) < 0) current.left = p;
            else current.right = p;
            current = p;
        }
        size++;
        return true;
    }
    public boolean removeKey(K k) {
        BSTNode<K,T> p = root, q = null;
        boolean found = false;
        while (p != null && !found) {
            int res = k.compareTo(p.key);
            if (res < 0) { q = p; p = p.left; }
            else if (res > 0) { q = p; p = p.right; }
            else found = true;
        }
        if (found) {
            if (p.left != null && p.right != null) {
                BSTNode<K,T> min = p.right;
                q = p;
                while (min.left != null) { q = min; min = min.left; }
                p.key = min.key;
                p.data = min.data;
                deleteNode(min, q);
            } else deleteNode(p, q);
            size--;
            current = root;
            return true;
        }
        return false;
    }
    // Remove a node that has at most one child. Compare node references, not
    // keys: the successor's key may already have been copied to its parent.
    private void deleteNode(BSTNode<K,T> n, BSTNode<K,T> parent) {
        BSTNode<K,T> child;
        if (n.left != null) child = n.left;
        else child = n.right;
        if (parent == null) root = child;
        else if (parent.left == n) parent.left = child;
        else parent.right = child;
    }
    public LinkedList<T> inOrder() {
        LinkedList<T> temp = new LinkedList<T>();
        inOrder(root, temp);
        return temp;
    }
    private void inOrder(BSTNode<K,T> p, LinkedList<T> result) {
        if (p == null) return;
        inOrder(p.left, result);
        result.insert(p.data);
        inOrder(p.right, result);
    }
    public int height() { return height(root); }
    private int height(BSTNode<K,T> p) {
        if (p == null) return 0;
        int left = height(p.left), right = height(p.right);
        return Math.max(left, right) + 1;
    }
    public LinkedList<T> rangeSearch(K low, K high) {
        LinkedList<T> result = new LinkedList<T>();
        if (low == null || high == null || low.compareTo(high) > 0) return result;
        rangeSearch(root, low, high, result);
        return result;
    }
    private void rangeSearch(BSTNode<K,T> p, K low, K high, LinkedList<T> result) {
        if (p == null) return;
        int cmpLow = p.key.compareTo(low), cmpHigh = p.key.compareTo(high);
        if (cmpLow > 0) rangeSearch(p.left, low, high, result);
        if (cmpLow >= 0 && cmpHigh <= 0) result.insert(p.data);
        if (cmpHigh < 0) rangeSearch(p.right, low, high, result);
    }
}
