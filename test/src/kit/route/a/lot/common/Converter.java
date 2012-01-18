package kit.route.a.lot.common;


import java.lang.Math;
import java.lang.Integer;

public class Converter{

    private final double pi = 3.14159265358979;

    /* Ellipsoid model constants (actual values here are for WGS84) */
    private final double sm_a = 6378137.0;
    private final double sm_b = 6356752.314;
    private  final double sm_EccSquared = 6.69437999013e-03;
    private final double UTMScaleFactor = 0.9996;
	/*Singleton*/
	private static Converter converter;
    /*Konstruktor*/
 	private Converter() { }
	/*Singleton*/
	public static Converter getInstance() {
		
		if(converter == null) {
			converter = new Converter();
		}//end if
		return converter;
	}//end getInstance

    /*
    * DegToRad
    *
    * Converts degrees to radians.
    *
    */
    private double DegToRad (double deg)
    {
        return (deg / 180.0 * pi);
    }


    /*
    * UTMCentralMeridian
    *
    * Determines the central meridian for the given UTM zone.
    *
    * Inputs:
    *     zone - An integer value designating the UTM zone, range [1,60].
    *
    * Returns:
    *   The central meridian for the given UTM zone, in radians, or zero
    *   if the UTM zone parameter is outside the range [1,60].
    *   Range of the central meridian is the radian equivalent of [-177,+177].
    *
    */
    private double UTMCentralMeridian (int zone)
    {
        double cmeridian;

        cmeridian = DegToRad (-183.0 + (zone * 6.0));
    
        return cmeridian;
    }

