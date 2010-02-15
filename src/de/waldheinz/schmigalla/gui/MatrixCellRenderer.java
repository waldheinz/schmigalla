/*
 * MatrixCellRenderer.java
 *
 * Created on 27. September 2007, 13:58
 */

package de.waldheinz.schmigalla.gui;

import java.awt.*;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Matthias Treydte <waldheinz@gmail.com>
 */
public class MatrixCellRenderer extends JLabel implements TableCellRenderer {
    
    protected Font fixedCellFont;
    protected Font nonZeroFont;
    protected Border focusBorder;
    protected float maximum;
    protected float value;
    
    /** Creates a new instance of MatrixCellRenderer */
    public MatrixCellRenderer() {
        Font def = (Font)UIManager.get("Table.font");
        gradientColor = (Color)UIManager.get("Button.focus");
        
        fixedCellFont = new Font(def.getName(), 
              Font.ITALIC, def.getSize());
        
        nonZeroFont = new Font(def.getName(),
              Font.BOLD, def.getSize());
        
        focusBorder = javax.swing.BorderFactory.
              createLineBorder(java.awt.Color.BLACK, 1);
        
        maximum = value = 0.0f;
        
        setOpaque(false);
    }
    
    public void setFixedCellFont(Font font) {
        fixedCellFont = font;
    }
    
    public Font getFixedCellFont() {
        return fixedCellFont;
    }
    
    public Component getTableCellRendererComponent(JTable table, Object val, 
          boolean selected, boolean focus, int row, int col) {
        
        maximum = (table.getModel() instanceof MatrixTableModel)?
            ((MatrixTableModel)table.getModel()).getMaximumValue() :
            0.0f;
        final boolean editable = table.getModel().isCellEditable(row, col);
        value = (val == MatrixTableModel.EMPTY_CELL) ? 0.0f : 
            ((Float)val).floatValue();
        
        setText(val.toString());
        
        if (editable) {
            setFont(value > 0 ? nonZeroFont : table.getFont());
        } else {
            setFont(fixedCellFont);
        }
        
        if (selected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
        }
        
        setBorder((focus && editable)?focusBorder:null);
        
        return this;
    }

    public static Color blend(Color c1, Color c2, float r) {
        final float nr = 1.0f - r;
        return new Color (
              (int)(c1.getRed() * r + c2.getRed() * nr),
              (int)(c1.getGreen() * r + c2.getGreen() * nr),
              (int)(c1.getBlue() * r + c2.getBlue() * nr));
    }
    
    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2d = (Graphics2D)graphics.create();
        
        if (value > 0.0f) {
            final int barWidth = 
                  (int)(value / maximum * getWidth()) - getGradientWidth();
                  
            int x = Math.max(barWidth, 0);
            float r = value / maximum;
            Color c = blend(getGradientColor(), getBackground(), r);
            GradientPaint gradient = new GradientPaint(x, 0, c,
                  x + this.getGradientWidth(), 0, getBackground());
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
        } else {
            g2d.setPaint(getBackground());
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        
        g2d.dispose();
        super.paintComponent(graphics);
    }

    /**
     * Holds value of property gradientWidth.
     */
    private int gradientWidth = 24;

    /**
     * Getter for property gradientWidth.
     * @return Value of property gradientWidth.
     */
    public int getGradientWidth() {
        return this.gradientWidth;
    }

    /**
     * Setter for property gradientWidth.
     * @param gradientWidth New value of property gradientWidth.
     */
    public void setGradientWidth(int gradientWidth) {
        this.gradientWidth = gradientWidth;
    }

    /**
     * Holds value of property gradientColor.
     */
    private Color gradientColor = SystemColor.textHighlight;

    /**
     * Getter for property gradientColor.
     * @return Value of property gradientColor.
     */
    public Color getGradientColor() {
        return this.gradientColor;
    }

    /**
     * Setter for property gradientColor.
     * @param gradientColor New value of property gradientColor.
     */
    public void setGradientColor(Color gradientColor) {
        this.gradientColor = gradientColor;
    }
    
}
