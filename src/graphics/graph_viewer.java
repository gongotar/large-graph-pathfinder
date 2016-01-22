/**
 * 
 */

package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.BasicRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import model.CoordinateManager;
import model.coordinate;
import model.edge;
import model.network;
import model.node;

/** 
 * @author Masoud Gholami
 *
 */
public class graph_viewer{

	private static network netw;
	private static Dimension Size = new Dimension(700, 500);
	private static int border = 20;
	
	public static void main(String[] args){
		// network netw = test_data_generation.generate_netw(7);
		// show_graph(netw);
		// network netw = create_graph_visually();
		// show_graph(netw);
	}
	
	/**
	 * Prepares a visualization to be able to create a 
	 * graph interactively
	 *  
	 * @return the created graph
	 * @see	graph_viewer
	 */
	@SuppressWarnings("unused")
	private static network create_graph_visually() {
    	editing_graph eg =
        		new editing_graph();
		// Layout<V, E>, VisualizationViewer<V,E>
		Layout<node, edge> layout = new StaticLayout<node, edge>(eg.getGraph());
		layout.setSize(new Dimension(300,300));
		VisualizationViewer<node, edge> vv =
		new VisualizationViewer<node, edge>(layout);
		vv.setPreferredSize(new Dimension(350,350));
		// Show vertex and edge labels
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<node>());
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<edge>());
		// Create a graph mouse and add it to the visualization viewer
		//
		EditingModalGraphMouse<node, edge> gm =
		new EditingModalGraphMouse<node, edge>(vv.getRenderContext(),
		eg.getVertexFactory(), eg.getEdgeFactory());
		vv.setGraphMouse(gm);
		create_visualization(vv);
				return null;
	}

	/**
	 * Creates a view for the given network
	 * 
	 * @param netw	the given network
	 * @see	graph_viewer
	 */
	public static void show_graph(network netw) {
		setNetw(netw);
		DirectedGraph<node, edge> g = fill_graph();
		VisualizationViewer<node,edge> bvs = 
				visualization_settings(g);
		create_visualization(bvs);
	}

	/**
	 * Creates the visualization for the given VisualizationViewer 
	 * containing the graph. After calling this method the graph
	 * will be displayed
	 * 
	 * @param	bvs		VisualizationViewer
	 * @see	graph_viewer
	 */
	private static void create_visualization(
			VisualizationViewer<node, edge> bvs) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		JFrame frame = new JFrame();
		frame.setSize(getFrameSize());
		frame.setPreferredSize(getFrameSize());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(bvs);
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Sets the needed visualization properties
	 * 
	 * @return VisualizationViewer
	 * @see graph_viewer
	 */
    private static VisualizationViewer<node, edge> 
    			visualization_settings(DirectedGraph<node, edge> g){
    				
    	// Create a layout for setting the node locations
		Layout<node, edge> l = new CircleLayout<node, edge>(g);
		
		// Set the layout size
		l.setSize(getSize());
		
		// Create a transformer for getting the node coordinaton from
		// each node object and converting it to 2D location
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
				
		// Set the transformer of node location to the layout
		l.setInitializer(node_location);
		
		// Create a renderer
		Renderer<node, edge> r = new BasicRenderer<node, edge>();

		// Create a VisualizationServer for the graph g and
		// it's settings with the created layout
		VisualizationViewer<node, edge> vv = 
				new VisualizationViewer<node, edge>(l);
		
		// Transformer for setting the node label considering the node properties		
		Transformer<node, String> node_label =
				new Transformer<node, String>() {
					
					@Override
					public String transform(node arg0) {
						
						return arg0.toString();
					}
				};		
				
		// Transformer for setting the edge label considering the edge properties
		Transformer<edge, String> edge_label =
				new Transformer<edge, String>() {
					
					@Override
					public String transform(edge arg0) {
						
						return arg0.toString();
					}
				};

		// Transformer for setting node color
		Transformer<node, Paint> node_fill = 
				new Transformer<node, Paint>() {
					
					@Override
					public Paint transform(node arg0) {
						return Color.BLUE;
					}
				};
								
		// Set the transformers
		vv.getRenderContext().setEdgeLabelTransformer(edge_label);
		vv.getRenderContext().setVertexLabelTransformer(node_label);
		vv.getRenderContext().setVertexFillPaintTransformer(node_fill);
		
		// Set the renderer
		vv.setRenderer(r);
		
		// Set size
		vv.setSize(getSize());
		
		DefaultModalGraphMouse<node, edge> gm =
				new DefaultModalGraphMouse<node, edge>();
		gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
		vv.setGraphMouse(gm);
		return vv;
	}

	/**
     * Fills the SimpleDirectedWeightedGraph with the information 
     * given about the network (nodes, edges, ...)
     * 
     * @param	g	the directed weighted graph
     * @see		graph_viewer
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
     */
	private static int getCoordinate_y(coordinate coordinate) {
		Dimension size = getSize();
		double distance = CoordinateManager.latitudeConstant()
				* Math.abs(coordinate.getLatitude() + 90);
		int y = (int)(2 * size.height * distance / (Math.PI * CoordinateManager.EARTH_DIAMETER));
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
		double distance = CoordinateManager.longitudeConstant(coordinate.getLatitude())
				* Math.abs(coordinate.getLongitude() + 180);
		int x = (int)(size.width * distance / (Math.PI * 
				CoordinateManager.EARTH_DIAMETER * Math.cos(Math.toRadians(coordinate.getLatitude()))));
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
		graph_viewer.netw = netw.clone();
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

	/**
	 * Returns a Dimension a little bit bigger than the Size
	 * 
	 * @return frame size a little bigger than the size
	 * @see graph_viewer
	 */
	private static Dimension getFrameSize() {
		Dimension fd =  new Dimension(getSize().width + getBorder(),
				getSize().height + getBorder());
		return fd;
	}

	/**
	 * @return the border
	 */
	public static int getBorder() {
		return border;
	}

	/**
	 * @param border the border to set
	 */
	public static void setBorder(int border) {
		graph_viewer.border = border;
	}
}
