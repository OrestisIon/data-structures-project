package uk.ac.warwick.cs126.stores;

import uk.ac.warwick.cs126.interfaces.IFavouriteStore;
import uk.ac.warwick.cs126.models.Customer;
import uk.ac.warwick.cs126.models.Favourite;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;

import uk.ac.warwick.cs126.structures.*;

import uk.ac.warwick.cs126.util.DataChecker;
import uk.ac.warwick.cs126.util.myCompare;
import uk.ac.warwick.cs126.util.Sorting;

public class FavouriteStore implements IFavouriteStore {

    private AVLTreeF<Favourite> favouriteTree;
    private DataChecker dataChecker;
    private AVLTree<Long> blackList; //The BlackList of the IDs
    private Favourite temp;//used as a temporary variable in the methods
    private AVLTree<Favourite> lookup;
    private AVLTreeF<Favourite> Custlookup;
    private AVLTreeF<Favourite> Restlookup;
    private Sorting<Favourite> sort= new Sorting<Favourite>();
    MyArrayList<Favourite> list;
    public FavouriteStore() {
        // Initialise variables here
        favouriteTree = new AVLTreeF<Favourite>(myCompare.FavID);
        blackList = new AVLTreeF<Long>(myCompare.LongNums);
        dataChecker = new DataChecker();
        //it's a tree specificly for finding Favourites with same Cust and Rest IDs
        lookup=new AVLTree<>(myCompare.FavCustID2);
        Custlookup=new AVLTreeF<>(myCompare.FavCustID);
        Restlookup=new AVLTreeF<>(myCompare.FavRestID2);
    }

    public Favourite[] loadFavouriteDataToArray(InputStream resource) {
        Favourite[] favouriteArray = new Favourite[0];

        try {
            byte[] inputStreamBytes = IOUtils.toByteArray(resource);
            BufferedReader lineReader = new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(inputStreamBytes), StandardCharsets.UTF_8));

            int lineCount = 0;
            String line;
            while ((line = lineReader.readLine()) != null) {
                if (!("".equals(line))) {
                    lineCount++;
                }
            }
            lineReader.close();

            Favourite[] loadedFavourites = new Favourite[lineCount - 1];

