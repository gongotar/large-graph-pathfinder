package core;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import enums.edge_type;
import model.connection;
import model.edge;
import model.label;
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
			LocalDateTime starttime){
		
		Queue<label> pq = new LinkedList<label>();
		
		for (node n : startnodes) {
			label l = new label(n);
			l.setStart(starttime);
			pq.add(l);
		}
		
		while(!pq.isEmpty()){
			label l = pq.poll();
			node node = l.getNode();
			for (edge e : node.getOutgoing_edges()) {
				if(!e.isFeasible()) continue;						// ignore this edge
				label new_label = create_label(l, e, starttime);
				if(dominated_in_list(e.getEnd().getLabels(), new_label))	// if the new label is dominated by old labels 
					continue;
				new_label.setNode(e.getEnd());
				pq.add(new_label);
				remove_dominated_labels(e.getEnd(), new_label);
			}
		}
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
				&& label.getDuration().compareTo(new_label.getDuration()) == 0
				&& label.getChange() == new_label.getChange()
				&& label.getRisk() == new_label.getRisk())
			return false;
		else if(label.getCost() <= new_label.getCost()
				&& label.getDuration().compareTo(new_label.getDuration()) <= 0
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
		Integer id = find_edge_id(l, e);
		connection<edge, Integer> conn = new connection<edge, Integer>(e, id);
		ArrayList<connection<edge, Integer>> extended_path = 
				new ArrayList<connection<edge,Integer>>(l.getPath());
		extended_path.add(conn);
		new_label.setPath(extended_path);			// set the new path
		
		ArrayList<timetable_row> timetable = e.getTimetable();
		
		int index = 0;
		for (int i = 0; i < timetable.size(); i++){
			if(timetable.get(i).getId() == id){
				index = i;
				break;
			}
		}
		
		timetable_row row = timetable.get(index);
		
		double new_cost = compute_new_cost(l, row);
		new_label.setCost(new_cost);				// set the new cost
		
		Duration new_duration = compute_new_duration(l, row, e.getType());
		new_label.setDuration(new_duration);		// set the new duration
		
		int new_change = compute_new_change(l, row, e.getType());
		new_label.setChange(new_change);			// set the new change
		
		double new_risk = 0;
		if(new_label.getChange() != l.getChange())
			new_risk = compute_new_risk(l, row, e.getType());
		else new_risk = l.getRisk();
		
		new_label.setRisk(new_risk);				// set the new risk
		
		new_label.setStart(starttime);
		
		return new_label;
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
		
		// get the arriving and departure time
		LocalTime arrived_at = LocalTime.from(l.getStart()).plus(l.getDuration());
		LocalTime departure_at = row.getStart_time();
		
		// get the last line used to reach here
		int size = l.getPath().size() - 1;
		timetable_row last_row = get_label_row(l, size);
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
		if(departure_at.isBefore(late_arrive))
			risky_time = Duration.between(departure_at, late_arrive);
		
		// No. of risky minutes considered as risk measurement
		double risk = (double)(risky_time.getSeconds()) / 60;
		
		return risk + l.getRisk();
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
	 */
	private static Duration compute_new_duration(label l, timetable_row row, edge_type edge_type) {
		 
		// compute the duration using the start time of
		// the journey and the end time of the new edge
		Duration duration;
		if(edge_type.equals(enums.edge_type.walk))						// if walking edge then simply add
																		// the edge duration to the label duration
			duration = Duration.between(row.getStart_time(), row.getEnd_time()).plus(l.getDuration());
		else
			duration = Duration.between(l.getStart().toLocalTime(), row.getEnd_time());
		return duration;
	}

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
	 * @return	the id of the right timetable row
	 * @see dijkstra
	 */
	private static Integer find_edge_id(label l, edge e) {
		long minimum_waiting_minutes = 1;						// minimum waiting time
		
		if(e.getType().equals(edge_type.walk))					// if walking edge then return 0
			return 0;
		int size = l.getPath().size();
		LocalTime arrived_at;
		if(size != 0){
			timetable_row last_row = get_label_row(l, size - 1);	// get the last row of the path in the label
			arrived_at = last_row.getEnd_time();		// get the arriving time of the path
		}else
			arrived_at = l.getStart().toLocalTime();
			
		ArrayList<timetable_row> timetable = 					// get the timetable of the new edge
				new ArrayList<timetable_row>(e.getTimetable());
		
		int index = 0, min_index = -1;
		Duration waiting_time = Duration.between(arrived_at, LocalDateTime.MAX);
		for (timetable_row row : timetable) {					// finding the minimum waiting time
			if(arrived_at.isBefore(row.getStart_time().minusMinutes(minimum_waiting_minutes))
					&& Duration.between(arrived_at, row.getStart_time()).compareTo(waiting_time) < 0){
				waiting_time = Duration.between(arrived_at, row.getStart_time());
				min_index = index;
			}
			index++;
		}
		// if late in the night, go with the first departure next day
		if(min_index == -1){
			LocalTime first_departure = LocalTime.MAX;
			for (int i = 0; i < timetable.size(); i++) {
				if(first_departure.isAfter(timetable.get(i).getStart_time())){
					min_index = i;
					first_departure = timetable.get(i).getStart_time();
				}
			}
		}
			 
		return min_index;
	}
	
}
