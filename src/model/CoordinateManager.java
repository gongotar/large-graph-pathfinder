/**
 * 
 */
package model;

/**
 * @author Masoud Gholami
 *
 */
public class CoordinateManager {

	  // declare public constants
	  
	  /**
	   * The minimum allowed latitude
	   */
	  public static float MIN_LATITUDE = Float.valueOf("-90.0000");
	  
	  /**
	   * The maximum allowed latitude
	   */
	  public static float MAX_LATITUDE = Float.valueOf("90.0000");
	  
	  /**
	   * The minimum allowed longitude
	   */
	  public static float MIN_LONGITUDE = Float.valueOf("-180.0000");
	  
	  /**
	   * The maximum allowed longitude 
	   */
	  public static float MAX_LONGITUDE = Float.valueOf("180.0000");
	  
	  /**
	   * The diameter of the Earth used in calculations
	   */
	  public static float EARTH_DIAMETER = Float.valueOf("12756.274");

	  /**
	   * A method to validate a latitude value
	   *
	   * @param latitude the latitude to check is valid
	   *
	   * @return         true if, and only if, the latitude is within the MIN and MAX latitude
	   */
	  public static boolean isValidLatitude(float latitude) {
	    if(latitude >= MIN_LATITUDE && latitude <= MAX_LATITUDE) {
	      return true;
	    } else {
	      return false;
	    }
	  }
	  
	  /**
	   * A method to validate a longitude value
	   *
	   * @param longitude the longitude to check is valid
	   *
	   * @return          true if, and only if, the longitude is between the MIN and MAX longitude
	   */
	  public static boolean isValidLongitude(float longitude) {
	    if(longitude >= MIN_LONGITUDE && longitude <= MAX_LONGITUDE) {
	      return true;
	    } else {
	      return false;
	    }
	  }
	  
	  /**
	   * A private method to calculate the latitude constant
	   *
	   * @return a double representing the latitude constant
	   */
	  public static double latitudeConstant() {
	    return EARTH_DIAMETER * (Math.PI / Float.valueOf("360"));
	    //return EARTH_DIAMETER * (Float.valueOf("3.14") / Float.valueOf("360"));
	  }
	  
	  /**
	   * A private method to caluclate the longitude constant
	   *
	   * @param latitude  a latitude coordinate in decimal notation
	   *
	   * @return a double representing the longitude constant
	   */
	  public static double longitudeConstant(float latitude) {
	  
	    //return Math.abs( Math.cos(Math.abs(latitude)));
	    return EARTH_DIAMETER * Math.PI * Math.abs(Math.cos(Math.abs(latitude))) / Float.valueOf("360");
	  
	  }
	  
	  /**
	   * A method to add distance in a northerly direction to a coordinate
	   *
	   * @param latitude  a latitude coordinate in decimal notation
	   * @param longitude a longitude coordinate in decimal notation
	   * @param distance  the distance to add in metres
	   *
	   * @return          the new coordinate
	   */
	  public static coordinate addDistanceNorth(float latitude, float longitude, int distance) {
	  
	    // check on the parameters
	    if(isValidLatitude(latitude) == false || isValidLongitude(longitude) == false || distance <= 0) {
	      throw new IllegalArgumentException("All parameters are required and must be valid");
	    }
	    
	    // convert the distance from metres to kilometers
	    float kilometers = distance / new Float(1000);  
	    
	    // calculate the new latitude
	    double newLat = latitude + (kilometers / latitudeConstant());
	    
	    return new coordinate(new Float(newLat).floatValue(), longitude);
	  
	  }
	  
