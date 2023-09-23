package uk.ac.warwick.cs126.util;

import uk.ac.warwick.cs126.interfaces.IConvertToPlace;
import uk.ac.warwick.cs126.models.Customer;
import uk.ac.warwick.cs126.models.Place;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import uk.ac.warwick.cs126.structures.BinaryTree;
import uk.ac.warwick.cs126.structures.MyHashMap;
import uk.ac.warwick.cs126.util.myCompare;

/**
 * Chose to implement BinaryTree, since we don't know the amount of Places that we want to search through. If we knew then
 * maybe a Hashmap would be more efficient. But since we don't, a Binary Tree can be considered more optimal. Also, there is
 * only one matching place for each location(therearenoduplicate latitude and longitude pairs.), which further justifies the
 * usage of BinaryTree structure.
 */
public class ConvertToPlace implements IConvertToPlace {
    private MyHashMap<Float,Place> places;
    private Place temp;
    public ConvertToPlace() {
        Place[] array;
        array=getPlacesArray();
        // Declaration of a new Binary tree

        places = new MyHashMap<>(array.length);
        //Store every place stored in the array to the newly created BinaryTree
        for (Place place : array) {
                places.add(place.getLatitude()/place.getLongitude()+place.getLongitude(),place);
        }
    }

    public Place convert(float latitude, float longitude) {
        //-DID-TODO
       // if(latitude==0.0f && longitude==0.0f)
        //    return new Place("", "", 0.0f, 0.0f);
        Place findthis=new Place("","",latitude,longitude);
        //call the function to find the place that has the same coordinates as the parameters
         temp=places.get(latitude/longitude+longitude);
         //if the element was not found
         if(temp==null)
             //return a null Declared Place instance
             return new Place("", "", 0.0f, 0.0f);
         //return the place that was found
         return temp;
    }

    public Place[] getPlacesArray() {
        Place[] placeArray = new Place[0];

        try {
            InputStream resource = ConvertToPlace.class.getResourceAsStream("/data/placeData.tsv");
            if (resource == null) {
                String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
                String resourcePath = Paths.get(currentPath, "data", "placeData.tsv").toString();
                File resourceFile = new File(resourcePath);
                resource = new FileInputStream(resourceFile);
            }

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

            Place[] loadedPlaces = new Place[lineCount - 1];

            BufferedReader tsvReader = new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(inputStreamBytes), StandardCharsets.UTF_8));

            int placeCount = 0;
            String row;

            tsvReader.readLine();
            while ((row = tsvReader.readLine()) != null) {
                if (!("".equals(row))) {
                    String[] data = row.split("\t");
                    Place place = new Place(
                            data[0],
                            data[1],
                            Float.parseFloat(data[2]),
                            Float.parseFloat(data[3]));
                    loadedPlaces[placeCount++] = place;
                }
            }
            tsvReader.close();

            placeArray = loadedPlaces;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return placeArray;
    }
}

