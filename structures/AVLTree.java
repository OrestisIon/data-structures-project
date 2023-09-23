package uk.ac.warwick.cs126.structures;


import java.util.Comparator;
/**
 * @param <E>
 */
public class AVLTree<E>  {
    public AVLTreeNode<E> root;
    public Comparator<E> sorter; //can only be assigned once
    private int result;
    private int size;
    private MyArrayList<E> sortedList;
    private MyArrayList<E> toList;
    public AVLTree(Comparator<E> sorter) {
        root = null;
        this.sorter=sorter;
        size=0;
    }

    private void addToSubTree(AVLTreeNode<E> n, E v) {
        if (n != null) // sanity check!
        {
            E nValue = n.getValue();
            if (sorter.compare(v,nValue) <= 0) {
               // System.out.println("Adding " + v + " to left sub-tree of " + nValue);
                if (n.getLeft() == null)
                    n.setLeft(new AVLTreeNode<>(v));
                else
                    addToSubTree(n.getLeft(), v);
            } else {
              //  System.out.println("Adding " + v + " to right sub-tree of " + nValue);
                if (n.getRight() == null)
                    n.setRight(new AVLTreeNode<>(v));
                else
                    addToSubTree(n.getRight(), v);
            }
        }
    }

    public void add(E e){
        if (root == null) {
            //System.out.println("Adding " + v + " to root.");
            root = new AVLTreeNode<>(e);
        } else
            addToSubTree(root, e);
        balanceTree(e);
        size++;
    }
    void balanceTree(E e) {
        int result;
        MyArrayList<AVLTreeNode<E>> path = path(e);
        for (int i = path.size() - 1; i >= 0; i--) {
            AVLTreeNode<E> tempN = path.get(i);
            updateHeight(tempN);
            AVLTreeNode<E> tempParent;
            if (tempN != root)
                tempParent = path.get(i - 1);
            else {
                tempParent = null;
            }

            if ((result = balanceFactor(tempN)) == -2) {
                if (balanceFactor(tempN.getLeft()) <= 0) {
                    LL_Balance(tempN, tempParent);
                } else LR_Balance(tempN, tempParent);
            } else if (result == 2) {
                if (balanceFactor(tempN.getRight()) >= 0)
                    RR_Balance(tempN, tempParent);
                else
                    RL_Balance(tempN, tempParent);

            }

        }
    }

    /**
     * Updating the height of a specific node
     * @param n
     */
    private void updateHeight(AVLTreeNode<E> n){
        //if it's a leaf
        if(n.getLeft()==null && n.getRight()==null)
            n.setHeight(0);
            //node has no right subtree
        else if (n.getRight()==null)
            n.setHeight(1+n.getLeft().getHeight());
            //node has no left subtree
        else if (n.getLeft()==null)
            n.setHeight(1+n.getRight().getHeight());
        else {
            n.setHeight(1+Math.max(n.getLeft().getHeight(),n.getRight().getHeight()));
        }
    }

    /**
     * It has time complexity O(1) --->efficient
     * @param n
     * @return the Balance factor of the given node
     */
    private int balanceFactor(AVLTreeNode<E> n){
        if(n.getLeft()==null)
            return n.getHeight();
        if(n.getRight()==null)
            return -n.getHeight();
        return (n.getRight().getHeight()-n.getLeft().getHeight());
    }
    /**
     * Finds the path of an element in the tree.
     * @param e
     * @return the path of the element as an arraylist of nodes
     */
    public MyArrayList<AVLTreeNode<E>> path(E e) {
        int result;
        MyArrayList<AVLTreeNode<E>> list = new MyArrayList<>();
        AVLTreeNode<E> current = root; // Start from the root
        while (current != null) {
            list.add(current);
            if((result=sorter.compare(e,current.getValue())) < 0){
                current = current.getLeft();
            }
            else if (result > 0) {
                current = current.getRight();
            }
            else break;
        }
        return list;
    }

