/**
 * 
 */
package net_info;

import java.util.ArrayList;

import javax.xml.datatype.Duration;

/**
 * @author Masoud Gholami
 *
 */
public class label {
	
	private ArrayList<connection<edge, Integer>> path;
	private Duration		duration;
	private int		change;
	private double	risk;
	private double	cost;
	
	/*
	 * Class constructor 
	 */
	public label(label l, connection<edge, Integer> c){
		path = new ArrayList<connection<edge, Integer>>(l.getPath());
		path.add(c);
	}
	
	/*
	 * Class constructor 
	 */
	public label(label l, connection<edge, Integer> c, Duration duration, int change, int risk){
		this(l, c);
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
	
}
