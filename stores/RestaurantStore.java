package uk.ac.warwick.cs126.stores;

import uk.ac.warwick.cs126.interfaces.IRestaurantStore;
import uk.ac.warwick.cs126.models.*;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.io.IOUtils;

import uk.ac.warwick.cs126.structures.AVLTree;
import uk.ac.warwick.cs126.structures.MyArrayList;

import uk.ac.warwick.cs126.structures.MyQueue;
import uk.ac.warwick.cs126.structures.SortedLinkedList;
import uk.ac.warwick.cs126.util.*;


public class RestaurantStore implements IRestaurantStore {

    private MyArrayList<Restaurant> restaurantArray;
    private DataChecker<Restaurant> dataChecker;
    private MyArrayList<Long> blackList; //The BlackList of the IDs
    private Restaurant temp;
    private boolean isValid;
    //Instance of class for soring Restaurant objects
    private Sorting<Restaurant> sorter;
    //Instance of class for soring RestaurantDistance objects
    private Sorting<RestaurantDistance> sorter2;
    private Restaurant[] sortedRestaurant;
    private MyArrayList<Restaurant>[] stars= new MyArrayList[3];
    private RestaurantDistance[] restaurant2;
    private ConvertToPlace getPlace= new ConvertToPlace();
    public RestaurantStore() {
        // Initialise variables here
        restaurantArray = new MyArrayList<>();
        dataChecker = new DataChecker<>();
        blackList= new MyArrayList<>();
        sorter = new Sorting<>();
        sorter2 = new Sorting<>();
        for(int i=0;i<3;i++)
           stars[i]= new MyArrayList();
    }

