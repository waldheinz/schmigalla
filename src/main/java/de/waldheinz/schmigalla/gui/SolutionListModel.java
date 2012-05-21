/*
 * SolutionsListModel.java
 *
 * Created on 27. September 2007, 19:54
 */

package de.waldheinz.schmigalla.gui;

import de.waldheinz.schmigalla.SchmigallaSolver;
import de.waldheinz.schmigalla.SolverListener;
import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Matthias Treydte <waldheinz@gmail.com>
 */
public class SolutionListModel extends DefaultListModel implements
      SolverListener {
    
    @Override
    public void solutionFound(SchmigallaSolver solver,
          final SchmigallaSolver.Board solution) {
        
        final SchmigallaSolver.Board copy =
                (SchmigallaSolver.Board) solution.clone();
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println(size());
//                if (!contains(copy))
                    addElement(copy);
            }
        });
    }
    
    @Override
    public void progressMade(SchmigallaSolver solver, float progress, int cachesize,
          SchmigallaSolver.Board board) {
        
        /* ignore */
    }

    @Override
    public void solverStopped() {
        /* ignore */
    }
    
}
