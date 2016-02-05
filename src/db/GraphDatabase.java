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
import model.timetable_row;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import enums.DbEdgePropertiesEnum;
import enums.DbNodePropertiesEnum;
import enums.edge_type;
import enums.node_type;


/**
 * @author Masoud Gholami
 *
 */
public class GraphDatabase {
	private String path;
	private GraphDatabaseService graphDb;
	
	/**
	 * Default class constructor
	 * 
	 * @see GraphDatabase
	 */
	public GraphDatabase() {
		
	}
	
	/**
	 * Class constructor setting the database path
	 * 
	 * @param path	the database path
	 * @see GraphDatabase
	 */
	public GraphDatabase(String path) {
		this();
		this.setPath(path);
		this.init();
	}
	
	/**
	 * Initiates the GraphDatabase
	 * 
	 * @see GraphDatabase
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
	 * @see GraphDatabase
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
	 * @see GraphDatabase
	 */
	public void Shutdown(){
		this.getGraphDb().shutdown();
	}

	/**
	 * Returns all of the edges in the database
	 * 
	 * @param	nodes	the nodes of the graph
	 * @return	database edges
	 * @see GraphDatabase
	 */
	public ArrayList<edge> getAllEdges(ArrayList<node> nodes){
		ArrayList<edge> edges = new ArrayList<edge>();
		try (Transaction tx = this.getGraphDb().beginTx()){
			Result result = this.getGraphDb()
					.execute("MATCH ()-[r]->() RETURN r;");
			while (result.hasNext()){
				Map<String, Object> row = result.next();
				Relationship rel = (Relationship) row.get("r");
				edge edge = new edge();
				edge.setId((int) rel.getProperty(DbEdgePropertiesEnum
						.id.toString()));
				edge.setFeasible((boolean) rel.getProperty(DbEdgePropertiesEnum
						.feasible.toString()));
				int id_n1 = (int) rel.getStartNode()
						.getProperty(DbNodePropertiesEnum.id.toString());
				int id_n2 = (int) rel.getEndNode()
						.getProperty(DbNodePropertiesEnum.id.toString());
				
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
				
				RelationshipType type = rel.getType();
				edge.setType(edge_type.valueOf(type.name()));
				
				String[] timetable = (String[]) rel.getProperty
						(DbEdgePropertiesEnum.timetable.toString());
				
				for (String t_row : timetable)
					edge.getTimetable().add(timetable_row.Parse(t_row));
				
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
	 * @see GraphDatabase
	 */
	public ArrayList<node> getAllNodes(){
		ArrayList<node> nodes = new ArrayList<node>();
		try (Transaction tx = this.getGraphDb().beginTx()){
			Result result = this.getGraphDb()
					.execute("MATCH (n) RETURN n;");
			while (result.hasNext()){
				Map<String, Object> row = result.next();
				Node n = (Node) row.get("n");
				node node = new node();
				node.setId((int) n.getProperty(DbNodePropertiesEnum.id.toString()));
				node.setCoordinate(coordinate.Parse((String)n.getProperty
						(DbNodePropertiesEnum.coordinate.toString())));
				
				Iterable<Label> l = n.getLabels();
				for (Label label : l)
					node.setType(node_type.valueOf(label.name()));
				
				nodes.add(node);
		    }
			tx.success();
		}
		return nodes;
	}

	/**
	 * Returns all of the edges in the database of the
	 * given type
	 * 
	 * @param	type	the type of the edge
	 * @param	nodes	the nodes of the graph
	 * @return	database edges
	 * @see GraphDatabase
	 */
	public ArrayList<edge> getEdgesOfType(edge_type type
			, ArrayList<node> nodes){
		ArrayList<edge> edges = new ArrayList<edge>();
		try (Transaction tx = this.getGraphDb().beginTx()){
			Result result = this.getGraphDb()
					.execute("MATCH ()-[r:" + type.toString() 
							+ "]->() RETURN r;");
			while (result.hasNext()){
				Map<String, Object> row = result.next();
				Relationship rel = (Relationship) row.get("r");
				edge edge = new edge();
				edge.setId((int) rel.getProperty(DbEdgePropertiesEnum
						.id.toString()));
				edge.setFeasible((boolean) rel.getProperty(DbEdgePropertiesEnum
						.feasible.toString()));
				edge.setType(type);

				int id_n1 = (int) rel.getStartNode()
						.getProperty(DbNodePropertiesEnum.id.toString());
				int id_n2 = (int) rel.getEndNode()
						.getProperty(DbNodePropertiesEnum.id.toString());
				
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
				
				String[] timetable = (String[]) rel.getProperty
						(DbEdgePropertiesEnum.timetable.toString());
				
				for (String t_row : timetable)
					edge.getTimetable().add(timetable_row.Parse(t_row));
				
				edges.add(edge);
		    }
			tx.success();
		}
		return edges;
	}
	
	/**
	 * Returns all of the nodes in the database of the
	 * given type
	 * 
	 * @param type	the node type
	 * @return	database nodes
	 * @see GraphDatabase
	 */
	public ArrayList<node> getNodesOfType(node_type type){
		ArrayList<node> nodes = new ArrayList<node>();
		try (Transaction tx = this.getGraphDb().beginTx()){
			Result result = this.getGraphDb()
					.execute("MATCH (n:" + type.toString() 
							+ ") RETURN n;");
			while (result.hasNext()){
				
				Map<String, Object> row = result.next();
				Node n = (Node) row.get("n");
				node node = new node();
				node.setId((int) n.getProperty(DbNodePropertiesEnum.id.toString()));
				node.setCoordinate(coordinate.Parse((String)n.getProperty
						(DbNodePropertiesEnum.coordinate.toString())));
				
				node.setType(type);
				
				nodes.add(node);
		    }
			tx.success();
		}
		return nodes;
	}

	
	/**
	 * Adds a new node to the database
	 * 
	 * @param node the node to be added
	 * @see GraphDatabase
	 */
	public void addNode(final node node){
		Node n;
		
		try (Transaction tx = this.getGraphDb().beginTx()){
			
			n = this.getGraphDb().createNode();
			
			Label label = new Label() {
				
				@Override
				public String name() {
					return node.getType().toString();
				}
			};
			
			n.addLabel(label);
			n.setProperty(DbNodePropertiesEnum.id.toString()
					, node.getId());
			n.setProperty(DbNodePropertiesEnum.coordinate
					.toString(), node.getCoordinate().toString());
			
			tx.success();
		}
	}
	
	/**
	 * Adds all nodes in the array to the database
	 * 
	 * @param nodes		the array of nodes to be added
	 * @see GraphDatabase
	 */
	public void addAllNodes(ArrayList<node> nodes){
		
		try (Transaction tx = this.getGraphDb().beginTx()){
			for (final node node : nodes) {
				Node n;
				n = this.getGraphDb().createNode();
				
				Label label = new Label() {
					
					@Override
					public String name() {
						return node.getType().toString();
					}
				};
				
				n.addLabel(label);
				n.setProperty(DbNodePropertiesEnum.id.toString()
						, node.getId());
				n.setProperty(DbNodePropertiesEnum.coordinate
						.toString(), node.getCoordinate().toString());
			}
			
			tx.success();
		}
	}
	
	/**
	 * Adds a new edge to the database
	 * 
	 * @param edge the edge to be added
	 * @see GraphDatabase
	 */
	public void addEdge(final edge edge){
		Relationship relationship;
		try (Transaction tx = this.getGraphDb().beginTx()){
			node startNode =edge.getStart();
			Node n1 = getGraphDbNode(startNode.getType()
					, startNode.getId());
			node endNode =edge.getEnd();
			Node n2 = getGraphDbNode(endNode.getType()
					, endNode.getId());
			RelationshipType relType = new RelationshipType() {
				
				@Override
				public String name() {
					return edge.getType().toString();
				}
			};
			
			relationship = 
					n1.createRelationshipTo(n2, relType);
			
			relationship.setProperty(DbEdgePropertiesEnum.id
					.toString(), edge.getId());
			relationship.setProperty(DbEdgePropertiesEnum.feasible
					.toString(), edge.isFeasible());
			relationship.setProperty(DbEdgePropertiesEnum.distance
					.toString(), edge.getDistance());
			
			String[] timetable = new String[edge.getTimetable().size()];
			
			for (int i = 0; i < timetable.length; i++)
				timetable[i] = edge.getTimetable().get(i).FullString();
			
			relationship.setProperty(DbEdgePropertiesEnum.timetable.toString()
					, timetable);

			tx.success();
		}
	}
	
	/**
	 * Adds all edges in the array to the database
	 * 
	 * @param edges		the array of edges to be added
	 * @see GraphDatabase
	 */
	public void addAllEdges(ArrayList<edge> edges){

		try (Transaction tx = this.getGraphDb().beginTx()){
			for (final edge edge : edges) {
				Relationship relationship;
				node startNode =edge.getStart();
				Node n1 = getGraphDbNode(startNode.getType()
						, startNode.getId());
				node endNode =edge.getEnd();
				Node n2 = getGraphDbNode(endNode.getType()
						, endNode.getId());
				RelationshipType relType = new RelationshipType() {
					
					@Override
					public String name() {
						return edge.getType().toString();
					}
				};
				
				relationship = 
						n1.createRelationshipTo(n2, relType);
				
				relationship.setProperty(DbEdgePropertiesEnum.id
						.toString(), edge.getId());
				relationship.setProperty(DbEdgePropertiesEnum.distance
						.toString(), edge.getDistance());
				relationship.setProperty(DbEdgePropertiesEnum.feasible
						.toString(), edge.isFeasible());
				
				String[] timetable = new String[edge.getTimetable().size()];
				
				for (int i = 0; i < timetable.length; i++)
					timetable[i] = edge.getTimetable().get(i).FullString();
				
				relationship.setProperty(DbEdgePropertiesEnum.timetable.toString()
						, timetable);
			}
			
			tx.success();
		}
	}	
	
	/**
	 * Finds a node in the graph database and return
	 * the node of the type org.neo4j.graphdb.Node
	 * 
	 * @param type	the type of the node
	 * @param id	the id of the node
	 * @return	the found node in the database
	 */
	public Node getGraphDbNode(final node_type type, int id){
		Label label = new Label() {
			
			@Override
			public String name() {
				return type.toString();
			}
		};
		
		ResourceIterator<Node> found = this.getGraphDb()
				.findNodes(label
						, DbNodePropertiesEnum.id.toString()
						, id);
		Node n = null;
		if(found.hasNext())
			n = found.next();
		
		return n;
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
	 * @see GraphDatabase
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
