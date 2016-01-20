/**
 * 
 */
package test_data;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import enums.edge_type;
import model.CoordinateManager;
import model.coordinate;
import model.edge;
import model.network;
import model.node;
import model.timetable_row;

/**
 * @author Masoud Gholami
 *
 */
public class test_data_generation {

	public static void main(String args[]){

	}
	
	/**
	 * Generates a random network of nodes and edges 
	 * with the given city no.
	 * 
	 * @param	size	no. of the cities in the network
	 * @return	network of the given city size
	 * @see 	test_data_generation
	 */
	public static network generate_netw(int size){
		network netw = new network();
		
		//generate cities and nodes (stations) in the cities
		ArrayList<city> cities = generate_cities(size);
		
		ArrayList<node> nodes = new ArrayList<node>();
		ArrayList<edge> edges = new ArrayList<edge>();
		
		// add generated nodes
		
		for (int i = 0; i < size; i++) {
			city c = cities.get(i);
			nodes.addAll(c.getStations());
		}
		
		// generate edges
		for (int i = 0; i < nodes.size(); i++) {
			edges.addAll(assign_random_outgoing_edges(nodes, i));
		}
		
		// set unique edge id
		for (int i = 0; i < edges.size(); i++) {
			edges.get(i).setId(i);
		}
		
		netw.setNodes(nodes);
		netw.setEdges(edges);
		assign_netw_lines(netw);
		
		return netw;
	}

	/**
	 * Generates cities in the given size, each city will have
	 * a set of different kinds of stations. the stations in each city
	 * are in the nearby
	 * 
	 * @param	size	no. of the cities
	 * @return	a set of cities of the given size
	 * @see		test_data_generation
	 */
	private static ArrayList<city> generate_cities(int size) {
		ArrayList<city> cities = new ArrayList<city>();
		ArrayList<coordinate> coordinations = 
				compute_city_center_coordinations(size);
		
		int node_id = 0;
		
		for (int i = 0; i < size; i++) {
			city c = new city();
			c.setId(i);
			c.setCity_center(coordinations.get(i));
			ArrayList<node> stations = gen_city_stations(c, node_id);
			c.setStations(stations);
			node_id = node_id + stations.size();
			cities.add(c);
		}
		
		return cities;
	}

	/**
	 * Gets the city as the input and generates a set
	 * of stations (nodes) of different types in the city
	 * 
	 * @param	city		the city with no stations 
	 * @param	node_id		the starting id that can be given to the 
	 * 							generated nodes
	 * @return	a set of nodes (stations) in the city
	 * @see		test_data_generation
	 */
	private static ArrayList<node> gen_city_stations(city city,
			int node_id) {
		// possibilities in the no. of stations in each city
		ArrayList<Double> p_st_no = new ArrayList<Double>();
		p_st_no.add(1.0);	// one station
		p_st_no.add(3.0);	// two stations
		p_st_no.add(5.0); 	// three stations
		
		// possibilities in the types of the stations
		ArrayList<Double> p_st_typ = new ArrayList<Double>();
		p_st_typ.add(3.0);	// bus station
		p_st_typ.add(1.0);	// car station
		p_st_typ.add(6.0);	// train station
		
		coordinate city_center = city.getCity_center();
		
		// no. of stations in the city
		int st_no = stochastic_choice(p_st_no) + 1;
		
		// type of the stations in the city
		ArrayList<Integer> node_types = stochastic_choice(p_st_typ, st_no);
		
		ArrayList<node> nodes = new ArrayList<node>();
		
		for (int i = 0; i < st_no; i++) {
			node n = new node();
			n.setId(node_id ++);
			n.setType(enums.node_type.values()[node_types.get(i)]);
			n.setCity(city);
			coordinate c = random_coordinate_in_city(city_center);
			n.setCoordinate(c);
			nodes.add(n);
		}
		
		return nodes;
	}

