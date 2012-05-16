/*
 * MatrixTableModel.java
 *
 * Created on 27. September 2007, 11:26
 */

package de.waldheinz.schmigalla.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Matthias Treydte <waldheinz@gmail.com>
 */
public final class MatrixTableModel extends AbstractTableModel {
    private final static long serialVersionUID = 1;

    /** The values of the intensity matrix. */
    protected ArrayList<ArrayList<Float>> matrix;
    
    /** The names of the machines. */
    protected ArrayList<String> names;
    
    protected float maximumValue = 0.0f;
    
    /**
     * A dummy object to represent the implicitely empty cells
     * of the main diagonal.
     */
    public final static Object EMPTY_CELL = new Object() {
        @Override public String toString() { return "X"; }
    };
    
    /** Creates a new instance of MatrixTableModel */
    public MatrixTableModel() {
        matrix = new ArrayList<ArrayList<Float>>();
        matrix.add(new ArrayList<Float>());
        matrix.get(0).add(0.0f);
        
        names = new ArrayList<String>();
        names.add("Anlage 1");
        names.add("Anlage 2");
        
        createNewColumn();
        createNewColumn();
        
    }

    @Override
    public int getRowCount() {
        return matrix.size() + 1;
    }

    @Override
    public int getColumnCount() {
        return matrix.get(0).size() + 1;
    }
    
    /**
     * Returns the maximum value in the table.
     * 
     * @return the maximum value
     */
    public float getMaximumValue() {
        return this.maximumValue;
    }
    
    protected void updateMaximumValue() {
        float max = 0.0f;
        
        for (ArrayList<Float> row : matrix) {
            for (Float value : row) if (value > 0) {
                max = Math.max(max, value);
            }
        }
        
        if (max != getMaximumValue()) {
            this.maximumValue = max;
            /* to make the JTable redraw the whole thing */
            fireTableDataChanged();
        }
    }
    
    @Override
    public Object getValueAt(int row, int col) {
        if (row >= getRowCount()) throw new NoSuchElementException(
              "row index " + row + " out of bounds");
        if (col >= getColumnCount()) throw new NoSuchElementException(
              "column index " + col + " out of bounds");
        
        if (row == col) return EMPTY_CELL;
        if (row > col) return 0.0f;
        
        return matrix.get(row).get(col - (row + 1));
        
    }
    
    public void createNewColumn() {
        names.add("Anlage " + (getColumnCount() + 1));
        
        for (ArrayList<Float> row : matrix) {
            row.add(0.0f);
        }
        
        ArrayList<Float> lastRow = new ArrayList<Float>();
        matrix.add(lastRow);
        
        lastRow.add(0.0f);
        
        fireTableStructureChanged();
        updateMaximumValue();
    }
    
    @Override
    public boolean isCellEditable(int row, int col) {
        return (row < col);
    }
    
    @Override
    public String getColumnName(int col) {
        if (col >= names.size()) return "<kein Name>";
        return names.get(col);
    }
    
    public void setColumnNames(String[] names) {
        this.names = new ArrayList<String>(Arrays.asList(names));
    }

    public String[] getColumnNames() {
        return this.names.toArray(new String[this.names.size()]);
    }
    
    @Override
    public void setValueAt(Object val, int row, int col) {
        if (!isCellEditable(row, col)) throw new IllegalArgumentException(
              "the cell is not editable"); //NOI18N
        
        float newVal;
        
        try {
            newVal = Float.parseFloat(val.toString());
        } catch (NumberFormatException e) { return; }
        
        newVal = Math.max(0.0f, newVal);
        matrix.get(row).set(col - (row + 1), newVal);
        
        updateMaximumValue();
        fireTableCellUpdated(row, col - (row + 1));
    }
    
    public void setValues(float[][] values) {
        ArrayList<ArrayList<Float>> m = new ArrayList<ArrayList<Float>>();
        
        for (int row=0; row < values.length; row++) {
            ArrayList<Float> rowVals = new ArrayList<Float>();
            
            for (float val : values[row])
                rowVals.add(val);
            
            m.add(rowVals);
        }
        
        this.matrix = m;
        this.fireTableStructureChanged();
        this.updateMaximumValue();
    }
    
    public float[][] getValues() {
        float[][] rows = new float[getRowCount()-1][];
        int rowNum = 0;
        
        for (ArrayList<Float> arow : matrix) {
            float[] row = new float[arow.size()];
            rows[rowNum++] = row;
            int colNum = 0;
            
            for (Float val : arow)
                row[colNum++] = val.floatValue();
        }
        
        return rows;
    }
    
}
