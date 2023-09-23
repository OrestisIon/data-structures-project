/**
 * Class that provides static methods to compare specific elements of a customer class entries
 */
package uk.ac.warwick.cs126.util;
import uk.ac.warwick.cs126.models.*;
import uk.ac.warwick.cs126.structures.Favourite2;
import uk.ac.warwick.cs126.util.DataChecker;
import java.util.Date;
import java.util.Comparator;


import uk.ac.warwick.cs126.util.HaversineDistanceCalculator;

/**
 * The Class that contains every comparison method used throughout the coursework.
 * There exception handling in each of these methods. In case of comparing IDs we know that
 * none of the entities should have it null to be called in such methods, but I chose to leave it
 * to make the code more robust to mistakes happening in the other classes.
 */
public class myCompare{
    private static boolean isValid(String str){
        return str != null && !str.equals("");
    }

    /**
     * Comparator of two String Variables
     */
    public static Comparator<String> Str = (c1, c2) -> {
        int result;
        if(isValid(c1)) {
            //if none of them is null
            if (isValid(c2))
                //compare them
                return c1.compareTo(c2);
            else
                //if only c2 is null-means that c2 is smaller than c1
                return 1;
        }
        //if only the c1 is null-means that c2 is larger than c1
        if(isValid(c2))
            return -1;
        //both are null - means that they are equal
        return 0;
    };
    /**
     * Comparator of two Long Variables
     */
    public static Comparator<Long> LongNums = (c1, c2) -> {
        long result;
        result = c1 - c2;
        //if c1 smaller than c2
        if (result<0)
            return -1;
        //if c1 larger than c2
        else if(result>0)
            return 1;
        //if both equal
        return 0;
    };
    /**
     * Comparator of two Int Variables
     */
    public static Comparator<Favourite2> TopCompID = (c1, c2) -> {
        int result;
        result=myCompare.LongNums.compare(c1.CustID,c2.CustID);
        return result;
    };
    /**
     * Comparator of two Int Variables
     */
    public static Comparator<Favourite2> TopCompDate = (c1, c2) -> {
        int result;
        result=myCompare.ReverseDate.compare(c1.getLatest(),c2.getLatest());
        if(result==0)
            return TopCompID.compare(c1,c2);
        return result;
    };
    /**
     * Comparator of two Int Variables
     */
    public static Comparator<Favourite2> TopCompCount = (c1, c2) -> {
        int result;
        result= c1.favouritesCount - c2.favouritesCount;
        if(result==0)
            return TopCompDate.compare(c1,c2);
        return result;
    };


    /**
     * Compare ID
     */
    public static Comparator<Customer> CustID = (c1, c2) -> {
        Long id2;
        Long id1;
        id1 = c1.getID();
        id2 = c2.getID();
        int result;
        result=LongNums.compare(id1,id2);
        return  result;
    };
    /**
     * Compare Name
     */
    public static Comparator<Customer> CustName = new Comparator<Customer>() {
        public int compare(Customer c1, Customer c2) {
           int result=0;
            String name2;
            String name1;
            name1 = c1.getFirstName();
            name2 = c2.getFirstName();
            //Making them both uppercase- since it must be case in-sensitive
            if(isValid(name1))
            name1=name1.toUpperCase().replace(" ","");
            if(isValid(name2))
            name2=name2.toUpperCase().replace(" ","");
            result=Str.compare(name1,name2);
            if(result==0)
                return CustID.compare(c1,c2); //else compare their last names next
            return result;
        }
    };
    /**
     * Compare Surname
     */
    public static Comparator<Customer> CustLastName = (c1, c2) -> {
        int result=0;
        String name2;
        String name1;
        name1 = c1.getLastName();
        name2 = c2.getLastName();
        //Making them both uppercase- since it must be case in-sensitive
        if(isValid(name1))
            name1=name1.toUpperCase().replace(" ","");
        if(isValid(name2))
            name2=name2.toUpperCase().replace(" ","");
        result=Str.compare(name1,name2);
        if(result==0)
            return CustName.compare(c1,c2); //else compare their first names next
        return result;
    };


