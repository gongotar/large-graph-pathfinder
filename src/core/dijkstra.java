package core;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import enums.CoordinateBox;
import enums.edge_type;
import model.CoordinateManager;
import model.connection;
import model.coordinate;
import model.edge;
import model.label;
import model.network;
import model.node;
import model.timetable_row;

/**
 * 
 */

/**
 * @author Masoud Gholami
 *
 */

public class dijkstra {

	private static LocalTime algorithm_start = null;
	public static node target = null;
	public static double density_computation_box_node_no = 3.0;
	public static double considered_node_no_in_area = 15.0;
	
	public static void main(String args[]){
		
	}
	
	/**
	 * Computes all of the Pareto-optimal solutions in a network (graph)
	 * using the modified (generalized) Dijkstra algorithm.
	 * 
	 * @param	netw		a digraph of nodes and weighted edges (Time table as the weight)
	 * @param	startnodes	a set of possible start nodes
	 * @param	starttime	start time of the journey
	 * @return			a set of Pareto-optimal labels defining paths in the graph
	 * @see				dijkstra 
	 */
	
	public static void pareto_opt(ArrayList<node> startnodes,
			LocalDateTime starttime, network netw){
		Queue<label> pq = new LinkedList<label>();

		report(netw, false);
		
		for (node n : startnodes) {
			label l = new label(n);
			l.setStart(starttime);
			l.setEnd(starttime);
			pq.add(l);
		}
		
		ArrayList<node> a_star_path = null;
		double path_len = 0;
		ArrayList<HashMap<CoordinateBox, coordinate>> boxes = null;
		
		if(target != null){
			a_star_path = 
					a_star(startnodes, target, netw);
			
			for (int i = 0; i < a_star_path.size() - 1; i++)
				path_len += a_star_path.get(i).getCoordinate()
						.getDistanceTo(a_star_path.get(i + 1).getCoordinate());
			
			ArrayList<Double> density = 
					compute_path_density(a_star_path
							, path_len, netw, density_computation_box_node_no);
			
			boxes = compute_path_boxes(a_star_path, density, considered_node_no_in_area);
		}

		while(!pq.isEmpty()){
			label l = pq.poll();
			node node = l.getNode();
			for (edge e : node.getOutgoing_edges()) {
				// if(!check_heuristics(e.getEnd(), a_star_path, path_len))		// better performance by checking only some nodes
					// continue;
				if(!check_heuristics(e.getEnd(), boxes))
					continue;
				if(!e.isFeasible()) continue;		// ignore this edge
				label new_label = create_label(l, e, starttime);
				if(dominated_in_list(e.getEnd().getLabels(), new_label))	// if the new label is dominated by old labels
					continue;
				new_label.setNode(e.getEnd());
				pq.add(new_label);
				remove_dominated_labels(e.getEnd(), new_label);
			}
		}
		
		report(netw, false);
		algorithm_start = null;
	}

	/**
	 * For each node on the a star path a bounding box will
	 * be computed. The size of the box will be determined
	 *  based on the given information about the density 
	 *  of the node and the needed number of nodes to be considered
	 *  in the box
	 *  
	 * @param a_star_path	the a star path of the nodes
	 * @param density		the set of node densities
	 * @param considered_node_no_in_area	needed number of the nodes in box
	 * @return	a set of boxes for each node on the a star path
	 * @see dijkstra
	 */
	private static ArrayList<HashMap<CoordinateBox, coordinate>> compute_path_boxes(
			ArrayList<node> a_star_path, ArrayList<Double> density,
			double considered_node_no_in_area) {
		
		ArrayList<HashMap<CoordinateBox, coordinate>> boxes =
				new ArrayList<HashMap<CoordinateBox,coordinate>>();
		
		for (int i = 0; i < a_star_path.size(); i++) {
			double needed_area = 1.0 / density.get(i)
					* considered_node_no_in_area;
			int size = (int)(Math.sqrt(needed_area) * 1000.0 / 2);
			HashMap<CoordinateBox, coordinate> box = 
					CoordinateManager.getBoundingBox(a_star_path.get(i).getLat(),
					a_star_path.get(i).getLon(), size);
			boxes.add(box);
		}
		
		return boxes;
	}

