/**
 * 
 */
package model;

import java.util.ArrayList;

import test_data.city;
import enums.node_type;

/**
 * @author Masoud Gholami
 *
 */
public class node {
	
	private int		id;
	private city	city;
	private coordinate	coordinate;
	private node_type	type;
	private ArrayList<edge> outgoing_edges;
	private ArrayList<edge> incoming_edges;
	private ArrayList<label> labels;
	
	/**
	 * Default class constructor
	 */
	public node() {
		outgoing_edges = new ArrayList<edge>();
		incoming_edges = new ArrayList<edge>();
		labels = new ArrayList<label>();
	}
	
	/**
	 * Prints the node properties as a string
	 * 
	 * @return	node properties
	 * @see	node
	 */
	@Override
	public String toString(){
		String text = "";
		//if(this.getCity() != null)
			//text += "City: " + this.getCity().getId() + ", ";
		text += this.getId();
		text += ", " + this.getType().toString();
		text += "@" + this.getCoordinate().toString();
		return text;
	}
	
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
	 * @return the lat
	 */
	public float getLat() {
		return this.getCoordinate().getLatitude();
	}
	/**
	 * @param lat the lat to set
	 */
	public void setLat(float lat) {
		this.getCoordinate().setLatitude(lat);
	}
	/**
	 * @return the lon
	 */
	public float getLon() {
		return this.getCoordinate().getLongitude();
	}
	/**
	 * @param lon the lon to set
	 */
	public void setLon(float lon) {
		this.getCoordinate().setLongitude(lon);
	}
	/**
	 * @return the type
	 */
	public node_type getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(node_type type) {
		this.type = type;
	}
	/**
	 * @return the edges
	 */
	public ArrayList<edge> getOutgoing_edges() {
		return outgoing_edges;
	}
	/**
	 * @param edges the edges to set
	 */
	public void setOutgoing_edges(ArrayList<edge> edges) {
		this.outgoing_edges = new ArrayList<edge>(edges);
	}
	/**
	 * @return the incoming_edges
	 */
	public ArrayList<edge> getIncoming_edges() {
		return incoming_edges;
	}
	/**
	 * @param incoming_edges the incoming_edges to set
	 */
	public void setIncoming_edges(ArrayList<edge> incoming_edges) {
		this.incoming_edges = new ArrayList<edge>(incoming_edges);
	}
	/**
	 * @return the labels
	 */
	public ArrayList<label> getLabels() {
		return labels;
	}
	/**
	 * @param labels the labels to set
	 */
	public void setLabels(ArrayList<label> labels) {
		this.labels = new ArrayList<label>(labels);
	}
	
	/**
	 * Adds a new label to the node
	 * 
	 * @param	l	the new label
	 * @see		node
	 */
	public void addLabel(label l){
		this.labels.add(l);
	}
	/**
	 * @return the coordinate
	 */
	public coordinate getCoordinate() {
		return coordinate;
	}
	/**
	 * @param coordinate the coordinate to set
	 */
	public void setCoordinate(coordinate coordinate) {
		this.coordinate = coordinate;
	}
	/**
	 * @return the city
	 */
	public city getCity() {
		return city;
	}
	/**
	 * @param city the city to set
	 */
	public void setCity(city city) {
		this.city = city;
	}
	
}
