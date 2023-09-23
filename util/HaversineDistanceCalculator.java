package uk.ac.warwick.cs126.util;

public class HaversineDistanceCalculator {
    private final static float R = 6372.8f;
    private final static float kilometresInAMile = 1.609344f;
    public static double Rlat1,Rlat2,Rlon1,Rlon2;
    public static float inKilometres(float lat1, float lon1, float lat2, float lon2) {
        //-Done-TODO
        double d;
        d=calculate(lat1, lon1, lat2, lon2);
        return (float)Math.round(d*10)/10;
    }

    public static float inMiles(float lat1, float lon1, float lat2, float lon2) {
        //-Done-TODO
        double inkm;
        inkm=calculate(lat1, lon1, lat2, lon2);
        return (float)Math.round((inkm/kilometresInAMile)*10)/10;
    }
    private static double calculate(float lat1, float lon1, float lat2, float lon2) {
        double a, d;
        double c;
        Rlat1=Math.toRadians(lat1);
        Rlat2=Math.toRadians(lat2);
        Rlon1=Math.toRadians(lon1);
        Rlon2=Math.toRadians(lon2);
        a=Math.pow(Math.sin((Rlat2-Rlat1)/2),2)+Math.cos(Rlat1)*Math.cos(Rlat2)*Math.pow(Math.sin((Rlon2-Rlon1)/2),2);
        c= 2* Math.asin(Math.sqrt(a));
        d=R*c;
        return d;
    }

}