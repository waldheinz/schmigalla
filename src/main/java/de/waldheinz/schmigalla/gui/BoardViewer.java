/*
 * SolutionViewer.java
 *
 * Created on 27. September 2007, 16:02
 */

package de.waldheinz.schmigalla.gui;

import de.waldheinz.schmigalla.SchmigallaSolver;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.GlyphVector;
import javax.swing.JPanel;
import javax.swing.Scrollable;

/**
 *
 * @author Matthias Treydte <waldheinz@gmail.com>
 */
public class BoardViewer extends JPanel implements Scrollable {
    
    protected SchmigallaSolver.Board board;
    
    protected int minX = Integer.MAX_VALUE;
    protected int maxX = Integer.MIN_VALUE;
    protected int minY = Integer.MAX_VALUE;
    protected int maxY = Integer.MIN_VALUE;
    
    /** Creates a new instance of BoardViewer */
    public BoardViewer() {
        
    }
    
    public void setBoard(SchmigallaSolver.Board board) {
        this.board = board;
        
        if (board != null) {
            
            minX = Integer.MAX_VALUE;
            maxX = Integer.MIN_VALUE;
            minY = Integer.MAX_VALUE;
            maxY = Integer.MIN_VALUE;
            
            for (SchmigallaSolver.Piece p : board.pieces) {
                minX = Math.min(minX, p.getPos().x);
                maxX = Math.max(maxX, p.getPos().x);
                minY = Math.min(minY, p.getPos().y);
                maxY = Math.max(maxY, p.getPos().y);
            }
        }
        
        revalidate();
        repaint();
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(
            (int)(((maxX - minX + 0.5f) * zoom * nodeSpacing) +
                (nodeDiameter * 1 * zoom) + 1),
                  
            (int)(((maxY - minY) * zoom * nodeSpacing) +
                (nodeDiameter * zoom)) + 1);
    }
    
    public SchmigallaSolver.Board getBoard() {
        return board;
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle rectangle, int i, int i0) {
        return 10;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle rectangle, int i, int i0) {
        return 20;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    /**
     * Holds value of property zoom.
     */
    private float zoom = 60;

    /**
     * Getter for property zoom.
     * @return Value of property zoom.
     */
    public float getZoom() {
        return this.zoom;
    }
    
    /**
     * Setter for property zoom.
     * @param zoom New value of property zoom.
     */
    public void setZoom(float zoom) {
        this.zoom = zoom;
    }
    
    protected int nodePosX(SchmigallaSolver.Coord p) {
        return (int)(((p.x - minX) * zoom * nodeSpacing) +
              ((p.y % 2) == 0 ? nodeSpacing * 0.5f * zoom : 0.0f));
    }
    
    protected int nodePosY(SchmigallaSolver.Coord p) {
        return (int)((p.y - minY) * zoom * nodeSpacing);
    }
    
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        
        Graphics2D g2d = (Graphics2D)graphics.create();
        
        if (board == null) {
            g2d.drawString("Keine Lösung gewählt.", 50, 50);
            g2d.dispose();
            return;
        }
        
        final int offset = (int)(nodeDiameter * 0.5f * zoom);
        
        for (SchmigallaSolver.Piece p : board.pieces) {
            final int px = nodePosX(p.getPos());
            final int py = nodePosY(p.getPos());
            
            for (SchmigallaSolver.Coord c : p.getPos().getNeighbours()) {
                if (!board.isPieceAt(c)) continue;
                
                final int dx = nodePosX(c);
                final int dy = nodePosY(c);
                
                g2d.drawLine(
                      px + offset, py + offset,
                      dx + offset, dy + offset);
            }
            
            
        }
        
        for (SchmigallaSolver.Piece p : board.pieces) {
            final int px = nodePosX(p.getPos());
            final int py = nodePosY(p.getPos());
            
            g2d.setPaint(getBackground());
            
            g2d.fillOval(px, py, 
                  (int)(nodeDiameter * zoom),
                  (int)(nodeDiameter * zoom));
            
            g2d.setPaint(getForeground());
            
            g2d.drawOval(px, py, 
                  (int)(nodeDiameter * zoom),
                  (int)(nodeDiameter * zoom));
            
            final String name = ("" + (p.nr + 1) );//+ " (" + p.getPos().x + ", " + p.getPos().y + ")");
            
            GlyphVector gv = getFont().layoutGlyphVector(g2d.getFontRenderContext(), name.toCharArray(), 0, name.length(), 0);
            g2d.drawGlyphVector(gv, 
                  px + offset - (float)gv.getOutline().getBounds2D().getWidth() * 0.5f,
                  py + offset + (float)gv.getOutline().getBounds2D().getHeight() * 0.5f);
            
        }
        
        g2d.dispose();
    }
    
    /**
     * Holds value of property nodeDiameter.
     */
    private float nodeDiameter = 0.75f;
    
    /**
     * Getter for property nodeDiameter.
     * @return Value of property nodeDiameter.
     */
    public float getNodeDiameter() {
        return this.nodeDiameter;
    }

    /**
     * Setter for property nodeDiameter.
     * @param nodeDiameter New value of property nodeDiameter.
     */
    public void setNodeDiameter(float nodeDiameter) {
        this.nodeDiameter = nodeDiameter;
    }

    /**
     * Holds value of property nodeSpacing.
     */
    private float nodeSpacing = 0.9f;

    /**
     * Getter for property nodeSpacing.
     * @return Value of property nodeSpacing.
     */
    public float getNodeSpacing() {
        return this.nodeSpacing;
    }

    /**
     * Setter for property nodeSpacing.
     * @param nodeSpacing New value of property nodeSpacing.
     */
    public void setNodeSpacing(float nodeSpacing) {
        this.nodeSpacing = nodeSpacing;
    }
    
    public Dimension getS5ize() {
        Dimension min = super.getSize();
        Dimension pref = getPreferredSize();
        return new Dimension(
              Math.max(min.width, pref.width),
              Math.max(min.height, pref.height));
    }
    
}