	/**
	 * For each node on the a star path a bounding box will
	 * be created and the density in that box will be computed.
	 * The size of the box depends on the path length and the
	 * distance between the nodes on the path
	 * 
	 * @param a_star_path	the a star path of the nodes
	 * @param path_len		the length of the a star path
	 * @param node_number	the number of neighbor nodes included in box
	 * @return	a set of densities computed for each node on the path
	 * @see dijkstra
	 */
	private static ArrayList<Double> compute_path_density(
			ArrayList<node> a_star_path, double path_len,
			network netw, double node_number) {
		
		// compute the box size in meters
		int box_size = (int)(node_number * path_len * 
				1000.0 / a_star_path.size());
		
		node[] nodes = new node[netw.getNodes().size()];
		nodes = netw.getNodes().toArray(nodes);
		ArrayList<Double> densities = 
				new ArrayList<Double>(Collections.nCopies(nodes.length, 0.0));
		double area = 4 * Math.pow((double)box_size / 1000.0, 2);
		int i = 0;
		for (node node : a_star_path) {
			HashMap<CoordinateBox, coordinate> box = 
					CoordinateManager.getBoundingBox(node.getLat()
							, node.getLon(), box_size);
			for (node n : nodes)
				if(isInBox(box, n))
					densities.set(i, densities.get(i) + 1);
			densities.set(i, densities.get(i) / area);
			i++;
		}
		
		return densities;
	}

	/**
	 * Checks if the node n is inside the given bounding box
	 * 
	 * @param box	the coordinates of the box
	 * @param n		the node to be checked
	 * @return	a boolean value deciding if the node is in the box or not
	 * @see dijkstra
	 */
	private static boolean isInBox(HashMap<CoordinateBox, coordinate> box,
			node n) {
		coordinate NE = box.get(CoordinateBox.NE);
		coordinate NW = box.get(CoordinateBox.NW);
		
		float lon = n.getLon();
		
		boolean lon_ok = false;
		
		if(NE.getLongitude() > NW.getLongitude()
				&& NE.getLongitude() >= lon
				&& NW.getLongitude() <= lon)
			lon_ok = true;
		else if(NE.getLongitude() < NW.getLongitude()
				&& NE.getLongitude() >= lon)
			lon_ok = true;
		else if(NE.getLongitude() > NW.getLongitude()
				&& NW.getLongitude() <= lon)
			lon_ok = true;
		else
			return lon_ok;
		
		coordinate SE = box.get(CoordinateBox.SE);
		float lat = n.getLat();
		
		if(NE.getLatitude() > SE.getLatitude()
				&& NE.getLatitude() >= lat
				&& SE.getLatitude() <= lat)
			return true;
		
		return false;
	}

	/**
	 * Computes a a* path from the source nodes to the target
	 * node considering the distances
	 * 
	 * @param startnodes	the set of start nodes
	 * @param target		the target
	 * @param netw			the network of nodes and edges		
	 * @return	the path between start nodes and target 
	 * 			containing nodes
	 * @see dijkstra
	 */
	public static ArrayList<node> a_star(ArrayList<node> startnodes,
			node target, network netw) {
		
		HashMap<node, Double> distlabels = new HashMap<node, Double>();
		HashMap<node, Double> eucldlabels = new HashMap<node, Double>();
		ArrayList<node> open = new ArrayList<node>();
		ArrayList<node> close = new ArrayList<node>();
		HashMap<node, node> pred = new HashMap<node, node>();
		
		// initialize the nodes
		ArrayList<node> nodes = netw.getNodes();
		for (node node : nodes){
			eucldlabels.put(node, node.getCoordinate()
					.getDistanceTo(target.getCoordinate()));
			distlabels.put(node, Double.MAX_VALUE);
			pred.put(node, null);
			if(startnodes.contains(node)){
				open.add(node);
				distlabels.replace(node, 0.0);
			}
		}
		
		while(!open.isEmpty()){
			node node = open_min(open, distlabels, eucldlabels);
			close.add(node);
			
			if(node.getId() == target.getId())
				break;
			
			ArrayList<edge> edges = node.getOutgoing_edges();
			for (edge edge : edges) {
				node dest = edge.getEnd();
				if(!open.contains(dest)
						&& !close.contains(dest)){
					open.add(dest);
					distlabels.replace(dest, edge.getDistance() 
							+ distlabels.get(node));
					pred.replace(dest, node);
				}
				else if(distlabels.get(dest) > edge.getDistance()
						+ distlabels.get(node)){
					distlabels.replace(dest, edge.getDistance() 
							+ distlabels.get(node));
					pred.replace(dest, node);
					if(close.contains(dest)){
						close.remove(dest);
						open.add(dest);
					}
				}
			}
		}
		
		ArrayList<node> path = new ArrayList<node>();
		node buf = target;
		while(!startnodes.contains(buf)){
			path.add(buf);
			buf = pred.get(buf);
		}
		return path;
	}

