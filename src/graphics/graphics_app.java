package graphics;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.JFrame;
import org.apache.commons.collections15.Transformer;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.renderers.BasicRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import model.CoordinateManager;
import model.coordinate;
import model.edge;
import model.network;
import model.node;

public class graphics_app{

	private static network netw;
	private static Dimension Size;
	
	public static void main(String[] args){
		//network netw = test_data_generation.generate_netw(10);
		network netw = new network();
		ArrayList<node> nodes = new ArrayList<node>();
		node n1 = new node();
		n1.setId(0);
		n1.setType(enums.node_type.bus_station);
		n1.setCoordinate(new coordinate(-50, 20));
		node n2 = new node();
		n2.setId(1);
		n2.setType(enums.node_type.bus_station);
		n2.setCoordinate(new coordinate(0, 0));
		nodes.add(n1);
		nodes.add(n2);
		ArrayList<edge> edges = new ArrayList<edge>();
		edge e = new edge();
		e.setStart(n1);
		e.setEnd(n2);
		e.setType(enums.edge_type.bus);
		edges.add(e);
		netw.setNodes(nodes);
		netw.setEdges(edges);
		show_graph(netw);
	}
	
	/**
	 * Creates a view for the given network
	 * 
	 * @param netw	the given network
	 * @see	graphics_app
	 */
	private static void show_graph(network netw) {
		setNetw(netw);
		DirectedGraph<node, edge> g = fill_graph();
		BasicVisualizationServer<node,edge> bvs = 
				visualization_settings(g);
		create_visualization(bvs);
	}

	/**
	 * Creates the visualization for the given BasicVisualizationServer 
	 * containing the graph. After calling this method the graph
	 * will be displayed
	 * 
	 * @param	bvs		BasicVisualizationServer
	 * @see	graphics_app
	 */
	private static void create_visualization(
			BasicVisualizationServer<node, edge> bvs) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(bvs);
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Sets the needed visualization properties
	 * 
	 * @return BasicVisualizationServer
	 * @see graphics_app
	 */
    private static BasicVisualizationServer<node, edge> 
    			visualization_settings(DirectedGraph<node, edge> g){
		Layout<node, edge> l = new CircleLayout<node, edge>(g);
		l.setSize(new Dimension(600,600));
		setSize(l.getSize());
		
		Transformer<node, Point2D> node_location = 
				new Transformer<node, Point2D>() {

					@Override
					public Point2D transform(node arg0) {
						int x = getCoordinate_x(arg0.getCoordinate());
						int y = getCoordinate_y(arg0.getCoordinate());
						Point2D.Double point = new Point2D.Double();
						point.setLocation(x, y);
						return point;
					}
				};
				
		l.setInitializer(node_location);
		Renderer<node, edge> r = new BasicRenderer<node, edge>();

		BasicVisualizationServer<node, edge> bvs = 
				new BasicVisualizationServer<node, edge>(l);
		
		Transformer<node, String> node_label =
				new Transformer<node, String>() {
					
					@Override
					public String transform(node arg0) {
						
						return arg0.toString();
					}
				};		
				
		Transformer<edge, String> edge_label =
				new Transformer<edge, String>() {
					
					@Override
					public String transform(edge arg0) {
						
						return arg0.toString();
					}
				};
								
		bvs.getRenderContext().setEdgeLabelTransformer(edge_label);
		bvs.getRenderContext().setVertexLabelTransformer(node_label);
		bvs.setRenderer(r);
		return bvs;
	}

	/**
     * Fills the SimpleDirectedWeightedGraph with the information 
     * given about the network (nodes, edges, ...)
     * 
     * @param	g	the directed weighted graph
     * @see		graphics_app
     * @author 	Masoud Gholami
     */
    private static DirectedGraph<node, edge> fill_graph() {
    	ArrayList<node> nodes = getNetw().getNodes();
    	ArrayList<edge> edges = getNetw().getEdges();
    	
    	DirectedGraph<node, edge> graph =
    		new DirectedSparseGraph<node, edge>();
    	
    	for (node node : nodes) {
    		graph.addVertex(node);
		}
    	
    	for (edge edge : edges) {
			graph.addEdge(edge, edge.getStart(), edge.getEnd());
		}
		
    	return graph;
	}

    /**
     * Gets the coordinate as the input and calculates
     * the y axe of the location on the board
     * 
     * @param coordinate	the node coordination
     * @param Size	the size of the board 
     * @return	the y axe value of the location on the board
     * @author Masoud Gholami
     */
	private static int getCoordinate_y(coordinate coordinate) {
		Dimension size = getSize();
		coordinate c = new coordinate(coordinate.getLatitude(), 0);
		int y = (int)c.getDistanceTo(new coordinate(-90, 0));
		y = (int)(size.height * y / (Math.PI * CoordinateManager.EARTH_DIAMETER));
		return y;
	}

	/**
     * Gets the coordinate as the input and calculates
     * the x axe of the location on the board
     * 
     * @param coordinate	the node coordination
     * @param Size 	the size of the board
     * @return	the x axe value of the location on the board
     * @author Masoud Gholami
     */
	private static int getCoordinate_x(coordinate coordinate) {
		Dimension size = getSize();
		coordinate c = new coordinate(0, coordinate.getLongitude());
		int x = (int)c.getDistanceTo(new coordinate(0, -180));
		System.out.println(size.width);
		x = (int)(size.width * x / (Math.PI * CoordinateManager.EARTH_DIAMETER));
		return x;
	}

    /**
	 * @return the netw
	 */
	public static network getNetw() {
		return netw;
	}

	/**
	 * @param netw the netw to set
	 */
	public static void setNetw(network netw) {
		graphics_app.netw = netw.clone();
	}

	/**
	 * @return the size
	 */
	public static Dimension getSize() {
		return Size;
	}

	/**
	 * @param size the size to set
	 */
	public static void setSize(Dimension size) {
		Size = size;
	}

}
