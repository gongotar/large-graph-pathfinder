/**
 * 
 */
package graphics;

import javax.swing.JLabel;

import model.network;

/**
 * @author Masoud Gholami
 *
 */
public class DbDialog extends javax.swing.JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	network netw;
	String path;
	boolean ReadOrWrite;
    
    /** Creates new form EdgeTimeTableDialog */
    public DbDialog(java.awt.Frame parent, network netw, boolean ReadOrWrite) {
        super(parent, true);
        this.netw = netw;
        this.ReadOrWrite = ReadOrWrite;
        initComponents();
        setTitle("Database");
    }

    private void initComponents() {
    	jButton1 = new javax.swing.JButton();
    	DbTextField = new javax.swing.JFormattedTextField();
    	jLabelDb = new JLabel();
    	
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Database");
        if(ReadOrWrite)
        	jButton1.setText("Load From DB");
        else
        	jButton1.setText("Store to DB");
        
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonHandler(evt);
            }
        });
        
        jLabelDb.setText("Database path: ");
        DbTextField.setValue("db/defaultDb");
        
        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jButton1)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabelDb))
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(DbTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 69, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelDb)
                    .add(DbTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton1))
        );
        
        pack();
    }
    
    private void okButtonHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonHandler
    	if(!ReadOrWrite)
    		netw.store_to_db(DbTextField.getText());
    	else{
    		netw.getEdges().clear();
    		netw.getNodes().clear();
    		netw.get_from_db(DbTextField.getText());
    	}
        dispose();
    }//GEN-LAST:event_okButtonHandler
    
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabelDb;
    private javax.swing.JFormattedTextField DbTextField;
}
