package com.hse.java.treeSet;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Realisation of set using balanced binary search tree.
 * @param <E>  the type of elements maintained by this set
 */
public class BSTSet<E> extends AbstractSet<E> implements MyTreeSet<E> {
    /**
     * Tree node.
     * @param <E> the type of elements maintained by this node
     */
    private static class Node<E> {
        /**
         * Element that node contains.
         */
        private E element;
        /**
         * Left child. All the elements in the left subtree
         * are less then the element.
         */
        private Node<E> left;
        /**
         * Right child. All the elements in the right subtree
         * are greater then the element.
         */
        private Node<E> right;
        /**
         * Node's parent in the subtree.
         */
        private Node<E> parent;
        /**
         * Node's height from the bottom of the tree.
         */
        private int height;

        /**
         * Constructs node with null left/right children.
         * @param element value to contain
         * @param parent node's parent in the tree
         */
        public Node(@Nullable E element, @Nullable Node<E> parent) {
            this.element = element;
            this.parent = parent;
            height = 1;
        }

        /**
         * Swap element fields in this and other nodes.
         * @param other other node to swap with
         */
        private void swapElements(@NotNull Node<E> other) {
            E tmp = other.element;
            other.element = element;
            element = tmp;
        }

        /**
         * Node height in the tree.
         * @return node height or 0 if node is null
         */
        private static int height(@Nullable Node<?> node) {
            return node == null ? 0 : node.height;
        }

        /**
         * Difference between left and right children height.
         */
        private int getLeftRightDiff() {
            return height(left) - height(right);
        }

        /**
         * Update node's height using children's height.
         */
        private void updateHeight() {
            height = 1 + Math.max(height(left), height(right));
        }

        /**
         * AVL right rotation.
         * @return the highest node after rotation
         */
        @NotNull
        private Node<E> rotateRight() {
            Node<E> tmp = left;
            tmp.parent = parent;

            left = tmp.right;
            if (tmp.right != null) {
                tmp.right.parent = this;
            }

            updateHeight();

            tmp.right = this;
            parent = tmp;
            tmp.updateHeight();

            if (tmp.parent != null) {
                if (tmp.parent.left == this) {
                    tmp.parent.left = tmp;
                } else {
                    tmp.parent.right = tmp;
                }
            }

            return tmp;
        }

        /**
         * AVL left rotation.
         * @return the highest node after rotation
         */
        @NotNull
        private Node<E> rotateLeft() {
            Node<E> tmp = right;
            tmp.parent = parent;

            right = tmp.left;
            if (tmp.left != null) {
                tmp.left.parent = this;
            }

            updateHeight();

            tmp.left = this;
            parent = tmp;
            tmp.updateHeight();

            if (tmp.parent != null) {
                if (tmp.parent.left == this) {
                    tmp.parent.left = tmp;
                } else {
                    tmp.parent.right = tmp;
                }
            }

            return tmp;
        }

        /**
         * AVL big right rotation.
         * @return the highest node after rotation
         */
        @NotNull
        private Node<E> bigRotateRight() {
            left = left.rotateLeft();
            return rotateRight();
        }

        /**
         * AVL big left rotation.
         * @return the highest node after rotation
         */
        @NotNull
        private Node<E> bigRotateLeft() {
            right = right.rotateRight();
            return rotateLeft();
        }

        /**
         * Checks if rotation is needed, exactly,
         * rotates if |height(left) - height(right)| > 1.
         * @return the highest node after rotation
         */
        @NotNull
        private Node<E> balance() {
            updateHeight();
            int balance = getLeftRightDiff();
            if (balance > 1) {
                return left.getLeftRightDiff() > 0
                        ? rotateRight()
                        : bigRotateRight();
            }
            if (balance < -1) {
                return right.getLeftRightDiff() > 0
                        ? bigRotateLeft()
                        : rotateLeft();
            }
            return this;
        }

        /**
         * Sequence of balancing up to the root.
         * Needed after deletion
         * @return the highest node after rotation(new root)
         */
        @NotNull
        private Node<E> cascadingBalance() {
            Node<E> tmp = this;
            while (tmp.parent != null) {
                tmp = tmp.balance().parent;
            }
            return tmp.balance();
        }
    }

    /**
     * Storage for tree's root, comparator and size.
     * Made in oder to avoid updating connection with
     * descending copy - it will be done automatically;
     * @param <E> node type that tree contains
     */
    private static class TreeId<E> {
        /**
         * AVL tree root.
         */
        private Node<E> root = null;
        /**
         * The number of elements in the set.
         */
        private int size = 0;
        /**
         * Comparator for navigating in the tree and comparing elements.
         */
        private final Comparator<? super E> comparator;

