/*
 * SolutionListCellRenderer.java
 *
 * Created on 27. September 2007, 20:50
 */

package de.waldheinz.schmigalla.gui;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import de.waldheinz.schmigalla.SchmigallaSolver;

/**
 *
 * @author Matthias Treydte <waldheinz@gmail.com>
 */
public class SolutionListCellRenderer extends JLabel implements
      ListCellRenderer {
    
    /** Creates a new instance of SolutionListCellRenderer */
    public SolutionListCellRenderer() {
        this.setOpaque(true);
    }

    public Component getListCellRendererComponent(JList list, Object value,
          int index, boolean selected, boolean focus) {
        
        if (!(value instanceof SchmigallaSolver.Board)) {
            setText(value.toString());
            return this;
        }
        
        SchmigallaSolver.Board board = (SchmigallaSolver.Board)value;
        
        setText("Lösung #" + (index+1) + " (Kosten: " + board.getCost() + ")");
        
        if (selected) setBackground(list.getSelectionBackground());
        else setBackground(list.getBackground());
        
        return this;
    }
    
}
