/**
 * 
 */
package graphics;

import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;

import model.edge;

import org.apache.commons.collections15.Transformer;

/**
 * @author Masoud Gholami
 *
 */
public class EdgeColor implements Transformer<edge, Paint>{
	public ArrayList<edge> path_edges = new ArrayList<edge>();
	@Override
	public Paint transform(edge arg0) {
		if(path_edges.contains(arg0))
			return Color.GREEN;
		else
			return null;
	}

}
