package uk.ac.warwick.cs126.util;

import uk.ac.warwick.cs126.interfaces.IDataChecker;

import uk.ac.warwick.cs126.models.Customer;
import uk.ac.warwick.cs126.models.Restaurant;
import uk.ac.warwick.cs126.models.Favourite;
import uk.ac.warwick.cs126.models.Review;
import uk.ac.warwick.cs126.structures.MyArrayList;
import java.util.Date;


public class DataChecker<E> implements IDataChecker {

    public DataChecker() {
        // Initialise things here
    }

    /**
     * Checks if the array parameter is not completely null and that it has exactly length 3.
     * After that it stores in an array which from these positions are null.
     * Then by iterating through the list it compares all the elements to find at least one pair that matches.
     * @param repeatedID an array with repeated IDs as elements
     * @return the consensus element that is found, o/w null
     */
    public Long extractTrueID(String[] repeatedID) {
        //--DID-TODO
        if(repeatedID==null || repeatedID.length!=3)
            return null;

        boolean[] valid= new boolean[3];
        //Checks if any of the ids are null
        valid[0]=isValid(repeatedID[0]);
        valid[1]=isValid(repeatedID[1]);
        valid[2]=isValid(repeatedID[2]);
        //check all the elements
        for(int i=0;i<2;i++){
            if (valid[i])
                for(int j=0;j<3;j++){
                        if(i!=j && valid[j] && repeatedID[i].equals(repeatedID[j]))
                            //return the consensus that is found
                            return Long.parseLong(repeatedID[i]);
                }
        }
        return null; //No consensus found

    }

    /**
     * Checks if the ID is valid or not. It checks if each digit of the number is correctly within the 1-9 range.
     * Stores the frequency of each digit in a new array. If the frequency of any of the digits is larger than 3, it
     * means that the ID is not Valid.
     * @param inputID
     * @return true if the ID is Valid and false if it's not
     */
    public boolean isValid(Long inputID) {
        // TODO
        int counter=1;
        int[] frequency= new int[9];
        int digit;
        //the ID cannot be negative
        if(inputID<0)
            return false;
        while(inputID>0 && counter<=16){ //do it for all the number of digits of the number
            if((digit= (int) (inputID % 10))<1 ) //Check if digit is between 1 and 9
                return false; //Invalid ID
            frequency[digit-1]++; //increment the frequency of the current digit
            inputID/=10;//to get the next digit
            counter++;
        }
        for(int i=0;i<9;i++) //check for every digit between 1-9
            if(frequency[i]>3) //if it appeared more than 3 times
                return false; //Invalid ID
        if(inputID>0 || counter<17) // if ID has more or less than 16 digits
            return false; //Invalid ID
        //else
        return true; //ID is valid
    }

    /**
     *Checks if the Customer is null. Then checks if any of the fields of Customer is null. And then checks if the ID
     * of the Customer is valid
     * @param customer
     * @return  true if Customer is valid, false otherwise
     */
    public boolean isValid(Customer customer) {
        // TODO
        if(customer==null) //if a Customer is null
            return false; //it's not valid
        //if any of the Customer fields is null
        if (customer.getID()==null || customer.getDateJoined()==null || customer.getFirstName()==null
                || customer.getLastName()==null || customer.getLatitude()==0.0f || customer.getLongitude()==0.0f)
            return false; //it's not valid

        return isValid(customer.getID()); //if the ID of Customer is valid too, then Customer is valid
    }
    /**
     * Checks if the Restaurant and any of its fields is null. Consider that the ID is already been checked in the
     * Restaurant Store. The boolean attributes of the Restaurant are not checked, since they have false as initial value.
     * Moreover, for the name either first or last is acceptable, not both names need to be filled.
     * @param restaurant
     * @return  true if Restaurant is valid, o/w false
     */
    public boolean isValid(Restaurant restaurant) {
        // TODO
        if(restaurant==null) //if a Restaurant is null
            return false; //it's not valid
        //if any of the Restaurant fields is null
        //Don't need to check the Boolean variables, since they are false by default if not assigned
        if ( !isValid(restaurant.getCuisine().toString()) || !isValidCustRating(restaurant.getCustomerRating())
                || !isValid(restaurant.getDateEstablished()) || !isValid(restaurant.getEstablishmentType().toString())|| !isValid(restaurant.getLastInspectedDate())
                || !isValidInspection(restaurant.getFoodInspectionRating()) || !isValidStars(restaurant.getWarwickStars())
                || !isValidFullName(restaurant.getOwnerFirstName(),restaurant.getOwnerLastName()) || !isValid(restaurant.getName())
                || !isValid(restaurant.getPriceRange().toString()) || restaurant.getLastInspectedDate().compareTo(restaurant.getDateEstablished())<0)
            //it's not valid
            return false;

        //the Restaurant is valid
        return true;
    }

    /**
     *Checks if the Favourite is null. Then checks if all  the fields of Favourite are valid. To do that is calls
     * the isValid() methods to validate IDs and the Date field.
     * @param favourite
     * @return  true if Favourite is valid, false otherwise
     */
    public boolean isValid(Favourite favourite) {
        // TODO
        if(favourite==null) //if a favourite is null
            return false; //it's not valid
        //if any of the favourite fields is null
        if ( !isValid(favourite.getID()) || !isValid(favourite.getDateFavourited())
                || !isValid(favourite.getCustomerID()) || !isValid(favourite.getRestaurantID()))
            //it's not valid
            return false;

        //the favourite is valid
        return true;
    }

    public boolean isValid(Review review) {
        // TODO
        return false;
    }

    /**
     * Method for checking validity of any String type variable
     * @param str
     * @return
     */
    public boolean isValid(String str){
        return str != null && !str.equals("");
    }

    public boolean isValid(MyArrayList<E> list){
        return list != null && list.size() != 0;
    }
    public boolean isValid(E[] array){
        return array!=null && array.length!=0;
    }

    public boolean isValid(Date date){
        return date!=null;
    }

    /**
     * The inspection rating must only take values 0,1,2,3,4,5
     * @param rating the Inspection Rating of a Restaurant-Integer value
     * @return true if the inspection rating  is one of  the required values, false o/w
     */
    public boolean isValidInspection(int rating){
        return rating <= 5 && rating >= 0;
    }
    /**
     * The Warwick Stars must only take values 0,1,2,3
     * @param rating the number of Warwick Stars of a Restaurant-Integer value
     * @return true if the number of stars is one of the required values, false o/w
     */
    public boolean isValidStars(int rating){
        return rating <= 3 && rating >= 0;
    }
    /**
     * The customer rating must only take values 0.0f,1.0f-5.0f
     * @param rating the Customer Rating of a Restaurant-Integer value
     * @return true if the customer rating  is withing the required bounds, false o/w
     */
    public boolean isValidCustRating(float rating){
        return (rating <= 5 && rating >= 1) || rating==0;
    }
    /**
     * The Full Name must contain at least a last name or a first name and not be completely null
     * @param name1 the first name
     * @param name2 the last name
     * @return false if both names are null
     */
    public boolean isValidFullName(String name1,String name2){
        return isValid(name1) || isValid(name2);
    }
}