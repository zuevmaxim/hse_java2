package com.hse.java.treeSet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

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
         * are less than the element.
         */
        private Node<E> left;
        /**
         * Right child. All the elements in the right subtree
         * are greater than the element.
         */
        private Node<E> right;
        /**
         * Node's parent in the subtree.
         */
        private Node<E> parent;
        /**
         * Node's height from the bottom of the tree.
         */
        private int height = 1;

        /**
         * Constructs node with null left/right children.
         * @param element value to contain
         * @param parent node's parent in the tree
         */
        private Node(@NotNull E element, @Nullable Node<E> parent) {
            this.element = element;
            this.parent = parent;
        }

        private Node() { }

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
         * AVL left or right rotation.
         * @param isRight true then right rotation happens and left otherwise
         * @return the highest node after rotation
         */
        @NotNull
        private Node<E> rotate(boolean isRight) {
            Node<E> tmp = isRight ? left : right;
            var tmpSon = isRight ? tmp.right : tmp.left;
            changeSon(parent, this, tmp);
            setSon(this, tmpSon, isRight);
            setSon(tmp, this, !isRight);
            updateHeight();
            tmp.updateHeight();
            return tmp;
        }

        /**
         * AVL right rotation.
         * @return the highest node after rotation
         */
        @NotNull
        private Node<E> rotateRight() {
            return rotate(true);
        }

        /**
         * AVL left rotation.
         * @return the highest node after rotation
         */
        @NotNull
        private Node<E> rotateLeft() {
            return rotate(false);
        }

        /**
         * Set son to a node, and set node as a parent for the son.
         * @param parent node to set son
         * @param son new son
         * @param isLeft true iff the son should be a left son
         */
        private static <T> void setSon(@Nullable Node<T> parent, @Nullable Node<T> son, boolean isLeft) {
            if (parent != null) {
                if (isLeft) {
                    parent.left = son;
                } else {
                    parent.right = son;
                }
            }
            if (son != null) {
                son.parent = parent;
            }
        }

        /**
         * Change son to a new one. Used if it is unknown either newSon is left or right.
         * @param parent node to set new son
         * @param currentSon old son
         * @param newSon new son
         */
        private static <T> void changeSon(@Nullable Node<T> parent,
                                          @Nullable Node<T> currentSon, @Nullable Node<T> newSon) {
            if (parent != null) {
                setSon(parent, newSon, parent.left == currentSon);
            } else if (newSon != null) {
                newSon.parent = null;
            }
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
         * Needed after deletion or adding.
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
     * Made in order to avoid updating connection with
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
     * It is saved in order to have only one descending copy.
     * Helps avoid constructing new copies in descendingSet() method.
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
        treeId = new TreeId<>(null);
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
     * @return 0, if a == b, integer less than 0, if a < b,
     *  integer greater than 0, if a > b
     */
    @SuppressWarnings("unchecked")
    private int compare(@NotNull Object a, @NotNull E b) {
        if (treeId.comparator != null) {
            return treeId.comparator.compare((E) a, b);
        }
        return ((Comparable<? super E>) a).compareTo(b);
    }

    /**
     * Compare E elements for ordering.
     * It is different for descending copy
     */
    private int compareOrder(@NotNull E a, @NotNull E b) {
        return descendingOrder ? compare(b, a) : compare(a, b);
    }

    /**
     * Find node containing element equals to given one
     * or null if there is no such element in the set.
     * @param element value to find in the set
     */
    @Nullable
    private Node<E> find(@NotNull Object element) {
        var node = findNodeOrParent(element);
        if (node != null && compare(element, node.element) == 0) {
            return node;
        }
        return null;
    }

    /**
     * Find node containing element or the place where to add such element.
     * @param element element to find
     */
    @Nullable
    private Node<E> findNodeOrParent(@NotNull Object element) {
        var currentNode = getRoot();
        Node<E> parent = null;
        while (currentNode != null) {
            parent = currentNode;
            int result = compare(element, currentNode.element);
            if (result == 0) {
                return currentNode;
            }
            currentNode = result < 0 ? currentNode.left : currentNode.right;
        }
        return parent;
    }

    /**
     * Checks if element is included into the set.
     * @param object object to be checked for containment in this set
     * @throws ClassCastException if object cannot be cased to E
     * or compared with elements correctly
     * @return true if this set contains the specified element
     */
    @Override
    public boolean contains(@NotNull Object object) throws ClassCastException {
        if (getRoot() == null) {
            return false;
        }
        return find(object) != null;
    }

    /**
     * Add element to the tree.
     * @param element element to add
     * @return new root of the tree(could change after rotations)
     */
    @NotNull
    private Node<E> addNode(@NotNull E element) {
        var parent = findNodeOrParent(element);
        var newNode = new Node<>(element, parent);
        if (parent == null) {
            return newNode;
        }
        Node.setSon(parent, newNode, compare(element, parent.element) < 0);
        return newNode.cascadingBalance();
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
        setRoot(addNode(element));
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
     * Remove element from the set.
     * @param currentNode node to remove
     * @return new root of the subtree
     */
    @Nullable
    private Node<E> remove(@NotNull Node<E> currentNode) {
        if (currentNode.left != null && currentNode.right != null) {
            var next = nextNode(currentNode);
            //noinspection ConstantConditions
            currentNode.swapElements(next); // cannot be null as current node has right child
            currentNode = next;
        }

        var son = currentNode.left == null ? currentNode.right : currentNode.left;
        var currentNodeParent = currentNode.parent;
        Node.changeSon(currentNodeParent, currentNode, son);
        return currentNodeParent == null
                ? son
                : currentNodeParent.cascadingBalance();
    }

    /**
     * Remove object from the set.
     * @param object object to remove
     * @throws ClassCastException if object cannot be casted to E
     * @return true if there was such object
     */
    @Override
    public boolean remove(@NotNull Object object) throws ClassCastException {
        var node = find(object);
        if (node == null) {
            return false;
        }
        treeId.size--;
        treeId.version++;
        setRoot(remove(node));
        return true;
    }

    /**
     * Returns an iterator over the elements in this set in descending order.
     */
    @Override
    @NotNull
    public Iterator<E> descendingIterator() {
        return descendingSet().iterator();
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
     * Find first/last node.
     * @param isFirst is true if first node should be found
     */
    @Nullable
    private Node<E> limitNode(boolean isFirst) {
        Node<E> node = getRoot();
        Function<Integer, Boolean> validDestination = (result) -> isFirst ? result > 0 : result < 0;
        while (node != null) {
            if (node.left != null && validDestination.apply(compareOrder(node.element, node.left.element))) {
                node = node.left;
                continue;
            }
            if (node.right != null && validDestination.apply(compareOrder(node.element, node.right.element))) {
                node = node.right;
                continue;
            }
            return node;
        }
        return null;
    }

    /**
     * Find the node containing the lowest element in the set
     * or null if there is none.
     */
    @Nullable
    private Node<E> firstNode() {
        return limitNode(true);
    }

    /**
     * Find the node containing the lowest element in the set
     * or null if there is none.
     */
    @Nullable
    private Node<E> lastNode() {
        return limitNode(false);
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
     * Type of comparator.
     * GE (>=) greater or equal
     * GT (>)  greater than
     * LE (<=) less or equal
     * LT (<)  less than
     */
    private enum CompareType { GE, GT, LE, LT }

    /**
     * Find a bound of element in the subtree.
     * @param element element to analyse
     * @param currentNode root of the subtree
     * @param bound node to save bound
     * @param compareType which bound to find :
     *                    GE - smallest element greater or equal than given one
     *                    GT - smallest element greater than given one
     *                    LE - greatest element less or equal than given one
     *                    LT - greatest element less than given one
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
     * Find the greatest element less than given one.
     * @param e element to find bound
     * @return bound or null if none
     */
    @Override
    @Nullable
    public E lower(@NotNull E e) {
        Node<E> result = new Node<>();
        boundedFind(e, getRoot(), result, CompareType.LT);
        return result.element;
    }

    /**
     * Find the greatest element less or equal than given one.
     * @param e element to find bound
     * @return bound or null if none
     */
    @Override
    @Nullable
    public E floor(@NotNull E e) {
        Node<E> result = new Node<>();
        boundedFind(e, getRoot(), result, CompareType.LE);
        return result.element;
    }

    /**
     * Find the smallest element greater or equal than given one.
     * @param e element to find bound
     * @return bound or null if none
     */
    @Override
    @Nullable
    public E ceiling(@NotNull E e) {
        Node<E> result = new Node<>();
        boundedFind(e, getRoot(), result, CompareType.GE);
        return result.element;
    }

    /**
     * Find the smallest element greater than given one.
     * @param e element to find bound
     * @return bound or null if none
     */
    @Override
    @Nullable
    public E higher(@NotNull E e) {
        Node<E> result = new Node<>();
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
        private TreeIterator() {
            startVersion = treeId.version;
            nextNode = firstNode();
        }

        /**
         * Check if iterator is valid.
         * @throws ConcurrentModificationException if set was modified.
         */
        private void checkValidity() throws ConcurrentModificationException {
            if (startVersion != treeId.version) {
                throw new ConcurrentModificationException(
                        "Iterator is invalid because set was modified.");
            }
        }

        /**
         * Checks if there is next element in the set.
         * @throws ConcurrentModificationException if set was modified.
         */
        @Override
        public boolean hasNext() throws ConcurrentModificationException {
            checkValidity();
            return nextNode != null;
        }

        /**
         * Move iterator to the next element.
         * @throws ConcurrentModificationException if set was modified.
         * @throws NoSuchElementException if hasNext() == false
         * @return next element
         */
        @Override
        public E next() throws ConcurrentModificationException, NoSuchElementException {
            checkValidity();
            if (!hasNext()) {
                throw new NoSuchElementException("Next element is null.");
            }
            E next = nextNode.element;
            nextNode = descendingOrder ? previousNode(nextNode) : nextNode(nextNode);
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
