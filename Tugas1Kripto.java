
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
/**
* ==================== TUGAS 1 KRIPTOGRAFI ====================
* Febriyola Anastasia 1306409500 - Valdi Rachman 1306381862
* Kelas utama untuk menjalankan program
* asumsi input >= 16 byte
* key 32 byte dan tweak 16 byte
*/
public class Tugas1Kripto extends javax.swing.JFrame {

    private static File input = null;
    private static File output = null;
    private static File key = null;
    private static File tweak = null;
    private static XTSAES xts;
    public static final int ENCRYPT = 0;
    public static final int DECRYPT = 1;

    private javax.swing.ButtonGroup buttonGroup1;
    private static javax.swing.JButton jButton1;
    private static javax.swing.JButton jButton2;
    private static javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private static javax.swing.JTextField jTextFieldInput;
    private static javax.swing.JTextField jTextFieldKey;
    private static javax.swing.JTextField jTextFieldOutput;
    private static javax.swing.JTextField jTextFieldTweak;

    /**
     * Creates new form GUI
     */
    public Tugas1Kripto() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jTextFieldInput = new javax.swing.JTextField();
        jTextFieldKey = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jTextFieldOutput = new javax.swing.JTextField();
        jTextFieldTweak = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("XTS AES");

        jTextFieldInput.setText("Input (length >= 16 byte)");
        jTextFieldInput.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mouseClickedEvent(evt);
            }
        });
        jTextFieldInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldInputActionPerformed(evt);
            }
        });

        jTextFieldKey.setText("Key");
        jTextFieldKey.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextFieldKeyMouseClicked(evt);
            }
        });

        jTextFieldTweak.setText("Tweak");
        jTextFieldTweak.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextFieldTweakMouseClicked(evt);
            }
        });

        jButton1.setText("Encrypt");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonMouseClicked(evt, ENCRYPT);
            }
        });

        jButton2.setText("Decrypt");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonMouseClicked(evt, DECRYPT);
            }
        });

        jTextFieldOutput.setText("Output");
        jTextFieldOutput.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextFieldOutputMouseClicked(evt);
            }
        });



        jLabel1.setText("Tugas 1 Kriptografi");
        jLabel2.setText("Febriyola Anastasia 1306409500 - Valdi Rachman 1306381862");
        jLabel3.setText("Bila tweak kosong, maka akan digunakan tweak default");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(7, 7, 7)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addContainerGap(0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldKey, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldInput, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldTweak, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldOutput, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(120, 120, 120)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton1)
                            .addComponent(jButton2))))));

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                .addComponent(jTextFieldInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldKey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jTextFieldTweak, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldOutput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addGap(40, 40, 40)
        ));

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void jTextFieldInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldInputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldInputActionPerformed

    private void mouseClickedEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mouseClickedEvent
        // TODO add your handling code here:
        int res = jFileChooser1.showOpenDialog(this);

        if (res == JFileChooser.APPROVE_OPTION) {
            input = jFileChooser1.getSelectedFile();
            jTextFieldInput.setText(input.getAbsolutePath());
        }
    }//GEN-LAST:event_mouseClickedEvent

    private void jTextFieldKeyMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextFieldKeyMouseClicked
        int res = jFileChooser1.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            key = jFileChooser1.getSelectedFile();
            jTextFieldKey.setText(key.getAbsolutePath());
        }
    }//GEN-LAST:event_jTextFieldKeyMouseClicked

    private void jTextFieldTweakMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextFieldKeyMouseClicked
        int res = jFileChooser1.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            tweak = jFileChooser1.getSelectedFile();
            jTextFieldTweak.setText(tweak.getAbsolutePath());
        }
    }//GEN-LAST:event_jTextFieldKeyMouseClicked

    private void jTextFieldOutputMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextFieldOutputMouseClicked
        int res = jFileChooser1.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            output = jFileChooser1.getSelectedFile();
            jTextFieldOutput.setText(output.getAbsolutePath());
        }
    }//GEN-LAST:event_jTextFieldOutputMouseClicked

    private void jButtonMouseClicked(java.awt.event.MouseEvent evt, int mode) {//GEN-FIRST:event_jButton1MouseClicked

        if (input != null && key != null && output != null) {
          if(tweak!=null){
            xts = new XTSAES(input, key, tweak,output);

          }else{
            xts = new XTSAES(input, key, output);

          }
            if(mode == ENCRYPT){
              xts.encrypt();
            }else{
              xts.decrypt();

            }
        } else if (input == null) {
            JOptionPane.showMessageDialog(this, "Masukkan file input", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } else if (key == null) {
            JOptionPane.showMessageDialog(this, "Masukkan file key", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } else if (output == null) {
            JOptionPane.showMessageDialog(this, "Masukkan file output", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton1MouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Tugas1Kripto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Tugas1Kripto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Tugas1Kripto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Tugas1Kripto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Tugas1Kripto().setVisible(true);

            }
        });

    }
}