	  /**
	   * A method to add distance in a southerly direction to a coordinate
	   *
	   * @param latitude  a latitude coordinate in decimal notation
	   * @param longitude a longitude coordinate in decimal notation
	   * @param distance  the distance to add in metres
	   *
	   * @return          the new coordinate
	   */
	  public static coordinate addDistanceSouth(float latitude, float longitude, int distance) {
	  
	    // check on the parameters
	    if(isValidLatitude(latitude) == false || isValidLongitude(longitude) == false || distance <= 0) {
	      throw new IllegalArgumentException("All parameters are required and must be valid");
	    }
	    
	    // convert the distance from metres to kilometers
	    float kilometers = distance / new Float(1000);
	    
	    // calculate the new latitude
	    double newLat = latitude - (kilometers / latitudeConstant());
	    
	    return new coordinate(new Float(newLat).floatValue(), longitude);
	  
	  }
	  
	  /**
	   * A method to add distance in an easterly direction to a coordinate
	   *
	   * @param latitude  a latitude coordinate in decimal notation
	   * @param longitude a longitude coordinate in decimal notation
	   * @param distance  the distance to add in metres
	   *
	   * @return          the new coordinate
	   */
	  public static coordinate addDistanceEast(float latitude, float longitude, int distance) {
	  
	    // check on the parameters
	    if(isValidLatitude(latitude) == false || isValidLongitude(longitude) == false || distance <= 0) {
	      throw new IllegalArgumentException("All parameters are required and must be valid");
	    }
	    
	    // calculate the new longitude
	    double newLng = longitude + (distance / longitudeConstant(latitude));
	    
	    return new coordinate(latitude, new Float(newLng).floatValue());  
	  }
	  
	  /**
	   * A method to add distance in an westerly direction to a coordinate
	   *
	   * @param latitude  a latitude coordinate in decimal notation
	   * @param longitude a longitude coordinate in decimal notation
	   * @param distance  the distance to add in metres
	   *
	   * @return          the new coordinate
	   */
	  public static coordinate addDistanceWest(float latitude, float longitude, int distance) {
	  
	    // check on the parameters
	    if(isValidLatitude(latitude) == false || isValidLongitude(longitude) == false || distance <= 0) {
	      throw new IllegalArgumentException("All parameters are required and must be valid");
	    }
	    
	    // calculate the new longitude
	    double newLng = longitude - (distance / longitudeConstant(latitude));
	    
	    return new coordinate(latitude, new Float(newLng).floatValue());  
	  }
	  
	  /**
	   * A method to build four coordinates representing a bounding box given a start coordinate and a distance
	   *
	   * @param latitude  a latitude coordinate in decimal notation
	   * @param longitude a longitude coordinate in decimal notation
	   * @param distance  the distance to add in metres
	   *
	   * @return          a hashMap representing the bounding box (NE,SE,SW,NW)
	   */
	  public static java.util.HashMap<String, coordinate> getBoundingBox(float latitude, float longitude, int distance) {
	  
	    // check on the parameters
	    if(isValidLatitude(latitude) == false || isValidLongitude(longitude) == false || distance <= 0) {
	      throw new IllegalArgumentException("All parameters are required and must be valid");
	    }
	    
	    // declare helper variables
	    java.util.HashMap<String, coordinate> boundingBox = new java.util.HashMap<String, coordinate>();
	    
	    // calculate the coordinates
	    coordinate north = addDistanceNorth(latitude, longitude, distance);
	    coordinate south = addDistanceSouth(latitude, longitude, distance);
	    coordinate east  = addDistanceEast(latitude, longitude, distance);
	    coordinate west  = addDistanceWest(latitude, longitude, distance);
	    
	    // build the bounding box object
	    boundingBox.put("NE", new coordinate(north.getLatitude(), east.getLongitude()));
	    boundingBox.put("SE", new coordinate(south.getLatitude(), east.getLongitude()));
	    boundingBox.put("SW", new coordinate(south.getLatitude(), west.getLongitude()));
	    boundingBox.put("NW", new coordinate(north.getLatitude(), west.getLongitude()));
	    
	    // return the bounding box object
	    return boundingBox;  
	  }
	}
