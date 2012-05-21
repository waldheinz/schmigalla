/*
 * Main.java
 *
 * Created on 25. Juni 2007, 21:05
 */

package de.waldheinz.schmigalla;

import java.util.*;

/**
 *
 * @author Matthias Treydte <waldheinz@gmail.com>
 */
public final class SchmigallaSolver implements Runnable {
    
    private ArrayList<SolverListener> listeners;
    
    private boolean shouldStop;
    private LRUCache cache;
    private long cacheLookups;
    private long cacheLookupsGood;
    
    /* The intensity matrix (which is the input
     * for the algorithm) 
     */
    private final float [][] intensities;
//    { {5, 3, 1, 4, 0, 10}, {6, 7, 8, 6}, {5, 4, 3}, {10, 3, 7}, {8, 2}, {4} };
//    {{64, 32,16,8,4,2,1}};
            
//    {{1}, {1}, {1}, {1}, {1}, {1}, {1}, {1}, {1}, {1}, {1}, {1}, {1}, {1},
//     {1}, {1}, {1}, {1}, {1}, {1}, {1}, {1}, {1}, {1}, {1}, {1}, {1}, {1},
//     {1}, {1}, {1}, {1}, {1}, {1}, {1}, {1}, {1}, {1}, {1}, {1}, {1}, {1}};
    
//    {{10, 10, 10, 10, 10, 10}, {1}, {1}, {1}, {1}, {1}, {1}, {1}, {1}, {1}};
            
    /* RFP - Matrix */
//    {{34, 24, 0, 0, 42},
//     {34},
//     {10, 10, 14, 0, 0, 24},
//     {10},
//     {0, 0, 20},
//     {60, 0, 0, 0, 14},
//     {42},
//     {20, 0, 42},
//     {10, 34},
//     {10}
//    };
    
//    {{240, 204, 0, 0, 570, 0, 0, 120},
//     {240},
//     {60, 60, 144, 0, 0, 180},
//     {60},
//     {0, 0, 120},
//     {570, 0, 0, 0, 144},
//     {570},
//     {0, 0, 690},
//     {60, 240},
//     {60}
//    };
    
    /* HILTI - Matrix */
    
//    {{1503, 47145, 23464, 2601, 914, 2630, 1625, 1738, 731, 892, 9468, 1462, 149, 718, 5351, 613, 1878},
//     {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 774},
//     {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 119, 0, 0, 0, 0, 834, 6951},
//     {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 67, 0, 0, 0, 0, 467, 3889},
//     {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 383, 1666},
//     {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 255, 595},
//     {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 505, 1467},
//     {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 442, 4424},
//     {0, 885, 0, 0, 0, 0, 105, 289, 136, 130},
//     {0, 0, 0, 0, 0, 90, 244, 134, 137},
//     {},
//     {0, 0, 0, 0, 0, 0, 0, 656, 4375},
//     {0, 185, 0, 0, 0, 0, 1728, 11521},
//     {0, 0, 0, 0, 0, 292, 2431},
//     {},
//     {0, 0, 0, 776},
//     {0, 0, 2676},
//     {0, 967},
//     {1143}
//    };
    
    public final static Coord START_POS = new Coord(0, 0);
    
    private Board b = new Board();
    private float bestCost = Float.POSITIVE_INFINITY;
    private final float lB;
    private final List<Piece> todo;
    
    /**
     * @param intensities The intensity matrix. This matrix is the input for
     * the solver and is to be given in the following form:
     *
     * The Array entry #n is an array which lists intensities from item n to
     * items [n+1 .. {number of elements this in sub-array}]. If there are
     * m facilities to be placed, <code>intensities.length == (m-1)</code>
     * will be true. 
     *
     * This implies that only the upper half of the matrix is filled, the other
     * half is implicitly zero.
     */
    public SchmigallaSolver(float[][] intensities) {
        /* first check input matrix */
        for (int row=0; row < intensities.length; row++) {
            if (intensities[row] == null) throw new
                  NullPointerException("matrix row #" + row + " is null");
        }
        
        this.intensities = intensities;
        this.listeners = new ArrayList<SolverListener>();
        this.cache = new LRUCache();
        this.shouldStop = false;
        this.todo = createTodo();
        this.lB = lowerBound(todo);
    }
    
