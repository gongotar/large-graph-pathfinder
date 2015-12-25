package net_info;

public class timetable_row <Id, Line, Cost, Start_time, End_time, Variation>{

	private Id 			id;
	private Line 		line;
	private Cost 		cost;
	private Start_time 	start_time;
	private End_time 	end_time;
	private Variation 	variation;
	
	/*
	 * Class constructor setting all of the class attributes
	 * 
	 * @param	id			the id of the connection
	 * @param 	line		the line no. of the connection
	 * @param	cost		the cost of taking the connection
	 * @param	start_time	the start time of the connection
	 * @param	end_time	the end time of the connection
	 * @param	variation	the duration variance (risk) of taking the connection 
	 */
	public timetable_row(Id id, Line line, Cost cost, Start_time start_time, 
			End_time end_time, Variation variation){
		
		this.setId(id);
		this.setLine(line);
		this.setCost(cost);
		this.setStart_time(start_time);
		this.setEnd_time(end_time);
		this.setVariation(variation);
		
	}
	
	/**
	 * @return the id
	 */
	public Id getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Id id) {
		this.id = id;
	}
	/**
	 * @return the line
	 */
	public Line getLine() {
		return line;
	}
	/**
	 * @param line the line to set
	 */
	public void setLine(Line line) {
		this.line = line;
	}
	/**
	 * @return the cost
	 */
	public Cost getCost() {
		return cost;
	}
	/**
	 * @param cost the cost to set
	 */
	public void setCost(Cost cost) {
		this.cost = cost;
	}
	/**
	 * @return the start_time
	 */
	public Start_time getStart_time() {
		return start_time;
	}
	/**
	 * @param start_time the start_time to set
	 */
	public void setStart_time(Start_time start_time) {
		this.start_time = start_time;
	}
	/**
	 * @return the end_time
	 */
	public End_time getEnd_time() {
		return end_time;
	}
	/**
	 * @param end_time the end_time to set
	 */
	public void setEnd_time(End_time end_time) {
		this.end_time = end_time;
	}
	/**
	 * @return the variation
	 */
	public Variation getVariation() {
		return variation;
	}
	/**
	 * @param variation the variation to set
	 */
	public void setVariation(Variation variation) {
		this.variation = variation;
	}
	
}
