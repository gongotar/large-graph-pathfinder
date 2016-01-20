package model;

import java.time.LocalTime;

public class timetable_row{

	private int 			id;
	private int 			line;
	private double 			cost;
	private LocalTime 	start_time;
	private LocalTime 	end_time;
	private double 			variation;
	
	/**
	 * Default class constructor
	 */
	public timetable_row(){
		
	}
	
	/**
	 * Class constructor setting all of the class attributes
	 * 
	 * @param	id			the id of the connection
	 * @param 	line		the line no. of the connection
	 * @param	cost		the cost of taking the connection
	 * @param	start_time	the start time of the connection
	 * @param	end_time	the end time of the connection
	 * @param	variation	the duration variance (risk) of taking the connection 
	 */
	public timetable_row(int id, int line, double cost, LocalTime start_time, 
			LocalTime end_time, double variation){
		this();
		this.setId(id);
		this.setLine(line);
		this.setCost(cost);
		this.setStart_time(start_time);
		this.setEnd_time(end_time);
		this.setVariation(variation);
		
	}
	
	/**
	 * Prints the row properties as a string
	 * 
	 * @return	row properties
	 * @see	timetable_row
	 */
	@Override
	public String toString(){
		String text = "";
		text += this.getStart_time();
		text += " - ";
		text += this.getEnd_time();
		text += "/";
		text += this.getLine();
		return text;
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the line
	 */
	public int getLine() {
		return line;
	}
	/**
	 * @param line the line to set
	 */
	public void setLine(int line) {
		this.line = line;
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
	 * @return the start_time
	 */
	public LocalTime getStart_time() {
		return start_time;
	}
	/**
	 * @param start_time the start_time to set
	 */
	public void setStart_time(LocalTime start_time) {
		this.start_time = start_time;
	}
	/**
	 * @return the end_time
	 */
	public LocalTime getEnd_time() {
		return end_time;
	}
	/**
	 * @param end_time the end_time to set
	 */
	public void setEnd_time(LocalTime end_time) {
		this.end_time = end_time;
	}
	/**
	 * @return the variation
	 */
	public double getVariation() {
		return variation;
	}
	/**
	 * @param variation the variation to set
	 */
	public void setVariation(double variation) {
		this.variation = variation;
	}
	
}
