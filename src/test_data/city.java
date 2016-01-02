/**
 * 
 */
package test_data;

import java.util.ArrayList;

import model.coordinate;
import model.node;

/**
 * @author Masoud Gholami
 *
 */
public class city {
	private int id;
	private coordinate city_center;
	private ArrayList<node> stations;
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the city_center
	 */
	public coordinate getCity_center() {
		return city_center;
	}
	/**
	 * @param city_center the city_center to set
	 */
	public void setCity_center(coordinate city_center) {
		this.city_center = city_center;
	}
	/**
	 * @return the stations
	 */
	public ArrayList<node> getStations() {
		return stations;
	}
	/**
	 * @param stations the stations to set
	 */
	public void setStations(ArrayList<node> stations) {
		this.stations = stations;
	}
	
}