    public void addSolverListener(SolverListener listener) {
        listeners.add(listener);
    }
    
    public void removeSolverListener(SolverListener listener) {
        listeners.remove(listener);
    }
    
    protected void fireSolutionFound(Board solution) {
        for (SolverListener l : listeners)
            l.solutionFound(this, solution);
    }
    
    protected void fireProgressMade(Board current, float progress) {
        for (SolverListener l : listeners)
            l.progressMade(this, progress, cache.size(), current);
    }
    
    public List<Piece> createTodo() {
        /* generate the todo-stack */
        final List<Piece> result = new LinkedList<Piece>();
        
        for (int nr=0; nr < intensities.length; nr++) {
            float[] intensity = intensities[nr];
            int idx = result.indexOf(new Piece(nr));
            Piece p;
            if (idx < 0) {
                /* create the current piece */
                p = new Piece(nr);
                result.add(p);
            } else
                p = result.get(idx);
            
            /* create or update the neighbours of this piece */
            for (int i=0; i < intensity.length; i++) {
                if (intensity[i] == 0) continue;
                
                int n = nr + i + 1;
                int nidx = result.indexOf(new Piece(n));
                Piece neighb;
                
                if (nidx < 0) {
                    /* ok, create it */
                    neighb = new Piece(n);
                    result.add(neighb);
                } else
                    neighb = result.get(nidx);
                
                p.addNeighbour(neighb);
                neighb.addNeighbour(p);
            }
        }
        
        return result;
    }
    
    public float intensity(final Piece p1, final Piece p2) {
        int min = Math.min(p1.nr, p2.nr);
        int max = Math.max(p1.nr, p2.nr);
        if (intensities.length <= min)
            return 0.0f;
        else if (intensities[min].length <= max-min-1)
            return 0.0f;
        else
            return intensities[min][max-min-1];
    }
    
    public void stop() {
        shouldStop = true;
    }
    
