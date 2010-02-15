/*
 * CSVReaderAdapter.java
 *
 * Created on 30. September 2007, 21:41
 */

package de.waldheinz.schmigalla;

import com.csvreader.CsvReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author Matthias Treydte <waldheinz@gmail.com>
 */
public class CSVReaderAdapter {
    
    protected final CsvReader reader;
    protected float[][] values;
    protected String[] names;
    protected boolean isRead;
    
    /** Creates a new instance of CSVReaderAdapter */
    public CSVReaderAdapter(File file) throws FileNotFoundException {
        this.reader = 
              new CsvReader(
              new InputStreamReader(
              new FileInputStream(file)));
        
        this.isRead = false;
    }
    
    public void read() throws IOException {
        if (isRead) return;
        isRead = true;
        if (!reader.readHeaders()) {
            return;
        }
        
        names = reader.getHeaders();
        
        values = new float[names.length - 1][];
        
        int row = 0;
        while (reader.readRecord() && (row < names.length - 1)) {
            values[row] = new float[names.length - row - 1];
            String[] vals = reader.getValues();
            int col = 0;
            
            while (col < row) {
                try {
                    float val = Float.parseFloat(vals[col]);
                    values[col][row - col - 1] += val;
                } catch (NumberFormatException e) {
                    throw new FileFormatException("\"" + vals[col] + "\" is not a number");
                }
                
                col++;
            }
            
            /* the main diagonal, ignore whats there */
            col++;
            
            while (col < names.length) {
                try {
                    float val = Float.parseFloat(vals[col]);
                    values[row][col - row - 1] = val;
                } catch (NumberFormatException e) {
                    throw new FileFormatException("\"" + vals[col] + "\" is not a number");
                }
                
                col++;
            }
            
            row++;
        }
        
        /* insufficient lines read?! fill with zeros */
        while (row < names.length - 1) values[row++] = new float[1];
        
        reader.close();
    }
    
    public String[] getNames() throws IOException {
        read();
        return names;
    }
    
    public float[][] getValues() throws IOException {
        read();
        return values;
    }
    
    public static class FileFormatException extends IOException {
        public FileFormatException(String message) {
            super(message);
        }
    }
}