        /**
         * Tree version.
         * Equals to the number of modifications that were made with this tree.
         * Made for iterator invalidation.
         */
        private int version = 0;

        private TreeId(Comparator<? super E> comparator) {
            this.comparator = comparator;
        }
    }

    /**
     * Contains information about root, comparator and size.
     */
    private TreeId<E> treeId;

    private Node<E> getRoot() {
        return treeId.root;
    }

    private void setRoot(Node<E> root) {
        treeId.root = root;
    }

    /**
     * Descending set copy.
     * It is saved in order to update connection with it.
     */
    private BSTSet<E> descendingBSTSet;
    /**
     * True iff elements are sorted in a descending order.
     */
    private boolean descendingOrder = false;

    /**
     * Constructs empty BSTSet. Natural comparator is used.
     * If elements cannot be compared, ClassCastException can be thrown
     */
    public BSTSet() {
        treeId = new TreeId<>((@NotNull E a, @NotNull E b) ->
                ((Comparable<? super E>) a).compareTo(b));
    }

    /**
     * Constructs empty BSTSet with comparator.
     * @param comparator comparator used for comparing set elements.
     */
    public BSTSet(@NotNull Comparator<? super E> comparator) {
        treeId = new TreeId<>(comparator);
    }

    /**
     * Compare E elements using comparator if it is given.
     * Used for navigation in the tree.
     * @return 0, if a == b, integer less then 0, if a < b,
     *  integer greater then 0, if a > b
     */
    private int compare(@NotNull E a, @NotNull E b) {
        return treeId.comparator.compare(a, b);
    }

    /**
     * Compare E elements for ordering.
     * It is different for descending copy
     */
    private int compareOrder(@NotNull E a, @NotNull E b) {
        if (descendingOrder) {
            return treeId.comparator.compare(b, a);
        }
        return treeId.comparator.compare(a, b);
    }

    /**
     * Checks if element is included into the subtree.
     * @param element element to be checked for containment in the subtree
     * @param currentNode root of the subtree
     * @return true if this subtree contains the specified element
     */
    private boolean contains(@NotNull E element, @NotNull Node<E> currentNode) {
        int result = compare(element, currentNode.element);
        if (result == 0) {
            return true;
        }
        return result < 0
                ? currentNode.left != null && contains(element, currentNode.left)
                : currentNode.right != null && contains(element, currentNode.right);
    }

    /**
     * Checks if element is included into the set.
     * @param object object to be checked for containment in this set
     * @throws ClassCastException if object cannot be cased to E
     * @return true if this set contains the specified element
     */
    @Override
    public boolean contains(@NotNull Object object) throws ClassCastException {
        if (getRoot() == null) {
            return false;
        }
        return contains((E) object, getRoot());
    }

    /**
     * Add element to the subtree.
     * @param element element to add
     * @param currentNode root of the subtree
     * @param parent parent of currentNode
     * @return new root of the subtree(could change after rotations)
     */
    @Contract("_, null, _ -> new")
    @NotNull
    private Node<E> add(@NotNull E element,
                        @Nullable Node<E> currentNode, @Nullable Node<E> parent) {
        if (currentNode == null) {
            return new Node<>(element, parent);
        }
        int result = compare(element, currentNode.element);
        if (result < 0) {
            currentNode.left = add(element, currentNode.left, currentNode);
        } else {
            currentNode.right = add(element, currentNode.right, currentNode);
        }
        return currentNode.balance();
    }

    /**
     * Add element to the set.
     * @param element element to add
     * @return true if there was no such element
     */
    @Override
    public boolean add(@NotNull E element) {
        if (contains(element)) {
            return false;
        }
        treeId.size++;
        treeId.version++;
        setRoot(add(element, getRoot(), null));
        return true;
    }

    /**
     * Find next element after currentNode.
     * @param currentNode node to start from
     * @return node containing next element or null if there is none
     */
    @Nullable
    private Node<E> nextNode(@NotNull Node<E> currentNode) {
        if (currentNode.right != null) {
            currentNode = currentNode.right;
            while (currentNode.left != null) {
                currentNode = currentNode.left;
            }
        } else {
            while (currentNode.parent != null
                    && currentNode.parent.right == currentNode) {
                currentNode = currentNode.parent;
            }
            currentNode = currentNode.parent;
        }
        return currentNode;
    }

    /**
     * Find previous element before currentNode.
     * @param currentNode node to start from
     * @return node containing previous element or null if there is none
     */
    @Nullable
    private Node<E> previousNode(@NotNull Node<E> currentNode) {
        if (currentNode.left != null) {
            currentNode = currentNode.left;
            while (currentNode.right != null) {
                currentNode = currentNode.right;
            }
        } else {
            while (currentNode.parent != null
                    && currentNode.parent.left == currentNode) {
                currentNode = currentNode.parent;
            }
            currentNode = currentNode.parent;
        }
        return currentNode;
    }

