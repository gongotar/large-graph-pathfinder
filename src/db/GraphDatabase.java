/**
 * 
 */
package db;

import java.io.File;
import java.util.ArrayList;

import model.edge;
import model.node;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import enums.DbEdgePropertiesEnum;
import enums.DbNodePropertiesEnum;
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
	 * @see GraphDatabase
	 */
	public GraphDatabase() {
		
	}
	
	/**
	 * Class constructor setting the database path
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
	 * @see GraphDatabase
	 */
	public void init(){
		this.setGraphDb(new GraphDatabaseFactory()
				.newEmbeddedDatabase(new File((this.getPath()))));
		registerShutdownHook(this.getGraphDb());
	}
	
	/**
	 * Shuts down the database
	 * @see GraphDatabase
	 */
	public void Shutdown(){
		this.getGraphDb().shutdown();
	}
	
	/**
	 * Adds a new node to the database
	 * @param node the node to be added
	 * @see GraphDatabase
	 */
	public void addNode(final node node){
		Node n;
		
		try (Transaction tx = graphDb.beginTx())
		{
			
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
			n.setProperty(DbNodePropertiesEnum.city.toString()
					, node.getCity());
			n.setProperty(DbNodePropertiesEnum.coordinate
					.toString(), node.getCoordinate());
			
			tx.success();
		}
	}
	
	/**
	 * Adds all nodes in the array to the database
	 * @param nodes		the array of nodes to be added
	 * @see GraphDatabase
	 */
	public void addAllNodes(ArrayList<node> nodes){
		
		try (Transaction tx = this.getGraphDb().beginTx())
		{
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
				n.setProperty(DbNodePropertiesEnum.city.toString()
						, node.getCity());
				n.setProperty(DbNodePropertiesEnum.coordinate
						.toString(), node.getCoordinate());
			}
			
			tx.success();
		}
	}
	
	/**
	 * Adds a new edge to the database
	 * @param edge the edge to be added
	 * @see GraphDatabase
	 */
	public void addEdge(final edge edge){
		Relationship relationship;
		try (Transaction tx = this.getGraphDb().beginTx())
		{
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
			relationship.setProperty(DbEdgePropertiesEnum.timetable
					.toString(), edge.getTimetable());
			
			tx.success();
		}
	}
	
	/**
	 * Adds all edges in the array to the database
	 * @param edges		the array of edges to be added
	 * @see GraphDatabase
	 */
	public void addAllEdges(ArrayList<edge> edges){

		try (Transaction tx = this.getGraphDb().beginTx())
		{
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
				relationship.setProperty(DbEdgePropertiesEnum.feasible
						.toString(), edge.isFeasible());
				relationship.setProperty(DbEdgePropertiesEnum.timetable
						.toString(), edge.getTimetable());
				
			}
			
			tx.success();
		}
	}	
	
	/**
	 * Finds a node in the graph database and return
	 * the node of the type org.neo4j.graphdb.Node;
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
	 * @param graphDb	graphDb instance
	 * @see GraphDatabase
	 */
	private static void registerShutdownHook( final GraphDatabaseService graphDb )
	{
	    // Registers a shutdown hook for the Neo4j instance so that it
	    // shuts down nicely when the VM exits (even if you "Ctrl-C" the
	    // running application).
	    Runtime.getRuntime().addShutdownHook( new Thread()
	    {
	        @Override
	        public void run()
	        {
	            graphDb.shutdown();
	        }
	    });
	}

}
