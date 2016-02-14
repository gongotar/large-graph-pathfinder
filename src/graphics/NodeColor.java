/**
 * 
 */
package graphics;

import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;

import model.node;

import org.apache.commons.collections15.Transformer;

/**
 * @author Masoud Gholami
 *
 */
public class NodeColor implements Transformer<node, Paint>{
	public ArrayList<node> path_nodes = new ArrayList<node>();
	@Override
	public Paint transform(node arg0) {
		
		if(arg0.getLabels().size() > 0)
			if(path_nodes.contains(arg0))
				return Color.GREEN;
			else
				return Color.YELLOW;
		else
			return Color.RED;
	}

}