    /*
    * ArcLengthOfMeridian
    *
    * Computes the ellipsoidal distance from the equator to a point at a
    * given latitude.
    *
    * Reference: Hoffmann-Wellenhof, B., Lichtenegger, H., and Collins, J.,
    * GPS: Theory and Practice, 3rd ed.  New York: Springer-Verlag Wien, 1994.
    *
    * Inputs:
    *     lat - Latitude of the point, in radians.
    *
    * Globals:
    *     sm_a - Ellipsoid model major axis.
    *     sm_b - Ellipsoid model minor axis.
    *
    * Returns:
    *     The ellipsoidal distance of the point from the equator, in meters.
    *
    */
    private double ArcLengthOfMeridian (double lat)
    {
        double alpha, beta, gamma, delta, epsilon, n;
        double result;

        /* Precalculate n */
        n = (sm_a - sm_b) / (sm_a + sm_b);

        /* Precalculate alpha */
        alpha = ((sm_a + sm_b) / 2.0)
           * (1.0 + (Math.pow (n, 2.0) / 4.0) + (Math.pow (n, 4.0) / 64.0));

        /* Precalculate beta */
        beta = (-3.0 * n / 2.0) + (9.0 * Math.pow (n, 3.0) / 16.0)
           + (-3.0 * Math.pow (n, 5.0) / 32.0);

        /* Precalculate gamma */
        gamma = (15.0 * Math.pow (n, 2.0) / 16.0)
            + (-15.0 * Math.pow (n, 4.0) / 32.0);
    
        /* Precalculate delta */
        delta = (-35.0 * Math.pow (n, 3.0) / 48.0)
            + (105.0 * Math.pow (n, 5.0) / 256.0);
    
        /* Precalculate epsilon */
        epsilon = (315.0 * Math.pow (n, 4.0) / 512.0);
    
    /* Now calculate the sum of the series and return */
    result = alpha
        * (lat + (beta * Math.sin (2.0 * lat))
            + (gamma * Math.sin (4.0 * lat))
            + (delta * Math.sin (6.0 * lat))
            + (epsilon * Math.sin (8.0 * lat)));

    return result;
    }


/*
    * MapLatLonToXY
    *
    * Converts a latitude/longitude pair to x and y coordinates in the
    * Transverse Mercator projection.  Note that Transverse Mercator is not
    * the same as UTM; a scale factor is required to convert between them.
    *
    * Reference: Hoffmann-Wellenhof, B., Lichtenegger, H., and Collins, J.,
    * GPS: Theory and Practice, 3rd ed.  New York: Springer-Verlag Wien, 1994.
    *
    * Inputs:
    *    lat - Latitude of the point, in radians.
    *    lon - Longitude of the point, in radians.
    *    centmeri - Longitude of the central meridian to be used, in radians.
    *
    * Outputs:
    *    xy - A 2-element array containing the x and y coordinates
    *         of the computed point.
    *
    * Returns:
    *    The function does not return a value.
    *
    */
    private double[] MapLatLonToXY (double lat, double lon, double centmeri)
    {
        double N, nu2, ep2, t, t2, l;
        double l3coef, l4coef, l5coef, l6coef, l7coef, l8coef;
        double tmp;
	double[] xy = new double[2];

        /* Precalculate ep2 */
        ep2 = (Math.pow (sm_a, 2.0) - Math.pow (sm_b, 2.0)) / Math.pow (sm_b, 2.0);
    
        /* Precalculate nu2 */
        nu2 = ep2 * Math.pow (Math.cos (lat), 2.0);
    
        /* Precalculate N */
        N = Math.pow (sm_a, 2.0) / (sm_b * Math.sqrt (1 + nu2));
    
        /* Precalculate t */
        t = Math.tan (lat);
        t2 = t * t;
        tmp = (t2 * t2 * t2) - Math.pow (t, 6.0);

        /* Precalculate l */
        l = lon - centmeri;
    
        /* Precalculate coefficients for l**n in the equations below
           so a normal human being can read the expressions for easting
           and northing
           -- l**1 and l**2 have coefficients of 1.0 */
        l3coef = 1.0 - t2 + nu2;
    
        l4coef = 5.0 - t2 + 9 * nu2 + 4.0 * (nu2 * nu2);
    
        l5coef = 5.0 - 18.0 * t2 + (t2 * t2) + 14.0 * nu2
            - 58.0 * t2 * nu2;
    
        l6coef = 61.0 - 58.0 * t2 + (t2 * t2) + 270.0 * nu2
            - 330.0 * t2 * nu2;
    
        l7coef = 61.0 - 479.0 * t2 + 179.0 * (t2 * t2) - (t2 * t2 * t2);
    
        l8coef = 1385.0 - 3111.0 * t2 + 543.0 * (t2 * t2) - (t2 * t2 * t2);
    
        /* Calculate easting (x) */
        xy[0] = N * Math.cos (lat) * l
            + (N / 6.0 * Math.pow (Math.cos (lat), 3.0) * l3coef * Math.pow (l, 3.0))
            + (N / 120.0 * Math.pow (Math.cos (lat), 5.0) * l5coef * Math.pow (l, 5.0))
            + (N / 5040.0 * Math.pow (Math.cos (lat), 7.0) * l7coef * Math.pow (l, 7.0));
    
        /* Calculate northing (y) */
        xy[1] = ArcLengthOfMeridian (lat)
            + (t / 2.0 * N * Math.pow (Math.cos (lat), 2.0) * Math.pow (l, 2.0))
            + (t / 24.0 * N * Math.pow (Math.cos (lat), 4.0) * l4coef * Math.pow (l, 4.0))
            + (t / 720.0 * N * Math.pow (Math.cos (lat), 6.0) * l6coef * Math.pow (l, 6.0))
            + (t / 40320.0 * N * Math.pow (Math.cos (lat), 8.0) * l8coef * Math.pow (l, 8.0));
    
        return xy;
    }
    

/*
    * LatLonToUTMXY
    *
    * Converts a latitude/longitude pair to x and y coordinates in the
    * Universal Transverse Mercator projection.
    *
    * Inputs:
    *   lat - Latitude of the point, in radians.
    *   lon - Longitude of the point, in radians.
    *   zone - UTM zone to be used for calculating values for x and y.
    *          If zone is less than 1 or greater than 60, the routine
    *          will determine the appropriate zone from the value of lon.
    *
    * Outputs:
    *   xy - A 2-element array where the UTM x and y values will be stored.
    *
    * Returns:
    *   The UTM zone used for calculating the values of x and y.
    *
    */
    private double[] LatLonToUTMXY (double lat, double lon, int zone)
    {
        double[] xy = MapLatLonToXY (lat, lon, UTMCentralMeridian (zone));
	int[] utmDaten = new int[2];

        /* Adjust easting and northing for UTM system. */
        xy[0] = xy[0] * UTMScaleFactor + 500000.0;
        xy[1] = xy[1] * UTMScaleFactor;
        if (xy[1] < 0)

            xy[1] = xy[1] + 10000000;

        return xy;
    }



     public int[] utmConverter(double lat, double lon){
        double xy[] = new double[2];
	int[] utmDaten = new int[2];
	int zone;	
	

        if ((lon < -180.0) || (180.0 <= lon)) {
            System.out.println("lon ist ungültig");
            return utmDaten;
        } else if ((lat < -90.0) || (90.0 < lat)) {
            System.out.println("lat ist ungültig");
            return utmDaten;
        }

        // Compute the UTM zone.
        //zone = Math.floor ((lon + 180.0) / 6) + 1;
	zone = (int)Math.floor((lon + 180.0) / 6) + 1;
	

        xy = LatLonToUTMXY (DegToRad (lat),DegToRad (lon), zone);
	utmDaten[0] = Math.round((float)xy[0]);
	utmDaten[1] = Math.round((float)xy[1]);
        
        System.out.println(Math.round((float)xy[0]));
        System.out.println(Math.round((float)xy[1]));
        return utmDaten;
	
    }//end main


}//end class
