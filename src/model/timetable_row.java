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
	 * Prints all of the row properties as a string
	 * 
	 * @return	row properties
	 * @see	timetable_row
	 */
	public String FullString(){
		String text = "";
		text += this.getId();
		text += ";";
		text += this.getLine();
		text += ";";
		text += this.getStart_time();
		text += ";";
		text += this.getEnd_time();
		text += ";";
		text += this.getCost();
		text += ";";
		text += this.getVariation();
		return text;
	}
	
	/**
	 * Prints the selected row properties as a string
	 * for the visualization proposes
	 * 
	 * @return	row properties
	 * @see	timetable_row
	 */
	@Override
	public String toString(){
		String text = "";
		text += this.getStart_time();
		text += ":";
		text += this.getEnd_time();
		text += "/";
		text += this.getLine();
		text += "@";
		text += this.getCost();
		return text;
	}
	
	/**
	 * Parses a string into a timetable row
	 * 
	 * @param arg	the string to be parsed
	 * @return	a new instance of a timetable row
	 * @see timetable_row
	 */
	public static timetable_row Parse(String arg){
		timetable_row row = new timetable_row();
		String info[] = arg.split(";");
		row.setId(Integer.parseInt(info[0].trim()));
		row.setLine(Integer.parseInt(info[1].trim()));
		row.setStart_time(LocalTime.parse(info[2].trim()));
		row.setEnd_time(LocalTime.parse(info[3].trim()));
		row.setCost(Double.parseDouble(info[4].trim()));
		row.setVariation(Double.parseDouble(info[5].trim()));
		return row;
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