    /**
     *
     * @param e
     * @return true if element was found and deleted, o/w false
     */
    public E delete(E e){
        if(root==null)
            return null;
        AVLTreeNode<E> temp=root;
        AVLTreeNode<E> parent=null;
        E delete=null;
        while(temp!=null && (result=sorter.compare(e,temp.getValue()))!=0){
            parent=temp;
            if(result>0) {
                temp = temp.getRight();
            }
            else {
                temp=temp.getLeft();
            }
        }
        //if element not found anywhere
        if (temp==null) {
            return delete;
        }
        delete=temp.getValue();
        if(temp.getLeft()==null) {
            if (parent == null)
                // Connect the parent with the right child of the current node
                root = temp.getRight();
            else {
                if (sorter.compare(parent.getValue(),e) > 0)
                    parent.setLeft(temp.getRight());
                else
                    parent.setRight(temp.getRight());
                //balance tree
                balanceTree(parent.getValue());
            }
        }
        else{
            //if there is a left child
            AVLTreeNode<E> rightest=temp.getLeft();
            AVLTreeNode<E> parentRight=temp;
            //find the rightest nod
            while(rightest.getRight()!=null){
                parentRight=rightest;
                rightest=rightest.getRight();
            }
            temp.setValue(rightest.getValue());
            if(parentRight.getRight()==rightest)
                parentRight.setRight(rightest.getLeft());
            else parentRight.setLeft(rightest.getLeft());
            balanceTree(parentRight.getValue());
        }
        size--;
        return delete;
    }

    private void RR_Balance(AVLTreeNode<E> n1, AVLTreeNode<E> nparent) {
        AVLTreeNode<E> n2 = n1.getRight(); // A is right-heavy and B is right-heavy

        if (n1 == root) {
            root = n2;
        }
        else {
            if (nparent.getLeft() == n1)
                nparent.setLeft(n2);
            else
                nparent.setRight(n2);
        }
        n1.setRight(n2.getLeft());
        n2.setLeft(n1);
        updateHeight(n1);
        updateHeight(n2);
    }
    private void RL_Balance(AVLTreeNode<E> n1, AVLTreeNode<E> nparent) {
        // n1 is heavier left
        AVLTreeNode<E> n2 = n1.getRight();
        AVLTreeNode<E> n3=n2.getLeft();
        if (n1 == root) {
            root = n3;
        }
        else {
            if (nparent.getLeft() == n1)
                nparent.setLeft(n3);
            else
                nparent.setRight(n3);
        }

        n1.setRight(n3.getLeft());
        n2.setLeft(n3.getRight());
        n3.setLeft(n1);
        n3.setRight(n2);
        updateHeight(n1);
        updateHeight(n2);
        updateHeight(n3);
    }
    /** Balance LR  */
    private void LR_Balance(AVLTreeNode<E> n1, AVLTreeNode<E> nparent) {

        AVLTreeNode<E> n2 = n1.getLeft();
        // n2 is heavier right
        AVLTreeNode<E> n3 = n2.getRight();
        if (n1 == root) {
            root = n3;
        } else {
            if (nparent.getLeft() == n1)
                nparent.setLeft(n3);
            else
                nparent.setRight(n3);

        }
        n1.setLeft(n3.getRight());
        n2.setRight(n3.getLeft());
        n3.setLeft(n2);
        n3.setRight(n1);
        updateHeight(n1);
        updateHeight(n2);
        updateHeight(n3);
    }
    private void LL_Balance(AVLTreeNode<E> n1, AVLTreeNode<E> nparent){
        AVLTreeNode<E> n2=n1.getLeft();
        if(n1==root){
            root=n2;
        }
        else{
            if(nparent.getLeft()==n1)
                nparent.setLeft(n2);
            else
                nparent.setRight(n2);
        }
        n1.setLeft(n2.getRight());
        n2.setRight(n1);
        updateHeight(n1);
        updateHeight(n2);
    }

    /**
     * The ideal was to make a toArray method. But that is not possible since, there cannot be a generic declaration
     * of an array in java (only if declared as Object type-but we don't want that).
     * @return
     */
    public MyArrayList<E> toList(){
        toList=new MyArrayList<>();
        inOrder(root);
        return toList;
    }

    private void inOrder(AVLTreeNode<E> n) {
        if (n != null) {
            inOrder(n.getLeft());
            toList.add(n.getValue());
            inOrder(n.getRight());
        }
    }

    /**
     * Finding an element if it matches the element using the comparator initially given
     * to the Tree. There is a possibility that a Node has a subtree. We must check the subtree too ,
     * if it exists
     * @param element
     * @return
     */
    public AVLTreeNode<E> find(E element){
        AVLTreeNode<E> temp;
        temp=root;
        int result;
        //While the tree end has not been reached yet
        //and while the element has not been found
        while (temp != null && (result = sorter.compare(element, temp.getValue())) != 0) {
            //move to the next tree position
            if (result > 0)
                temp = temp.getRight();
            else temp = temp.getLeft();
        }
        //return result of the search
        return temp;
    }


    public int getSize(){
        return size;
    }
    public void increaseSize(){
        size++;
    }
    public void decreaseSize(){
         size--;
    }
}