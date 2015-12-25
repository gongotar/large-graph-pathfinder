package core;
import java.util.ArrayList;
import java.util.PriorityQueue;

import model.edge;
import model.label;
import model.network;
import model.node;

/**
 * 
 */

/**
 * @author Masoud Gholami
 *
 */

public class dijkstra {

	public static void main(String args[]){
	
		
	}
	
	/*
	 * Computes all of the Pareto-optimal solutions in a network (graph)
	 * using the modified (generalized) Dijkstra algorithm.
	 * 
	 * @param	netw	a digraph of nodes and weighted edges (Time table as the weight)
	 * @param	start	a set of possible start nodes
	 * @return			a set of Pareto-optimal labels defining paths in the graph
	 * @see				dijkstra 
	 */
	
	public static network pareto_opt(network netw, ArrayList<node> start){
		network new_netw = netw.clone();
		
		PriorityQueue<label> pq = new PriorityQueue<label>();
		
		for (node n : start) {
			label l = new label(n);
			pq.add(l);
		}
		
		while(!pq.isEmpty()){
			label l = pq.poll();
			node node = l.getNode();
			for (edge e : node.getOutgoing_edges()) {
				if(!e.isFeasible()) continue;	// ignore this edge
				label new_label = create_label(l, e);
			}
		}
		
		// TODO Method implementations here
		
		return new_netw;
	}

	/*
	 * Creates a new label by using the previous label and 
	 * adding a new edge to it considering the aggregation
	 * of the edge attributes and the label attributes
	 * 
	 * @param	l		the previous label
	 * @param	e		to be added edge
	 * @return	label	new label containing the previous label and the new edge
	 * @see		dijkstra
	 */
	private static label create_label(label l, edge e) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