    /**
     * Remove element from the subtree.
     * @param element element to remove
     * @param currentNode root of the subtree
     * @return new root of the subtree
     */
    @Nullable
    private Node<E> remove(@NotNull E element, @NotNull Node<E> currentNode) {
        int result = compare(element, currentNode.element);
        if (result < 0) {
            return remove(element, currentNode.left);
        }
        if (result > 0) {
            return remove(element, currentNode.right);
        }
        if (currentNode.left == null || currentNode.right == null) {
            Node<E> son = null;
            if (currentNode.left == null) {
                son = currentNode.right;
            }
            if (currentNode.right == null) {
                son = currentNode.left;
            }
            Node<E> currentNodeParent = currentNode.parent;
            if (currentNodeParent != null) {
                if (currentNodeParent.left == currentNode) {
                    currentNodeParent.left = son;
                } else {
                    currentNodeParent.right = son;
                }
            }
            if (son != null) {
                son.parent = currentNodeParent;
            }
            return currentNodeParent == null
                    ? son
                    : currentNodeParent.cascadingBalance();
        }

        Node<E> next = nextNode(currentNode);
        currentNode.swapElements(next); // cannot be null as current node has right child
        return remove(next.element, next);
    }

    /**
     * Remove object from the set.
     * @param object object to remove
     * @throws ClassCastException if object cannot be casted to E
     * @return true if there was such object
     */
    @Override
    public boolean remove(@NotNull Object object) throws ClassCastException {
        if (!contains(object)) {
            return false;
        }
        treeId.size--;
        treeId.version++;
        setRoot(remove((E) object, getRoot()));
        return true;
    }

    /**
     * Returns an iterator over the elements in this set in descending order.
     */
    @Override
    @NotNull
    public Iterator<E> descendingIterator() {
        return new TreeDescendingIterator();
    }

    /**
     * Returns a reverse order view of the elements contained in this set.
     */
    @Override
    @NotNull
    public MyTreeSet<E> descendingSet() {
        if (descendingBSTSet == null) {
            descendingBSTSet = new BSTSet<>();
            descendingBSTSet.descendingBSTSet = this;
            descendingBSTSet.descendingOrder = !descendingOrder;
            descendingBSTSet.treeId = treeId;
        }
        return descendingBSTSet;
    }

    /**
     * Find the node containing the lowest element in the set
     * or null if there is none.
     */
    @Nullable
    private Node<E> firstNode() {
        Node<E> node = getRoot();
        while (node != null) {
            if (node.left != null && compareOrder(node.element, node.left.element) > 0) {
                node = node.left;
                continue;
            }
            if (node.right != null && compareOrder(node.element, node.right.element) > 0) {
                node = node.right;
                continue;
            }
            return node;
        }
        return null;
    }

    /**
     * Find the lowest element in the set
     * or null if there is none.
     */
    @Override
    @Nullable
    public E first() {
        Node<E> node = firstNode();
        if (node == null) {
            return null;
        }
        return node.element;
    }

    /**
     * Find the highest element in the set
     * or null if there is none.
     */
    @Override
    @Nullable
    public E last() {
        Node<E> node = lastNode();
        if (node == null) {
            return null;
        }
        return node.element;
    }

    /**
     * Find the node containing the lowest element in the set
     * or null if there is none.
     */
    @Nullable
    private Node<E>  lastNode() {
        Node<E> node = getRoot();
        while (node != null) {
            if (node.left != null && compareOrder(node.element, node.left.element) < 0) {
                node = node.left;
                continue;
            }
            if (node.right != null && compareOrder(node.element, node.right.element) < 0) {
                node = node.right;
                continue;
            }
            return node;
        }
        return null;
    }

    /**
     * Type of comparator.
     * GE (>=) greater or equal
     * GT (>)  greater then
     * LE (<=) less or equal
     * LT (<)  less then
     */
    private enum CompareType { GE, GT, LE, LT }

    /**
     * Find a bound of element in the subtree.
     * @param element element to analyse
     * @param currentNode root of the subtree
     * @param bound node to save bound
     * @param compareType which bound to find :
     *                    GE - smallest element greater or equal then given one
     *                    GT - smallest element greater then given one
     *                    LE - greatest element less or equal then given one
     *                    LT - greatest element less then given one
     */
    private void boundedFind(@NotNull E element, @Nullable Node<E> currentNode,
                             @NotNull Node<E> bound, @NotNull CompareType compareType) {
        if (currentNode == null) {
            return;
        }
        int result = compareOrder(element, currentNode.element);
        if (result < 0) {
            if (compareType == CompareType.GT || compareType == CompareType.GE) {
                bound.element = currentNode.element;
            }
            boundedFind(element, currentNode.left, bound, compareType);
        } else if (result > 0) {
            if (compareType == CompareType.LT || compareType == CompareType.LE) {
                bound.element = currentNode.element;
            }
            boundedFind(element, currentNode.right, bound, compareType);
        } else {
            if (compareType == CompareType.LT) {
                boundedFind(element, currentNode.left, bound, compareType);
            } else if (compareType == CompareType.GT) {
                boundedFind(element, currentNode.right, bound, compareType);
            } else {
                bound.element = currentNode.element;
            }
        }
    }

