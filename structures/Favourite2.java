package uk.ac.warwick.cs126.structures;

import uk.ac.warwick.cs126.models.Favourite;
import java.util.Date;
import uk.ac.warwick.cs126.util.myCompare;
public class Favourite2 {
    public int favouritesCount;
    public Long CustID;
    public Favourite[] favourites;
    public Favourite2(Long CustID, int favouritesCount,Favourite[] favourites){
        this.CustID=CustID;
        this.favouritesCount=favouritesCount;
        this.favourites=favourites;
    }
    //O(n) to calculate latest date
    public Date getLatest(){
        return favourites[favourites.length-1].getDateFavourited();
    }
}
