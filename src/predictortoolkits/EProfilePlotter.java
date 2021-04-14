/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PredictorToolkits;

import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author USER
 */
public class EProfilePlotter extends javax.swing.JFrame {
    int file_Height = 1;
    int file_Width = 2;

    
    public void plotgen(int file_Height, int file_Width){
       
        try {
            File dir = new File( txtCdxPath.getText());
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String filedir = txtCdxPath.getText() + "\\" + txtFileName.getText() + ".cdxml";
            //File cdxmlfile = new File(filedir);
            //BufferedWriter bf_cdxml = new BufferedWriter(new FileWriter(filedir));
            OutputStream os_cdxml = new FileOutputStream(filedir);
            PrintWriter cdxml_write = new PrintWriter(new OutputStreamWriter(os_cdxml, "UTF-8"));
            float curvature = 3;
        
        
           String temp = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
                + " <!DOCTYPE CDXML SYSTEM \"http://www.cambridgesoft.com/xml/cdxml.dtd\" >\n"
                + " <CDXML><fonttable>\n" + "<font id=\"3\" charset=\"iso-8859-1\" name=\"Arial\"/>\n<font id=\"7\" charset=\"Unknown\" name=\"Symbol\"/>\n"
                + "</fonttable>";
           
           String temp1 = "<page\n"
                + " id=\"6\"\n"
                + " BoundingBox=\"0 0 523.20 769.68\"\n" // 1cm is approx 28, 1 column = 212.8 / 7.6 cm , 2 column = 425.6 / 15.2 cm
                + " HeaderPosition=\"36\"\n"
                + " FooterPosition=\"36\"\n"
                + " PrintTrimMarks=\"yes\"\n"
                + " HeightPages=\"" + file_Height + "\"\n"
                + " WidthPages=\"" + file_Width + "\"\n"
                + ">";
           
            String Arrow = "<arrow\n" + " id=\"13\"\n" + " BoundingBox=\"13.87 13.84 18.63 60\"\n" + " Z=\"6\"\n"
                    + " LineWidth=\"0.85\"\n" + " FillType=\"None\"\n" + " ArrowheadHead=\"Full\"\n" + " ArrowheadType=\"Solid\"\n"
                    + " HeadSize=\"1000\"\n" + " ArrowheadCenterSize=\"875\"\n" + " ArrowheadWidth=\"250\"\n" + " Head3D=\"16.50 13.84 0\"\n"
                    + " Tail3D=\"16.50 64 0\"\n" + " Center3D=\"21.75 45.59 0\"\n" + " MajorAxisEnd3D=\"71.91 45.59 0\"\n"
                    + " MinorAxisEnd3D=\"21.75 95.75 0\"\n" + "/>";
            
            String Gsol = "<t\n" + " id=\"11\"\n" + " p=\"40.50 24.50\"\n" + " BoundingBox=\"20.99 15.45 60.01 37.65\"\n" + " Z=\"7\"\n" 
                    + " CaptionJustification=\"Center\"\n" + " Justification=\"Center\"\n" + " LineHeight=\"auto\"\n"
                    + " LineStarts=\"8 18\"\n"  + "><s font=\"7\" size=\"9\" color=\"0\" face=\"1\">D</s><s font=\"3\" size=\"9\" color=\"0\" face=\"1\">G(sol)\n"
                    + "</s><s font=\"3\" size=\"9\" color=\"0\">(kcal/mol)</s></t>";
           
            cdxml_write.print(temp);
            gen_colortable(cdxml_write);
            cdxml_write.print(temp1);
            gen_points(cdxml_write, txtEnergies.getText(), 4, 1);
        /*
        points = gen_points(txtEnergies.getText(), 50, 50, 10);
        gen_curve(cdxml_write, points, curvature);
        gen_points(cdxml_write, points);
        */
            cdxml_write.print(Arrow);
            cdxml_write.print(Gsol);
            cdxml_write.println("</page></CDXML>");
            
            cdxml_write.close();
            os_cdxml.close();
            JOptionPane.showMessageDialog(null, "Completed!");
            File file = new File(txtCdxPath.getText());
            Desktop.getDesktop().open(file);
        } catch (IOException ex) {
            String test = "1";
        }
    }
        
