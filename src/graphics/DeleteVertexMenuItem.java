/*
 * DeleteVertexMenuItem.java
 *
 * Created on March 21, 2007, 2:03 PM; Updated May 29, 2007
 *
 * Copyright March 21, 2007 Grotto Networking
 *
 */

package graphics;

import edu.uci.ics.jung.visualization.VisualizationViewer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenuItem;

import model.node;
import model.edge;

/**
 * A class to implement the deletion of a vertex from within a 
 * PopupVertexEdgeMenuMousePlugin.
 * @author Dr. Greg M. Bernstein
 */
public class DeleteVertexMenuItem<V> extends JMenuItem implements VertexMenuListener<V> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private node vertex;
    private VisualizationViewer<node, edge> visComp;
    
    /** Creates a new instance of DeleteVertexMenuItem */
    public DeleteVertexMenuItem() {
        super("Delete Vertex");
        this.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                visComp.getPickedVertexState().pick(vertex, false);
                visComp.getGraphLayout().getGraph().removeVertex(vertex);
                for (int i = 0; i < GraphFactory.getNetw().getNodes().size(); i++) {
					if(GraphFactory.getNetw().getNodes().get(i).getId()
							== vertex.getId()){
						ArrayList<edge> out_going_edges = 
								GraphFactory.getNetw().getNodes().get(i).getOutgoing_edges();
						ArrayList<edge> incoming_edges = 
								GraphFactory.getNetw().getNodes().get(i).getIncoming_edges();
						
						int j = 0;
						while(j < GraphFactory.getNetw().getEdges().size()){
							if(search_edges(GraphFactory.getNetw().getEdges().get(j), out_going_edges)
									|| search_edges(GraphFactory.getNetw().getEdges().get(j), incoming_edges))
								GraphFactory.getNetw().getEdges().remove(j);
							else
								j++;
						}
						
						GraphFactory.getNetw().getNodes().remove(i);
						break;
					}
				}

                visComp.repaint();
            }

            /**
             * Searches the edge in the array of edges,
             * if found returns true else false
             * 
             * @param edge the edge to be searched
             * @param edges the array of edges	
             * @return	a boolean value, true if found
             * @author Masoud Gholami
             * @see DeleteVertexMenuItem
             */
			private boolean search_edges(edge edge,
					ArrayList<edge> edges) {
				for (edge e : edges) {
					if(e.getId() == edge.getId())
						return true;
				}
				return false;
			}
        });
    }

    /**
     * Implements the VertexMenuListener interface.
     * @param v 
     * @param visComp 
     */
    public void setVertexAndView(node v, VisualizationViewer<node, edge> visComp) {
        this.vertex = v;
        this.visComp = visComp;
        this.setText("Delete Vertex " + v.toString());
    }
    
}
