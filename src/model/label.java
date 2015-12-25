/**
 * 
 */
package model;

import java.util.ArrayList;

import java.time.Duration;

/**
 * @author Masoud Gholami
 *
 */
public class label {
	
	private node node;
	private ArrayList<connection<edge, Integer>> path;
	private Duration		duration;
	private int		change;
	private double	risk;
	private double	cost;
	
	
	/*
	 * Default class constructor
	 * creates the initial label for the start nodes
	 * 
	 * @param	node	assign the label to the node
	 * 
	 * @see label
	 */
	public label(node node){
		this.setDuration(Duration.ZERO);
		this.setChange(0);
		this.setRisk(0);
		this.setCost(0);
		this.setNode(node);
	}
	
	/*
	 * Class constructor 
	 */
	public label(node node, label l, connection<edge, Integer> c){
		path = new ArrayList<connection<edge, Integer>>(l.getPath());
		path.add(c);
		this.setNode(node);
	}
	
	/*
	 * Class constructor 
	 */
	public label(node node, label l, connection<edge, Integer> c, Duration duration, int change, int risk){
		this(node, l, c);
		this.setDuration(duration);
		this.setChange(change);
		this.setRisk(risk);
	}

	/**
	 * @return the path
	 */
	public ArrayList<connection<edge, Integer>> getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(ArrayList<connection<edge, Integer>> path) {
		this.path = new ArrayList<connection<edge, Integer>>(path);
	}

	/**
	 * @return the duration
	 */
	public Duration getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	/**
	 * @return the change
	 */
	public int getChange() {
		return change;
	}

	/**
	 * @param change the change to set
	 */
	public void setChange(int change) {
		this.change = change;
	}

	/**
	 * @return the risk
	 */
	public double getRisk() {
		return risk;
	}

	/**
	 * @param risk the risk to set
	 */
	public void setRisk(double risk) {
		this.risk = risk;
	}

	/**
	 * @return the cost
	 */
	public double getCost() {
		return cost;
	}

	/**
	 * @param cost the cost to set
	 */
	public void setCost(double cost) {
		this.cost = cost;
	}

	/**
	 * @return the node
	 */
	public node getNode() {
		return node;
	}

	/**
	 * @param node the node to set
	 */
	public void setNode(node node) {
		node.addLabel(this);
		this.node = node;
	}
	
}
