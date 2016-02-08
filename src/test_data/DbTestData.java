/**
 * 
 */
package test_data;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import db.ReadDatabase_alt;
import enums.edge_type;
import graphics.EditorMouseMenu;
import model.edge;
import model.network;
import model.node;
import model.timetable_row;

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
		traverseEulerianPath(netw);
		ArrayList<node> nodes = new ArrayList<node>();
		node node = null;
		for (node n : netw.getNodes()) {
			if(n.getId() == 897){
				node = n;
				break;
			}
		}
		nodes.add(node);
		//dijkstra.netw = netw;
		EditorMouseMenu.create_graph_visually(netw);
		//dijkstra.pareto_opt(nodes, LocalDateTime.now());
	}

	/**
	 * Traverses the graph based on Hierhozer's algorithm
	 * and creates the timetables of the edges in the
	 * meantime
	 * 
	 * @param netw	the network of nodes and edges
	 * @see DbTestData
	 */
	private static void traverseEulerianPath(network netw) {
		node node = netw.getNodes().get(0);
		Queue<node> forward = new LinkedList<node>();
		edge edge;
		edge last_edge = null;
		int line = -1;
		while(unvisitedEdge(node) != null){
			edge = unvisitedEdge(node);
			line = createTimeTable(edge, last_edge, line);
			forward.add(node);
			node = edge.getEnd();
			last_edge = edge;
		}
		
		while(!forward.isEmpty()){
			node n = forward.poll();
			last_edge = null;
			while(unvisitedEdge(n) != null){
				edge = unvisitedEdge(n);
				line = createTimeTable(edge, last_edge, line);
				forward.add(n);
				n = edge.getEnd();
				last_edge = edge;
			}
		}
	}

	/**
	 * Returns an unvisited edge of the node by checking the
	 * edge's timetable
	 * 
	 * @param node	a node with a set of outgoing edges
	 * @return	an unvisited edge of the node
	 * @see DbTestData
	 */
	private static model.edge unvisitedEdge(node node) {
		ArrayList<edge> edges = node.getOutgoing_edges();
		for (edge edge : edges) {
			if(edge.getTimetable().size() == 0)
				return edge;
		}
		return null;
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
					new_edge.setDistance(edge.getDistance());
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
	 * Creates current edge timetable considering the last edge's
	 * timetable and the distance the edge is going through
	 *  
	 * @param currect_edge		the current edge with empty timetable (to be computed)
	 * @param last_edge			the last edge (to be considered)
	 * @param last_line			the last line generated overall
	 * @return	the line no. of the generated timetable
	 * @see DbTestData
	 */
	private static int createTimeTable(edge current_edge, edge last_edge, int last_line) {
		// average line length
		int	line_len = 10;
		
		// stay at each station (minutes)
		int waiting_minutes = 2;

		// No of repeated lines at each station
		int line_no = 8;
		
		// cost/distance constant. normal cost per kilometer (euro)
		double cost_dist_const = 0.1;
		
		// cost variance
		double cost_variance = 0.05;
		
		// cost factor
		double cost_factor = 1.2;	// train
		
		// fixed cost
		double fixed_cost = 20;

		// duration/distance constant. normal duration per kilometer
		Duration duration_dist_const = Duration.ofSeconds(40);
		
		// duration variance
		double duration_variance = 0.05;

		// duration factor
		double duration_factor = 1.0;	// train
				
		// fixed duration
		Duration fixed_duration = Duration.ofSeconds(60);
		
		// maximum variance of delays in minutes
		double max_variance = 5;
		
		/*
		 * Create the timetable rows
		 */
		
		ArrayList<timetable_row> new_rows = new ArrayList<timetable_row>();
		
		int line = last_line;
		boolean	line_changed = false;
		Random rnd = new Random();
		
		double random_area = rnd.nextDouble();
		if(random_area < (1.0 / line_len) 
				|| last_edge == null){
			line ++;
			line_changed = true;
		}
		
		
		// generate a random duration variance
		double dur_rnd_var = 1 - (duration_variance 
				* (rnd.nextDouble() * 2 - 1));

		// compute the line_no number of randomized durations
		Duration[] durations = new Duration[line_no];

		
		for (int i = 0; i < line_no; i++){
			new_rows.add(new timetable_row());
			new_rows.get(i).setLine(line);
			new_rows.get(i).setId(i);
			
			// generate a random cost variance
			double cost_rnd_var = 1 - (cost_variance 
					* (rnd.nextDouble() * 2 - 1));
			// compute the randomized cost
			double cost = fixed_cost + cost_factor * current_edge.getDistance() * 
					cost_dist_const * cost_rnd_var;
			
			new_rows.get(i).setCost(cost);
			
			durations[i] = duration_dist_const
					.multipliedBy((long)
							(duration_factor * current_edge
									.getDistance() 
									* dur_rnd_var)).plus(fixed_duration);
			
			double variance = rnd.nextDouble() * 
					Math.min(durations[i].toMinutes(), max_variance);
			new_rows.get(i).setVariation(variance);
		}
		
		// compute departure/arrive times
		if(line_changed){
			// new random departure times should be generated
			for (int i = 0; i < line_no; i++) {
				LocalTime departure = LocalTime.of(rnd.nextInt(24), rnd.nextInt(60));
				LocalTime arrive = departure.plus(durations[i]);
				new_rows.get(i).setStart_time(departure);
				new_rows.get(i).setEnd_time(arrive);
			}
		}
		else{	// departure times are a little after the end time
				// of the last step
			int i = 0;
			for (timetable_row row : last_edge.getTimetable()) {
				if(row.getLine() == line){
					LocalTime departure = row.getEnd_time()
							.plusMinutes(waiting_minutes);
					LocalTime arrive = departure.plus(durations[i]);
					new_rows.get(i).setStart_time(departure);
					new_rows.get(i).setEnd_time(arrive);
					i++;
				}
			}
		}
		
		current_edge.setTimetable(new_rows);
		
		return line;
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