	/**
	 * From open indices chooses the one with the minimum
	 * distance in labels
	 * 
	 * @param open	the set of open indices
	 * @param distlabels	the set of labels (distances)
	 * @param eucldlabels	the set of the distances to target
	 * @return	the index of the open node with the minimum distance
	 * @see dijkstra
	 */
	private static node open_min(ArrayList<node> open,
			HashMap<node, Double> distlabels, HashMap<node, Double> eucldlabels) {
		double min = Integer.MAX_VALUE;
		node min_node = null;
		for (node node : open) {
			if(distlabels.get(node) + eucldlabels.get(node) < min){
				min = distlabels.get(node) + eucldlabels.get(node);
				min_node = node;
			}
		}
		open.remove(min_node);
		return min_node;
	}

	/**
	 * Checks the node at the end of edge e and decides if
	 * it's meaningful to explore the node or note using
	 * a given heuristic in distance
	 * Here the heuristics are the existence of the node
	 * inside of at least one of the area boxes of the 
	 * nodes on the a star path
	 * 
	 * @param node	the start node of the edge
	 * @param boxes	the bounding boxes of the a star path
	 * @return	a boolean value deciding whether the end node
	 * 				should be explored or not
	 * @see dijkstra
	 */
	private static boolean check_heuristics(node node, 
			ArrayList<HashMap<CoordinateBox, coordinate>> boxes) {
		
		for (HashMap<CoordinateBox, coordinate> box : boxes)
			if(isInBox(box, node))
				return true;
		
		/* if(path == null)
			return true;
		
		for (int i = 0; i < path.size(); i++) {
			if(path.get(i).getCoordinate()
					.getDistanceTo(node.getCoordinate()) 
					< compute_threshod(path_len, i, path.size()))
				return true;
		}
		*/
		return false;
	}

	/**
	 * Computes a threshold for checking the heuristics condition
	 * considering the a* path length and the current step
	 * of the path and the total size of the path.
	 * At the end an area will be computed within that area
	 * the nodes are considered for finding the pareto-optimal
	 * solutions
	 * 
	 * @param path_len	the length of the a* path (distance)	
	 * @param i			the current position in the path
	 * @param size		the size of the path (no. of nodes)
	 * @return	an appropriate threshold
	 * @see dijkstra

	private static double compute_threshod(double path_len, int i, int size) {
		return path_len / 6.0;
	}
	*/

	/**
	 * Provides a reporting mechanism for the modified dijkstra
	 * function
	 * 
	 * @param netw 	network of the nodes
	 * @param light	defines if a full report is requested or a light report
	 * @see dijkstra
	 */
	private static void report(network netw, boolean light) {
		LocalTime now = LocalTime.now();

		if(algorithm_start == null){
			algorithm_start = LocalTime.now();
			return;
		}
		
		if(!light){
			if(netw == null)
				return;
			ArrayList<node> nodes = netw.getNodes();
			int c = 0, p = 0;
			for (node node : nodes) {
				if(node.getLabels().size() > 0){
					c ++;
					p += node.getLabels().size();
				}
			}
			System.out.println(c + " from " + nodes.size() + 
					" (" + p + " labels added) in " + 
					Duration.between(algorithm_start,
							now).toMillis() + " ms");
		}
		else
			System.out.println("Time spent: " +  
					Duration.between(algorithm_start,
							now).toMillis() + " ms");
	}

