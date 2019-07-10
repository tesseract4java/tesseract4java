/*
 * Copyright (C) 2013 R.C.C.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package eu.digitisation.math;

/**
 * Standard operations on arrays: sum, average, max, min, standard deviation.
 *
 * @author R.C.C.
 * @version 20131110
 */
public class Arrays {

    /**
     * @param array array of int
     * @return the sum of all ints in array
     */
    public static int sum(int[] array) {
        int sum = 0;

        for (int n = 0; n < array.length; ++n) {
            sum += array[n];
        }
        return sum;
    }

    /**
     *
     * @param array array of double
     * @return the sum of all doubles in array
     */
    public static double sum(double[] array) {
        double sum = 0;
        for (int n = 0; n < array.length; ++n) {
            sum += array[n];
        }
        return sum;
    }

    /**
     * Transform an int array into a an array of doubles
     *
     * @param array integer array
     * @return the array of double precision values
     */
    public static double[] toDouble(int[] array) {
        double[] darray = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            darray[i] = array[i];
        }
        return darray;
    }

    /**
     * Create an array containing the logarithms of the source array
     *
     * @param array integer array
     * @return the array of logs
     */
    public static double[] log(int[] array) {
        double[] darray = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            darray[i] = Math.log(array[i]);
        }
        return darray;
    }

    /**
     * Create an array containing the logarithms of the source array
     *
     * @param array array of double
     * @return the array of logs
     */
    public static double[] log(double[] array) {
        double[] darray = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            darray[i] = Math.log(array[i]);
        }
        return darray;
    }

    /**
     * @param array
     * @return the average of all integers in an array
     */
    public static double average(int[] array) {
        return sum(array) / (double) array.length;
    }

    /**
     * The average of all doubles in an array
     *
     * @param array
     * @return the average of all doubles in an array
     */
    public static double average(double[] array) {
        return sum(array) / array.length;
    }

    /**
     * @param array
     * @return the geometric mean (log-average) of all integers in an array
     */
    public static double logaverage(int[] array) {
        return Math.exp(average(log(array)));
    }

    /**
     * The geometric mean (log-average) of all doubles in an array
     *
     * @param array
     * @return the average of all doubles in an array
     */
    public static double logaverage(double[] array) {
        return Math.exp(average(log(array)));
    }

    /**
     * The scalar product
     *
     * @param x the first array
     * @param y the second array
     * @return the scalar product of x and y
     */
    public static double scalar(double[] x, double[] y) {
        double sum = 0;
        for (int n = 0; n < x.length; ++n) {
            sum += x[n] * y[n];
        }
        return sum;
    }

    /**
     * @param array int array
     * @return the max value in int array
     */
    public static int max(int[] array) {
        int mu = array[0];

        for (int n = 1; n < array.length; ++n) {
            mu = Math.max(mu, array[n]);
        }
        return mu;
    }

    /**
     * @param array array of doubles
     * @return the max value in this array
     */
    public static double max(double[] array) {
        double mu = array[0];

        for (int n = 1; n < array.length; ++n) {
            mu = Math.max(mu, array[n]);
        }
        return mu;
    }

    /**
     * @param array int array
     * @return the min value in int array
     */
    public static int min(int[] array) {
        int mu = array[0];

        for (int n = 1; n < array.length; ++n) {
            mu = Math.min(mu, array[n]);
        }
        return mu;
    }

    /**
     * @param array array of doubles
     * @return the min value in this array
     */
    public static double min(double[] array) {
        double mu = array[0];

        for (int n = 1; n < array.length; ++n) {
            mu = Math.min(mu, array[n]);
        }
        return mu;
    }

    /**
     * @param array int array
     * @return first position containing the max value in int array
     */
    public static int argmax(int[] array) {
        int pos = 0;

        for (int n = 1; n < array.length; ++n) {
            if (array[n] > array[pos]) {
                pos = n;
            }
        }
        return pos;
    }
    
  /**
     * @param array int array
     * @return first position containing the min value in int array
     */
    public static int argmin(int[] array) {
        int pos = 0;

        for (int n = 1; n < array.length; ++n) {
            if (array[n] < array[pos]) {
                pos = n;
            }
        }
        return pos;
    }
    
    /**
     * @param X array of int
     * @param Y another array of int
     * @return the covariance of two variables X and Y are expected to have same
     * length
     */
    public static double cov(int[] X, int[] Y) {
        int len = Math.min(X.length, Y.length);
        double sum = 0;  // double safer against overflows

        for (int n = 0; n < len; ++n) {
            sum += X[n] * (double) Y[n];
        }

        return sum / len - average(X) * average(Y);
    }

    /**
     * Covariance of two variables
     *
     * @param X array of double
     * @param Y another array of double
     * @return Covariance of X and Y
     */
    public static double cov(double[] X, double[] Y) {
        int len = Math.min(X.length, Y.length);
        double sum = 0;

        for (int n = 0; n < len; ++n) {
            sum += X[n] * Y[n];
        }
        return sum / len - average(X) * average(Y);
    }

    /**
     * @param X the array with the values of the variable
     * @return the standard deviation of the values in X
     */
    public static double std(int[] X) {
        return Math.sqrt(cov(X, X));
    }

    /**
     * Standard deviation
     *
     * @param X the array with the values of the variable
     * @return the standard deviation of the values in X
     */
    public static double std(double[] X) {
        return Math.sqrt(cov(X, X));
    }
}
