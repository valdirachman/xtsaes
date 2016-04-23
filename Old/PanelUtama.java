/*
 * Class PanelUtama extends JPanelimplements ActionListener
 * Version:
 * -1.0 (20-04-2012) develop class PanelUtama
 *
 * Copyright 2010 Omar Abdillah, Prahesa Kusuma Setia, Yahya Muhammad
 */

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.io.File;



/** Class PanelUtama
  * Class ini berfungsi untuk membuat panel yang merupakan tampilan badan dari program ini.
  *
  * @author Omar Abdillah (0906510451)
  * @author Prahesa Kusuma  Setia (0906510470)
  * @author Yahya Muhammad (0906510565)
  */
class PanelUtama extends JPanel implements ActionListener  {
    // lebel-label yang digunakan dalam program ini
    private JLabel judul, plaintext, key, ciphertext;
    // textfield yang digunakan
    private JTextField inputField, keyField, outputField;
    // button-button yang digunakan
    private JButton dekripsi, enkripsi, openKey, openFile, saveFile;
    // data-data bertipe String yang digunakan dalam program ini
    File inputFile, keyFile, outputFile;

    //Constructor

    /** Constructor PanelUtama() merupakan sebuah
    * constructor dari class PanelUtama yang melakukan
    * pembuatan objek panel berisi label, button, dan textfiled yang ada pada tampilan program.
    */
    public PanelUtama () {
        setLayout(null);
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        initComponents();
        setLocation(10,100);
        setSize(692,300);
        addTampilan();

    }

     // Method

    /** Method addTampilan() merupakan sebuah method
    * yang berisi perintah-perintah add() dari semua atribut
    * yang digunakan dalam kelas ini
    */
    private void addTampilan() {
        add(judul);
        add(plaintext);
        add(key);
        add(ciphertext);
        add(inputField);
        add(keyField);
        add(outputField);
        add(dekripsi);
        add(enkripsi);
        add(openKey);
        add(openFile);
        add(saveFile);
    }

    // Method

    /*
     * Method initComponents() berisi pengaturan dari tata letak dan ukuran dari
     * atribut-atribut yang digunakan dalam kelas ini.
     */
    private void initComponents() {
        judul = new JLabel("EKRIPSI DAN DEKRIPSI XTS-AES");
        judul.setHorizontalAlignment(SwingConstants.CENTER);
        judul.setSize(680,40);
        judul.setFont(new Font("Times New Roman",0,25));
        judul.setHorizontalTextPosition(SwingConstants.CENTER);
        judul.setVerticalTextPosition(SwingConstants.CENTER);

        plaintext = new JLabel("Source         :");
        plaintext.setHorizontalAlignment(SwingConstants.LEFT);
        plaintext.setSize(220,20);
        plaintext.setFont(new Font("Times New Roman",0,20));
        plaintext.setHorizontalTextPosition(SwingConstants.CENTER);
        plaintext.setVerticalTextPosition(SwingConstants.CENTER);
        plaintext.setLocation(10,70);

        key = new JLabel("Key             :");
        key.setHorizontalAlignment(SwingConstants.LEFT);
        key.setSize(220,20);
        key.setFont(new Font("Times New Roman",0,20));
        key.setHorizontalTextPosition(SwingConstants.CENTER);
        key.setVerticalTextPosition(SwingConstants.CENTER);
        key.setLocation(10,120);

        ciphertext = new JLabel("Target       :");
        ciphertext.setHorizontalAlignment(SwingConstants.LEFT);
        ciphertext.setSize(220,20);
        ciphertext.setFont(new Font("Times New Roman",0,20));
        ciphertext.setHorizontalTextPosition(SwingConstants.CENTER);
        ciphertext.setVerticalTextPosition(SwingConstants.CENTER);
        ciphertext.setLocation(10,170);

        inputField =new JTextField(20);
        inputField.setSize(inputField.getPreferredSize());
        inputField.setLocation(130,70);
        inputField.setSize(220,20);

        keyField =new JTextField(20);
        keyField.setSize(keyField.getPreferredSize());
        keyField.setLocation(130,120);
        keyField.setSize(220,20);

        outputField =new JTextField(20);
        outputField.setSize(outputField.getPreferredSize());
        outputField.setLocation(130,170);
        outputField.setSize(220,20);

        dekripsi = new JButton("Dekripsi");
        dekripsi.setSize(100,20);
        dekripsi.setLocation(190,230);
        dekripsi.addActionListener(this);

        enkripsi = new JButton("Enkripsi");
        enkripsi.setSize(100,20);
        enkripsi.setLocation(350,230);
        enkripsi.addActionListener(this);

        openFile = new JButton("Source");
        openFile.setSize(150,20);
        openFile.setLocation(400,70);
        openFile.addActionListener(this);

        openKey = new JButton("Key");
        openKey.setSize(150,20);
        openKey.setLocation(400,120);
        openKey.addActionListener(this);

        saveFile = new JButton("Target");
        saveFile.setSize(150,20);
        saveFile.setLocation(400,170);
        saveFile.addActionListener(this);
    }

    // Method

    /*
     * Method actionPerformed() merupakan method override dari interface ActionListener
     * Method ini digunakan untuk mengatur event-event atau aksi-aksi
     * yang akan dilaksanakan ketika terjadi sebuah perintah dari user
     *
     * @param ActionEvent aksi merupakan perintah dari user
     */
    @Override
    public void actionPerformed(ActionEvent aksi) {
        if ( aksi.getSource() == openFile) {

            JFileChooser selected =  new JFileChooser(); // inisiasi atribut pilihan
            int result = selected.showOpenDialog(null); // result sebagai return value dari showSaveDialog
            if( result == JFileChooser.APPROVE_OPTION) {
              inputFile = selected.getSelectedFile(); // inisiasi dari atribut file
            } else if ( result ==  JFileChooser.CANCEL_OPTION) {
              return;
            }
            inputField.setText(inputFile.getPath());
        } else if (aksi.getSource() == openKey) {
            JFileChooser selected =  new JFileChooser(); // inisiasi atribut pilihan
            int result = selected.showOpenDialog(null); // result sebagai return value dari showSaveDialog
            if( result == JFileChooser.APPROVE_OPTION) {
              keyFile = selected.getSelectedFile(); // inisiasi dari atribut file
            } else if ( result ==  JFileChooser.CANCEL_OPTION) {
              return;
            }
            keyField.setText(keyFile.getPath());
        } else if(aksi.getSource() == saveFile) {
          JFileChooser selected =  new JFileChooser(); // inisiasi atribut pilihan
          int result = selected.showSaveDialog(null); // result sebagai return value dari showSaveDialog
          if( result == JFileChooser.APPROVE_OPTION) {
            outputFile = selected.getSelectedFile(); // inisiasi dari atribut file
          } else if ( result ==  JFileChooser.CANCEL_OPTION) {
            return;
          }
          outputField.setText(outputFile.getPath());
        } else if (aksi.getSource() == enkripsi) {
            XTSAES xts = new XTSAES(inputFile, keyFile, outputFile);
            try {
              xts.encrypt();
            } catch (Exception ex) {
                Logger.getLogger(PanelUtama.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (aksi.getSource() == dekripsi) {
            XTSAES xts = new XTSAES(inputFile, keyFile, outputFile);
            try {
              xts.decrypt();
            } catch (Exception ex) {
                Logger.getLogger(PanelUtama.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


}
