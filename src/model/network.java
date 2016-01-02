/**
 * 
 */

package model;

import java.util.ArrayList;

/**
 * @author Masoud Gholami
 *
 */
public class network {
	
	private ArrayList<node> nodes;
	private ArrayList<edge> edges;
	
	/**
	 * Default class constructor
	 * 
	 * @see network
	 */
	public network(){
		
	}
	
	/**
	 * Class constructor specifying the url of 
	 * the graph database to be loaded.
	 * 
	 * @param	url		the url of a graph database
	 * @see		network
	 */
	public network(String url){
		this();
		this.get_from_db(url);
	}
	
	/**
	 * (non-Javadoc)
	 * Creates a new instance of this network with the same edges and nodes
	 * @see java.lang.Object#clone()
	 */
	public network clone(){
		network new_netw = new network();
		new_netw.setEdges(this.getEdges());
		new_netw.setNodes(this.getNodes());
		return new_netw;
	}
	
	/**
	 * Loads a graph from a graph database.
	 * 
	 * @param	url		the url of a graph database
	 * @see		network
	 */
	public void get_from_db(String url){
		// TODO method implementation
	}
	
	/**
	 * Store this network to a graph database.
	 * 
	 * @param	url		the url of a graph database
	 * @see		network
	 */
	public void store_to_db(String url){
		// TODO method implementation
	}
	
	/**
	 * @return the nodes
	 */
	public ArrayList<node> getNodes() {
		return nodes;
	}

	/**
	 * @param nodes 	the nodes to set
	 */
	public void setNodes(ArrayList<node> nodes) {
		this.nodes = new ArrayList<node>(nodes);
	}

	/**
	 * @return the edges
	 */
	public ArrayList<edge> getEdges() {
		return edges;
	}

	/**
	 * @param edges the edges to set
	 */
	public void setEdges(ArrayList<edge> edges) {
		this.edges = new ArrayList<edge>(edges);
	}
}
