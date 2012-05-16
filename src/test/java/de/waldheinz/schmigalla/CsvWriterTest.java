
package de.waldheinz.schmigalla;

import java.io.*;
import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;

/**
 *
 * @author Matthias Treydte &lt;waldheinz at gmail.com&gt;
 */
public class CsvWriterTest {
    
    @Test
    public void testSomeMethod() throws IOException {

        final String[] names = new String[] {"", "test1", "test2", "test3" };
        final float[][] values = new float[][] {
            { 0, 1, 2, 3 },
            { 0, 0, 4, 5 },
            { 0, 0, 0, 6 }
        };
        
        CsvWriter w = new CsvWriter(names, values);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        w.write(baos);
        
        final ByteArrayInputStream bais = new ByteArrayInputStream(
                baos.toByteArray());

        final BufferedReader isr = new BufferedReader(
                new InputStreamReader(bais));
        
        while (isr.ready()) {
            System.out.println(isr.readLine());
        }
        
        bais.reset();
        CSVReaderAdapter r = new CSVReaderAdapter(bais);
//        
//        assertArrayEquals(
//                Arrays.copyOf(values, values.length ), r.getValues());
        assertArrayEquals(names, r.getNames());
    }
    
}
