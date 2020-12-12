import java.util.ArrayList;

public class NumericalIntegration {
    private final ArrayList<Double> xValues;
    private final ArrayList<Double> fxValues;
    private final ArrayList<Double> xGraphValues;
    private final ArrayList<Double> fxGraphValues;
    private final int size;
    private int modulusDividend;

    NumericalIntegration(ArrayList<Double> xValues, ArrayList<Double> fxValues){
        this.xValues = xValues;
        this.fxValues = fxValues;
        this.xGraphValues = new ArrayList<>();
        this.fxGraphValues = new ArrayList<>();
        size = xValues.size();
    }
    public double numericalIntegration(int n, double h, boolean trapRule){
        modulusDividend = (n > 16) ? (int)Math.round(n / 16.0) : 1;
        if (trapRule)
            return trapezoidalRule(n, h);
        else
            return simpsonsRule(n, h);
    }
    private double trapezoidalRule(int n, double h){
        double area = 0, sum = 0, a = xValues.get(0), b = xValues.get(size-1), fxk, xk;

        // Increment f(xo)
        double fxo = fxValues.get(0);

        area += fxo;
        xGraphValues.add(Math.round(a * 100)/100.0);
        fxGraphValues.add(Math.round(fxo * 100)/100.0);

        // Increment 2 * ∑(fxi) for i ∈ [1, N-1]
        for (double k = 1; k < n; k++){
            xk = a + k*h;
            if (xValues.contains(xk))
                fxk = fxValues.get(xValues.indexOf(xk));
            else
                fxk = getFunctionValue(xk);

            sum += fxk;

            if (k % modulusDividend == 0){
                xGraphValues.add(Math.round(xk * 100)/100.0);
                fxGraphValues.add(Math.round(fxk * 100)/100.0);
            }
        }

        area += 2 * sum;

        // Increment f(xn)
        double fxn = fxValues.get(size-1);
        area += fxn;
        xGraphValues.add(Math.round(b * 100)/100.0);
        fxGraphValues.add(Math.round(fxn * 100)/100.0);
        return h * area / 2;
    }
    private double simpsonsRule(int n, double h){
        double area = 0, a = xValues.get(0), b = xValues.get(size-1), fxk, xk;
        int scalar;

        // Increment f(a)
        double fxo = fxValues.get(0);

        area += fxo;
        xGraphValues.add(Math.round(a * 100)/100.0);
        fxGraphValues.add(Math.round(fxo * 100)/100.0);

        // Increment 2 * ∑(fx2k) for i ∈ [1, (N-2)-1]
        for (double k = 1; k < n; k++){
            xk = a + k*h;
            scalar = (k % 2 == 0) ? 2 : 4;

            if (xValues.contains(xk))
                fxk = fxValues.get(xValues.indexOf(xk));
            else
                fxk = getFunctionValue(xk);

            area += scalar * fxk;
            
            if (k % modulusDividend == 0){
                xGraphValues.add(Math.round(xk * 100)/100.0);
                fxGraphValues.add(Math.round(fxk * 100)/100.0);
            }
        }

        // Increment f(b)
        double fxn = fxValues.get(size-1);

        area += fxn;
        xGraphValues.add(Math.round(b * 100)/100.0);
        fxGraphValues.add(Math.round(fxn * 100)/100.0);

        return h * area / 3;
    }

    // Function will return the fx value of the x value entered. Quadratic interpolation is performed when necessary,
    private double getFunctionValue(double x){
        if (xValues.contains(x))
            return fxValues.get(xValues.indexOf(x));
        else{
            int firstIndexOfSubset = getFirstIndexOfSubset(x);
            ArrayList<Double> newXVec = new ArrayList<>(xValues.subList(firstIndexOfSubset, firstIndexOfSubset+3));
            ArrayList<Double> newFXVec = new ArrayList<>(fxValues.subList(firstIndexOfSubset, firstIndexOfSubset+3));
            return lagrangeInterpolation(newXVec, newFXVec, x);
        }
    }

    // Gets the first index of the subset of 3 data points for quadratic interpolation
    private int getFirstIndexOfSubset(double x){
        if (xValues.size() >= 3){
            int n = xValues.size();

            if (x < xValues.get(0))
                return 0;
            if (x > xValues.get(n-1))
                return n-3;

            int index = 0;
            double leftVal, rightVal;
            for (int i = 0; i < n-1; i++){
                leftVal = xValues.get(i);
                rightVal = xValues.get(i+1);
                if (leftVal < x && x < rightVal){
                    if (i+2 < n)
                        index = i;
                    else
                        index = i-1;
                    break;
                }
            }
            return index;
        }
        else
            return -1;
    }

    // Lagrange Interpolation Algorithm
    private double lagrangeInterpolation(ArrayList<Double> xCoords, ArrayList<Double> yCoords, double x){
        int n = xCoords.size();
        double interpolatedValue = 0, lagrangian, xi, xj;

        for (int i = 0; i < n; i++){
            lagrangian = 1;
            xi = xCoords.get(i);
            for (int j = 0; j < n; j++){
                if (i != j){
                    xj = xCoords.get(j);
                    lagrangian *= (x - xj)/(xi - xj);
                }
            }
            interpolatedValue += lagrangian * yCoords.get(i);
        }
        return interpolatedValue;
    }

    public ArrayList<Double> getxGraphValues() {
        return xGraphValues;
    }
    public ArrayList<Double> getfxGraphValues() {
        return fxGraphValues;
    }
}
