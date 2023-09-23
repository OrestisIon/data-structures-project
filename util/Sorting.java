package uk.ac.warwick.cs126.util;

import java.util.Arrays;
import java.util.Comparator;

import uk.ac.warwick.cs126.structures.MyArrayList;

public class Sorting<E> {
    public void mergeSort(E[] S, Comparator<E> elements){
         int n=S.length;
         if(n<2)
             return;
         int mid=n/2;
         E[] S1= Arrays.copyOfRange(S,0,mid);
         E[] S2= Arrays.copyOfRange(S,mid,n);
         mergeSort(S1,elements);
         mergeSort(S2,elements);
         merge(S1,S2,S,elements);
    }
    private void merge(E[] S1, E[] S2,E[] S, Comparator<E> elements){
         int i=0,j=0;
         while (i+j < S.length){
             if (j==(S2.length) || (i<S1.length && elements.compare(S1[i],S2[j])< 0)){
                 S[i+j]=S1[i++];
             }
             else {
                 S[i + j] = S2[j++];
             }
         }
    }

}
