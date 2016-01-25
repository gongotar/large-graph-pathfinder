/**
 * 
 */
package graphics;

import model.coordinate;
import model.edge;
import model.network;
import model.node;

import org.apache.commons.collections15.Factory;

import test_data.city;
import enums.edge_type;
import enums.node_type;

/**
 * @author Masoud Gholami
 *
 */
public class GraphFactory {

	private static network netw;
	
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
		GraphFactory.netw = netw;
	}
	
	public static class nodeFactory implements Factory<node> {
        private static int nodeId = 0;
        private static node_type defaultType = node_type.train_station;
        private static city defaultCity = new city();
        private static nodeFactory instance = new nodeFactory();
        private static coordinate mouse_location;
        
        private nodeFactory() {            
        }
        
        public static nodeFactory getInstance() {
            return instance;
        }
        
        public node create() {
            node v = new node();
            int maxId = -1;
            for (node node : GraphFactory.getNetw().getNodes())
				if(node.getId() > maxId)
					maxId = node.getId();
			
            nodeId = Math.max(nodeId, maxId + 1);
            v.setCity(defaultCity);
            v.setId(nodeId ++);
            v.setType(getDefaultType());
            v.setCoordinate(mouse_location);
            GraphFactory.getNetw().getNodes().add(v);
            return v;
        }        

   		/**
		 * @return the defaultType
		 */
		public static node_type getDefaultType() {
			return defaultType;
		}

		/**
		 * @param defaultType the defaultType to set
		 */
		public static void setDefaultType(node_type defaultType) {
			nodeFactory.defaultType = defaultType;
		}

		/**
		 * @return the mouse_location
		 */
		public static coordinate getMouse_location() {
			return mouse_location;
		}

		/**
		 * @param mouse_location the mouse_location to set
		 */
		public static void setMouse_location(coordinate mouse_location) {
			nodeFactory.mouse_location = mouse_location;
		}
    }

	public static class edgeFactory implements Factory<edge> {
        private static int edgeId = 0;
        private static edge_type defaultType = edge_type.train;
        private static edgeFactory instance = new edgeFactory();
        private static node start;
        private static node end;
        
        private edgeFactory() {            
        }
        
        public static edgeFactory getInstance() {
            return instance;
        }
        
        public edge create() {
            edge e = new edge();
            int maxId = -1;
            for (edge edge : GraphFactory.getNetw().getEdges())
				if(edge.getId() > maxId)
					maxId = edge.getId();
			
            edgeId = Math.max(edgeId, maxId + 1);
            e.setId(edgeId ++);
            e.setType(getDefaultType());
            e.setStart(start);
            e.setEnd(end);
            start.getOutgoing_edges().add(e);
            end.getIncoming_edges().add(e);
            GraphFactory.getNetw().getEdges().add(e);
            return e;
        }        

   		/**
		 * @return the defaultType
		 */
		public static edge_type getDefaultType() {
			return defaultType;
		}

		/**
		 * @param defaultType the defaultType to set
		 */
		public static void setDefaultType(edge_type defaultType) {
			edgeFactory.defaultType = defaultType;
		}

		/**
		 * @return the start
		 */
		public static node getStart() {
			return start;
		}

		/**
		 * @param start the start to set
		 */
		public static void setStart(node start) {
			edgeFactory.start = start;
		}

		/**
		 * @return the end
		 */
		public static node getEnd() {
			return end;
		}

		/**
		 * @param end the end to set
		 */
		public static void setEnd(node end) {
			edgeFactory.end = end;
		}
    }
	
	
}