	/**
	 * Generates a random coordination in the city given by it's city
	 * center coordination. It uses the city area in meters.
	 * 
	 * @param	city_center	the coordination of the city center
	 * @return	a randomly generated coordination within the city
	 * @see		test_data_generation
	 */
	private static coordinate random_coordinate_in_city(coordinate city_center) {
		// city radius in kilometers
		double city_radius = 30;
		
		Random rnd = new Random();
		
		// kilometers to meters
		city_radius = city_radius * new Float(1000);
		
		// distance in longitude direction, between 0 and 25
		int long_dist = (int)(rnd.nextDouble() * city_radius);
		
		// distance in latitude direction, considering long_dist, in the
		// city circle
		int lat_dist = (int)(Math.sqrt(Math.pow(city_radius, 2) 
				- Math.pow(long_dist, 2)));
		
		coordinate long_c;
		
		// a random boolean value deciding whether to go to east (true) or west (false)
		boolean random_east_west = rnd.nextBoolean();
		
		if(random_east_west)
			long_c = CoordinateManager.addDistanceEast(city_center.getLatitude(), 
					city_center.getLongitude(), long_dist);
		else
			long_c = CoordinateManager.addDistanceWest(city_center.getLatitude(), 
					city_center.getLongitude(), long_dist);
		
		coordinate final_c;
		
		// a random boolean value deciding whether to go to north (true) or south (false)
		boolean random_north_south = rnd.nextBoolean();
		
		if(random_north_south)
			final_c = CoordinateManager.addDistanceNorth(long_c.getLatitude(), 
					long_c.getLongitude(), lat_dist);
		else
			final_c = CoordinateManager.addDistanceSouth(long_c.getLatitude(), 
					long_c.getLongitude(), lat_dist);
		
		return final_c;
	}

	/**
	 * Assigns randomly generated lines and timetables to
	 * each edge
	 * 
	 * @param	netw	the given network with all the nodes and edges
	 * @see		test_data_generation
	 */
	private static void assign_netw_lines(network netw){
		// Start from one node and randomly explore edges 
		// and go to other nodes and then randomly switch lines		
		
		ArrayList<node> nodes = netw.getNodes();
		ArrayList<edge> edges = netw.getEdges();
		
		// the maximum number of the loops
		int limit = (int)Math.pow(edges.size(), 2);
		
		// choose the starting node
		node node = nodes.get(0);
		
		edge save_edge = null;
		int count = 0;
		
		while(!all_processed(edges)){
			// choose randomly one of the outgoing edges of the node
			edge edge = choose_random_edge(node.getOutgoing_edges(), save_edge);
			// get the node at the other side of the edge
			node = edge.getEnd();
			// generate a set of new random timetable rows considering the 
			// previous edge (timetable) and the current edge
			ArrayList<timetable_row> rows = generate_timetable_row(edge, save_edge);
			// add new rows the the timetable of the edge
			edge.getTimetable().addAll(rows);
			// save the edge for the next step (will be used as previous edge)
			save_edge = edge;
			count ++;
			if(count > limit)
				//throw new RuntimeException("Not all the edges could be processed");
				break;
		}
			
		// process walking edges
		for (edge edge : edges) {
			if(edge.getType().equals(enums.edge_type.walk)){
				edge.setTimetable(new ArrayList<timetable_row>());
				timetable_row row = generate_walking_row(edge, 0);
				edge.addToTimetable(row);
			}
		}
		
	}

	/**
	 * Generates for a walking edge a new timetable row
	 * with all the properties needed for a timetable row
	 * of a walking edge 
	 * @param edge	the walking edge
	 * @param line_no line no. reaching this edge
	 * @return	a new timetable row
	 * @see	test_data_generation
	 */
	private static timetable_row generate_walking_row(edge edge, int line_no) {
		// walking speed (= time needed to walk one kilometer)
		Duration walking_speed = Duration.ofMinutes(10);
		
		timetable_row row = new timetable_row();
		row.setCost(0);
		row.setId(0);
		row.setLine(0);
		row.setVariation(0);
		row.setStart_time(LocalTime.of(8, 0));
		// get the edge length in kilometers
		long distance = (long)edge.getStart().getCoordinate().
				getDistanceTo(edge.getEnd().getCoordinate());
		Duration duration = walking_speed.multipliedBy(distance);
		// maximum duration 23 hours
		if(duration.compareTo(Duration.ofHours(23)) > 0)
			duration = Duration.ofHours(23);
		
		row.setEnd_time(row.getStart_time()
				.plus(duration));
		return row;
	}

