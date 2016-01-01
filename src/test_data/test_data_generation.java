/**
 * 
 */
package test_data;

import java.util.ArrayList;
import java.util.Random;

import enums.edge_type;
import enums.node_type;
import model.coordinate;
import model.edge;
import model.network;
import model.node;

/**
 * @author Masoud Gholami
 *
 */
public class test_data_generation {

	public static void main(String args[]){
		int bus = 0, car = 0, train = 0;
		for (int i = 0; i < 1000; i++) {
			node_type t = random_node_type();
			if(t.equals(node_type.bus_station))
				bus++;
			if(t.equals(node_type.car_station))
				car++;
			if(t.equals(node_type.train_station))
				train++;
		}
		System.out.println("bus: " + bus + ", car: " + car + ", train: " + train);
	}
	
	/*
	 * Generates a random network of nodes and edges 
	 * with the given size
	 * 
	 * @param	size	no. of the nodes in the network
	 * @return	network of the given size
	 * @see 	test_data_generation
	 */
	public static network generate_netw(int size){
		network netw = new network();
		ArrayList<coordinate> coordinations =
				compute_node_coordinations(size);
		ArrayList<node> nodes = new ArrayList<node>();
		ArrayList<edge> edges = new ArrayList<edge>();
		
		for (int i = 0; i < size; i++) {
			node node = new node();
			node.setId(i);
			node.setType(random_node_type());
			node.setCoordinate(coordinations.get(i));
			edges.addAll(assign_random_outgoing_edges(nodes, i));
			nodes.add(node);
		}
		
		assign_netw_lines(nodes);
		netw.setNodes(nodes);
		netw.setEdges(edges);
		return netw;
	}

	/*
	 * Assigns randomly generated lines and timetables to
	 * each edge
	 * 
	 * @param	nodes	list of all of the nodes
	 * @see		test_data_generation
	 */
	private static void assign_netw_lines(ArrayList<node> nodes) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * Assigns random outgoing edges from the i-th node in the 
	 * node list (nodes) to the other nodes in the list. 
	 * the nodes in the near by have more chances to get 
	 * connected through an edge
	 * 
	 * @param	nodes	the list of all of the nodes
	 * @param	i		index of the node in the list whose edges are 
	 * 					to be assigned
	 * @see		test_data_generation
	 */
	private static ArrayList<edge> assign_random_outgoing_edges(ArrayList<node> nodes, int i) {
		// greater values results shorter edges
		double propagation_degree = 4;
		// minimum no. of nodes to be considered for being target_node
		int	minimum_node_consideration = 3;
		// minimum no. of edges for each node
		int min_edge = 1;
		// maximum no. of edges for each node
		int max_edge = 8;
		
		node node = nodes.get(i);
		ArrayList<Double> p = new ArrayList<Double>(); // maximum length is the number of nodes

		double max_distance = Double.MIN_VALUE;
		double min_distance = Double.MAX_VALUE;
		for (int j = 0; j < nodes.size(); j++) {
			if(j == i)		// ignore this node 
				continue;
			node target_node = nodes.get(j);
			double dist = node.getCoordinate()
					.getDistanceTo(target_node.getCoordinate());
			p.add(dist);
			if(max_distance < dist)
				max_distance = dist;
			if(min_distance > dist)
				min_distance = dist;
		}
		
		// p preparation so that it contains more values (probability)
		// for the nodes in the nearby
		
		double treshold = 0;
		for (int j = 0; j < p.size(); j++){
			double modified_value = Math.pow(max_distance + min_distance - p.get(j), propagation_degree);
			p.set(j, modified_value);
			treshold += modified_value;
		}
		
		// eliminate nodes that are far away
		
		treshold = treshold / p.size();
		int more_than_treshold = 0;
		
		if(minimum_node_consideration < nodes.size()){
			while(more_than_treshold <= minimum_node_consideration){
				more_than_treshold = 0;
				for (double d : p)
					if(d >= treshold--)
						more_than_treshold++;
			}
			treshold++;
			for (int j = 0; j < p.size(); j++)
				if(p.get(j) < treshold)
					p.set(j, 0.0);	// set the probability to zero (far away nodes)
		}
		
		// eliminate nodes that are not the same type

		for (int j = 0; j < p.size(); j++) {
			int index = j;
			if(index >= i)
				index ++;	
			if(! nodes.get(index).getType().equals(node.getType())){
				p.set(j, 0.0); // set the probability to zero (nodes of the other types)
			}
			
		}
		
		// Choose n items from p, n between a min & max
		
		// Choose a random value between min and max no. of edges
		Random rand = new Random();
		int n = (int)(rand.nextDouble() * (max_edge - min_edge) + min_edge);
		ArrayList<Integer> indexes = stochastic_choice(p, n);
		
		// Create edges
		
		ArrayList<edge> edges = new ArrayList<edge>();
		
		for (int j = 0; j < indexes.size(); j++) {
			int index = indexes.get(j);
			if(index >= i)
				indexes.set(j, index ++);
			edge new_edge = new edge();
			new_edge.setStart(node);
			new_edge.setEnd(nodes.get(index));
			new_edge.setFeasible(false);
			new_edge.setType(find_edge_type(node, nodes.get(index)));
			if(new_edge.getType() != null)
				edges.add(new_edge);
		}
		
		for (int j = 0; j < nodes.size(); j++) {
			if(i != j && in_neighborhood(node, nodes.get(j))){
				edge new_edge = new edge();
				new_edge.setStart(node);
				new_edge.setEnd(nodes.get(j));
				new_edge.setFeasible(false);
				new_edge.setType(enums.edge_type.walk);
				edges.add(new_edge);
			}
		}
		
		return edges;
	}

