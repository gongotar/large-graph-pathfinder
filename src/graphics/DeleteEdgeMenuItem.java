/*
 * DeleteEdgeMenuItem.java
 *
 * Created on March 21, 2007, 2:47 PM; Updated May 29, 2007
 *
 * Copyright March 21, 2007 Grotto Networking
 *
 */

package graphics;

import edu.uci.ics.jung.visualization.VisualizationViewer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import model.node;
import model.edge;

/**
 * A class to implement the deletion of an edge from within a 
 * PopupVertexEdgeMenuMousePlugin.
 * @author Dr. Greg M. Bernstein
 */
public class DeleteEdgeMenuItem<E> extends JMenuItem implements EdgeMenuListener<E> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private edge edge;
    private VisualizationViewer<node, edge> visComp;
    
    /** Creates a new instance of DeleteEdgeMenuItem */
    public DeleteEdgeMenuItem() {
        super("Delete Edge");
        this.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                visComp.getPickedEdgeState().pick(edge, false);
                visComp.getGraphLayout().getGraph().removeEdge(edge);
                for (int i = 0; i < GraphFactory.getNetw().getNodes().size(); i++) {
					if(GraphFactory.getNetw().getEdges().get(i).getId()
							== edge.getId()){
						GraphFactory.getNetw().getEdges().remove(i);
						break;
					}
				}
                visComp.repaint();
            }
        });
    }

    /**
     * Implements the EdgeMenuListener interface to update the menu item with info
     * on the currently chosen edge.
     * @param edge 
     * @param visComp 
     */
    public void setEdgeAndView(edge edge, VisualizationViewer<node, edge> visComp) {
        this.edge = edge;
        this.visComp = visComp;
        this.setText("Delete Edge " + edge.toString());
    }
    
}
