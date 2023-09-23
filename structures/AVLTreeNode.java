package uk.ac.warwick.cs126.structures;

import java.util.Comparator;

public class AVLTreeNode<E> {

    private E value;
    private AVLTreeNode<E> left;
    private AVLTreeNode<E> right;
    private int height=0;
    public AVLTreeF<E> subtree=null;
    public AVLTreeNode(E val) {
        value = val;
        left = null;
        right = null;
    }

    public E getValue() {
        return value;
    }

    public AVLTreeNode<E> getLeft() {
        return left;
    }

    public AVLTreeNode<E> getRight() {
        return right;
    }

    public int getHeight() {
        return height;
    }

    public void setValue(E v) {
        value = v;
    }

    public void setLeft(AVLTreeNode<E> p) {
        left = p;
    }

    public void setRight(AVLTreeNode<E> p) {
        right = p;
    }

    public void setHeight(int h) {
        height = h;
    }

    public boolean isEmptySubtree(){
        return subtree == null || subtree.getSize() == 0;
    }
    //A list that defines priority of elements- that is different from the way they are sorted
    public AVLTreeF<E> priority=null;//used in the Subtrees to show which elements have priority
}
