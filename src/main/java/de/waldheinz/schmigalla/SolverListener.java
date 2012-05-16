/*
 * SolverListener.java
 *
 * Created on 27. September 2007, 19:49
 */

package de.waldheinz.schmigalla;

/**
 *
 * @author Matthias Treydte <waldheinz@gmail.com>
 */
public interface SolverListener {
    
    public void solutionFound(SchmigallaSolver solver, 
          SchmigallaSolver.Board solution);
    
    /**
     * @param board This is the board currently examined
     *      by the solver. If a reference to this board 
     *      is needed by the listener, it <em>must</em> be 
     *      copied using the <code>clone()</code> method.
     */
    public void progressMade(SchmigallaSolver solver,
          float chacheHitRatio, int cacheSize, SchmigallaSolver.Board board);
    
    /**
     * This is the last event a solver emits.
     */
    public void solverStopped();
    
}