    public void gen_points(PrintWriter targetfile, String energies, float rel_dx, float curvature){
        String[] energy_arr = energies.split("\\s+");
        double linewidth = 0.03;
        double firstitem = Float.parseFloat(energy_arr[0]);
        double inc_x = 0;
        double inc_y = 0;
        double start_x = 30;
        double start_y = 50;
        double horizontal = 212.8;
        double vertical = 770;
        double maxen = 0;
        double minen = 0;
        
        
        if (ComboBoxColumnSize.getSelectedObjects()[0].toString().equals("2 Column")) {
            horizontal = 425.6;
        }         
        
        double x_spacing = horizontal / (energy_arr.length);
        if (ComboBoxColumnSize.getSelectedObjects()[0].toString().equals("Fixed")) {
            x_spacing = 30;
        }
        
        double y_spacing = 5; // 560
        
        ArrayList<Double> points_list_x = new ArrayList<Double>();
        ArrayList<Double> points_list_y = new ArrayList<Double>();
        for (String item : energy_arr){
            double temp = Float.parseFloat(item);
            if (temp > maxen){
                maxen = temp;
            }
            if (temp < minen){
                minen = temp;
            }
        }
        
        if (maxen - minen > 100){
            y_spacing = 500 / (maxen - minen);
        }
        
        for (String item : energy_arr){
            double temp = Float.parseFloat(item);
            inc_y = temp - firstitem;
            points_list_x.add(start_x + inc_x);
            points_list_y.add(start_y - inc_y * y_spacing);
            inc_x += x_spacing;
        }
        
        // Make the sequence for drawing the curves // 
        String curvepoints = "";
        double minval = Collections.min(points_list_y);
        double maxval = Collections.max(points_list_y);
        double y_shift = points_list_y.get(0) - minval;
        double fix_y = Float.parseFloat(txtFontSize.getText()) + 3;
        
        for (int i = 0; i < points_list_x.size() ; i = i+1) {
            curvepoints = curvepoints + " " + String.format("%.2f", points_list_x.get(i) - 15 * curvature) + " " + String.format("%.2f", points_list_y.get(i) + y_shift);
            curvepoints = curvepoints + " " + String.format("%.2f", points_list_x.get(i)) + " " + String.format("%.2f", points_list_y.get(i) + y_shift);
            curvepoints = curvepoints + " " + String.format("%.2f", points_list_x.get(i) + 15 * curvature) + " " + String.format("%.2f", points_list_y.get(i) + y_shift);
            String dot = "<graphic\n BoundingBox=\"" +  String.format("%.2f", points_list_x.get(i)) + " " + String.format("%.2f", points_list_y.get(i) + y_shift) +" ";
            dot = dot + String.format("%.2f", points_list_x.get(i)) + " " + String.format("%.2f", points_list_y.get(i) + 15 + y_shift) + "\"\n color=\"4\"\n GraphicType=\"Symbol\"\n SymbolType=\"Electron\"\n/>\n";
            targetfile.print(dot);
            
            String label_before = "<t\n" +" id=\"18\"\n" +
            " p=\"" + String.format("%.2f", points_list_x.get(i)) + " " +  String.format("%.2f", (points_list_y.get(i) + y_shift + fix_y)) + "\"\n" +
            " BoundingBox=\"" +String.format("%.2f", points_list_x.get(i)) + " " +  String.format("%.2f", points_list_y.get(i) + y_shift) + " " + String.format("%.2f", points_list_x.get(i) + 25) + " " + String.format("%.2f", points_list_y.get(i) +24 + y_shift) + "\"\n" +
            " Z=\"13\"\n color=\"4\"\n" +
            " Warning=\"Chemical Interpretation is not possible for this label\"\n" +
            " CaptionJustification=\"Center\"\n" + " Justification=\"Center\"\n" +
            " LineHeight=\"auto\"\n" + " LineStarts=\"5 11\"\n" +
            "><s font=\"3\" size=\"" + txtFontSize.getText() + "\" color=\"4\" face=\"1\">";
            
            String label_middle = "</s><s font=\"4\" size=\"" + txtFontSize.getText() + "\" color=\"4\">";
            String label_end = "</s></t>";

            
            if (i%2 == 0){ // for intermediate
                String label = Integer.toString((i + 2)/2);
                targetfile.print(label_before + label + "\n");
                String energyval = String.format("%.02f", Float.parseFloat(energy_arr[i]));
                targetfile.print(label_middle + "(" + energyval.replace("-",  "\u2013") + ")"); // minus to en dash
                targetfile.print(label_end);
                fix_y = -1 * Float.parseFloat(txtFontSize.getText()) -6 ;
                
            } else { // for TS
                String label = Integer.toString((i + 1)/2) + "-TS";
                targetfile.print(label_before + label + "\n");
                String energyval = String.format("%.02f", Float.parseFloat(energy_arr[i]));
                targetfile.print(label_middle + "(" + energyval.replace("-",  "\u2013") + ")");
                targetfile.print(label_end);
                fix_y = Float.parseFloat(txtFontSize.getText()) + 3;
            }

        }
        targetfile.print("<curve\n id=\"4\"\n Z=\"1\"\n color=\"4\"\n LineWidth=\"" + String.valueOf(linewidth * 28.3) + "\"\n ArrowheadType=\"Solid\"\n CurvePoints=\"" + curvepoints);
        targetfile.print("\"\n/>\n");
    }
    
