import org.jetbrains.annotations.NotNull;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;

public class BSTSet<E> extends AbstractSet<E> implements MyTreeSet<E> {
    private static class Node<E> {
        private E element;
        private Node<E> left;
        private Node<E> right;
        private Node<E> parent;
        private int height;

        public Node(E element, Node<E> parent) {
            this.element = element;
            this.parent = parent;
            height = 1;
        }

        private void swapElements(Node<E> other) {
            E tmp = other.element;
            other.element = element;
            element = tmp;
        }

        private static int height(Node<?> node) {
            return node == null ? 0 : node.height;
        }

        private int getLeftRightDiff() {
            return height(left) - height(right);
        }

        private void updateHeight() {
            height = 1 + Math.max(height(left), height(right));
        }

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

        private Node<E> bigRotateRight() {
            left = left.rotateLeft();
            return rotateRight();
        }

        private Node<E> bigRotateLeft() {
            right = right.rotateRight();
            return rotateLeft();
        }

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

        private Node<E> cascadingBalance() {
            Node<E> tmp = this;
            while (tmp.parent != null) {
                tmp = tmp.balance().parent;
            }
            return tmp.balance();
        }
    }

    private Node<E> root = null;
    private int size = 0;
    private Comparator<? super E> comparator;

    public BSTSet() { }

    public BSTSet(@NotNull Comparator<? super E> comparator) {
        this.comparator = comparator;
    }

    private int compare(E a, E b) {
        if (comparator != null) {
            return comparator.compare(a, b);
        }
        return ((Comparable<? super E>)a).compareTo(b);
    }

    private boolean contains(@NotNull E element, Node<E> currentNode) {
        int result = compare(element, currentNode.element);
        if (result == 0) {
            return true;
        }
        return result < 0
                ? currentNode.left != null && contains(element, currentNode.left)
                : currentNode.right != null && contains(element, currentNode.right);
    }


    @Override
    public boolean contains(@NotNull Object object) {
        if (root == null) {
            return false;
        }
        return contains((E) object, root);
    }

    private Node<E> add(E element, Node<E> currentNode, Node<E> parent) {
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

    @Override
    public boolean add(@NotNull E element) {
        if (contains(element)) {
            return false;
        }
        size++;
        if (root == null) {
            root = new Node<>(element, null);
            return true;
        }
        root = add(element, root, null);
        return true;
    }

    private Node<E> next(@NotNull Node<E> currentNode) {
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

    private Node<E> previous(@NotNull Node<E> currentNode) {
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

    private Node<E> remove(E element, Node<E> currentNode) {
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

        Node<E> next = next(currentNode);
        currentNode.swapElements(next);
        return remove(next.element, next);
    }

    @Override
    public boolean remove(Object object) {
        if (!contains(object)) {
            return false;
        }
        --size;
        root = remove((E)object, root);
        return true;
    }


    @Override
    public Iterator<E> descendingIterator() {
        return null;
    }

    @Override
    public MyTreeSet<E> descendingSet() {
        return null;
    }

    @Override
    public E first() {
        return null;
    }

    @Override
    public E last() {
        return null;
    }

    @Override
    public E lower(E e) {
        return null;
    }

    @Override
    public E floor(E e) {
        return null;
    }

    @Override
    public E ceiling(E e) {
        return null;
    }

    @Override
    public E higher(E e) {
        return null;
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public int size() {
        return size;
    }
}
