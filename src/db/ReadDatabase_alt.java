/**
 * 
 */
package db;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import model.coordinate;
import model.edge;
import model.node;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import enums.DbEdgePropertiesEnum_alt;
import enums.DbNodePropertiesEnum_alt;
import enums.edge_type;
import enums.node_type;

/**
 * @author Masoud Gholami
 *
 */
public class ReadDatabase_alt {
	private String path;
	private GraphDatabaseService graphDb;
	
	/**
	 * Default class constructor
	 * 
	 * @see ReadDatabase_alt
	 */
	public ReadDatabase_alt() {
		
	}
	
	/**
	 * Class constructor setting the database path
	 * 
	 * @param path	the database path
	 * @see ReadDatabase_alt
	 */
	public ReadDatabase_alt(String path) {
		this();
		this.setPath(path);
		this.init();
	}
	
	/**
	 * Initiates the Graph Database
	 * 
	 * @see ReadDatabase_alt
	 */
	public void init(){
		File file = new File(this.getPath());
		this.setGraphDb(new GraphDatabaseFactory()
				.newEmbeddedDatabase(file));
		registerShutdownHook(this.getGraphDb());
	}
	
	/**
	 * Clears the database and removes all of 
	 * the edges and nodes
	 * 
	 * @see ReadDatabase_alt
	 */
	public void clearDatabase(){
		try (Transaction tx = this.getGraphDb().beginTx()){
			this.getGraphDb()
					.execute("MATCH (n) DETACH DELETE n");
			tx.success();
		}
	}
	
	/**
	 * Shuts down the database
	 * 
	 * @see ReadDatabase_alt
	 */
	public void Shutdown(){
		this.getGraphDb().shutdown();
	}

	/**
	 * Returns all of the edges in the database
	 * 
	 * @param	nodes	the nodes of the graph
	 * @return	database edges
	 * @see ReadDatabase_alt
	 */
	public ArrayList<edge> getAllEdges(ArrayList<node> nodes){
		ArrayList<edge> edges = new ArrayList<edge>();
		try (Transaction tx = this.getGraphDb().beginTx()){
			Result result = this.getGraphDb()
					.execute("MATCH ()-[r:NEWN]->() RETURN distinct r;");
			while (result.hasNext()){
				Map<String, Object> row = result.next();
				Relationship rel = (Relationship) row.get("r");
				edge edge = new edge();
				edge.setId((int) rel.getId());
				edge.setDistance(Double.valueOf
						(rel.getProperty(DbEdgePropertiesEnum_alt
								.distance.toString()).toString()));
				edge.setFeasible(true);
				int id_n1 = (int) rel.getStartNode().getId();
				int id_n2 = (int) rel.getEndNode().getId();
				
				for (node node : nodes) {
					if(node.getId() == id_n1){
						edge.setStart(node);
						node.getOutgoing_edges().add(edge);
					}
					if(node.getId() == id_n2){
						edge.setEnd(node);
						node.getIncoming_edges().add(edge);
					}
				}
				
				edge.setType(edge_type.car);
				
				edges.add(edge);
		    }
			tx.success();
		}
		return edges;
	}
	
	/**
	 * Returns all of the nodes in the database
	 * 
	 * @return	database nodes
	 * @see ReadDatabase_alt
	 */
	public ArrayList<node> getAllNodes(){
		ArrayList<node> nodes = new ArrayList<node>();
		try (Transaction tx = this.getGraphDb().beginTx()){
			Result result = this.getGraphDb()
					.execute("match (n)-[r:NEWN]-() return distinct n;");
			while (result.hasNext()){
				Map<String, Object> row = result.next();
				Node n = (Node) row.get("n");
				node node = new node();
				node.setId((int)n.getId());
				node.setCoordinate(new coordinate(Float.valueOf(n.getProperty
						(DbNodePropertiesEnum_alt.lat.toString()).toString()),
						Float.valueOf(n.getProperty(DbNodePropertiesEnum_alt.lon.toString()).toString())));
				
				node.setType(node_type.car_station);
				
				nodes.add(node);
		    }
			tx.success();
		}
		return nodes;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the graphDb
	 */
	public GraphDatabaseService getGraphDb() {
		return graphDb;
	}

	/**
	 * @param graphDb the graphDb to set
	 */
	public void setGraphDb(GraphDatabaseService graphDb) {
		this.graphDb = graphDb;
	}
	
	/**
	 * Registers a shutdown hook for the Graphdb instance
	 * 
	 * @param graphDb	graphDb instance
	 * @see ReadDatabase_alt
	 */
	private static void registerShutdownHook( final GraphDatabaseService graphDb )
	{
	    // Registers a shutdown hook for the Neo4j instance so that it
	    // shuts down nicely when the VM exits (even if you "Ctrl-C" the
	    // running application).
	    Runtime.getRuntime().addShutdownHook( new Thread(){
	        @Override
	        public void run(){
	            graphDb.shutdown();
	        }
	    });
	}

}