    public static Comparator<Restaurant> RestID = (c1, c2) -> {
        Long id2;
        Long id1;
        try {
            id1 = c1.getID();
        }catch (NullPointerException e){
            id1=0L;
        }
        try {
            id2 = c2.getID();
        }catch (NullPointerException e){
            id2=0L;
        }
        int result;
        try {
            result=id1.compareTo(id2);
        }
        catch (NullPointerException e){
            if(id1==0L && id2!=0L)
                return -1;
            if(id2==0L && id1!=0L) //is name2 is empty but name1 not
                return 1; //return a negative number indicating that num1 is larger than num2
            return 0; //they are equal, but both null
        }
        return  result;
    };
    /**
     * Compare Surname
     */
    public static Comparator<Restaurant> RestName = (c1, c2) -> {
        String name2;
        String name1;
        int result;
        try {
            name1 = c1.getName();
        }
        catch (NullPointerException e){
            name1="";
        }
        try {
            name2 = c2.getName();
        }
        catch (NullPointerException e){
            name2="";
        }
        //Making them both uppercase- since it must be case in-sensitive
        name1=name1.toUpperCase().replace(" ","");
        name2=name2.toUpperCase().replace(" ","");
        try {
            result = name1.compareTo(name2);//compares the first names
        }
        catch (NullPointerException e ){
            if(name1.isEmpty() && !name2.isEmpty())
                return -1;
            if(name2.isEmpty() && !name1.isEmpty()) //is name2 is empty but name1 not
                return 1; //return a negative number indicating that num1 is larger than num2
            result=0; //they are equal, but both null
        }
        if (result != 0) //if they are not the same
            return result;//return the result of the comparison
        return RestID.compare(c1,c2); //else compare their IDs next
    };
    public static Comparator<Restaurant> RestDate = (c1, c2) -> {
        Date date1;
        Date date2;
        int result;
        try {
            date1 = c1.getDateEstablished();
        }
        catch (NullPointerException e){
            date1=null;
        }
        try {
            date2 = c2.getDateEstablished();
        }
        catch (NullPointerException e){
            date2=null;
        }
        try {
            result = date1.compareTo(date2);//compares the dates
        }
        catch (NullPointerException e ){
            if(date1==null && date2!=null)
                return -1;
            if(date2==null && date1!=null) //is name2 is empty but name1 not
                return 1; //return a negative number indicating that num1 is larger than num2
            result=0; //they are equal, but both null
        }
        if (result != 0) //if they are not the same
            return result;//return the result of the comparison
        return RestName.compare(c1,c2); //else compare their IDs next
    };

    public static Comparator<Restaurant> RestRating = (c1, c2) -> {
        int stars1;
        int stars2;

        try {
            stars1 = c1.getFoodInspectionRating();
        }
        catch (NullPointerException e){ //in case it's a null parameter
            stars1=0;//set stars to 0
        }
        try {
            stars2 = c2.getFoodInspectionRating();
        }
        catch (NullPointerException e){
            stars2=0;
        }
        //Comparing the two--In Descending order
        if(stars2>stars1)
            return 1;
        else if(stars2<stars1)
            return -1;
        return RestName.compare(c1,c2); //else compare their Names next
    };

    public static Comparator<RestaurantDistance> RestDistance = (c1, c2) -> {
        float distance1, distance2;
        distance1=c1.getDistance();
        distance2=c2.getDistance();
        //find the two distances and store them absolute values

        //Comparing the two--In Descending order
        if(distance1>distance2)
            return 1;
        else if(distance1<distance2)
            return -1;
        return RestID.compare(c1.getRestaurant(),c2.getRestaurant()); //else compare their Names next
    };