	/*
	 * Checks if node1 and node2 in the neighborhood are, so that they 
	 * can be reached from each other by walking
	 * 
	 * @param	node1	the first node to be checked
	 * @param	node2	the second node to be checked
	 * @return	a boolean value, true if the nodes are in the neighborhood, false otherwise
	 * @see		test_data_generation
	 */
	private static boolean in_neighborhood(node node1, node node2) {
		// Maximum distance that can be reached by walking
		double treshold = 1;
		double dist = node1.getCoordinate().getDistanceTo(node2.getCoordinate());
		
		return (dist > treshold) ? false : true;
	}

	/*
	 * Returns the correct type of the edge considering the 
	 * the source node and the destination node 
	 * 
	 * @param	source_node		source node 
	 * @param	dest_node		destination node 
	 * @return	edge type
	 * @see		test__data_generation
	 */
	private static edge_type find_edge_type(node source_node, node dest_node) {
		if(! source_node.getType().equals(dest_node.getType()))
			return null;
		
		edge_type type = null;
		
		if(source_node.getType().equals(enums.node_type.bus_station))
			type = enums.edge_type.bus;
		if(source_node.getType().equals(enums.node_type.car_station))
			type = enums.edge_type.car;
		if(source_node.getType().equals(enums.node_type.train_station))
			type = enums.edge_type.train;
		
		return type;
	}

	/*
	 * Gets an array of n probabilities as input and chooses
	 * n distinct numbers between 0 to n - 1 randomly based on the given
	 * probabilities
	 * 
	 * @param	p	array of probabilities of n numbers
	 * @param	n	number of choices
	 * @return	randomly chosen numbers
	 * @see		test_data_generation 
	 */
	private static ArrayList<Integer> stochastic_choice(ArrayList<Double> p, int n) {

		ArrayList<Double> p_clone = new ArrayList<Double>(p);
		ArrayList<Integer> chosen_indexes = new ArrayList<Integer>();
		ArrayList<Integer> result = new ArrayList<Integer>();
		
		for (int i = 0; i < n; i++) {
			int index = stochastic_choice(p_clone);
			int fixed_index = index;
			for (int j = chosen_indexes.size(); j >= 0; j--) {
				if(fixed_index >= chosen_indexes.get(j))
					fixed_index ++;
			}
			chosen_indexes.add(index);
			p_clone.remove(index);
			result.add(fixed_index);
		}
		return result;
		
	}

	/*
	 * Gets the size of the network as the input
	 * and generates randomly node coordinations
	 * in a normal distributed manner
	 * 
	 * @param	size	size of the network
	 * @return	an array of n randomly generated coordinations
	 * @see		test_data_generation 
	 */
	private static ArrayList<coordinate> compute_node_coordinations(int size) {
		// TODO Auto-generated method stub
		int weight = (int) Math.sqrt(size / 1.6);
		int height = size / weight;
		@SuppressWarnings("unused")
		int extra = size - weight * height;
		return null;
	}

	/*
	 * Returns randomly different node types
	 * with different probabilities
	 * 
	 * @return	randomly chosen node type
	 * @see		test_data_generation  
	 */
	private static node_type random_node_type() {
		ArrayList<Double> p = new ArrayList<Double>();
		p.add(0, 3.0);	// 3 buses
		p.add(1, 1.0);	// 1 car
		p.add(2, 6.0);	// 6 trains
		int choice = stochastic_choice(p);
		return node_type.values()[choice];
	}
	
	/*
	 * Gets an array of n probabilities as input and chooses
	 * a number between 0 to n - 1 randomly based on the given
	 * probabilities
	 * 
	 * @param	p	array of probabilities of n numbers
	 * @return	randomly chosen number
	 * @see		test_data_generation 
	 */
	private static int stochastic_choice(ArrayList<Double> p){
		double sum = 0;
		for (double d : p)
			sum += d;
		Random rn = new Random();
		double rand = rn.nextDouble() * sum;
		double flag = 0;
		for (int i = 0; i < p.size(); i++) {
			flag += p.get(i);
			if(rand <= flag)
				return i;
		}
		return -1;
	}
	
}
