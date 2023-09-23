package uk.ac.warwick.cs126.structures;


import java.util.Comparator;
/**
 * This is a special AVL Tree. It does the basic operations tha every other AVLTree does. You can insert,delete and find
 * elements with time complexity of just O(logn). Every time there is a change in the tree, the method balanceTree is called
 * to rebalance the tree. That way we can have a tree that keeps its balance and it performs well.Not only that but the
 * current tree has been design in such a way that each node can have its own different tree attached. The possibilities
 * are endless. And if we want we can create multi-dimensional tree. However, that doesn't mean that the tree's functions
 * ignore the subtrees. It's design in such way that each tree's performance operations, are performed on each subtree of the tree
 * too. For example the delete method will search to delete an element in the main tree, but it will also be checking all
 * the subtrees to delete that element.
 * @param <E>
 */
public class AVLTreeF<E> extends AVLTree<E> {
    private int result;
    MyArrayList<E> toList;
    public AVLTreeF(Comparator<E> sorter) {
        super(sorter);
    }

    /**
     * Finds and deletes an element from the Tree
     * @param e
     * @return return the element that was deleted , o/w if element not found return  null
     */
    public E delete(E e){
        boolean inSubtree=false;//used later to indicate whether a node has a subtree
        E deleted=null;
        //if tree is null-stop this method and return null
        if(root==null)
            return null;
        AVLTreeNode<E> temp=root;
        AVLTreeNode<E> parent=null;
        AVLTreeNode<E> tempNode=null; //a temporary variable that will be used later
        AVLTreeF<E> tempTree=null; //a temporary variable that will be used later
        AVLTreeF<E> tempTree2=null; //a temporary variable that will be used later
        while(temp!=null && (result=sorter.compare(e,temp.getValue()))!=0){
            //if there exists a subtree in the current node
            if(!temp.isEmptySubtree()) {
                //check if element is in subtree, and delete if so
                if ((deleted = temp.subtree.delete(e)) != null) {
                    //delete the  element from the priority tree as well
                    temp.priority.delete(deleted);
                    inSubtree = true;
                    //stop looking for the element-just found and deleted
                    break;
                }
            }
            //move to next node of tree
            if(result>0) {
                parent = temp;
                temp = temp.getRight();
            }
            else {
                parent=temp;
                temp=temp.getLeft();
            }
        }
        //if found and deleted in at least one of the subtrees or if element not found anywhere
        if(inSubtree || temp==null){
            return deleted;
        }
        deleted=temp.getValue();
        //if the node that is going to be deleted has a subtree
        if(!temp.isEmptySubtree()){
            //we need to replace the node with the node that has the priority
            //which is the root of the priority tree
            tempNode=temp.priority.root;
            //delete the priority node from the subtree as well as the priorityTree
            temp.priority.delete(tempNode.getValue());
            temp.subtree.delete(tempNode.getValue());
            //store pointer of current subtree and priorityTree into a temporary variables
            tempTree=temp.subtree;
            tempTree2=temp.priority;
        }
        if(temp.getLeft()==null) {
            if (parent == null)
                // Connect the parent with the right child of the current node
                root = temp.getRight();
            else {
                if (sorter.compare(e, parent.getValue()) >= 0)
                    parent.setRight(temp.getRight());
                else
                    parent.setLeft(temp.getRight());
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
            else
                parentRight.setLeft(rightest.getLeft());
            balanceTree(parentRight.getValue());
        }
        if(tempNode!=null) {
            //add the new element to the tree(previous root of subtree)
            add(tempNode.getValue());
            tempNode=find(tempNode.getValue());
            //make subtree of new node the remaining old subtree
            tempNode.subtree=tempTree;
            tempNode.priority=tempTree2;
        }
        decreaseSize();
        return deleted;
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
        AVLTreeNode<E> temp2;
        if(getSize()==0)
            return null;
        temp=root;
        int result;
        //While the tree end has not been reached yet
        //and while the element has not been found
        while (temp != null && (result = sorter.compare(element, temp.getValue())) != 0) {
            //Checking to see if the element might be in the subtree of the node if it exists
            if (temp.subtree!=null && temp.subtree.getSize() != 0) {
                temp2 = temp.subtree.find(element);
                //if the element was found in the subtree
                if (temp2 != null) {
                    //return the node that has this element inside its subtree
                    return temp2;
                }
            }
            //move to the next tree position
            if (result > 0)
                temp = temp.getRight();
            else temp = temp.getLeft();
        }
        //return result of the search
        return temp;
    }

    public MyArrayList<E> findALL(E element){
        AVLTreeNode<E> current;
        AVLTree<E> tempTree;
        MyArrayList<E> list= new MyArrayList<>();
        if(getSize()==0)
            return null;
        current=root;
        while(current!=null){
            if((result = sorter.compare(element, current.getValue())) == 0) {
                if(!current.isEmptySubtree()) {
                    tempTree = current.subtree;
                    list=tempTree.toList();
                }
                else list.add(current.getValue());
                break;
            }
            //move to the next tree position
            else if (result > 0)
                current = current.getRight();
            else current = current.getLeft();
        }
        return list;
    }
    private void addToSubTreeB(AVLTreeNode<E> n, E v) {
        if (n != null) // sanity check!
        {
            int result;
            E nValue = n.getValue();
            if ((result=sorter.compare(v,nValue)) < 0) {
                // System.out.println("Adding " + v + " to left sub-tree of " + nValue);
                if (n.getLeft() == null)
                    n.setLeft(new AVLTreeNode<>(v));
                else
                    addToSubTreeB(n.getLeft(), v);
            } else if (result>0){
                //  System.out.println("Adding " + v + " to right sub-tree of " + nValue);
                if (n.getRight() == null)
                    n.setRight(new AVLTreeNode<>(v));
                else
                    addToSubTreeB(n.getRight(), v);
            }else{
                if(n.isEmptySubtree()) {
                    n.subtree = new AVLTreeF<>(sorter);
                    //we also want the Tree node to be included in its subtree
                    n.subtree.add(nValue);
                }
                n.subtree.add(v);
            }
        }
    }

    /**
     * Different method to add data to tree. Adds nodes with equal result from comparison in a subtree
     * @param e
     */
    public void addwithMerge(E e){
        if (root == null) {
            //System.out.println("Adding " + v + " to root.");
            root = new AVLTreeNode<>(e);
        } else
            addToSubTreeB(root, e);
        balanceTree(e);
        increaseSize();
    }
    //in case of not being larger or equal than the first 20 elements then don't add
    private void addToSubTreeC(AVLTreeNode<E> n, E v) {
        counteradd++;
        if (n != null) // sanity check!
        {
            int result;
            E nValue = n.getValue();
            if ((result = sorter.compare(v, nValue)) < 0) {
                // System.out.println("Adding " + v + " to left sub-tree of " + nValue);
                if (n.getLeft() == null)
                    n.setLeft(new AVLTreeNode<>(v));
                else
                    addToSubTreeB(n.getLeft(), v);
            } else if (result > 0) {
                //  System.out.println("Adding " + v + " to right sub-tree of " + nValue);
                if (n.getRight() == null)
                    n.setRight(new AVLTreeNode<>(v));
                else
                    addToSubTreeB(n.getRight(), v);
            }
        }
    }
    private int counteradd;

    /**
     * Another Method of adding elements in the tree. When the element added is smaller than
     * the first 20 elements it will not be added.
     * @param e
     */
    public void addtoTop(E e){
        if (root == null) {
            //System.out.println("Adding " + v + " to root.");
            root = new AVLTreeNode<>(e);
        } else {
            counteradd=-1;
            addToSubTreeC(root, e);
        }
        balanceTree(e);
        increaseSize();
    }

    /**
     * Method of getting the Tree into descending order as a List
     * @return
     */
    public MyArrayList<E> toList2(){
        toList=new MyArrayList<>();
        inOrder2(root);
        return toList;
    }

    private void inOrder2(AVLTreeNode<E> n) {
        if (n != null) {
            inOrder2(n.getRight());
            toList.add(n.getValue());
            inOrder2(n.getLeft());
        }
    }
}