    public static Comparator<Place> placeLongComparator = (c1, c2) -> {
        float lat1;
        float lat2;
        try {
            lat1 = c1.getLongitude();
        }catch (NullPointerException e){
            lat1=0L;
        }
        try {
            lat2 = c2.getLongitude();
        }catch (NullPointerException e){
            lat2=0L;
        }
        if(lat1>lat2)
            return 1;
        else if(lat1<lat2)
            return -1;
        return  0;
    };

    public static Comparator<Place> placeLatComparator = (c1, c2) -> {
        float lat1;
        float lat2;

        try {
            lat1 = c1.getLatitude();
        }catch (NullPointerException e){
            lat1=0L;
        }
        try {
            lat2 = c2.getLatitude();
        }catch (NullPointerException e){
            lat2=0L;
        }
        if(lat1>lat2)
            return 1;
        else if(lat1<lat2)
            return -1;
        return placeLongComparator.compare(c1,c2);
    };
    public static Comparator<Favourite> FavID = (c1, c2) -> {
        Long id2;
        Long id1;
        try {
            id1 = c1.getID();
        }catch (NullPointerException e){
            e.printStackTrace();
            id1=0L;
        }
        try {
            id2 = c2.getID();
        }catch (NullPointerException e){
            e.printStackTrace();
            id2=0L;
        }
        Long result;
        result=id1-id2;
            if(result<0)
                return -1;
            if(result>0) //is c2 is empty but c1 not
                return 1; //return a negative number indicating that c1 is larger than c2
            return 0; //they are equal, but both null
    };
    /**
     * Only comparing the Restaurant IDs of two favourites
     */
    public static Comparator<Favourite> FavRestID2 = (c1, c2) -> {
        int result;
        result =LongNums.compare(c1.getRestaurantID() ,c2.getRestaurantID()) ;
        return result;
    };
    public static Comparator<Favourite> FavCustID2 = (c1, c2) -> {
        int result;
        result =LongNums.compare(c1.getCustomerID() ,c2.getCustomerID());
        if(result==0)
            return FavRestID2.compare(c1,c2);
        return result;
    };

    public static Comparator<Date> ReverseDate = (c1, c2) -> {
        int result=0;
        if (c1 != null && c2 != null)
            result=c1.compareTo(c2);//compares the dates
        if(result>0)
            return -1;
        else if(result<0)
            return 1;
        return 0 ; //else return 0 as an indication that something is wrong(at least one of the parameters where null)
    };
    public static Comparator<Date> Date = (c1, c2) -> {
        if (c1 != null && c2 != null)
            return c1.compareTo(c2);//compares the dates
        if(c1==null && c2!=null)
            return -1;
        else if (c2==null && c1!=null)
            return 1;
        return 0; //else return 0 as an indication that something is wrong(at least one of the parameters where null)
    };


    public static Comparator<Favourite> FavDate = (c1, c2) -> {
        Date date1;
        Date date2;
        date1 = c1.getDateFavourited();
        date2 = c2.getDateFavourited();
        return Date.compare(date1,date2);
    };
    public static Comparator<Favourite> DescFavDate = (c1, c2) -> {
        int result;
        result=ReverseDate.compare(c1.getDateFavourited(),c2.getDateFavourited());
        if(result==0)
            return FavID.compare(c1,c2);
        return result;
    };
    /**
     * For the Tree that stores the Favourites sorted by Customer IDs
     */
    public static Comparator<Favourite> FavCustID = (c1, c2) -> {
        int result;
        result =LongNums.compare(c1.getCustomerID() ,c2.getCustomerID());
        return result;
    };
    /**
     * For the Tree that stores the Favourites sorted by Restaurant IDs
     */
    public static Comparator<Favourite> FavRestID = (c1, c2) -> {
        int result;
        result =LongNums.compare(c1.getCustomerID() ,c2.getCustomerID());
        if(result==0)
            return DescFavDate.compare(c1,c2);
        else
            return result;
    };
    public static Comparator<Favourite> FavCommonRest = (c1, c2) -> {
        int result;
        result=ReverseDate.compare(c1.getDateFavourited(),c2.getDateFavourited());
        if(result==0)
            return 1;
        else
            return result;
    };




}
