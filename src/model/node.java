/**
 * 
 */
package model;

import java.util.ArrayList;

import enums.node_type;

/**
 * @author Masoud Gholami
 *
 */
public class node {
	
	private int		id;
	private long	lat, lon;
	private node_type	type;
	private ArrayList<edge> outgoing_edges;
	private ArrayList<edge> incoming_edges;
	private ArrayList<label> labels;
	
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
	public long getLat() {
		return lat;
	}
	/**
	 * @param lat the lat to set
	 */
	public void setLat(long lat) {
		this.lat = lat;
	}
	/**
	 * @return the lon
	 */
	public long getLon() {
		return lon;
	}
	/**
	 * @param lon the lon to set
	 */
	public void setLon(long lon) {
		this.lon = lon;
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
	
	/*
	 * Adds a new label to the node
	 * 
	 * @param	l	the new label
	 * @see		node
	 */
	public void addLabel(label l){
		this.labels.add(l);
	}
	
}
