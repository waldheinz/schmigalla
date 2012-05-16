/*
 * CSVReaderAdapter.java
 *
 * Created on 30. September 2007, 21:41
 */

package de.waldheinz.schmigalla;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.jumpmind.symmetric.csv.CsvReader;

/**
 *
 * @author Matthias Treydte <waldheinz@gmail.com>
 */
public final class CSVReaderAdapter {
    
    protected final CsvReader reader;
    protected float[][] values;
    protected String[] names;
    protected boolean isRead;
    
    /**
     * Creates a new instance of CSVReaderAdapter that reads from the specified
     * stream.
     * 
     * @param is the stream to read the CSVs from
     */
    public CSVReaderAdapter(InputStream is) {
        this.reader = 
              new CsvReader(
              new InputStreamReader(is));
        
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
                    throw new IOException(
                            "\"" + vals[col] + "\" is not a number");
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
                    throw new IOException(
                            "\"" + vals[col] + "\" is not a number");
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
    
}