	/**
	 * Chooses randomly an edge among the given edges.
	 * The edges with less no. of timetable rows are more
	 * likely to be chosen than the edges with more timetable
	 * rows. walking edges won't be chosen
	 * 
	 * @param outgoing_edges the list of edges
	 * @param save_edge 	the previous edge for avoiding repeated returning 
	 * @return	a randomly chosen edge
	 */
	private static model.edge choose_random_edge(ArrayList<edge> outgoing_edges, edge save_edge) {
		// the greater value means to give the edges with less no.
		// of timetable rows more chance to be chosen
		int fairness_degree = 4;
		
		ArrayList<Double> p = new ArrayList<Double>();
		int max_row = 0;
		// get the no. of timetable rows of each edge
		for (edge edge : outgoing_edges) {
			if(edge.getTimetable() == null)
				p.add(0.0);
			else {
				p.add((double)edge.getTimetable().size());
				if(max_row < edge.getTimetable().size())
					max_row = edge.getTimetable().size();
			}
		}
		// modify the computed values so that we can get
		// more probabilities for those edges with less timetable rows
		for (int i = 0; i < p.size(); i++) {
			double v = Math.pow(max_row - p.get(i) + 1, fairness_degree);
			p.set(i, v);
		}
		
		for (int i = 0; i < p.size(); i++) {
			if(save_edge != null){
				// no returning edges
				if(outgoing_edges.get(i).getEnd().equals(save_edge.getStart()))
					p.set(i, 0.0);
				// if the line is already added to the edge
				//if(found_line(save_edge,outgoing_edges.get(i)))
					//p.set(i, 0.0);
			}
					
		}
				
		// choose randomly an index considering the given probabilities
		int index = stochastic_choice(p);
		// return the edge at the computed random index
		return outgoing_edges.get(index);
	}

	/**
	 * Checks if the last line added to the save_edge is already
	 * been added to the edge or not
	 * @param save_edge	previous edge
	 * @param edge		current edge
	 * @return	a boolean value deciding whether the last line in the 
	 * 					save_edge is already added to the edge (true)
	 * 					or not (false)
	 * @see test_data_generation
	 */
	private static boolean found_line(edge save_edge, edge edge) {
		int time_table_size = save_edge.getTimetable().size();
		// get the last added line
		int line = save_edge.getTimetable()
				.get(time_table_size - 1).getLine();
		ArrayList<timetable_row> timetable = edge.getTimetable();
		for (timetable_row row : timetable)
			if(row.getLine() == line)
				return true;
		
		return false;
	}

	/**
	 * Checks if all of the edges are processed or not.
	 * An edge is processed if there is at least one timetable row
	 * assigned to it.
	 * 
	 * @param edges
	 * @return true if all the edges are processed, false otherwise
	 * @see test_data_generation
	 */
	private static boolean all_processed(ArrayList<edge> edges) {
		for (edge edge : edges)
			if(edge.getTimetable().size() == 0 && 
			!edge.getType().equals(enums.edge_type.walk))
				return false;
		
		return true;
	}