	/**
	 * Searches in the labels of the node and checks if any labels in the list
	 * are dominated by the new label, if finds one then it will be removed from
	 * the list
	 * 
	 * @param	node		the node containing the label list
	 * @param	new_label	the new label that is to be checked against all the labels in the list
	 * @see		dijkstra
	 */
	private static void remove_dominated_labels(node node, label new_label) {
		int i = 0;
		while(i < node.getLabels().size()){
			if(dominates(new_label, node.getLabels().get(i)))
				node.getLabels().remove(i);
			else
				i++;
		}
	}

	/**
	 * Checks if the new label is dominated by any other labels in the label list
	 * 
	 * @param 	label_list	the list of the labels in a node
	 * @param	new_label	the new label to be checked
	 * @return	a boolean value indicating whether the new label is dominated or not
	 * @see 	dijkstra
	 */
	private static boolean dominated_in_list(ArrayList<label> label_list, label new_label) {
		for (label label : label_list) {
			if(dominates(label, new_label))
				return true;
		}
		return false;
	}

	/**
	 * Decides whether the label dominates the new label or not
	 * considering the attributes of the both labels
	 * 
	 * @param	label		the label to be checked
	 * @param	new_label	the new label to be checked against
	 * @return	a boolean value, true if label dominates the new label, false otherwise
	 * @see		dijkstra
	 */
	private static boolean dominates(label label, label new_label) {
		// TODO Dominance evaluation algorithm here
		// for now a basic dominance evaluation methodology is implemented
		// simply checking if all label attributes are less than or equal to
		// new label attributes
		if(label.getCost() == new_label.getCost()
				&& label.getDuration().getSeconds() == new_label.getDuration().getSeconds()
				&& label.getChange() == new_label.getChange()
				&& label.getRisk() == new_label.getRisk())
			return false;
		else if(label.getCost() <= new_label.getCost()
				&& label.getDuration().getSeconds() <= new_label.getDuration().getSeconds()
				&& label.getChange() <= new_label.getChange()
				&& label.getRisk() <= new_label.getRisk())
			return true;
		return false;
	}

	/**
	 * Creates a new label by using the previous label and 
	 * adding a new edge to it considering the aggregation
	 * of the edge attributes and the label attributes
	 * 
	 * @param	l			the previous label
	 * @param	e			to be added edge
	 * @param	starttime	the start time of the journey
	 * @return	label	new label containing the previous label and the new edge
	 * @see		dijkstra
	 */
	private static label create_label(label l, edge e, 
			LocalDateTime starttime) {
		
		label new_label = new label();				// create the label
		timetable_row row = find_edge_id(l, e);
		connection<edge, Integer> conn = new connection<edge, Integer>(e, row.getId());
		ArrayList<connection<edge, Integer>> extended_path = 
				new ArrayList<connection<edge,Integer>>(l.getPath());
		extended_path.add(conn);
		new_label.setPath(extended_path);			// set the new path

		new_label.setStart(starttime);
		
		LocalDateTime new_end = compute_new_end(l, row, e.getType());
		new_label.setEnd(new_end);					// set the new end
		
		// Duration new_duration = compute_new_duration(l, row, e.getType());
		Duration new_duration = Duration.between(new_label.getStart(), new_label.getEnd());
		new_label.setDuration(new_duration);		// set the new duration
		
		double new_cost = compute_new_cost(l, row);
		new_label.setCost(new_cost);				// set the new cost
		
		int new_change = compute_new_change(l, row, e.getType());
		new_label.setChange(new_change);			// set the new change
		
		double new_risk = 0;
		if(new_label.getChange() != l.getChange())
			new_risk = compute_new_risk(l, row, e.getType());
		else new_risk = l.getRisk();
		
		new_label.setRisk(new_risk);				// set the new risk
		
		return new_label;
	}

