
package de.waldheinz.schmigalla;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 *
 * @author Matthias Treydte &lt;waldheinz at gmail.com&gt;
 */
public final class CsvWriter {
    
    private final String[] names;
    private final float[][] values;
    
    /**
     * 
     *
     * @param names
     * @param values
     */
    public CsvWriter(String[] names, float[][] values) {
        this.names = names;
        this.values = values;
    }

    public void write(OutputStream os) {
        final PrintStream ps = new PrintStream(new BufferedOutputStream(os));

        /* write the header */
        
        for (int i=0; i < names.length; i++) {
            ps.print("\"" + names[i] + "\""); //NOI18N
            
            if (i < names.length - 1) ps.print(", ");
        }

        ps.println();

        /* write the data */
        
        for (int i=0; i < values.length; i++) {

            for (int j=0; j <= i; j++) {
                ps.print("0.0, "); //NOI18N
            }

            for (int j=0; j < values[i].length; j++) {
                ps.print(values[i][j]);
                if (j < values[i].length - 1) ps.print(", ");
            }

            ps.println();
        }
        
        ps.println();
        
        ps.flush();
    }
}
