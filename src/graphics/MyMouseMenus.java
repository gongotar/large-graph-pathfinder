/*
 * MyMouseMenus.java
 *
 * Created on March 21, 2007, 3:34 PM; Updated May 29, 2007
 *
 * Copyright March 21, 2007 Grotto Networking
 *
 */

package graphics;

import edu.uci.ics.jung.visualization.VisualizationViewer;

import java.awt.Color;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.apache.commons.collections15.Transformer;

import core.dijkstra;
import model.connection;
import model.edge;
import model.label;
import model.network;
import model.node;

/**
 * A collection of classes used to assemble popup mouse menus for the custom
 * edges and vertices developed in this example.
 * @author Dr. Greg M. Bernstein
 */
public class MyMouseMenus {
    
    public static class EdgeMenu extends JPopupMenu {        
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		// private JFrame frame; 
        public EdgeMenu(final JFrame frame) {
            super("Edge Menu");
            // this.frame = frame;
            this.add(new DeleteEdgeMenuItem<edge>());
            this.addSeparator();
            this.add(new EdgePropItem(frame));  
            this.add(new EdgeTimeTable(frame));
        }
        
    }
    
    public static class EdgePropItem extends JMenuItem implements EdgeMenuListener<edge>,
            MenuPointListener {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		edge edge;
        VisualizationViewer<node, edge> visComp;
        Point2D point;
        
        public void setEdgeAndView(edge edge, VisualizationViewer<node, edge> visComp) {
            this.edge = edge;
            this.visComp = visComp;
        }

        public void setPoint(Point2D point) {
            this.point = point;
        }
        
        public EdgePropItem(final JFrame frame) {            
            super("Edit Edge Properties...");
            this.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    EdgePropertyDialog dialog = new EdgePropertyDialog(frame, edge);
                    dialog.setLocation((int)point.getX()+ frame.getX(), (int)point.getY()+ frame.getY());
                    dialog.setVisible(true);
                }
                
            });
        }
        
    }
    
    public static class VertexMenu extends JPopupMenu {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		network netw;

		public VertexMenu(final JFrame frame, network netw) {
            super("Vertex Menu");
            this.netw = netw; 
            this.add(new DeleteVertexMenuItem<node>());
            this.addSeparator();
            this.add(new NodePropItem(frame));
            this.add(new NodeLabels(frame));
            this.add(new ParetoOptimals(frame, netw));
            this.add(new SetTarget(frame));
            this.add(new HighlightPath(frame));
        }
    }
    
    public static class NodePropItem extends JMenuItem implements VertexMenuListener<node>,
    MenuPointListener {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		node v;
		VisualizationViewer<node, edge> visComp;
        Point2D point;

        public void setPoint(Point2D point) {
            this.point = point;
        }
        
        public void setVertexAndView(node v, VisualizationViewer<node, edge> visComp) {
            this.v = v;
            this.setSelected(true);
        }
        
        public NodePropItem(final JFrame frame) {            
            super("Edit Node Properties...");
            this.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    NodePropertyDialog dialog = new NodePropertyDialog(frame, v);
                    dialog.setLocation((int)point.getX()+ frame.getX(), (int)point.getY()+ frame.getY());
                    dialog.setVisible(true);
                }
                
            });
        }
    }

    public static class SetTarget extends JMenuItem implements VertexMenuListener<node>,
    MenuPointListener {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		node v;
		VisualizationViewer<node, edge> visComp;
        Point2D point;

        public void setPoint(Point2D point) {
            this.point = point;
        }
        
        public void setVertexAndView(node v, VisualizationViewer<node, edge> visComp) {
            this.v = v;
            this.setSelected(true);
        }
        
        public SetTarget(final JFrame frame) {           
        	super("Set as Target");
            this.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dijkstra.target = v;
                }
                
            });
        }
    }
    
    public static class EdgeTimeTable extends JMenuItem implements EdgeMenuListener<edge>,
	    MenuPointListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		edge edge;
		VisualizationViewer<node, edge> visComp;
		Point2D point;
		
		public void setEdgeAndView(edge edge, VisualizationViewer<node, edge> visComp) {
		    this.edge = edge;
		    this.visComp = visComp;
		}
		
		public void setPoint(Point2D point) {
		    this.point = point;
		}
		
		public EdgeTimeTable(final JFrame frame) {            
		    super("Edit Edge Timetable...");
		    this.addActionListener(new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		            EdgeTimeTableDialog dialog = new EdgeTimeTableDialog(frame, edge);
		            dialog.setLocation((int)point.getX()+ frame.getX(), (int)point.getY()+ frame.getY());
		            dialog.setVisible(true);
		        }
		        
		    });
		}
		
	}

    public static class NodeLabels extends JMenuItem implements VertexMenuListener<node>,
    MenuPointListener {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		node v;
		VisualizationViewer<node, edge> visComp;
        Point2D point;

        public void setPoint(Point2D point) {
            this.point = point;
        }
        
        public void setVertexAndView(node v, VisualizationViewer<node, edge> visComp) {
            this.v = v;
            this.setSelected(true);
        }
        
        public NodeLabels(final JFrame frame) {            
            super("Display Node Labels...");
            this.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    NodeLabelsDialog dialog = new NodeLabelsDialog(frame, v);
                    dialog.setLocation((int)point.getX()+ frame.getX(), (int)point.getY()+ frame.getY());
                    dialog.setVisible(true);
                }
                
            });
        }
    }

    public static class ParetoOptimals extends JMenuItem implements VertexMenuListener<node>,
    MenuPointListener {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		node v;
		VisualizationViewer<node, edge> visComp;
        Point2D point;

        public void setPoint(Point2D point) {
            this.point = point;
        }
        
        public void setVertexAndView(node v, VisualizationViewer<node, edge> visComp) {
            this.v = v;
            this.visComp = visComp;
            this.setSelected(true);
        }
        
        public ParetoOptimals(final JFrame frame, final network netw) {            
            super("Pareto Optimals...");
            this.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    NodeParetoDialog dialog = new NodeParetoDialog(frame, v, netw);
                    dialog.setLocation((int)point.getX()+ frame.getX(), (int)point.getY()+ frame.getY());
                    dialog.setVisible(true);
                }
                
            });
        }
    }

    public static class HighlightPath extends JMenuItem implements VertexMenuListener<node>,
    MenuPointListener {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		node v;
		VisualizationViewer<node, edge> visComp;
        Point2D point;

        public void setPoint(Point2D point) {
            this.point = point;
        }
        
        public void setVertexAndView(node v, VisualizationViewer<node, edge> visComp) {
            this.v = v;
            this.visComp = visComp;
            this.setSelected(true);
        }
        
        public HighlightPath(final JFrame frame) {            
            super("Highlight Path...");
            this.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	ArrayList<label> labels = v.getLabels();
                	final ArrayList<edge> edges = new ArrayList<edge>();
                	for (label label : labels) {
						for (connection<edge, Integer> conn : label.getPath()) {
							edges.add(conn.getEdge());
						}
					}
                	Transformer<edge, Paint> edges_color =
                			new Transformer<edge, Paint>() {
								@Override
								public Paint transform(edge arg0) {
									for (edge edge : edges) {
										if(arg0.getId() == edge.getId())
											return Color.YELLOW;
									}
									return null;
								}
							};
					visComp.getRenderContext().setEdgeFillPaintTransformer(edges_color);
                }
                
            });
        }
    }

    
}
