/**
 * 
 */
package graphics;

import model.edge;
import model.node;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;

/**
 * @author Masoud Gholami
 *
 */
public class editing_graph {
	
	private Graph<node, edge> graph;
    private int nodeCount, edgeCount;
    private Factory<node> vertexFactory;
    private Factory<edge> edgeFactory;
    
    /**
     * Default class constructor
     */
    public editing_graph() {
        this.setGraph(new SparseMultigraph<node, edge>());
        nodeCount = 0; edgeCount = 0;
        vertexFactory = new Factory<node>() {
            public node create() {
            	node node = new node();
            	node.setType(enums.node_type.bus_station);
            	node.setId(nodeCount ++);
                return node;
            }
        };
        edgeFactory = new Factory<edge>() {
            public edge create() {
            	edgeCount++;
                return new edge();
            }
        };
    }
	/**
	 * @return the graph
	 */
	public Graph<node, edge> getGraph() {
		return graph;
	}
	/**
	 * @param sparseMultigraph the graph to set
	 */
	public void setGraph(SparseMultigraph<node, edge> sparseMultigraph) {
		this.graph = sparseMultigraph;
	}
	/**
	 * @return the edgeCount
	 */
	public int getEdgeCount() {
		return edgeCount;
	}
	/**
	 * @param edgeCount the edgeCount to set
	 */
	public void setEdgeCount(int edgeCount) {
		this.edgeCount = edgeCount;
	}
	/**
	 * @return the nodeCount
	 */
	public int getNodeCount() {
		return nodeCount;
	}
	/**
	 * @param nodeCount the nodeCount to set
	 */
	public void setNodeCount(int nodeCount) {
		this.nodeCount = nodeCount;
	}
	/**
	 * @return the vertexFactory
	 */
	public Factory<node> getVertexFactory() {
		return vertexFactory;
	}
	/**
	 * @param vertexFactory the vertexFactory to set
	 */
	public void setVertexFactory(Factory<node> vertexFactory) {
		this.vertexFactory = vertexFactory;
	}
	/**
	 * @return the edgeFactory
	 */
	public Factory<edge> getEdgeFactory() {
		return edgeFactory;
	}
	/**
	 * @param edgeFactory the edgeFactory to set
	 */
	public void setEdgeFactory(Factory<edge> edgeFactory) {
		this.edgeFactory = edgeFactory;
	}
    
}
