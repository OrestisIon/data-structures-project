package uk.ac.warwick.cs126.stores;

import uk.ac.warwick.cs126.interfaces.ICustomerStore;
import uk.ac.warwick.cs126.models.Customer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.Callable;
import org.apache.commons.io.IOUtils;
import uk.ac.warwick.cs126.models.Favourite;
import uk.ac.warwick.cs126.models.Restaurant;
import uk.ac.warwick.cs126.structures.AVLTree;
import uk.ac.warwick.cs126.structures.MyArrayList;
import uk.ac.warwick.cs126.structures.SortedLinkedList;
import uk.ac.warwick.cs126.util.DataChecker;
import uk.ac.warwick.cs126.util.myCompare;
import uk.ac.warwick.cs126.util.StringFormatter;
import uk.ac.warwick.cs126.util.Sorting;


public class CustomerStore implements ICustomerStore {
    private MyArrayList<Customer> customerArray;
    private final DataChecker<Customer> dataChecker;
    private MyArrayList<Long> blackList; //The BlackList of the IDs
    private Sorting<Customer> sort= new Sorting<Customer>();
    public CustomerStore() {
            // Initialise variables here
            customerArray = new MyArrayList<Customer>();
            dataChecker = new DataChecker();
            blackList = new MyArrayList<>();
        }

        public Customer[] loadCustomerDataToArray (InputStream resource){
            Customer[] customerArray = new Customer[0];

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

                Customer[] loadedCustomers = new Customer[lineCount - 1];

                BufferedReader csvReader = new BufferedReader(new InputStreamReader(
                        new ByteArrayInputStream(inputStreamBytes), StandardCharsets.UTF_8));

                int customerCount = 0;
                String row;
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                csvReader.readLine();
                while ((row = csvReader.readLine()) != null) {
                    if (!("".equals(row))) {
                        String[] data = row.split(",");

                        Customer customer = (new Customer(
                                Long.parseLong(data[0]),
                                data[1],
                                data[2],
                                formatter.parse(data[3]),
                                Float.parseFloat(data[4]),
                                Float.parseFloat(data[5])));

                        loadedCustomers[customerCount++] = customer;
                    }
                }
                csvReader.close();

                customerArray = loadedCustomers;

            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }

            return customerArray;
        }

        public boolean addCustomer (Customer customer){
            if (!dataChecker.isValid(customer)) //if customer entered not valid
                return false; //don't add
            if (isBlackList(customer.getID()))
                return false;
            if (!isIDunique(customer.getID()))
                return false;
            customerArray.add(customer);
            return true;
        }

        public boolean addCustomer (Customer[]customers){
            //-DID-TODO
            boolean isValid = true;
            if (customers == null) //if the whole array is blank
                return false;
            for (Customer customer : customers) { //for every element in customers array
                if (customer != null) { //if not null
                    if (!addCustomer(customer)) //add customers element to the Customers list
                        //and if this element is not a valid customer, therefore  not added to the list
                        isValid = false; //set isValid to false
                } else //there was a blank element in the array
                    isValid = false;
            }
            return isValid;
        }
        public Customer getCustomer (Long id){
            //-DID-TODO
            if (id != 0.0f)//checks if the id is not null
                for (int i = 0; i < customerArray.size(); i++)//for every element in the customers list
                    if (customerArray.get(i).getID().equals(id)) //if id matches with id of element in the customer list
                        return customerArray.get(i); //return current element
            //not found
            return null;
        }

        public Customer[] getCustomers () {
            //-DID-TODO
            if (customerArray == null || customerArray.size() == 0) { //if the list is empty
                return new Customer[0]; //returns a Customer Array of 0 size
            }
            int i = 0;
            Customer[] sortedCustomer = new Customer[customerArray.size()];
            customerArray.toArray(sortedCustomer);
            sort.mergeSort(sortedCustomer, myCompare.CustID); //copy customerArray Arraylist into sortedCustomer array
            return sortedCustomer;
        }

        public Customer[] getCustomers (Customer[] customers){
            // TODO
            if (customers != null && customers.length != 0) //if it's not a null array
                sort.mergeSort(customers, myCompare.CustID); //then sort it
            return customers;
        }

        /**
         * Copies the arraylist of Customers into an array
         * Sorts the array
         * @return the sorted array
         */
        public Customer[] getCustomersByName () {
            //-DID-TODO
            if (!dataChecker.isValid(customerArray)) { //if the list is empty
                return new Customer[0];
            }
            Customer[] sortedCustomer = new Customer[customerArray.size()];
            customerArray.toArray(sortedCustomer); //copy customerArray Arraylist into sortedCustomer array
            sort.mergeSort(sortedCustomer, myCompare.CustLastName); //sort the array
            return sortedCustomer;
        }
        //NOMIZO EXI LATHOS
        public Customer[] getCustomersByName (Customer[]customers){
            //-DID-TODO
            if (dataChecker.isValid(customers)){ //if it's not a null array//if it's not a null array
                sort.mergeSort(customers, myCompare.CustLastName);//then sort it
                return customers;
            }
            return new Customer[0];
        }

        public Customer[] getCustomersContaining (String searchTerm){
            //-DID-TODO
            //Testing=System.out.println(StringFormatter.convertAccentsFaster("ℋℑ₱ῴₖΆ")+"   "+ StringFormatter.convertAccentsFaster("   ~```-/ Lora Maria ΓιΩργος"));
            if (!dataChecker.isValid(searchTerm) || !dataChecker.isValid(customerArray)) {
                return new Customer[0];
            }
            int j = 0; //used as a counter
            String searchTermConvertedFaster = StringFormatter.convertAccentsFaster(searchTerm);
            String lookup;
            Customer current;
            Customer[] found;
            AVLTree<Customer> foundElements= new AVLTree<>(myCompare.CustLastName); //Initializing a LinkedList that stores the data sorted by LastName
            searchTermConvertedFaster = StringFormatter.toQueryFormat(searchTermConvertedFaster); //transforming string into format that we want to use to search it
            searchTermConvertedFaster = searchTermConvertedFaster.toUpperCase();
           // System.out.println("Query: "+ searchTermConvertedFaster + "Size: "+searchTermConvertedFaster.length());
            for (int i = 0; i < customerArray.size(); i++) {
                current = customerArray.get(i);
                if (dataChecker.isValid(current)) { //if it's a valid Entry in the Customer arraylist
                    lookup = StringFormatter.convertAccentsFaster(StringFormatter.toQueryFormat(current.getFirstName().toUpperCase()+ " " + current.getLastName().toUpperCase())); //give the right format to the string in order to compare it
                    //compare two strings
                    if(lookup.contains(searchTermConvertedFaster))
                        foundElements.add(current);//add the entry to the sorted list if it matches the query;
                }
            }
            MyArrayList<Customer> copyList;
            copyList= foundElements.toList();
            j=0;
            //Copping the List to an array
            found = new Customer[copyList.size()];
            while (j<(found.length)) {
                found[j] = copyList.get(j);//copy current list element to the array in the corresponding position
                //System.out.println(j+" " + found[j].getLastName()+ " " + found[j].getFirstName()+" " + found[j].getID());
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
            for (int i = 0; i < customerArray.size(); i++) {
                //used as a temporary variable in the methods
                Customer temp = customerArray.get(i);
                if (temp.getID().equals(inputID)) { //want to check if they are equal content-wise , not their memory locations
                    //blacklistID and Remove customer
                    blackList.add(inputID);
                    customerArray.remove(temp);
                    return false;
                }
            }
            return true;
        }
}