	/**
	 * Generates a new timetable row considering the edge length
	 * and previous edge properties.
	 * The results are randomized
	 * @param edge			the current edge
	 * @param save_edge		the previous edge
	 * @return	a set of new timetable rows for the given edge
	 * @see test_data_generation
	 */
	private static ArrayList<timetable_row> generate_timetable_row(edge edge,
			edge save_edge) {
		// average line length
		int	line_len = 10;
		
		// stay at each station (minutes)
		int waiting_minutes = 2;
		
		// no. of repeated lines at each station
		int line_no = 4;
		
		// cost factor
		double[] cost_factor = new double[]{
				1,			// bus
				1.5,		// car
				1.2			// train
		};
		
		// cost/distance constant. normal cost per kilometer (euro)
		double cost_dist_const = 0.1;
		
		// cost variance
		double cost_variance = 0.05;
		
		// duration factor
		double[] duration_factor = new double[]{
				1.6,		// bus
				1.3,		// car
				1.0			// train
		};
		
		// duration/distance constant. normal duration per kilometer
		Duration duration_dist_const = Duration.ofSeconds(40);
		
		// duration variance
		double duration_variance = 0.05;
		
		// code
		
		ArrayList<timetable_row> new_rows = new ArrayList<timetable_row>();
		for (int i = 0; i < line_no; i++)
			new_rows.add(new timetable_row());	
		
		
		// compute the line
	
		// generate a random no. used to decide if line should end here
		// and go to the next line (line + 1) or use the same line
		Random rnd = new Random();
		int line = 0;
		boolean line_changed = false;
		if(save_edge != null){
			double random_area = rnd.nextDouble();
			// get the line used to reach this edge
			if(edge.getTimetable().size() != 0)
				line = save_edge.getTimetable()
					.get(save_edge.getTimetable().size() - 1).getLine();
			// change the line by the probability of 1.0 / line_len
			if(random_area < (1.0 / line_len) || 
					!edge.getType().equals(save_edge.getType())
					|| found_line(save_edge, edge)){
				line ++;
				line_changed = true;
			}
		}
		else
			line_changed = true;
		
		if(edge.getType().equals(enums.edge_type.walk)){
			ArrayList<timetable_row> ret = new ArrayList<timetable_row>();
			ret.add(generate_walking_row(edge, line));
			return ret;
		}
		
		for (int i = 0; i < new_rows.size(); i++)
			new_rows.get(i).setLine(line);
		
		// compute the id
		
		// get the last timetable row id of the edge
		int id = 0;
		if(edge.getTimetable().size() != 0)
			id = edge.getTimetable()
				.get(edge.getTimetable().size() - 1).getId();
		
		for (int i = 0; i < new_rows.size(); i++)
			new_rows.get(i).setId(++id);
		
		// get the edge length in kilometers
		double distance = edge.getStart().getCoordinate().
				getDistanceTo(edge.getEnd().getCoordinate());
		
		// get the type cost/duration factor
		edge_type[] types = enums.edge_type.values();
		double type_cost_factor = 0;
		double type_duration_factor = 0;
		for (int i = 0; i < types.length; i++) {
			if(edge.getType().equals(types[i])){
				type_cost_factor = cost_factor[i];
				type_duration_factor = duration_factor[i];
			}
		}
		
		// compute random costs
		for (int i = 0; i < new_rows.size(); i++){
			// generate a random cost variance
			double cost_rnd_var = 1 - (cost_variance 
					* (rnd.nextDouble() * 2 - 1));
			// compute the randomized cost
			double cost = type_cost_factor * distance * 
					cost_dist_const * cost_rnd_var;
			new_rows.get(i).setCost(cost);
		}
		
		// compute random duration
		
		// generate a random duration variance
		double dur_rnd_var = 1 - (duration_variance 
				* (rnd.nextDouble() * 2 - 1));

		// compute the line_no number of randomized durations
		Duration[] durations = new Duration[line_no];
		for (int i = 0; i < durations.length; i++) {
			durations[i] = duration_dist_const
				.multipliedBy((long)(type_duration_factor * distance
						* dur_rnd_var));	
		}
		
		// compute departure/arrive times
		
		if(line_changed || save_edge.getType().equals(enums.edge_type.walk)){
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
			for (timetable_row row : save_edge.getTimetable()) {
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
		
		return new_rows;
	}

	/**
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
		double propagation_degree = 8;
		// maximum no. of edges
		int maximum_node_consideration = 8;
		// minimum no. of nodes to be considered for being target_node
		int	minimum_node_consideration = 2;
		// minimum no. of edges for each node
		int min_edge = 1;
		// maximum no. of edges for each node
		int max_edge = 5;
		
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
		
		// modify p so that it contains proper probabilities for each node  
		for (int j = 0; j < p.size(); j++){
			double modified_value = Math.pow(max_distance + min_distance - p.get(j), propagation_degree);
			p.set(j, modified_value);
		}
		
		ArrayList<Double> buffer = new ArrayList<Double>(p);
		int[] max_index = new int[p.size()];
		Arrays.fill(max_index, 0);
		
		// choose a random number between minimum_node_consideration
		// and max_node_consideration
		Random rand = new Random();
		int node_consideration_no = (int)((rand.nextDouble() * 
				(maximum_node_consideration - minimum_node_consideration)) + minimum_node_consideration);
		
		if(minimum_node_consideration < nodes.size()){
			int loop = Math.min(node_consideration_no, p.size());
			int z = 0;
			int same_type_found_no = 0;
			while(z < loop) {
				double n_max = -1;
				int id = -1;
				for (int k = 0; k < buffer.size(); k++){
					if(buffer.get(k) > n_max){
						n_max = buffer.get(k);
						id = k; 
					}
				}
				max_index[id] = 1;
				buffer.set(id, -1.0);
				z++;
				if(id >= i) 
					id ++;
				// there should be at least one node of the same type
				// in the candidates
				if(node.getType().equals(nodes.get(id).getType()))
					same_type_found_no ++;
				
				if(z == loop && same_type_found_no < min_edge)
					loop ++;
			}

			// keep only first node_consideration_no number of nodes
			for (int j = 0; j < p.size(); j++)
				if(max_index[j] != 1)
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
		int n = (int)(rand.nextDouble() * (max_edge - min_edge) + min_edge);
		int choice_counter = 0;
		for (Double d : p)
			if(d != 0)
				choice_counter ++;
		n = Math.min(n, p.size());
		n = Math.min(n, choice_counter);
		ArrayList<Integer> indexes = stochastic_choice(p, n);
		
		// Create edges
		
		ArrayList<edge> edges = new ArrayList<edge>();
		
		for (int j = 0; j < indexes.size(); j++) {
			int index = indexes.get(j);
			if(index >= i)
				indexes.set(j, ++ index);
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
		
		for (edge edge : edges) {
			edge.getStart().getOutgoing_edges().add(edge);
			edge.getEnd().getIncoming_edges().add(edge);
		}
		
		return edges;
	}

	/**
	 * Checks if node1 and node2 in the neighborhood are, so that they 
	 * can be reached from each other by walking
	 * 
	 * @param	node1	the first node to be checked
	 * @param	node2	the second node to be checked
	 * @return	a boolean value, true if the nodes are in the neighborhood, false otherwise
	 * @see		test_data_generation
	 */
	private static boolean in_neighborhood(node node1, node node2) {
		// Maximum distance that can be reached by walking (kilometers)
		double treshold = 3;
		
		if(node1.getCity().equals(node2.getCity()))
			return true;
		
		double dist = node1.getCoordinate().getDistanceTo(node2.getCoordinate());
		
		return (dist > treshold) ? false : true;
	}

	/**
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

	/**
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
			for (int j = chosen_indexes.size() - 1; j >= 0; j--) {
				if(fixed_index >= chosen_indexes.get(j))
					fixed_index ++;
			}
			chosen_indexes.add(index);
			p_clone.remove(index);
			result.add(fixed_index);
		}
		return result;
		
	}

	/**
	 * Gets the city size of the network as the input
	 * and generates randomly city coordinations
	 * in a normal distributed manner
	 * 
	 * @param	size	no. of cities in the network
	 * @return	an array of n randomly generated coordinations
	 * @see		test_data_generation 
	 */
	private static ArrayList<coordinate> compute_city_center_coordinations(int size) {

		ArrayList<coordinate> coordinations = new ArrayList<coordinate>();

		Random rnd = new Random();
		
		for (int i = 0; i < size; i++) {
			float longitude = (rnd.nextFloat() * 2 - 1)
					* CoordinateManager.MAX_LONGITUDE;
			float latitude = (rnd.nextFloat() * 2 - 1)
					* CoordinateManager.MAX_LATITUDE;
			coordinations.add(new coordinate(latitude, longitude));
		}
		
		return coordinations;
	}
	
	/**
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
		if(sum == 0)
			throw new RuntimeException("all the weights are zero");
		Random rn = new Random();
		double rand = rn.nextDouble() * sum;
		double flag = 0;
		for (int i = 0; i < p.size(); i++) {
			flag += p.get(i);
			if(rand <= flag)
				return i;
		}
		System.out.println(p.toString());
		System.out.println(rand);
		System.err.println(flag);
		System.out.println(sum);
		return -1;
	}
	
}