	/**
	 * Computes the end time of the new label considering the 
	 * end time of the previous label and the timetable row
	 * used in the path
	 * @param l		the previous label
	 * @param row	the timetable row used
	 * @param type	the type of the edge
	 * @return	the end time of the new label
	 * @see dijkstra
	 */
	private static LocalDateTime compute_new_end(label l, timetable_row row, edge_type type) {
		LocalDateTime end = l.getEnd();
		
		LocalTime end_time = l.getEnd().toLocalTime();
		
		Duration delay;
		
		if(type.equals(enums.edge_type.walk))
			delay = Duration.between(row.getStart_time(), row.getEnd_time());
		else if(end_time.isBefore(row.getEnd_time()))
			delay = Duration.between(end_time, row.getEnd_time());
		else
			delay = Duration.between(end_time, LocalTime.MAX)
					.plus(Duration.between(LocalTime.MIN, row.getEnd_time()));
		
		end = end.plus(delay);
		
		return end;
	}

	/**
	 * Computes the new risk of the path, considering the risk
	 * of the label and the variation of the last row of the label
	 * and also using the start time of the new row
	 * 
	 *  @param	l		previous label
	 *  @param	row		a row of the new edge's timetable
	 *  @return	new risk evaluation
	 *  @see dijkstra
	 */
	private static double compute_new_risk(label l, timetable_row row, edge_type edge_type) {
		
		// if walking edge then no risk
		if(edge_type.equals(enums.edge_type.walk))
			return 0;
		
		// get the last line used to reach here
		int size = l.getPath().size() - 1;
		timetable_row last_row = get_label_row(l, size);
		if(last_row.getVariation() == 0)
			return 0;
		
		// get the arriving and departure time
		LocalTime arrived_at = LocalTime.from(l.getEnd());
		LocalTime departure_at = row.getStart_time();
		
		double arrive_risk = 0;
		// get the info about the variation of the last line
		if(! l.getPath().get(size)
				.getEdge().getType()
				.equals(enums.edge_type.walk))
			arrive_risk = last_row.getVariation();
		
		// add the variation of last line to the arriving time
		// so we get the late arrive time
		LocalTime late_arrive = arrived_at
				.plusMinutes((int) Math.ceil(arrive_risk));
		
		// if the departure time is before the late arrive then
		// we have a risky change here
		Duration risky_time = Duration.ZERO;
		if(departure_at.isBefore(late_arrive) && departure_at.isAfter(arrived_at))
			risky_time = Duration.between(departure_at, late_arrive);
		
		// No. of risky time seconds
		double risk = risky_time.getSeconds();
		
		// No of total seconds
		double total = last_row.getVariation() * 60.0;
		
		double success_prob = 1.0 - risk / total;
		
		double total_success = success_prob * (1.0 - l.getRisk()); 
		
		return 1.0 - total_success;
	}

	/**
	 * Computes the new change number of the path, considering the number of changes
	 * of the label and deciding if adding the new edge will increase the total
	 * number of changes by one or not.
	 * 
	 *  @param	l		previous label
	 *  @param	row		a row of the new edge's timetable
	 *  @param	edge_type	the type of the edge
	 *  @return	new change number
	 *  @see dijkstra
	 */
	private static int compute_new_change(label l, timetable_row row, edge_type edge_type) {
		int change = l.getChange();							// get the change no. of l
		if(edge_type.equals(enums.edge_type.walk))			// if walking edge then no change increment
			return change;
		int size = l.getPath().size();
		if(size == 0)
			return 0;
		timetable_row last_row = get_label_row(l, size - 1);// get the last row
		
		if(last_row.getLine() != row.getLine())				// get the last line used in the l and check
			change++;										// whether it's equal to the new row's line no. 
															// if not equal, we have a new change
		return change;
	}

	/**
	 * Gets the used row in the timetable of the row_index-th step of the label
	 * 
	 *  @param	l			the label with many steps and timetables in each step
	 *  @param	row_index	the needed step
	 *  @return	timetable_row of the needed step (all needed information)
	 *  @see dijkstra
	 */
	private static timetable_row get_label_row(label l, int row_index) {
		edge edge = l.getPath().get(row_index).getEdge();	// get the row_index-th edge in the path
		ArrayList<timetable_row> timetable = 				// get the row_index-th timetable of l
				new ArrayList<timetable_row>(edge.getTimetable());
		
		int edge_index = 0;
		for (int i = 0; i < timetable.size(); i++){
			if(timetable.get(i).getId() == l.getPath().get(row_index).getId()){
				edge_index = i;								// find the corresponding row id of the edge
				break;
			}
		}
		timetable_row row = timetable.get(edge_index);
		return row;
	}

