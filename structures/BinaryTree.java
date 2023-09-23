package uk.ac.warwick.cs126.structures;

import uk.ac.warwick.cs126.util.myCompare;

import java.util.Comparator;

public class BinaryTree<E> {
    BinaryTreeNode<E> root;
    public Comparator<E> sorter; //can only be assigned once
    public BinaryTree(Comparator<E> sorter) {
        root = null;
        this.sorter=sorter;
    }

    private void addToSubTree(BinaryTreeNode<E> n, E v) {
        if (n != null) // sanity check!
        {
            E nValue = n.getValue();
            if (sorter.compare(v,nValue) <= 0) {
                System.out.println("Adding " + v + " to left sub-tree of " + nValue);
                if (n.getLeft() == null)
                    n.setLeft(new BinaryTreeNode<>(v));
                else
                    addToSubTree(n.getLeft(), v);
            } else {
                System.out.println("Adding " + v + " to right sub-tree of " + nValue);
                if (n.getRight() == null)
                    n.setRight(new BinaryTreeNode<>(v));
                else
                    addToSubTree(n.getRight(), v);
            }
        }
    }

    public void add(E v) {
        if (root == null) {
            System.out.println("Adding " + v + " to root.");
            root = new BinaryTreeNode<>(v);
        } else
            addToSubTree(root, v);
    }

    private void inOrder(BinaryTreeNode<E> n) {
        if (n != null) {
            inOrder(n.getLeft());
            System.out.print(((Integer) n.getValue()).intValue() + " ");
            inOrder(n.getRight());
        }
    }

    // INCOMPLETE.
    private void preOrder(BinaryTreeNode<E> n) {
        // this method is to be completed...
        // return if the current node is empty
        if (n == null) {
            return;
        }

        // Display the data part of the root (or current node)
        System.out.print(((Integer) n.getValue()).intValue() + " ");
        // Traverse the left subtree
        preOrder(n.getLeft());

        // Traverse the right subtree
        preOrder(n.getRight());
    }

    // INCOMPLETE.
    private void postOrder(BinaryTreeNode<E> n) {
        // this method is to be completed...
        if (n != null) {
            postOrder(n.getRight());
            System.out.print(((Integer) n.getValue()).intValue() + " ");
            postOrder(n.getLeft());
        }
    }

    public void traversal() {
        System.out.println("Inorder traversal: ");
        inOrder(root);
        System.out.println();
        System.out.println("Postorder traversal: ");
        postOrder(root);
        System.out.println();
        System.out.println("Preorder traversal: ");
        preOrder(root);

        System.out.println();
    }
    public E find(E element){
        BinaryTreeNode<E> temp;
        temp=root;
        int result;
        //While the tree end has not been reached yet
        //and while the element has not been found
        while(temp.getValue()!=null && (result=sorter.compare(element,temp.getValue()))!=0){
            //move to the next tree position
            if(result>0)
                temp=temp.getRight();
            else temp=temp.getLeft();
        }
        return temp.getValue();
    }
}