    /**
     * Recursively enumerates and evaluates all solutions.
     *
     * @param b The board to place pieces on.
     * @param todo A list of pieces which are to be placed.
     * @param costSoFar The cost which was caused by placing
     *      the previous pieces.
     * @return The lowest cost possible for the given board and todo list.
     */
    public float solve(final Board b, final List<Piece> todo, float costSoFar) {
        /* prune the tree */
        if ((costSoFar + lowerBound(todo)) > bestCost ||
                bestCost == lB || shouldStop) {
            fireProgressMade(b, (float)cacheLookupsGood / cacheLookups);
            return Float.POSITIVE_INFINITY;
        }
        
        /* we found a solution */
        if (todo.isEmpty()) {
            //System.out.println("New best cost: " + costSoFar);
            
            if (costSoFar < bestCost) {
                final Board copy = (Board)b.clone();
                copy.setCost(costSoFar);
                fireSolutionFound(copy);
                bestCost = costSoFar;
            }
            
            return costSoFar;
        }
        
        /* check the cache */
        if (todo.size() >= getCacheThreshold()) {
            Float res = cache.get(b);
            cacheLookups++;
            if (res != null) {
                cacheLookupsGood++;
                return res;
            }
        }
        
        float myBest = Float.POSITIVE_INFINITY;
        
        /* sort the pieces so the most interesting pieces
         * are used first
         */
        Piece[] pcs = new Piece[todo.size()];
        todo.toArray(pcs);
        Arrays.sort(pcs, new Comparator<Piece>() {
            
            @Override
            public int compare(Piece p1, Piece p2) {
                final Piece[] p = {p1, p2};
                final float[] in = {0, 0};
                
                /* by sum of intensities */
                for (int i=0; i<2; i++) {
                    for (Piece pp : b.pieces)
                        in[i] += intensity(pp, p[i]);
                }
                
                float res = in[1] - in[0];
                if (res != 0) return (int)Math.signum(res);
                
                /* by number of connections to already placed pieces */
                in[0] = in[1] = 0;
                for (int i=0; i<2; i++) {
                    for (Piece pp : b.pieces)
                        in[i] += pp.neighbours.contains(p[i])?1:0;
                }
                
                res = in[1] - in[0];
                if (res != 0) return (int)Math.signum(res);
                
                /* by max intensity to any neighbour, placed or not */
                in[0] = in[1] = 0;
                for (int i=0; i<2; i++) {
                    for (Piece pp : p[i].neighbours)
                        in[i] = Math.max(in[i],
                                intensity(p[i], pp));
                }
                
                res = in[1] - in[0];
                if (res != 0) return (int)Math.signum(res);
                
                /* by bare number of connections */
                for (int i=0; i<2; i++) {
                   in[i] = p[i].neighbours.size();
                }
                
                return (int)Math.signum(in[1] - in[0]);
            }
        });
        
        /* iterate over pieces */
        for (int i=0; i < todo.size(); i++) {
            final Piece p = pcs[i];
            int pos = todo.indexOf(p);
            todo.remove(pos);
            
            /* sort the candidate positions so
             * ones which result in a low cost
             * increase are used first
             */
            Coord cs[] = new Coord[b.getCandidates().size()];
            b.getCandidates().toArray(cs);
            Arrays.sort(cs, new Comparator<Coord>() {
                
                @Override
                public int compare(Coord c1, Coord c2) {
                    Coord[] cc = {c1, c2};
                    int co[] = {0, 0};
                    
                    for (int i=0; i < 2; i++)
                        for (Piece n : p.neighbours)
                            co[i] += n.isPlaced()?
                                intensity(p, n) * Coord.distance(cc[i], n.pos):
                                0;
                    
                    return co[0] - co[1];
                }
            });
            
            /* iterate over sorted candidate positions for piece */
            for (final Coord c : cs) {
                b.place(p, c);
                float myCurrent = 0.0f;
                
                for (Piece n : p.neighbours) {
                    if (n.isPlaced())
                        myCurrent += Coord.distance(p.pos, n.pos) *
                                intensity(p, n);
                }
                
                myCurrent += solve(b, todo, costSoFar + myCurrent);
                myBest = Math.min(myCurrent, myBest);
                b.pick(p);
            }
            
            /* put this piece back into todo list */
            todo.add(pos, p);
        }                
        
        /* remember this */
        if (todo.size() >= getCacheThreshold())
            cache.put((Board)b.clone(), myBest + costSoFar);
        
        return myBest + costSoFar;
    }
    
    /**
     * Computes a lower bound on the cost which will be
     * caused by placing the given pieces.
     *
     * @param pcs The pieces to compute the lower bound for.
     * @return A lower bound on the cost which will be caused
     *      by placing the given pieces.
     */
    private float lowerBound(final List<Piece> pcs) {
        float lb = 0;
        
        for (final Piece p : pcs)
            for (final Piece n : p.neighbours)
                if (!(n.nr < p.nr)) /* do not double-count costs */
                    lb += intensity(p, n);
        
        return lb;
    }

    @Override
    public void run() {
        solve(b, this.todo, 0);
    }
    
    public static class Coord implements Comparable<Coord> {
        public final int x;
        public final int y;
        