	/**
	 * Computes the new duration considering the start time of the label and 
	 * the end time of the edge
	 * 
	 *  @param	l			previous label
	 *  @param	row			a row of the new edge's timetable
	 *  @param	edge_type	the type of the edge
	 *  @return	new duration
	 *  @see dijkstra
	 *
	private static Duration compute_new_duration(label l, timetable_row row, edge_type edge_type) {
		 
		// compute the duration using the start time of
		// the journey and the end time of the new edge
		Duration duration;
		if(edge_type.equals(enums.edge_type.walk)){					// if walking edge then simply add
																	// the edge duration to the label duration
			duration = Duration.between(row.getStart_time(), row.getEnd_time()).plus(l.getDuration());
		}
		else{
			if(row.getEnd_time().isBefore(l.getEnd().toLocalTime())){
				duration = Duration.between(l.getEnd().toLocalTime(), LocalTime.MAX)
						.plus(Duration.between(LocalTime.MIN, row.getEnd_time()));
			}
			else{
				LocalTime labelEnd = l.getEnd().toLocalTime();
				duration = Duration.between(labelEnd, row.getEnd_time());
			}
			duration = duration.plus(l.getDuration());
		}
		
		return duration;
	}*/

	/**
	 * Computes the new cost considering the cost of the label and 
	 * the cost of the edge
	 * 
	 *  @param	l		previous label
	 *  @param	row		a row of the new edge's timetable
	 *  @return	new cost
	 *  @see dijkstra
	 */
	private static double compute_new_cost(label l, timetable_row row) {
		double new_cost = row.getCost() + l.getCost();
		return new_cost;
	}

	/**
	 * Defines the correct timetable row of the new edge to be
	 * used when the path of the given label is taken.
	 * It uses the arriving information of the path in the label,
	 * so that means the end time of the last row of the path and 
	 * searches for the nearest departure of the new edge 
	 * 
	 * @param	l	the label containing the path information
	 * @param	e	the new edge with a timetable
	 * @return	the right timetable row
	 * @see dijkstra
	 */
	private static timetable_row find_edge_id(label l, edge e) {
		long minimum_waiting_minutes = 1;						// minimum waiting time
		
		if(e.getType().equals(edge_type.walk))					// if walking edge then return 0
			return e.getTimetable().get(0);
		int size = l.getPath().size();
		LocalTime arrived_at;
		if(size != 0){
			timetable_row last_row = get_label_row(l, size - 1);	// get the last row of the path in the label
			arrived_at = last_row.getEnd_time();		// get the arriving time of the path
		}else
			arrived_at = l.getStart().toLocalTime();
			
		ArrayList<timetable_row> timetable = 					// get the timetable of the new edge
				new ArrayList<timetable_row>(e.getTimetable());
		
		timetable_row final_row = null;
		Duration waiting_time = Duration.between(arrived_at, LocalDateTime.MAX);
		for (timetable_row row : timetable) {					// finding the minimum waiting time
			if(arrived_at.isBefore(row.getStart_time().minusMinutes(minimum_waiting_minutes))
					&& Duration.between(arrived_at, row.getStart_time()).getSeconds() < waiting_time.getSeconds()){
				waiting_time = Duration.between(arrived_at, row.getStart_time());
				final_row = row;
			}
		}
		// if late in the night, go with the first departure next day
		if(final_row == null){
			LocalTime first_departure = LocalTime.MAX;
			for (int i = 0; i < timetable.size(); i++) {
				if(first_departure.isAfter(timetable.get(i).getStart_time())){
					final_row = timetable.get(i);
					first_departure = timetable.get(i).getStart_time();
				}
			}
		}
			 
		return final_row;
	}
	
}
