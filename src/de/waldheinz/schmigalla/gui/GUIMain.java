/*
 * GUIMain.java
 *
 * Created on 27. September 2007, 11:14
 */

package de.waldheinz.schmigalla.gui;

import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import de.waldheinz.schmigalla.CSVReaderAdapter;
import de.waldheinz.schmigalla.SchmigallaSolver;
import de.waldheinz.schmigalla.SolverListener;
import java.io.FileInputStream;

/**
 *
 * @author  Matthias Treydte <waldheinz@gmail.com>
 */
public final class GUIMain extends javax.swing.JFrame
        implements SolverListener {
        
    private final static long serialVersionUID = 1;

    private SchmigallaSolver solver;
    private Thread solverThread;
    private long lastStatsUpdate = 0;
    
    
    /** Creates new form GUIMain */
    public GUIMain() {
        initComponents();
        matrixTable.setDefaultRenderer(Object.class, matrixCellRenderer);
        matrixScrollPane.setRowHeader(
              new TableRowHeader(matrixTable, matrixScrollPane));
        this.getRootPane().setDefaultButton(this.startButton);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        matrixTableModel = new de.waldheinz.schmigalla.gui.MatrixTableModel();
        matrixCellRenderer = new de.waldheinz.schmigalla.gui.MatrixCellRenderer();
        solutionListModel = new de.waldheinz.schmigalla.gui.SolutionListModel();
        tabPane = new javax.swing.JTabbedPane();
        matrixPanel = new javax.swing.JPanel();
        matrixScrollPane = new javax.swing.JScrollPane();
        matrixTable = new javax.swing.JTable();
        addButton = new javax.swing.JButton();
        startButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        loadButton = new javax.swing.JButton();
        solutionsPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        solutionList = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        boardViewer = new de.waldheinz.schmigalla.gui.BoardViewer();
        progressPanel = new javax.swing.JPanel();
        cacheRatioBar = new javax.swing.JProgressBar();
        jScrollPane3 = new javax.swing.JScrollPane();
        progressBoardViewer = new de.waldheinz.schmigalla.gui.BoardViewer();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cacheUsageLabel = new javax.swing.JLabel();

        matrixCellRenderer.setText("matrixCellRenderer1");

        solutionListModel.addListDataListener(new javax.swing.event.ListDataListener() {
            public void contentsChanged(javax.swing.event.ListDataEvent evt) {
            }
            public void intervalAdded(javax.swing.event.ListDataEvent evt) {
                solutionListModelIntervalAdded(evt);
            }
            public void intervalRemoved(javax.swing.event.ListDataEvent evt) {
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Schmigalla");
        setLocationByPlatform(true);

        matrixPanel.setOpaque(false);

        matrixScrollPane.setBorder(null);

        matrixTable.setModel(matrixTableModel);
        matrixTable.setOpaque(false);
        matrixScrollPane.setViewportView(matrixTable);

        addButton.setText("+");
        addButton.setToolTipText("neue Spalte");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        startButton.setText("los!");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        saveButton.setText("speichern");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        loadButton.setText("laden");
        loadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout matrixPanelLayout = new org.jdesktop.layout.GroupLayout(matrixPanel);
        matrixPanel.setLayout(matrixPanelLayout);
        matrixPanelLayout.setHorizontalGroup(
            matrixPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(matrixPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(matrixPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(matrixScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
                    .add(matrixPanelLayout.createSequentialGroup()
                        .add(addButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 343, Short.MAX_VALUE)
                        .add(loadButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(saveButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(startButton)))
                .addContainerGap())
        );

        matrixPanelLayout.linkSize(new java.awt.Component[] {loadButton, saveButton, startButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        matrixPanelLayout.setVerticalGroup(
            matrixPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, matrixPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(matrixScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(matrixPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(addButton)
                    .add(startButton)
                    .add(saveButton)
                    .add(loadButton))
                .addContainerGap())
        );

        tabPane.addTab("Intensitätsmatrix", matrixPanel);

        solutionsPanel.setOpaque(false);

        solutionList.setModel(solutionListModel);
        solutionList.setCellRenderer(new SolutionListCellRenderer());
        solutionList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                solutionListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(solutionList);

        jScrollPane2.setBackground(new java.awt.Color(255, 255, 255));

        boardViewer.setBackground(new java.awt.Color(255, 255, 255));

        org.jdesktop.layout.GroupLayout boardViewerLayout = new org.jdesktop.layout.GroupLayout(boardViewer);
        boardViewer.setLayout(boardViewerLayout);
        boardViewerLayout.setHorizontalGroup(
            boardViewerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 127, Short.MAX_VALUE)
        );
        boardViewerLayout.setVerticalGroup(
            boardViewerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 100, Short.MAX_VALUE)
        );

        jScrollPane2.setViewportView(boardViewer);

        org.jdesktop.layout.GroupLayout solutionsPanelLayout = new org.jdesktop.layout.GroupLayout(solutionsPanel);
        solutionsPanel.setLayout(solutionsPanelLayout);
        solutionsPanelLayout.setHorizontalGroup(
            solutionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(solutionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                .addContainerGap())
        );
        solutionsPanelLayout.setVerticalGroup(
            solutionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, solutionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(solutionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE))
                .addContainerGap())
        );

        tabPane.addTab("Lösungen", solutionsPanel);

        progressBoardViewer.setBackground(new java.awt.Color(255, 255, 255));

        org.jdesktop.layout.GroupLayout progressBoardViewerLayout = new org.jdesktop.layout.GroupLayout(progressBoardViewer);
        progressBoardViewer.setLayout(progressBoardViewerLayout);
        progressBoardViewerLayout.setHorizontalGroup(
            progressBoardViewerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 127, Short.MAX_VALUE)
        );
        progressBoardViewerLayout.setVerticalGroup(
            progressBoardViewerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 100, Short.MAX_VALUE)
        );

        jScrollPane3.setViewportView(progressBoardViewer);

        jLabel1.setText("Cache Treffer:");

        jLabel2.setText("Cache Ausnutzung:");

        cacheUsageLabel.setText("<läuft nicht>");

        org.jdesktop.layout.GroupLayout progressPanelLayout = new org.jdesktop.layout.GroupLayout(progressPanel);
        progressPanel.setLayout(progressPanelLayout);
        progressPanelLayout.setHorizontalGroup(
            progressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, progressPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(progressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, progressPanelLayout.createSequentialGroup()
                        .add(progressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel2)
                            .add(jLabel1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(progressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(cacheUsageLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(cacheRatioBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE))))
                .addContainerGap())
        );

        progressPanelLayout.linkSize(new java.awt.Component[] {jLabel1, jLabel2}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        progressPanelLayout.setVerticalGroup(
            progressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, progressPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(progressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(cacheUsageLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(progressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel1)
                    .add(cacheRatioBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        tabPane.addTab("Fortschritt", progressPanel);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tabPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tabPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void loadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadButtonActionPerformed
        JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
        int rc = fc.showDialog(null, "Select Data File");
        if (rc == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
                CSVReaderAdapter cra = new CSVReaderAdapter(
                        new FileInputStream(file));
                this.matrixTableModel.setColumnNames(cra.getNames());
                this.matrixTableModel.setValues(cra.getValues());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
        }
    }//GEN-LAST:event_loadButtonActionPerformed

    private void solutionListModelIntervalAdded(javax.swing.event.ListDataEvent evt) {//GEN-FIRST:event_solutionListModelIntervalAdded
       solutionList.setSelectedIndex(evt.getIndex0());
       solutionList.ensureIndexIsVisible(evt.getIndex0());
       solutionsPanel.revalidate();
    }//GEN-LAST:event_solutionListModelIntervalAdded

    private void solutionListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_solutionListValueChanged
        boardViewer.setBoard((SchmigallaSolver.Board)solutionList.getSelectedValue());
    }//GEN-LAST:event_solutionListValueChanged

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        
        if (solverThread != null) {
            solver.stop();
            try {
                solverThread.join();
            } catch (InterruptedException ex) {
                /* nothing */
            }
            
        }
        
        solver = new SchmigallaSolver(matrixTableModel.getValues());
        solver.addSolverListener(solutionListModel);
        solver.addSolverListener(this);
        
        solutionListModel.clear();
        tabPane.setSelectedComponent(solutionsPanel);
        
        solverThread = new Thread(solver);
        solverThread.start();
    }//GEN-LAST:event_startButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        matrixTableModel.createNewColumn();
    }//GEN-LAST:event_addButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        
    }//GEN-LAST:event_saveButtonActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GUIMain().setVisible(true);
            }
        });
    }

    public void solutionFound(SchmigallaSolver solver,
          SchmigallaSolver.Board solution) {
        
        /* ignore */
    }

    public void progressMade(SchmigallaSolver solver, final float cacheR,
          final int cacheUsed, final SchmigallaSolver.Board board) {
        
        if ((System.currentTimeMillis() - GUIMain.this.lastStatsUpdate) > 
              1000) {
            
            GUIMain.this.lastStatsUpdate = System.currentTimeMillis();
            
            final SchmigallaSolver.Board copy =
                  (SchmigallaSolver.Board)board.clone();

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {        
                    if (tabPane.getSelectedComponent() == progressPanel) {
                        progressBoardViewer.setBoard(copy);
                        cacheRatioBar.setValue((int)(cacheR * 100));
                        cacheUsageLabel.setText(Integer.toString(cacheUsed));
                    }
                }
            });
        }
    }

    public void solverStopped() {
        /* ignore */
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private de.waldheinz.schmigalla.gui.BoardViewer boardViewer;
    private javax.swing.JProgressBar cacheRatioBar;
    private javax.swing.JLabel cacheUsageLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JButton loadButton;
    private de.waldheinz.schmigalla.gui.MatrixCellRenderer matrixCellRenderer;
    private javax.swing.JPanel matrixPanel;
    private javax.swing.JScrollPane matrixScrollPane;
    private javax.swing.JTable matrixTable;
    private de.waldheinz.schmigalla.gui.MatrixTableModel matrixTableModel;
    private de.waldheinz.schmigalla.gui.BoardViewer progressBoardViewer;
    private javax.swing.JPanel progressPanel;
    private javax.swing.JButton saveButton;
    private javax.swing.JList solutionList;
    private de.waldheinz.schmigalla.gui.SolutionListModel solutionListModel;
    private javax.swing.JPanel solutionsPanel;
    private javax.swing.JButton startButton;
    private javax.swing.JTabbedPane tabPane;
    // End of variables declaration//GEN-END:variables
    
}
