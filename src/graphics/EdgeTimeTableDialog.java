/**
 * 
 */
package graphics;

import model.edge;

/**
 * @author Masoud Gholami
 *
 */
public class EdgeTimeTableDialog  extends javax.swing.JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	edge edge;
    
    /** Creates new form EdgeTimeTableDialog */
    public EdgeTimeTableDialog(java.awt.Frame parent, edge edge) {
        super(parent, true);
        initComponents();
        this.edge = edge;
        setTitle("Edge: " + edge.toString());
    }

    private void initComponents() {
    	jButton1 = new javax.swing.JButton();
    	
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Edge Timetable");
        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonHandler(evt);
            }
        });
        
    }
    
    private void okButtonHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonHandler        
        dispose();
    }//GEN-LAST:event_okButtonHandler
    
    private javax.swing.JButton jButton1;
}