    public Restaurant[] loadRestaurantDataToArray(InputStream resource) {
        Restaurant[] restaurantArray = new Restaurant[0];

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

            Restaurant[] loadedRestaurants = new Restaurant[lineCount - 1];

            BufferedReader csvReader = new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(inputStreamBytes), StandardCharsets.UTF_8));

            String row;
            int restaurantCount = 0;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            csvReader.readLine();
            while ((row = csvReader.readLine()) != null) {
                if (!("".equals(row))) {
                    String[] data = row.split(",");

                    Restaurant restaurant = new Restaurant(
                            data[0],
                            data[1],
                            data[2],
                            data[3],
                            Cuisine.valueOf(data[4]),
                            EstablishmentType.valueOf(data[5]),
                            PriceRange.valueOf(data[6]),
                            formatter.parse(data[7]),
                            Float.parseFloat(data[8]),
                            Float.parseFloat(data[9]),
                            Boolean.parseBoolean(data[10]),
                            Boolean.parseBoolean(data[11]),
                            Boolean.parseBoolean(data[12]),
                            Boolean.parseBoolean(data[13]),
                            Boolean.parseBoolean(data[14]),
                            Boolean.parseBoolean(data[15]),
                            formatter.parse(data[16]),
                            Integer.parseInt(data[17]),
                            Integer.parseInt(data[18]));

                    loadedRestaurants[restaurantCount++] = restaurant;
                }
            }
            csvReader.close();

            restaurantArray = loadedRestaurants;

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return restaurantArray;
    }

    public boolean addRestaurant(Restaurant restaurant) {
        // TODO
        String[] tempID;
        Long restID;
        //If fails to extractID, don't add restaurant

        if((restID=dataChecker.extractTrueID(restaurant.getRepeatedID()))==null) {
            return false;
        }
      //  System.out.println("ExtractedID: "+ restID);
        if(!dataChecker.isValid(restID)) {
            restaurant.setID(-1L);
            return false;
        }
        restaurant.setID(restID);

       // System.out.println("True ID is added");
        if(!dataChecker.isValid(restaurant)) {
            return false;
        }
        if (isBlackList(restID)) {
            return false;
        }
        if (!isIDunique(restaurant.getID())) {
            return false;
        }
        restaurantArray.add(restaurant);
        return true;
    }

    public boolean addRestaurant(Restaurant[] restaurants) {
        // TODO
        isValid = true;
        if (restaurants == null) //if the whole array is null
            return false;
        for (int i = 0; i < restaurants.length; i++) { //for every element in restaurants array
            if (restaurants[i] != null) { //if not null
                if (!addRestaurant(restaurants[i])) //add customers element to the Restaurant list
                    //and if this element is not a valid customer, therefore  not added to the list
                    isValid = false; //set isValid to false
            } else //there was a blank element in the array
                isValid = false;
        }
        return isValid;
    }


    public Restaurant getRestaurant(Long id) {
        // TODO
        if (id != 0.0f)//checks if the id is not null
            for (int i = 0; i < restaurantArray.size(); i++)//for every element in the restaurants list
                if (restaurantArray.get(i).getID().equals(id)) //if id matches with id of element in the restaurant list
                    return restaurantArray.get(i); //return current element
        //not found
        return null;
    }

    public Restaurant[] getRestaurants() {
        // TODO
        if (!dataChecker.isValid(restaurantArray)) { //if the list is empty
            return new Restaurant[0]; //returns a Customer Array of 0 size
        }
        int i = 0;
        sortedRestaurant = new Restaurant[restaurantArray.size()];
        restaurantArray.toArray(sortedRestaurant);
        sorter.mergeSort(sortedRestaurant, myCompare.RestID); //copy customerArray Arraylist into sortedCustomer array
        return sortedRestaurant;
    }

    public Restaurant[] getRestaurants(Restaurant[] restaurants) {
        // TODO
        if (dataChecker.isValid(restaurants)) { //if it's not a null array
            sorter.mergeSort(restaurants, myCompare.RestID); //then sort it
            return restaurants;
        }
        //if the array parameter was invalid
        return new Restaurant[0];
    }

    public Restaurant[] getRestaurantsByName() {
        //-DID-TODO
        if (!dataChecker.isValid(restaurantArray)) { //if the list is empty
            return new Restaurant[0]; //returns a Restaurant Array of 0 size
        }
        sortedRestaurant = new Restaurant[restaurantArray.size()];
        restaurantArray.toArray(sortedRestaurant); //copy customerArray Arraylist into sortedCustomer array
        sorter.mergeSort(sortedRestaurant, myCompare.RestName); //sort the array
        return sortedRestaurant;
    }
    public Restaurant[] getRestaurantsByName(Restaurant[] restaurants) {
        // TODO
        if (dataChecker.isValid(restaurants)) { //if it's not a null array
            sorter.mergeSort(restaurants, myCompare.RestName); //then sort it
            return restaurants;
        }
        //if the array parameter was invalid
        return new Restaurant[0];
    }
    public Restaurant[] getRestaurantsByName(MyArrayList<Restaurant> restaurants) {
        // TODO
        if (!dataChecker.isValid(restaurants)) { //if the list is empty
            return new Restaurant[0]; //returns a Restaurant Array of 0 size
        }
        sortedRestaurant = new Restaurant[restaurants.size()];
        restaurants.toArray(sortedRestaurant); //copy customerArray Arraylist into sortedCustomer array
        sorter.mergeSort(sortedRestaurant, myCompare.RestName); //sort the array
        return sortedRestaurant;
    }
    public Restaurant[] getRestaurantsByDateEstablished() {
        //-DID-TODO
        if (!dataChecker.isValid(restaurantArray)) { //if the list is empty
            return new Restaurant[0]; //returns a Restaurant Array of 0 size
        }
        sortedRestaurant = new Restaurant[restaurantArray.size()];
        restaurantArray.toArray(sortedRestaurant); //copy customerArray Arraylist into sortedCustomer array
        sorter.mergeSort(sortedRestaurant, myCompare.RestDate); //sort the array
        return sortedRestaurant;
    }

    public Restaurant[] getRestaurantsByDateEstablished(Restaurant[] restaurants) {
        //-DID-TODO
        if (dataChecker.isValid(restaurants)) { //if it's not a null array
            sorter.mergeSort(restaurants, myCompare.RestDate); //then sort it
            return restaurants;
        }
        //if the array parameter was invalid
        return new Restaurant[0];
    }

    public Restaurant[] getRestaurantsByWarwickStars() {
        //-DID-TODO
        Restaurant temp;
       float counter;
       int counter2=0;
        for(int i=0;i<restaurantArray.size();i++){
            temp=restaurantArray.get(i);
            //if WarwickStars are larger than 0
            counter=temp.getWarwickStars();
            //add restaurant in list that is inside the array position corresponding to it's stars
            if(counter>0 && counter<=3) {
                if (counter == 1)
                    stars[0].add(temp);
                else if (counter == 2)
                    stars[1].add(temp);
                else if (counter == 3)
                    stars[2].add(temp);
                counter2++;
            }
                //increment number of restaurants added to the array
        }
        if(counter2==0)
            return new Restaurant[0];
        Restaurant[] arr1,arr2,arr3;
        //finally sort each array by name
        arr3=getRestaurantsByName(stars[0]);
        arr2=getRestaurantsByName(stars[1]);
        arr1=getRestaurantsByName(stars[2]);
        Restaurant[] arrfinal= new Restaurant[arr1.length+ arr2.length+arr3.length];
        for(int i=0;i<arr1.length;i++)
            arrfinal[i]=arr1[i];
        for(int i=0;i<arr2.length;i++)
            arrfinal[i+arr1.length]=arr2[i];
        for(int i=0;i<arr3.length;i++)
            arrfinal[i+arr2.length+arr1.length]=arr3[i];
        return arrfinal;
    }



    public Restaurant[] getRestaurantsByRating(Restaurant[] restaurants) {
        //-Done-TODO
        if (dataChecker.isValid(restaurants)) { //if it's not a null array
            sorter.mergeSort(restaurants, myCompare.RestRating); //then sort it
            return restaurants;
        }
        //if the array parameter was invalid
        return new Restaurant[0];
    }

    public RestaurantDistance[] getRestaurantsByDistanceFrom(float latitude, float longitude) {
        //-DID-TODO
        //checking validity of list elements
        if (!dataChecker.isValid(restaurantArray)) { //if the list is empty
            return new RestaurantDistance[0]; //returns a Restaurant Array of 0 size
        }
        //Calling method to create a RestaurantDistance array-using the existing Restaurant arraylist
        createRestDistanceArray(latitude,longitude);
        //sort the newly created array
        sorter2.mergeSort(restaurant2, myCompare.RestDistance); //sort the array
        return restaurant2;
    }

    public RestaurantDistance[] getRestaurantsByDistanceFrom(Restaurant[] restaurants, float latitude, float longitude) {
        // TODO
        if (dataChecker.isValid(restaurants)) { //if it's not a null array
            //Create a RestaurantDistance array
            restaurant2 =new RestaurantDistance[restaurants.length];
            for (int i = 0; i < restaurants.length; i++)
                restaurant2[i] = new RestaurantDistance(restaurants[i], Math.abs(HaversineDistanceCalculator.inKilometres(restaurants[i].getLatitude(), restaurants[i].getLongitude(), latitude, longitude)));
            //Sort the newly created RestaurantDistance array
            sorter2.mergeSort(restaurant2, myCompare.RestDistance); //sort the array
            return restaurant2;
        }
        return new RestaurantDistance[0];
    }

    public Restaurant[] getRestaurantsContaining(String searchTerm) {
        // TODO
        if(searchTerm.equals(""))
            return new Restaurant[0];
        //remove accents
        searchTerm = StringFormatter.convertAccentsFaster(searchTerm);
        //Make it uppercase
        searchTerm = StringFormatter.toQueryFormat(searchTerm).toUpperCase(Locale.ROOT);
        System.out.println("Query: "+searchTerm);
        Restaurant current;
        String lookup1, lookup2,lookup3,lookup4;
        Place placedata;
        AVLTree<Restaurant> foundElements= new AVLTree<>(myCompare.RestName);
        Restaurant[] found;
        int j=0;
        for (int i=0; i< restaurantArray.size();i++){
            current = restaurantArray.get(i);
            if (dataChecker.isValid(current)) { //if it's a valid Entry in the Restaurant arraylist
                //first string that we will compare
                lookup1 = current.getCuisine().toString().toUpperCase();
                if(lookup1.contains(searchTerm)){
                    foundElements.add(current);//add the entry to the sorted list if it matches the query;
                    continue;
                }
                //Second string that we will compare
                lookup2= StringFormatter.convertAccentsFaster(current.getName().toUpperCase());
                if(lookup2.contains(searchTerm)){
                    foundElements.add(current);//add the entry to the sorted list if it matches the query;
                    continue;
                }
                //Load the current place data into placedata variable
                placedata=getPlace.convert(current.getLatitude(),current.getLongitude());
                //if the place is found and therefore the placedata variable is not null
                if(placedata!=null){
                    //Third string that we will compare
                    lookup3=placedata.getName().toUpperCase();
                    if(lookup3.contains(searchTerm)){
                        foundElements.add(current);//add the entry to the sorted list if it matches the query;
                        continue;
                    }
                    lookup4=placedata.getPostcode().toUpperCase();
                    if(lookup4.contains(searchTerm)){
                        foundElements.add(current);//add the entry to the sorted list if it matches the query;
                    }
                }
            }
        }
        MyArrayList<Restaurant> copyList;
        copyList= foundElements.toList();
        //Copping the List to an array
        found = new Restaurant[copyList.size()];
        while (j<found.length) {
            //The List is descending order. So now we want to get it in ascending
            found[j] = copyList.get(j);//copy current list element to the array in the corresponding position
           // System.out.println(j+" " + found[j].getName()+ " " + found[j].getID());
            j++;
        }
        return found;
    }

    private boolean isBlackList (Long inputID){
        return blackList.contains(inputID); // checks if ID is in blackList ArrayList
    }

    /**
     * If the inputted ID already exists then
     * removes the customer with the same ID from the table, and BlackLists the specific ID
     * @param inputID
     * @return true if ID doesn't already exist in the list, false if an existing customer has the same ID
     */
    private boolean isIDunique (Long inputID){
        for (int i = 0; i < restaurantArray.size(); i++) {
            temp=restaurantArray.get(i);
            try {
                if (temp.getID().equals(inputID)) { //want to check if they are equal content-wise , not their memory locations
                    //blacklistID and Remove customer
                    blackList.add(inputID);
                    restaurantArray.remove(temp);
                    return false;
                }
            }catch (NullPointerException e){
                continue;
            }
        }
        return true;
    }

    /**
     * Generates a new array of RestaurantDistance instances.
     * Calculates the distance of each Restaurant stored in the restaurantArray and
     * stores it along with the restaurant in a RestaurantDistance array-which is global in this class.
     * @param currentlat
     * @param currentlon
     */
    private  void createRestDistanceArray(float currentlat, float currentlon){
        restaurant2 =new RestaurantDistance[restaurantArray.size()];
        for(int i=0;i<restaurantArray.size();i++)
            restaurant2[i]=new RestaurantDistance(restaurantArray.get(i),Math.abs(HaversineDistanceCalculator.inKilometres(restaurantArray.get(i).getLatitude(),restaurantArray.get(i).getLongitude(),currentlat,currentlon)));
    }


}
