/*
 * EditorMouseMenu.java
 *
 * Created on March 21, 2007, 10:34 AM; Updated May 29, 2007
 *
 * Copyright 2007 Grotto Networking
 */

package graphics;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationServer.Paintable;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import enums.CoordinateBox;
import enums.CoordinateRangeRepresetation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.apache.commons.collections15.Transformer;

import core.dijkstra;
import model.CoordinateManager;
import model.connection;
import model.coordinate;
import model.edge;
import model.label;
import model.network;
import model.node;

/**
 * Illustrates the use of custom edge and vertex classes in a graph editing application.
 * Demonstrates a new graph mouse plugin for bringing up popup menus for vertices and
 * edges.
 * @author Masoud Gholami
 */
public class EditorMouseMenu {
    
	private static Dimension size = new Dimension(800, 550);
	private static boolean Highlight_area = false;
	
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	network netw = new network();
    	create_graph_visually(netw);
    }
    
    public static void create_graph_visually(final network netw){
        final JFrame frame = new JFrame("Editing and Mouse Menu Demo");
        
        GraphFactory.setNetw(netw);
        final HashMap<CoordinateRangeRepresetation, Double> coordinateRange= 
        		getNetworkCoordinateRange(netw);
        final DirectedGraph<node, edge> g =
        		new DirectedSparseGraph<node, edge>();
        Layout<node, edge> layout = new StaticLayout<node, edge>(g);
        layout.setSize(size);
        final VisualizationViewer<node, edge> vv = 
                new VisualizationViewer<node, edge>(layout);
                
        GraphZoomScrollPane scrollPane = new GraphZoomScrollPane(vv);
                
        Transformer<node, Point2D> node_location = 
				new Transformer<node, Point2D>() {

					@Override
					public Point2D transform(node arg0) {
						int x = graph_viewer.getCoordinate_x(arg0.getCoordinate(), coordinateRange, size);
						int y = graph_viewer.getCoordinate_y(arg0.getCoordinate(), coordinateRange, size);
						Point2D.Double point = new Point2D.Double();
						point.setLocation(x, y);
						return point;
					}
				};
		
		/*
		Transformer<node, Paint> node_color = new Transformer<node, Paint>() {
			
			@Override
			public Paint transform(node arg0) {
				if(arg0.getId() == 897)
					return Color.YELLOW;
				else
					return Color.RED;
			}
		};		
		
		// Set the transformer of node color
		vv.getRenderContext().setVertexFillPaintTransformer(node_color );
		*/
				
		// Set the transformer of node location to the layout
		layout.setInitializer(node_location);
               
        vv.setPreferredSize(size);
        // Show vertex and edge labels
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<node>());
        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<edge>());
        
        // Set the color of the nodes
        NodeColor nc = new NodeColor();
		vv.getRenderContext().setVertexFillPaintTransformer(nc);
		
		// Set the color of the edges
		EdgeColor ec = new EdgeColor();
		vv.getRenderContext().setEdgeFillPaintTransformer(ec);
        
		
		Paintable paintable = new Paintable() {

			@Override
			public boolean useTransform() {
				return true;
			}
			
			@Override
			public void paint(Graphics g) {
				g.clearRect(0, 0, size.width, size.height);
				if(!Highlight_area || dijkstra.boxes == null)
					return;
				
				Color myColor = new Color(0, 0, 96, 8);
				g.setColor(myColor);
				for (HashMap<CoordinateBox, coordinate> box : dijkstra.boxes) {
					int[] xPoly = new int[4];
					int[] yPoly = new int[4];
					xPoly[0] = graph_viewer.getCoordinate_x(box.get(CoordinateBox.NE)
							, coordinateRange, size);
					yPoly[0] = graph_viewer.getCoordinate_y(box.get(CoordinateBox.NE)
							, coordinateRange, size);
					xPoly[1] = graph_viewer.getCoordinate_x(box.get(CoordinateBox.NW)
							, coordinateRange, size);
					yPoly[1] = graph_viewer.getCoordinate_y(box.get(CoordinateBox.NW)
							, coordinateRange, size);
					xPoly[2] = graph_viewer.getCoordinate_x(box.get(CoordinateBox.SW)
							, coordinateRange, size);
					yPoly[2] = graph_viewer.getCoordinate_y(box.get(CoordinateBox.SW)
							, coordinateRange, size);
					xPoly[3] = graph_viewer.getCoordinate_x(box.get(CoordinateBox.SE)
							, coordinateRange, size);
					yPoly[3] = graph_viewer.getCoordinate_y(box.get(CoordinateBox.SE)
							, coordinateRange, size);
					Polygon poly = new Polygon(xPoly, yPoly, xPoly.length);
					g.fillPolygon(poly);
				}
			}
		};
		vv.addPreRenderPaintable(paintable);
        // Create a graph mouse and add it to the visualization viewer
        EditingModalGraphMouse<node, edge> gm = new EditingModalGraphMouse<node, edge>(vv.getRenderContext(), 
                 GraphFactory.nodeFactory.getInstance(),
                GraphFactory.edgeFactory.getInstance()){
        	
        	/**
        	 * Converts the (x, y) point on a 2D grid to the 
        	 * latitude and longitude pair
        	 * @param point	the corresponding point
        	 * @return	the coordination
        	 * @author Masoud Gholami
        	 * @see EditorMouseMenu
        	 */
        	private coordinate xyToCoordinate(Point point) {
				double s = (size.getHeight() - point.y) * Math.PI * CoordinateManager.EARTH_DIAMETER;
				double m = 2 * size.height * CoordinateManager.latitudeConstant();
				float latitude = (float)(s / m - 90.0);
				float longitude = (float)(Float.valueOf(360) 
						* (double)(point.x) / (double)size.width - Float.valueOf(180));
				coordinate c = new coordinate(latitude, longitude);
				return c;
			}

			@Override
			public void mousePressed(MouseEvent e) {
        		coordinate c = xyToCoordinate(e.getPoint());
        		GraphFactory.nodeFactory.setMouse_location(c);
        		
        		Object obj = e.getSource();
            	@SuppressWarnings("unchecked")
        		final VisualizationViewer<node, edge> vv = (VisualizationViewer<node, edge>) obj;
                		
                Point2D p = e.getPoint();
                
                GraphElementAccessor<node, edge> pickSupport = vv.getPickSupport();
                if(pickSupport != null) {
                    node v = pickSupport.getVertex(vv.getGraphLayout(), p.getX(), p.getY());
                    if(v != null) {
                    	if(mode.equals(ModalGraphMouse.Mode.EDITING))
                    		GraphFactory.edgeFactory.setStart(v);
                    }
                }
				super.mousePressed(e);
			}
			@Override
			public void mouseDragged(MouseEvent e) {
				if(mode.equals(ModalGraphMouse.Mode.PICKING)){
					Object obj = e.getSource();
	            	@SuppressWarnings("unchecked")
	        		final VisualizationViewer<node, edge> vv = (VisualizationViewer<node, edge>) obj;
	                		
	                Point2D p = e.getPoint();
	                
	                GraphElementAccessor<node, edge> pickSupport = vv.getPickSupport();
	                if(pickSupport != null) {
	                    node v = pickSupport.getVertex(vv.getGraphLayout(), p.getX(), p.getY());
	                    if(v != null) {
	                    		coordinate c = xyToCoordinate(e.getPoint());
	                    		v.setCoordinate(c);                    	
	                    }
	                }
					super.mouseDragged(e);
				}
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				
				for (node node : netw.getNodes())
					g.addVertex(node);
				
				for (edge edge : netw.getEdges())
					g.addEdge(edge, edge.getStart(), edge.getEnd());
							
				node target = dijkstra.target;
				if(target != null){
					ArrayList<label> labels = target.getLabels();
					ArrayList<edge> edges = new ArrayList<edge>();
	            	ArrayList<node> nodes = new ArrayList<node>();
	            	for (label label : labels) {
	            		edge edge = null;
						for (connection<edge, Integer> conn : label.getPath()) {
							edge = conn.getEdge();
							edges.add(edge);
							nodes.add(edge.getStart());
						}
						nodes.add(edge.getEnd());
					}
	            	
					NodeColor nodes_color = 
							(NodeColor)vv.getRenderContext().getVertexFillPaintTransformer();
					nodes_color.path_nodes = nodes;	
					EdgeColor edge_color = (EdgeColor) vv.getRenderContext().getEdgeFillPaintTransformer();
					edge_color.path_edges = edges;
				}	
				frame.repaint();
				super.mouseEntered(e);
			}
        	@Override
			public void mouseReleased(MouseEvent e) {
        		Object obj = e.getSource();
            	@SuppressWarnings("unchecked")
        		final VisualizationViewer<node, edge> vv = (VisualizationViewer<node, edge>) obj;
                		
                Point2D p = e.getPoint();
                
                GraphElementAccessor<node, edge> pickSupport = vv.getPickSupport();
                if(pickSupport != null) {
                    node v = pickSupport.getVertex(vv.getGraphLayout(), p.getX(), p.getY());
                    if(v != null) {
                    	if(mode.equals(ModalGraphMouse.Mode.EDITING))
                    		GraphFactory.edgeFactory.setEnd(v);
                    	else if(mode.equals(ModalGraphMouse.Mode.PICKING)){
                    		coordinate c = xyToCoordinate(e.getPoint());
                    		v.setCoordinate(c);
                    	}
                    }
                }
				super.mouseReleased(e);
			}
        	
        };
        
        // Trying out our new popup menu mouse plugin...
        PopupVertexEdgeMenuMousePlugin<node, edge> myPlugin = new PopupVertexEdgeMenuMousePlugin<node, edge>();
        // Add some popup menus for the edges and vertices to our mouse plugin.
        JPopupMenu edgeMenu = new MyMouseMenus.EdgeMenu(frame);
        JPopupMenu vertexMenu = new MyMouseMenus.VertexMenu(frame, netw);
        myPlugin.setEdgePopup(edgeMenu);
        myPlugin.setVertexPopup(vertexMenu);
        gm.remove(gm.getPopupEditingPlugin());  // Removes the existing popup editing plugin
        
        gm.add(myPlugin);   // Add our new plugin to the mouse
        
        vv.setGraphMouse(gm);

        
        //JFrame frame = new JFrame("Editing and Mouse Menu Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(scrollPane);
        frame.setResizable(false);
        
        // Let's add a menu for changing mouse modes
        JMenuBar menuBar = new JMenuBar();
        JMenu modeMenu = gm.getModeMenu();
        
        JMenuItem item1 = new JMenuItem("Compute Pareto Optimal");
        item1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				QueryDialog dialog = new QueryDialog(frame, netw);
				dialog.setLocation(frame.getLocation().x + frame.getWidth() / 2
						, frame.getLocation().y + frame.getHeight() / 2);
	            dialog.setVisible(true);
	            /* ((EdgeColor)vv.getRenderContext()
                		.getEdgeFillPaintTransformer()).path_edges.clear();
                ((NodeColor)vv.getRenderContext()
                		.getVertexFillPaintTransformer()).path_nodes.clear();
                */
			}
		});
        
        JMenuItem item2 = new JMenuItem("Store to Database");
        item2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DbDialog dialog = new DbDialog(frame, netw, false);
				dialog.setLocation(frame.getLocation().x + frame.getWidth() / 2
						, frame.getLocation().y + frame.getHeight() / 2);
	            dialog.setVisible(true);
			}
		});
        
        JMenuItem item3 = new JMenuItem("Load from Database");
        item3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (node node : netw.getNodes())
					vv.getGraphLayout().getGraph().removeVertex(node);
				
				for (edge edge : netw.getEdges())
					vv.getGraphLayout().getGraph().removeEdge(edge);
				
				DbDialog dialog = new DbDialog(frame, netw, true);
				dialog.setLocation(frame.getLocation().x + frame.getWidth() / 2
						, frame.getLocation().y + frame.getHeight() / 2);
	            dialog.setVisible(true);
			}
		});

        JMenuItem item4 = new JMenuItem("Show/Hide search area highlight");
        item4.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Highlight_area = ! Highlight_area;
			}
		});
        
        JMenuItem item5 = new JMenuItem("Reset the Target");
        item5.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (node node : netw.getNodes())
					node.getLabels().clear();
				((NodeColor)vv.getRenderContext()
						.getVertexFillPaintTransformer()).path_nodes.clear();
				((EdgeColor)vv.getRenderContext()
						.getEdgeFillPaintTransformer()).path_edges.clear();
				dijkstra.boxes = null;
				dijkstra.target = null;
			}
		});
        
        modeMenu.add(item1);
        modeMenu.add(item2);
        modeMenu.add(item3);
        modeMenu.add(item4);
		modeMenu.add(item5);
        
        modeMenu.setText("Mouse Mode");
        modeMenu.setIcon(null); // I'm using this in a main menu
        modeMenu.setPreferredSize(new Dimension(100, 20)); // Change the size so I can see the text
        
        menuBar.add(modeMenu);
        frame.setJMenuBar(menuBar);
        
        gm.setMode(ModalGraphMouse.Mode.ANNOTATING); // Start off in Transforming mode
        frame.pack();
        frame.setVisible(true);    
    }

	private static HashMap<CoordinateRangeRepresetation, Double> getNetworkCoordinateRange(
			network netw) {
		
		HashMap<CoordinateRangeRepresetation, Double> range
			= new HashMap<CoordinateRangeRepresetation, Double>();
		
		range.put(CoordinateRangeRepresetation.minlat, Double.MAX_VALUE);
		range.put(CoordinateRangeRepresetation.maxlat, Double.MIN_VALUE);
		range.put(CoordinateRangeRepresetation.minlong, Double.MAX_VALUE);
		range.put(CoordinateRangeRepresetation.maxlong, Double.MIN_VALUE);
		
		for (node node : netw.getNodes()) {
			double lat = node.getCoordinate().getLatitude();
			double longt = node.getCoordinate().getLongitude(); 
			
			if(lat > range.get(CoordinateRangeRepresetation.maxlat))
				range.replace(CoordinateRangeRepresetation.maxlat, lat);
			else if(lat < range.get(CoordinateRangeRepresetation.minlat))
				range.replace(CoordinateRangeRepresetation.minlat, lat);
			
			if(longt > range.get(CoordinateRangeRepresetation.maxlong))
				range.replace(CoordinateRangeRepresetation.maxlong, longt);
			else if(longt < range.get(CoordinateRangeRepresetation.minlong))
				range.replace(CoordinateRangeRepresetation.minlong, longt);
		}
		
		return range;
	}
}
