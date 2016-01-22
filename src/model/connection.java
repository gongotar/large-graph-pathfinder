/**
 * Edge-Connection_id pair, defining the exact edge, time & line of the chosen path
 */
package model;

/**
 * @author Masoud Gholami
 *
 */
public class connection <Edge, Connection_id>{
	
    private Edge e;
    private Connection_id id;
    
    /**
     * Class constructor setting values of edge & connection id
     * 
     * @param	e	an edge of the chosen path
     * @param	id	connection id defining the exact time & line information
     * 
     * @see connection
     */
    public connection(Edge e, Connection_id id){
        this.setEdge(e);
        this.setId(id);
    }
    
    @Override
    public String toString() {
    	String text = "";
    	text = "(" + ((edge)this.getEdge()).getId() + ", "
    			+ this.getId() + ")";
    	return text;
    }
	/**
	 * @return the edge
	 */
    public Edge getEdge(){ 
    	return e; 
    }
    
	/**
	 * @return the connection id
	 */
    public Connection_id getId(){
    	return id; 
    }
    
	/**
	 * @param e		the edge to set
	 */
    public void setEdge(Edge e){ 
    	this.e = e; 
    }
    
	/**
	 * @param id	the connection id to set
	 */
    public void setId(Connection_id id){ 
    	this.id = id; 
    }

}
