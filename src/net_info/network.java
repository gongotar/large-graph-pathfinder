/**
 * 
 */

package net_info;

/**
 * @author Masoud Gholami
 *
 */
public class network {
	
	private node[] nodes;
	private edge[] edges;
	
	/*
	 * Default class constructor
	 * 
	 * @see network
	 */
	public network(){
		
	}
	
	/*
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
	
	/*
	 * Loads a graph from a graph database.
	 * 
	 * @param	url		the url of a graph database
	 * @see		network
	 */
	public void get_from_db(String url){
		
	}

	/**
	 * @return the nodes
	 */
	public node[] getNodes() {
		return nodes;
	}

	/**
	 * @param nodes 	the nodes to set
	 */
	public void setNodes(node[] nodes) {
		this.nodes = nodes.clone();
	}

	/**
	 * @return the edges
	 */
	public edge[] getEdges() {
		return edges;
	}

	/**
	 * @param edges the edges to set
	 */
	public void setEdges(edge[] edges) {
		this.edges = edges.clone();
	}
}
