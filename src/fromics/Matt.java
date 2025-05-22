package fromics;

import java.util.Arrays;

public class Matt {
    private double[][] vals;

    public Matt() {
        vals = new double[2][2];
    }

    public Matt(Point[] components) {
        vals = new double[components.length][];
        for(int i = 0; i < components.length; i++) {
            vals[i] = Arrays.copyOf(components[i].vals, components[i].vals.length);
        }
    }

    public Matt(double[][] vals) {
        this.vals = new double[vals.length][vals[0].length];
        for(int i = 0; i < vals.length; i++) {
            System.arraycopy(vals[i], 0, this.vals[i], 0, vals[0].length);
        }
    }

    public Matt(int size) {
        vals = new double[size][size];
    }

    public Matt(int width, int height) {
        vals = new double[width][height];
    }

    public Point applyTransformation(Point p) {
        Point transformed = new Point(vals[0].length);
        int dimsModified = Math.min(p.dims(), vals.length);
        for(int i = 0; i < dimsModified; i++) {
            for(int j = 0; j < vals[i].length; j++) {
                transformed.set(j, transformed.get(j) + transformed.get(i) * p.get(i));
            }
        }
        return transformed;
    }

    public double determinant() {
        if(vals.length != vals[0].length) {
            throw new IllegalStateException("you can only find the determinant of square matricies");
        }
        return determinantOf(vals);
    }

    private static double determinantOf(double[][] arr) {
        if(arr.length == 2) {
            return arr[0][0] * arr[1][1] - arr[0][1] * arr[1][0];
        } else {
            double sum = 0;
            for(int i = 0; i < arr.length; i++) {
                double[][] sub = new double[arr.length-1][arr.length-1];
                for(int j = i + 1, x = 0; j != i; j = (j + 1) % arr.length, x++) {
                    for(int k = 1, y = 0; k < arr.length; k++, y++) {
                        sub[x][y] = arr[j][k];
                    }
                }
                sum += arr[i][0] * determinantOf(sub) * (i % 2 == 0 ? 1 : -1);
            }
            return sum;
        }
    }

    public Matt copy() {
        Matt newM = new Matt(vals);
        return newM;
    }

    public Matt invert() {
        return this;
    }

    public Point solveSystem(Point vals) {
        return new Point();//TODO: make this method
    }

    //composes this matrix with another one, applying the given matrix before this one, then returns the new matrix
    public Matt compose(Matt m) {
        if(m.vals[0].length != this.vals.length) {
            throw new IllegalArgumentException("given matrix's height must match this matrix's width");
        }
        Matt composed = new Matt(m.vals.length, this.vals[0].length);
        for(int i = 0; i < m.vals.length; i++) {
            for(int j = 0; j < this.vals[0].length; j++) {
                for(int k = 0; k < this.vals.length; k++) {
                    composed.vals[i][j] += vals[k][j] * m.vals[i][k];
                }
            }
        }
        return this;
    }
}