        public Coord(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public Coord[] getNeighbours() {
            Coord[] n;
            if (this.equals(START_POS)) {
                /* ignore rotated / mirrored solutions */
                n = new Coord[2];
                n[0] = new Coord(x+1, y);
                n[1] = new Coord(x+1, y+1);
            } else {
                n = new Coord[6];
                if (y % 2 == 0) {
                    /* right-aligned */
                    n[0] = new Coord(x+1, y  );
                    n[1] = new Coord(x+1, y-1);
                    n[2] = new Coord(x  , y-1);
                    n[3] = new Coord(x-1, y  );
                    n[4] = new Coord(x  , y+1);
                    n[5] = new Coord(x+1, y+1);
                } else {
                    /* left-aligned */
                    n[0] = new Coord(x+1, y  );
                    n[1] = new Coord(x  , y-1);
                    n[2] = new Coord(x-1, y-1);
                    n[3] = new Coord(x-1, y  );
                    n[4] = new Coord(x-1, y+1);
                    n[5] = new Coord(x  , y+1);
                }
            }
            
            return n;
        }
        
        
        public static int distance(final Coord c1, final Coord c2) {
            final int dx = Math.abs(c2.x - c1.x);
            final int dy = Math.abs(c2.y - c1.y);

            if (dy % 2 == 0) {
                if (dx <= dy / 2) {
                    return dy;
                } else {
                    return dx + dy/2;
                }
            } else {
                int g = dy/2;
                if ((c1.y % 2 == 0) && (c1.x < c2.x)) g++;
                else if ((c1.y % 2 != 0) && (c1.x > c2.x)) g++;
                if (g >= dx) return dy;
                else return dx + dy - g;
            }
        }
        
        @Override
        public int compareTo(Coord oc) {
            if (oc.y == y) {
                return x - oc.x;
            } else
                return y - oc.y;
        }
        
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Coord)) return false;
            return (((Coord)o).x == x) && (((Coord)o).y == y);
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 11 * hash + this.x;
            hash = 11 * hash + this.y;
            return hash;
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
        
    }
    
    public class Board implements Cloneable {
        
        public List<Coord> free =
                new ArrayList<Coord>();
        
        public SortedSet<Piece> pieces =
                new TreeSet<Piece>();
        
        /** Allows to associate some user-data with the board. */
        protected float cost;
        
        public Board() {
            /* we always start at the origin */
            free.add(START_POS);
            cost = -1;
        }
        
        public List<Coord> getCandidates() {
            return new ArrayList<Coord>(free);
        }
        
        public void setCost(float cost) {
            this.cost = cost;
        }
        
        public Object getCost() {
            if (cost < 0) throw new IllegalStateException("no cost set");
            return cost;
        }
        
        public Piece getPiece(int nr) {
            Piece tmp = new Piece(nr);
            SortedSet<Piece> tail = pieces.tailSet(tmp);
            if (tail.size() == 0) return null;
            if (tail.first().equals(tmp)) return tail.first();
            return null;
        }
        
        /**
         * 
         * The list of coords which became "free" because
         *      this piece was added is assigned to the piece.
         */
        public void place(Piece p, Coord pos) {
            p.pos = pos;
            
            if (!free.remove(p.pos)) throw new
                  IllegalArgumentException("the piece's position is not free");
            if (p.isPlaced()) throw new
                  IllegalArgumentException("the piece is already placed");
            
            List<Coord> newFree = new ArrayList<Coord>(Arrays.asList(p.pos.getNeighbours()));
            
            /* remove all already-used positions from the list */
            for (Piece pp : pieces) {
                newFree.remove(pp.pos);
            }
            
            newFree.removeAll(free);
            p.setFreeContrib(newFree);
            free.addAll(newFree);
            pieces.add(p);
        }
        
        public void pick(Piece p) {
            if (!p.isPlaced())
                throw new IllegalArgumentException("the piece is not placed");
            
            free.removeAll(p.getDeleteFreeContrib());
            free.add(p.pos);
            pieces.remove(p);
        }
        
        public boolean isPieceAt(Coord pos) {
            for (Piece p : pieces)
                if (p.getPos().equals(pos))
                    return true;
            
            return false;    
        }
    
        
        public void prettyPrint() {
            
            /* un-pretty... */
            if (false) {
                for (Piece p : pieces) {
                    System.out.println(""+p.nr+": (" + p.pos.x+", " + p.pos.y+")");
                }
                return;
            }
            
            /* find minimum x value */
            int minX = Integer.MAX_VALUE;
            for (Piece p : pieces)
                minX = Math.min(p.pos.x, minX);
            
            int x = minX;
            
            Piece[] pcs = new Piece[pieces.size()];
            pieces.toArray(pcs);
            
            Arrays.sort(pcs, new Comparator<Piece>() {
                
                @Override
                public int compare(Piece p1, Piece p2) {
                    return p1.pos.compareTo(p2.pos);
                }
            });
            
            int y = Integer.MAX_VALUE;
            for (Piece p : pcs) {
                if (p.pos.y != y) {
                    /* new line */
                    System.out.print("\n");
                    if (y % 2 == 0)
                        for (int i=minX; i < x; i++) System.out.print("\\ / ");
                    else
                        for (int i=minX; i < x; i++) System.out.print("/ \\ ");
                    System.out.println("");
                    x=minX;
                    y = p.pos.y;
                    
                    if (y % 2 == 0) System.out.print("  ");
                }
                
                for (int i=0; i < p.pos.x - x; i++) {
                    System.out.print("    ");
                    
                }
                
                x = p.pos.x;
                System.out.printf("%2d", p.nr);
                System.out.print("--");
                x++;
            }
            
            System.out.println("\n\n");
        }
        
        @Override
        public Object clone() {
            final Board copy = new Board();
            
            copy.pieces = new TreeSet<Piece>();
            
            for (Piece p : pieces) {
                copy.pieces.add((SchmigallaSolver.Piece) p.clone());
            }
            
            copy.free = new ArrayList<Coord>(this.free);
            copy.cost = this.cost;
            
            return copy;
        }
        
        @Override
        public boolean equals(Object object) {
            if (object == this) return true;
            if (!(object instanceof Board)) return false;
            Board ob = (Board)object;
            
            for (Piece my_p: pieces) {
                Piece other_p = ob.getPiece(my_p.nr);
                if (other_p == null) return false;
                if (!other_p.pos.equals(my_p.pos)) return false;
            }
            
            return true;
        }

        @Override
        public int hashCode() {
            int hash = pieces.size();
            
            for (Piece p : pieces) {
                hash ^= p.getPos().x ^ p.getPos().y;
            }
            
            return hash;
        }
        
    }
    
    public class Piece implements Comparable<Piece>, Cloneable {
        public final int nr;
        private Coord pos;
        private ArrayList<Piece> neighbours;
        private List<Coord> freeContrib;
        
        public Piece(int nr, Coord pos, ArrayList<Piece> neighbours) {
            this.nr = nr;
            this.pos = pos;
            this.neighbours = neighbours;
        }
        
        public Piece(int nr, Coord pos) {
            this(nr, pos, new ArrayList<Piece>());
        }
        
        protected Piece(int nr) {
            this(nr, null);
        }
        
        @Override
        public Object clone() {
            Piece copy = new Piece(nr, pos, new ArrayList<Piece>(neighbours));
            copy.freeContrib = new ArrayList<Coord>(freeContrib);
            return copy;
        }
        
        public void addNeighbour(Piece p) {
            neighbours.add(p);
        }
        
        @Override
        public int compareTo(Piece p) {
            return nr - p.nr;
        }
        
        public boolean isPlaced() {
            return (freeContrib != null);
        }
        
        public void setFreeContrib(List<Coord> freeContrib) {
            if (isPlaced()) throw new IllegalStateException("the piece is already placed");
            this.freeContrib = freeContrib;
        }
        
        public List<Coord> getDeleteFreeContrib() {
            if (!isPlaced()) throw new IllegalStateException("the piece is not placed");
            final List<Coord> tmp = freeContrib;
            this.freeContrib = null;
            return tmp;
        }
        
        public void setPos(Coord pos) {
            this.pos = pos;
        }
        
        public Coord getPos() {
            return pos;
        }
        
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Piece)) return false;
            Piece op = (Piece)o;
            return op.nr == nr;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 97 * hash + this.nr;
            return hash;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Piece #").append(nr).append(" neighbours: [");
            
            for (int i=0; i < neighbours.size(); i++) {
                sb.append(neighbours.get(i).nr);
                sb.append("(");
                sb.append(intensity(this, neighbours.get(i)));
                sb.append(")");
                if (i < neighbours.size()-1)
                    sb.append(", ");
            }
            
            sb.append("]");
            return sb.toString();
        }
    }

    /**
     * Holds value of property cacheThreshold.
     */
    private int cacheThreshold = 3;

    /**
     * Getter for property cacheThreshold.
     * @return Value of property cacheThreshold.
     */
    public int getCacheThreshold() {
        return this.cacheThreshold;
    }

    /**
     * Setter for property cacheThreshold.
     * @param cacheThreshold New value of property cacheThreshold.
     */
    public void setCacheThreshold(int cacheThreshold) {
        this.cacheThreshold = cacheThreshold;
    }
}