    public void gen_colortable(PrintWriter targetfile){
        double r = txtBoxColor.getBackground().getRed();
        double g = txtBoxColor.getBackground().getGreen();
        double b = txtBoxColor.getBackground().getBlue();
        
        String colortable = "<colortable>\n<color r=\"1\" g=\"1\" b=\"1\"/>\n<color r=\"0\" g=\"0\" b=\"0\"/>\n"
                + "<color r=\"" + r/255.00 + "\" g=\"" + g/255.00 + "\" b=\"" + b/255.00 + "\"/>\n</colortable>";
        targetfile.print(colortable);

    }
       
    
    
    /**
     * Creates new form EProfilePlotter
     */
    public EProfilePlotter() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel6 = new javax.swing.JPanel();
        btnPathPicker = new javax.swing.JButton();
        txtFileName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btnInfo = new javax.swing.JButton();
        ComboBoxColumnSize = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        txtCdxPath = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtFontSize = new javax.swing.JTextField();
        txtBoxColor = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtEnergies = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btnGenerate = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Energy Profile Plotter");
        setMinimumSize(new java.awt.Dimension(357, 240));
        setPreferredSize(new java.awt.Dimension(357, 205));

        jPanel6.setBackground(new java.awt.Color(204, 204, 204));

        btnPathPicker.setText("Path");
        btnPathPicker.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPathPickerActionPerformed(evt);
            }
        });

        txtFileName.setText("Energy_profile");
        txtFileName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFileNameActionPerformed(evt);
            }
        });

        jLabel3.setText(".cdxml");

        btnInfo.setBackground(new java.awt.Color(255, 204, 204));
        btnInfo.setFont(new java.awt.Font("굴림", 1, 12)); // NOI18N
        btnInfo.setText("?");
        btnInfo.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnInfo.setMaximumSize(new java.awt.Dimension(77, 77));
        btnInfo.setMinimumSize(new java.awt.Dimension(77, 77));
        btnInfo.setPreferredSize(new java.awt.Dimension(77, 77));
        btnInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInfoActionPerformed(evt);
            }
        });

        ComboBoxColumnSize.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1 Coulmn", "2 Column", "Fixed" }));
        ComboBoxColumnSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboBoxColumnSizeActionPerformed(evt);
            }
        });

        jLabel2.setText("Width");

        txtCdxPath.setText("C:\\Predictor\\Energy_Profile");

        jLabel4.setText("Filename:");

        jLabel5.setText("Font");

        txtFontSize.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtFontSize.setText("9");
        txtFontSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFontSizeActionPerformed(evt);
            }
        });

        txtBoxColor.setEditable(false);
        txtBoxColor.setBackground(new java.awt.Color(0, 0, 0));
        txtBoxColor.setToolTipText("");
        txtBoxColor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtBoxColorMouseClicked(evt);
            }
        });
        txtBoxColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBoxColorActionPerformed(evt);
            }
        });

        jLabel6.setText("Color");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFontSize, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBoxColor, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ComboBoxColumnSize, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnPathPicker)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtFileName)
                            .addComponent(txtCdxPath, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(btnInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPathPicker)
                    .addComponent(txtCdxPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ComboBoxColumnSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(txtFontSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6)
                        .addComponent(txtBoxColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txtEnergies.setToolTipText("Type your energies here. ex) 0 10 -5 4");
        txtEnergies.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEnergiesActionPerformed(evt);
            }
        });

        jLabel1.setText("Energy Sequence:");

        btnGenerate.setText("Generate");
        btnGenerate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerateActionPerformed(evt);
            }
        });

        jLabel7.setForeground(new java.awt.Color(153, 153, 153));
        jLabel7.setText("ex) 0 10 -5 …");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(txtEnergies))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnGenerate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(7, 7, 7)
                                .addComponent(jLabel7))
                            .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel7))
                .addGap(6, 6, 6)
                .addComponent(txtEnergies, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnGenerate)
                .addGap(11, 11, 11))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnPathPickerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPathPickerActionPerformed
        // TODO add your handling code here:
        String pathtocdx = "";
        JFileChooser jfc = new JFileChooser("C\\");
            jfc.setMultiSelectionEnabled(true);
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = jfc.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File[] file = jfc.getSelectedFiles();
                pathtocdx = file[0].getAbsolutePath();
            }
            txtCdxPath.setText(pathtocdx);
    }//GEN-LAST:event_btnPathPickerActionPerformed

    private void txtFileNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFileNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFileNameActionPerformed

    private void btnInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInfoActionPerformed
        // TODO add your handling code here:
        JOptionPane.showMessageDialog(null, " Initial code by Dr. Florian Mulks & Dr. Jinhoon Jeong\n Modified & Implemented by Mina Son @ Baikgroup KAIST");
    }//GEN-LAST:event_btnInfoActionPerformed

    private void ComboBoxColumnSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboBoxColumnSizeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ComboBoxColumnSizeActionPerformed

    private void txtFontSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFontSizeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFontSizeActionPerformed

    private void txtBoxColorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtBoxColorMouseClicked
        // TODO add your handling code here:
        Color newColor = JColorChooser.showDialog(EProfilePlotter.this, "Choose color", txtBoxColor.getBackground());
        if (newColor != null) {
            txtBoxColor.setBackground(newColor);
        }
    }//GEN-LAST:event_txtBoxColorMouseClicked

    private void txtBoxColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBoxColorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBoxColorActionPerformed

    private void txtEnergiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEnergiesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEnergiesActionPerformed

    private void btnGenerateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerateActionPerformed
        plotgen(1, 1); // TODO add your handling code here:
    }//GEN-LAST:event_btnGenerateActionPerformed

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
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(EProfilePlotter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EProfilePlotter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EProfilePlotter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EProfilePlotter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new EProfilePlotter().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBoxColumnSize;
    private javax.swing.JButton btnGenerate;
    private javax.swing.JButton btnInfo;
    private javax.swing.JButton btnPathPicker;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JTextField txtBoxColor;
    private javax.swing.JTextField txtCdxPath;
    private javax.swing.JTextField txtEnergies;
    private javax.swing.JTextField txtFileName;
    private javax.swing.JTextField txtFontSize;
    // End of variables declaration//GEN-END:variables
}
