/**
 * 
 */
package model;

import java.util.ArrayList;

import enums.edge_type;


/**
 * @author Masoud Gholami
 *
 */
public class edge {

	private node start, end;
	private ArrayList<timetable_row> timetable;
	private edge_type type;
	private boolean feasible;

	/**
	 * @return the start
	 */
	public node getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(node start) {
		this.start = start;
	}

	/**
	 * @return the end
	 */
	public node getEnd() {
		return end;
	}

	/**
	 * @param end the end to set
	 */
	public void setEnd(node end) {
		this.end = end;
	}

	/**
	 * @return the timetable
	 */
	public ArrayList<timetable_row> getTimetable() {
		return timetable;
	}

	/**
	 * @param timetable the timetable to set
	 */
	public void setTimetable(ArrayList<timetable_row> timetable) {
		this.timetable = new ArrayList<timetable_row>(timetable);
	}

	/**
	 * @return the type
	 */
	public edge_type getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(edge_type type) {
		this.type = type;
	}

	/**
	 * @return the feasible
	 */
	public boolean isFeasible() {
		return feasible;
	}

	/**
	 * @param feasible the feasible to set
	 */
	public void setFeasible(boolean feasible) {
		this.feasible = feasible;
	}
	
}
