package EjemploGeoTools;

/*
 * Copyright 2009 Michael Bedward
 *
 * This file is part of jai-tools
 *
 * jai-tools is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.

 * jai-tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with jai-tools.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
//package jaitools.util;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * A utility class for cubic spline interpolation in two dimensions.
 *
 * @author Michael Bedward
 */
public class SplineInterpolator {

    /**
     * Cubic spline interpolation through the set of points with
     * coordinates given by x and y input arrays.
     * <P>
     * Returns the coords of a line interpolated through np points
     * (including the first and last control points) which are
     * approximately evenly-spaced (fudged using linear interp).
     * <P>
     * Adapted from an algorithm in Press et al. 1992
     * "Numeical Recipes in C". Cambridge University Press
     *
     * @param ctrlCoords the 'control points' that the interpolated curve must pass through
     * @param numPoints number of interpolation points
     * @param includeCtrlCoords if true the returned Coordinates will include the input
     * control points; if false only the first and last control points are guaranteed to
     * be included (though the curve still passes through the other points)
     *
     * @return array of interpolated Coordinates
     *
     * @throws java.lang.IllegalArgumentException
     */
    public Coordinate[] interpolate(Coordinate[] ctrlCoords, int numPoints, boolean includeCtrlCoords)
            throws IllegalArgumentException {

        int N = ctrlCoords.length;
        if (numPoints <= N) {
            throw new IllegalArgumentException(
                    "np should be larger than length of input coords array");
        }

        // x values need to be considered in ascending order
        int[] o = getXOrder(ctrlCoords);

        // Interpolation (as done below) will fail if any of the input
        // coordinates x values are equal
        double prec = 1.0e-8;
        for (int i = 1; i < N; i++) {
            if (Math.abs(ctrlCoords[o[i]].x - ctrlCoords[o[i - 1]].x) < prec) {
                // jiggle the second coord
                ctrlCoords[o[i - 1]].x += 2 * prec;
            }
        }

        // first derivatives
        double[] dy = new double[N];

        // second derivatives
        double[] ddy = new double[N];

        /*
         * In this implementation we set the value of the second
         * derivatives at the end-points to 0 (the so-called natural
         * cublic spline). See Press et al. 1992 for alternative
         * approaches.
         */
        ddy[0] = ddy[N - 1] = dy[0] = 0.0;

        // tridiagonal algorithm to calculate the piece-wise splines
        double sig, p;
        for (int i = 1; i < N - 1; i++) {
            sig = (ctrlCoords[o[i]].x - ctrlCoords[o[i - 1]].x) / (ctrlCoords[o[i + 1]].x - ctrlCoords[o[i - 1]].x);
            p = sig * ddy[i - 1] + 2.0;
            ddy[i] = (sig - 1.0) / p;
            dy[i] = (ctrlCoords[o[i + 1]].y - ctrlCoords[o[i]].y) / (ctrlCoords[o[i + 1]].x - ctrlCoords[o[i]].x) - (ctrlCoords[o[i]].y - ctrlCoords[o[i - 1]].y) / (ctrlCoords[o[i]].x - ctrlCoords[o[i - 1]].x);
            dy[i] = (6.0d * dy[i] / (ctrlCoords[o[i + 1]].x - ctrlCoords[o[i - 1]].x) - sig * dy[i - 1]) / p;
        }

        for (int i = N - 2; i >= 0; i--) {
            ddy[i] = ddy[i] * ddy[i + 1] + dy[i];
        }

        // interpolation

        // first find approximate distance along curve by linear interp
        double[] dist = new double[N];
        double[] xdiff = new double[N];
        dist[0] = xdiff[0] = 0.0;
        for (int i = 1; i < N; i++) {
            xdiff[i] = ctrlCoords[o[i]].x - ctrlCoords[o[i - 1]].x;
            double ydiff = ctrlCoords[o[i]].y - ctrlCoords[o[i - 1]].y;
            dist[i] = dist[i - 1] + Math.sqrt(xdiff[i] * xdiff[i] +
                    ydiff * ydiff);
        }

        // spacing
        double dIncr = dist[N - 1] / (numPoints - 1);

        Coordinate[] interpCoords;
        if (includeCtrlCoords) {
            interpCoords = new Coordinate[numPoints + N - 2];
        } else {
            interpCoords = new Coordinate[numPoints];
        }

        interpCoords[0] = new Coordinate(ctrlCoords[o[0]]);
        interpCoords[interpCoords.length - 1] = new Coordinate(ctrlCoords[o[N - 1]]);

        double curDist = dIncr;
        int kCtrl = 1;
        int kInterp = 1;
        for (int i = 1; i < numPoints - 1; i++, curDist += dIncr) {
            while (curDist > dist[kCtrl]) {
                kCtrl++;
                if (includeCtrlCoords) {
                    interpCoords[kInterp++] = new Coordinate(ctrlCoords[o[kCtrl - 1]]);
                }
            }

            Coordinate c = new Coordinate();

            // locate the x coord of the interpolated point
            double delta = (curDist - dist[kCtrl - 1]) / (dist[kCtrl] - dist[kCtrl - 1]);
            c.x = ctrlCoords[o[kCtrl - 1]].x + delta * xdiff[kCtrl];

            // locate the y coord by evaluating the cubic spline polynomial
            double h = ctrlCoords[o[kCtrl]].x - ctrlCoords[o[kCtrl - 1]].x;
            double a = (ctrlCoords[o[kCtrl]].x - c.x) / h;
            double b = (c.x - ctrlCoords[o[kCtrl - 1]].x) / h;
            c.y = a * ctrlCoords[o[kCtrl - 1]].y + b * ctrlCoords[o[kCtrl]].y +
                    ((a * a * a - a) * ddy[kCtrl - 1] + (b * b * b - b) *
                    ddy[kCtrl]) * (h * h) / 6.0;

            interpCoords[kInterp++] = c;
        }

        return interpCoords;
    }

    /*
     * Get the order of the x ordinates in the given Coordinate array
     */
    private static int[] getXOrder(Coordinate[] coords) {
        if (coords.length == 0) {
            return null;
        } else if (coords.length == 1) {
            return new int[]{0};
        }

        int[] order = new int[coords.length];
        for (int i = 0; i < coords.length; i++) {
            order[i] = i;
        }

        // using shellsort algorithm as modified from Sedgewick 1990...
        // Algorithms in C
        //
        int[] incs = new int[]{
            1391376, 463792, 198768, 86961, 33936, 13776,
            4592, 1968, 861, 336, 112, 48, 21, 7, 3, 1
        };

        int i0, j;
        double v;
        for (int k = 0; k < incs.length; k++) {
            for (int h = incs[k], i = h; i < coords.length; i++) {
                j = i;
                i0 = order[j];
                v = coords[i0].x;
                while (j > h - 1 && coords[order[j - h]].x > v) {
                    order[j] = order[j - h];
                    j -= h;
                }
                order[j] = i0;
            }
        }

        return order;
    }
}