            BufferedReader csvReader = new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(inputStreamBytes), StandardCharsets.UTF_8));

            int favouriteCount = 0;
            String row;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            csvReader.readLine();
            while ((row = csvReader.readLine()) != null) {
                if (!("".equals(row))) {
                    String[] data = row.split(",");
                    Favourite favourite = new Favourite(
                            Long.parseLong(data[0]),
                            Long.parseLong(data[1]),
                            Long.parseLong(data[2]),
                            formatter.parse(data[3]));
                    loadedFavourites[favouriteCount++] = favourite;
                }
            }
            csvReader.close();

            favouriteArray = loadedFavourites;

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return favouriteArray;
    }

    public boolean addFavourite(Favourite favourite) {
        // TODO
        AVLTreeNode<Favourite> foundNode;
        Favourite temp;
        if (!dataChecker.isValid(favourite)) //if favourite entered is not valid
            return false; //don't add
        if (blackList.find(favourite.getID())!=null) {//if favourite has blacklisted ID
            return false;//don't add
        }
        //Searching if a Favourite of the tree has the same id as the parameter, and delete it if it does
        if((temp=favouriteTree.delete(favourite))!=null){
            lookup.delete(temp);
            blackList.add(favourite.getID());//also blacklist the duplicated ID
            return false;
        }
        //find if there is a favourite with the same CustID and RestID
        foundNode=lookup.find(favourite);
        if(foundNode!=null){
            //if there is a matching Customer and Restaurant ID with some node
            //if the date of the new favourite is not older than the current one in the Tree
            if(myCompare.FavDate.compare(foundNode.getValue(),favourite)<0){
                //initialize a new subtree and priority Tree if it has not been created yet
                if(foundNode.isEmptySubtree()) {
                    foundNode.priority=new AVLTreeF<>(myCompare.FavDate);//creates the priority List
                    foundNode.subtree=new AVLTreeF<>(myCompare.FavID);
                }
                //Adding element to the subtree of the current Node
                foundNode.subtree.add(favourite);
                //Adding element to the node's subtree that is sorted by date and indicates which one has priority
                foundNode.priority.add(favourite);
            }
            else{
                if(foundNode.isEmptySubtree()) {
                    foundNode.priority=new AVLTreeF<>(myCompare.FavDate);//creates the priority List
                    foundNode.subtree=new AVLTreeF<>(myCompare.FavID);
                }
                //Replacing the current Node in the tree with the new element
                //The current is added in the subtree and priority tree
                foundNode.subtree.add(foundNode.getValue());
                foundNode.priority.add(foundNode.getValue());
                foundNode.setValue(favourite);
            }
            //Node was added to the tree. Either in the main part or in a subtree.
            return true;
        }
        //if none of the above special conditions are met, and it's valid
        favouriteTree.add(favourite);//add the favourite to the tree
        lookup.add(favourite);//and in the tree that is sorted by Customer and Favourite IDs
        return true;
    }

    public boolean addFavourite(Favourite[] favourites) {
        // TODO
        boolean isValid = true;
        if (favourites == null) //if the whole array is blank
            return false;
        for (int i = 0; i < favourites.length; i++) { //for every element in favourites array
            if (favourites[i] != null) { //if not null
                if (!addFavourite(favourites[i])) //add favourite element to the Favourites list
                    //and if this element is not a valid favourite, therefore  not added to the list
                    isValid = false; //set isValid to false
            } else //there was a blank element in the array
                isValid = false;
        }
        return isValid;
    }

    public Favourite getFavourite(Long id) {
        // TODO
        AVLTreeNode<Favourite> result; //variable that will store the result of the search
        Favourite temp= new Favourite(id,0L,0L,null);
        result=favouriteTree.find(temp);
        if(result!=null)
            return result.getValue();
        return null;
    }

    /**
     * If the tree of Favourite is empty then return a Favourite array of size 0. Else call the method toList to
     * get in ascending order sorted the binary tree into a List.Then we copy the list into an array.
     * Unfortunately this is not ideal since the operation to copy the List into the array takes time O(n), but
     * there is no other way of doing it, since generic array declaration is not possible. All in all, this method will
     * only be used once each time the Website is loaded, therefore it does not affect the overall performance.
     *
     */
    public Favourite[] getFavourites() {
        // TODO
        //if the tree is empty
        if(favouriteTree.getSize()==0)
            //return an array of size 0
            return new Favourite[0];
        list=favouriteTree.toList();
        Favourite[] array=new Favourite[list.size()];
        //else return the result of the method that makes a sorted list out of the AVLTree
        for(int i=0;i< list.size();i++){
            array[i]=list.get(i);
        }
        sort.mergeSort(array,myCompare.DescFavDate);
        return array;
    }

    public Favourite[] getFavouritesByCustomerID(Long id) {
       // TODO
        MyArrayList<Favourite> list;
        if(id==0L)
            return new Favourite[0];
        if(Custlookup.getSize()==0)
            createCustTree();
        list=Custlookup.findALL(new Favourite(0L,id,0L,null));
        Favourite[] array= new Favourite[list.size()];
        for(int i=0;i< list.size();i++)
            array[i]=list.get(i);
        //Now we need to sort the array in descending order of Date
        sort.mergeSort(array,myCompare.DescFavDate);
        return array;
    }
    public Favourite[] getFavouritesByRestaurantID(Long id) {
    // TODO
        MyArrayList<Favourite> list;
        if(id==0L)
            return new Favourite[0];
        if(Restlookup.getSize()==0)
            createRestTree();
        list=Restlookup.findALL(new Favourite(0L,0L,id,null));
        Favourite[] array= new Favourite[list.size()];
        for(int i=0;i< list.size();i++)
            array[i]=list.get(i);
        //Now we need to sort the array in descending order of Date
        sort.mergeSort(array,myCompare.DescFavDate);
        return array;
    }

    /**
     * To search every element of one array into every element in the other its O(n^2)
     * which is very BAD. Whereas using a Hashmap is only O(n). Also after the comparison I chose to store the
     * IDs in a List because, the IDs are partly sorted, so mergesort would be quite efficient
     * @param customer1ID       The ID of the first Customer.
     * @param customer2ID       The ID of the second Customer.
     * @return
     */
    public Long[] getCommonFavouriteRestaurants(Long customer1ID, Long customer2ID) {
        // TODO
        //Only in case both ids are null then we return an array of length 0
        if(customer1ID==0L && customer2ID==0L)
            return new Long[0];

        Favourite[] array1;
        Favourite[] array2;
        MyArrayList<Favourite> list= new MyArrayList<>();
        Favourite temp;
        array1=getFavouritesByCustomerID(customer1ID);
        array2=getFavouritesByCustomerID(customer2ID);
        MyHashMap<Long,Favourite> cust1= new MyHashMap<>(array1.length+ array2.length);
        for(Favourite e : array1 ) {
            cust1.add(e.getRestaurantID(), e);
        }
        for(Favourite e : array2){
            temp=cust1.get(e.getRestaurantID());
            if(temp==null)
                continue;
            if(myCompare.FavCommonRest.compare(e,temp)>0) {
                System.out.println(temp);
                list.add(e);
                System.out.println(e);
            }
            else
                list.add(temp);
        }
        return sortListtoRestIDs(list);
    }

    public Long[] getMissingFavouriteRestaurants(Long customer1ID, Long customer2ID) {
        // TODO
        //Only in case both ids are null then we return an array of length 0
        if(customer1ID==0L && customer2ID==0L)
            return new Long[0];
        Favourite[] array1;
        Favourite[] array2;
        MyArrayList<Favourite> list= new MyArrayList<>();
        AVLTreeF<Favourite> temp=new AVLTreeF<>(myCompare.FavRestID2);
        array1=getFavouritesByCustomerID(customer1ID);
        array2=getFavouritesByCustomerID(customer2ID);
        //add all the favourites of customer1 to a tree sorted by Restaurant IDs
        for(Favourite i : array1)
            temp.add(i);
        //delete all the favourites that have the same RestID as the customer2
        for(Favourite i : array2)
            temp.delete(i);
        return sortListtoRestIDs(list);
    }

    /**
     * A repeated process between getMissingFavouriteRestaurants() and getCommonFavouriteRestaurants()
     * Therefore new method for this, is more modular
     * @param list
     * @return
     */
    private Long[] sortListtoRestIDs(MyArrayList<Favourite> list){
        Favourite[] array1;
        Long[] result;
        array1=new Favourite[list.size()];
        result=new Long[list.size()];
        for(int i=0;i< list.size();i++)
            array1[i]=list.get(i);
        sort.mergeSort(array1,myCompare.DescFavDate);
        for(int i=0;i< list.size();i++)
            result[i]=array1[i].getRestaurantID();
        return result;
    }

    /**
     * Using the set difference theorem.
     * @param customer1ID       The ID of the first Customer.
     * @param customer2ID       The ID of the second Customer.
     * @return
     */
    public Long[] getNotCommonFavouriteRestaurants(Long customer1ID, Long customer2ID) {
        // TODO
        //Only in case both ids are null then we return an array of length 0
        if(customer1ID==0L && customer2ID==0L)
            return new Long[0];
        Favourite[] set1;
        Favourite[] set2;
        Long[] set3;
        Long[] result;
        Favourite temp;
        int len;
        MyArrayList<Favourite> list= new MyArrayList<>();
        AVLTreeF<Favourite> treeFav= new AVLTreeF<>(myCompare.FavRestID2);
        set1=getFavouritesByCustomerID(customer1ID);
        set2=getFavouritesByCustomerID(customer1ID);
        set3=getCommonFavouriteRestaurants(customer1ID,customer2ID);

        if(set1.length<set2.length)
            len=set1.length;
        else len=set2.length;
        for (int i=0;i<len;i++){
            treeFav.add(set1[i]);
            treeFav.add(set2[i]);
        }
        //find which array still hasn't added all of its elements and add it
        if(len!=0)
        if(len==set1.length )
            for (int i=len-1;i<set2.length;i++){
                treeFav.add(set2[i]);
            }
        else
            for (int i=len-1;i<set1.length;i++)
                treeFav.add(set1[i]);
            //remove the common IDs from the whole set
        for (int i=0;i<set3.length;i++){
            temp=treeFav.delete(new Favourite(0L,0L,set3[i],null));
        }
        list=treeFav.toList();
        Favourite[] array=new Favourite[list.size()];
        //else return the result of the method that makes a sorted list out of the AVLTree
        for(int i=0;i< list.size();i++){
            array[i]=list.get(i);
        }
        sort.mergeSort(array,myCompare.DescFavDate);
        result= new Long[array.length];
        for(int i=0;i< list.size();i++)
            result[i]=array[i].getRestaurantID();
        return result;
    }

    public Long[] getTopCustomersByFavouriteCount() {
        // TODO
        Long[] top20Array= new Long[20];
        AVLTreeF<Favourite2> top20= new AVLTreeF<>(myCompare.TopCompCount);
        MyArrayList<Favourite2> topList;
        Favourite[] temp;
        MyArrayList<Favourite> list;
        if(Custlookup.getSize()==0)
            //Create a list - that creates clusters of customers Ids
            createCustTree();
        list= Custlookup.toList();
        //for all the customers
        for(int i=0;i<list.size();i++) {
            //Get current Customer Id
            temp = getFavouritesByCustomerID(list.get(i).getCustomerID());
            //Add entry to tree
            top20.addtoTop(new Favourite2(list.get(i).getCustomerID(), temp.length, temp));
        }
        topList=top20.toList2();
        //Put the first 20 to an array
        for(int i=0;i<20;i++){
            if(i>=topList.size())
                top20Array[i]=0L;
            else
                top20Array[i]=topList.get(i).CustID;
        }
        return top20Array;
    }

    public Long[] getTopRestaurantsByFavouriteCount() {
        // TODO
        Long[] top20Array= new Long[20];
        AVLTreeF<Favourite2> top20= new AVLTreeF<>(myCompare.TopCompCount);
        MyArrayList<Favourite2> topList;
        Favourite[] temp;
        MyArrayList<Favourite> list;
        if(Restlookup.getSize()==0)
            //create a tree that creates clusters of restaurant Ids
            createRestTree();
        list= Restlookup.toList();
        //for all the restaurants
        for(int i=0;i<list.size();i++) {
            //Get current Restaurant Id
            temp = getFavouritesByRestaurantID(list.get(i).getRestaurantID());
            //Add entry to tree
            top20.addtoTop(new Favourite2(list.get(i).getRestaurantID(), temp.length, temp));
        }
        topList=top20.toList2();
        //Put the first 20 to an array
        for(int i=0;i<20;i++){
            if(i>=topList.size())
                top20Array[i]=0L;
            else
                top20Array[i]=topList.get(i).CustID;
        }
        return top20Array;
    }

    private void createCustTree(){
        for(int i=0;i<list.size();i++)
            Custlookup.addwithMerge(list.get(i));
    }
    private void createRestTree(){
        for(int i=0;i<list.size();i++)
            Restlookup.addwithMerge(list.get(i));
    }
}