    /**
     * Find the greatest element less then given one.
     * @param e element to find bound
     * @return bound or null if none
     */
    @Override
    @Nullable
    public E lower(@NotNull E e) {
        Node<E> result = new Node<>(null, null);
        boundedFind(e, getRoot(), result, CompareType.LT);
        return result.element;
    }

    /**
     * Find the greatest element less or equal then given one.
     * @param e element to find bound
     * @return bound or null if none
     */
    @Override
    @Nullable
    public E floor(@NotNull E e) {
        Node<E> result = new Node<>(null, null);
        boundedFind(e, getRoot(), result, CompareType.LE);
        return result.element;
    }

    /**
     * Find the smallest element greater or equal then given one.
     * @param e element to find bound
     * @return bound or null if none
     */
    @Override
    @Nullable
    public E ceiling(@NotNull E e) {
        Node<E> result = new Node<>(null, null);
        boundedFind(e, getRoot(), result, CompareType.GE);
        return result.element;
    }

    /**
     * Find the smallest element greater then given one.
     * @param e element to find bound
     * @return bound or null if none
     */
    @Override
    @Nullable
    public E higher(@NotNull E e) {
        Node<E> result = new Node<>(null, null);
        boundedFind(e, getRoot(), result, CompareType.GT);
        return result.element;
    }

    /**
     * Returns an iterator over the elements in this set in ascending order.
     */
    @Override
    @NotNull
    public Iterator<E> iterator() {
        return new TreeIterator();
    }

    /**
     * Tree iterator provides an ability to iterate in the BSTSet.
     */
    private class TreeIterator implements Iterator<E> {
        private Node<E> nextNode;
        /**
         * Tree version when the iterator was constructed.
         * Iterator is valid iff start version equals to current tree version.
         */
        private final int startVersion;

        /**
         * Construct iterator. Starts from the smallest element.
         */
        public TreeIterator() {
            startVersion = treeId.version;
            nextNode = firstNode();
        }

        /**
         * Check if iterator is valid.
         * @throws IllegalStateException if set was modified.
         */
        private void checkValidity() throws IllegalStateException {
            if (startVersion != treeId.version) {
                throw new IllegalStateException("Iterator is invalid because set was modified.");
            }
        }

        /**
         * Checks if there is next element in the set.
         * @throws IllegalStateException if set was modified.
         */
        @Override
        public boolean hasNext() throws IllegalStateException {
            checkValidity();
            return nextNode != null;
        }

        /**
         * Move iterator to the next element.
         * @throws IllegalStateException if set was modified.
         * @return next element
         */
        @Override
        public E next() throws IllegalStateException {
            checkValidity();
            E next = nextNode.element;
            nextNode = nextNode(nextNode);
            return next;
        }
    }

    /**
     * Tree iterator provides an ability to iterate
     * in the BSTSet in a reverse order.
     */
    private class TreeDescendingIterator implements Iterator<E> {
        private Node<E> nextNode;
        /**
         * Tree version when the iterator was constructed.
         * Iterator is valid iff start version equals to current tree version.
         */
        private final int startVersion;

        /**
         * Construct iterator. Starts from the greatest element.
         */
        public TreeDescendingIterator() {
            startVersion = treeId.version;
            nextNode = lastNode();
        }

        /**
         * Check if iterator is valid.
         * @throws IllegalStateException if set was modified.
         */
        private void checkValidity() throws IllegalStateException {
            if (startVersion != treeId.version) {
                throw new IllegalStateException("Iterator is invalid because set was modified.");
            }
        }

        /**
         * Checks if there is next element in the set.
         * @throws IllegalStateException if set was modified.
         */
        @Override
        public boolean hasNext() throws IllegalStateException {
            checkValidity();
            return nextNode != null;
        }

        /**
         * Move iterator to the next element.
         * @throws IllegalStateException if set was modified.
         * @return next element
         */
        @Override
        public E next() throws IllegalStateException {
            checkValidity();
            E next = nextNode.element;
            nextNode = previousNode(nextNode);
            return next;
        }
    }

    /**
     * Returns the number of elements in this set.
     */
    @Override
    public int size() {
        return treeId.size;
    }
}
