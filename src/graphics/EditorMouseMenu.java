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
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.apache.commons.collections15.Transformer;

import model.CoordinateManager;
import model.coordinate;
import model.edge;
import model.network;
import model.node;

/**
 * Illustrates the use of custom edge and vertex classes in a graph editing application.
 * Demonstrates a new graph mouse plugin for bringing up popup menus for vertices and
 * edges.
 * @author Masoud Gholami
 */
public class EditorMouseMenu {
    
	private static Dimension size = new Dimension(800, 500);
	
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
        final DirectedGraph<node, edge> g =
        		new DirectedSparseGraph<node, edge>();
        Layout<node, edge> layout = new StaticLayout<node, edge>(g);
        layout.setSize(size);
        final VisualizationViewer<node, edge> vv = 
                new VisualizationViewer<node, edge>(layout);
                
        Transformer<node, Point2D> node_location = 
				new Transformer<node, Point2D>() {

					@Override
					public Point2D transform(node arg0) {
						int x = graph_viewer.getCoordinate_x(arg0.getCoordinate());
						int y = graph_viewer.getCoordinate_y(arg0.getCoordinate());
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
				double s = point.y * Math.PI * CoordinateManager.EARTH_DIAMETER;
				double m = 2 * size.height * CoordinateManager.latitudeConstant();
				long latitude = (long)(s / m - 90.0);
				long longitude = (long)(Float.valueOf(360) 
						* point.x / size.width - Float.valueOf(180));
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
				Object obj = e.getSource();
            	@SuppressWarnings("unchecked")
        		final VisualizationViewer<node, edge> vv = (VisualizationViewer<node, edge>) obj;
                		
                Point2D p = e.getPoint();
                
                GraphElementAccessor<node, edge> pickSupport = vv.getPickSupport();
                if(pickSupport != null) {
                    node v = pickSupport.getVertex(vv.getGraphLayout(), p.getX(), p.getY());
                    if(v != null) {
                    	if(mode.equals(ModalGraphMouse.Mode.PICKING)){
                    		coordinate c = xyToCoordinate(e.getPoint());
                    		v.setCoordinate(c);
                    	}
                    }
                }
				super.mouseDragged(e);
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				
				for (node node : netw.getNodes())
					g.addVertex(node);
				
				for (edge edge : netw.getEdges())
					g.addEdge(edge, edge.getStart(), edge.getEnd());
				
				Transformer<node, Paint> node_color = new Transformer<node, Paint>() {
					
					@Override
					public Paint transform(node arg0) {
						if(arg0.getLabels().size() > 0)
							return Color.YELLOW;
						else
							return Color.RED;
					}
				};
				
				vv.getRenderContext().setVertexFillPaintTransformer(node_color);
				
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
        frame.getContentPane().add(vv);
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

        JMenuItem item4 = new JMenuItem("Highight checked nodes");
        item4.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				Transformer<node, Paint> node_color = new Transformer<node, Paint>() {
					
					@Override
					public Paint transform(node arg0) {
						if(arg0.getLabels().size() > 0)
							return Color.YELLOW;
						else
							return Color.RED;
					}
				};
				
				vv.getRenderContext().setVertexFillPaintTransformer(node_color);
			}
		});

        
        modeMenu.add(item1);
        modeMenu.add(item2);
        modeMenu.add(item3);
        modeMenu.add(item4);
        
        modeMenu.setText("Mouse Mode");
        modeMenu.setIcon(null); // I'm using this in a main menu
        modeMenu.setPreferredSize(new Dimension(100, 20)); // Change the size so I can see the text
        
        menuBar.add(modeMenu);
        frame.setJMenuBar(menuBar);
        
        gm.setMode(ModalGraphMouse.Mode.EDITING); // Start off in editing mode
        frame.pack();
        frame.setVisible(true);    
    }
}
