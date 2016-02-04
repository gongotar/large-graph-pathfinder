/**
 * 
 */
package test_data;

import java.util.ArrayList;

import db.ReadDatabase_alt;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Forest;
import enums.edge_type;
import graphics.EditorMouseMenu;
import model.edge;
import model.network;
import model.node;

/**
 * @author Masoud Gholami
 *
 */
public class DbTestData {

	private static String path = "db/simple.graphdb";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		network netw = getDataFromDb(path);
		fix_network_edges(netw);
		Forest<node, edge> forest
			= minimumSpanningTree(netw, netw.getNodes().get(0));
		addTimeTable(forest);
		EditorMouseMenu.create_graph_visually(netw);
	}

	/**
	 * If there is an edge to one direction, then there 
	 * should be another edge in the opposite direction
	 * 
	 * @param netw	the network of nodes and edges
	 * @see DbTestData
	 */
	private static void fix_network_edges(network netw) {
		ArrayList<node> nodes = netw.getNodes();
		ArrayList<edge> edges = netw.getEdges();
		int id = 0;
		for (edge edge : edges) {
			if(id < edge.getId())
				id = edge.getId();
		}
		
		id ++;
		
		for (node node : nodes) {
			ArrayList<edge> out_edges = node.getOutgoing_edges();
			for (edge edge : out_edges) {
				node target = edge.getEnd();
				ArrayList<model.edge> target_out_edges 
					= target.getOutgoing_edges();
				boolean check = false;
				for (edge edge2 : target_out_edges) {
					if(edge2.getEnd().equals(node)){
						check = true;
						break;
					}
				}
				if(!check){
					edge new_edge = new edge();
					new_edge.setEnd(node);
					new_edge.setStart(target);
					new_edge.setType(edge_type.car);
					new_edge.setFeasible(true);
					new_edge.setId(id ++);
					node.getIncoming_edges().add(new_edge);
					target.getOutgoing_edges().add(new_edge);
					netw.getEdges().add(new_edge);
				}
					
			}
		}
		
	}

	/**
	 * Assigns randomly generated lines and timetables to
	 * each edge
	 * 
	 * @param	netw	the given network with all the nodes and edges
	 * @see		DbTestData
	 */
	private static void addTimeTable(Forest<node, edge> forest) {
		// TODO Auto-generated method stub
		
	}

	private static Forest<node, edge> minimumSpanningTree(network netw, node root) {
    	ArrayList<node> nodes = netw.getNodes();
    	ArrayList<edge> edges = netw.getEdges();
    	
    	DirectedGraph<node, edge> graph =
    		new DirectedSparseGraph<node, edge>();
    	
    	for (node node : nodes) {
    		graph.addVertex(node);
		}
    	
    	for (edge edge : edges) {
			graph.addEdge(edge, edge.getStart(), edge.getEnd());
		}
    	
    	Forest<node, edge> forest = new DelegateForest<node, edge>();
    	MinimumSpanningForest<node, edge> msf 
    		= new MinimumSpanningForest<node, edge>(graph, forest, root);
    		
		return msf.getForest();
	}

	/**
	 * Loads the data from the test database and converts them
	 * into network format
	 * 
	 * @param path	the path of the database
	 * @return	a network of the data
	 * @see DbTestData
	 */
	private static network getDataFromDb(String path) {
		ReadDatabase_alt db = new ReadDatabase_alt(path);
		ArrayList<node> nodes = db.getAllNodes();
		ArrayList<edge> edges = db.getAllEdges(nodes);
		db.Shutdown();
		network netw = new network();
		netw.setEdges(edges);
		netw.setNodes(nodes);
		
		return netw;
	}

	 
}
