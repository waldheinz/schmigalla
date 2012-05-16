/*
 * LRUCache.java
 *
 * Created on 28. September 2007, 13:55
 */

package de.waldheinz.schmigalla;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Matthias Treydte <waldheinz@gmail.com>
 */
public class LRUCache extends LinkedHashMap<SchmigallaSolver.Board, Float> {
    
    protected int maxCapacity;
    
    /** Creates a new instance of LRUCache */
    public LRUCache() {
        maxCapacity = 5000;
    }
    
    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
    
    public int getMaxCapacity() {
        return maxCapacity;
    }
    
    @Override
    protected boolean removeEldestEntry(
          Map.Entry<SchmigallaSolver.Board, Float> eldest) {
        
        return size() > maxCapacity;
    }
    
}
