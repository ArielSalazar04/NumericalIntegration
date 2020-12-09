import java.util.ArrayList;

public class LagrangeInterpolation {
    private final ArrayList<Double> xValues;
    private final ArrayList<Double> fxValues;
    private final ArrayList<Double> xGraphValues;
    private final ArrayList<Double> fxGraphValues;
    private int modulusDividend;

    LagrangeInterpolation(ArrayList<Double> xValues, ArrayList<Double> fxValues){
        this.xValues = xValues;
        this.fxValues = fxValues;
        this.xGraphValues = new ArrayList<>();
        this.fxGraphValues = new ArrayList<>();
    }
    public double numericalIntegration(ArrayList<Double> xCoords, ArrayList<Double> fxCoords, int n, double h, boolean trapRule){
        modulusDividend = (n > 16) ? (int)Math.round(n / 16.0) : 1;
        if (trapRule)
            return trapezoidalRule(xCoords, fxCoords, n, h);
        else
            return simpsonsRule(xCoords, fxCoords, n, h);
    }
    private double trapezoidalRule(ArrayList<Double> xCoords, ArrayList<Double> fxCoords, int n, double h){
        double sum = 0, a = xCoords.get(0), b = xCoords.get(xCoords.size()-1), fxo, fxk, fxn, xk;

        // Increment f(xo)
        fxo = fxCoords.get(xCoords.indexOf(a));

        sum += fxo;
        xGraphValues.add(Math.round(a * 100)/100.0);
        fxGraphValues.add(Math.round(fxo * 100)/100.0);

        // Increment 2 * ∑(fxi) for i ∈ [1, N-1]
        for (double k = 1; k < n; k++){
            xk = a + k*h;
            if (xCoords.contains(xk))
                fxk = 2 * fxCoords.get(xCoords.indexOf(xk));
            else
                fxk = 2 * getFunctionValue(xCoords, fxCoords, xk);

            sum += fxk;

            if (k % modulusDividend == 0){
                xGraphValues.add(Math.round(xk * 100)/100.0);
                fxGraphValues.add(Math.round(fxk * 100)/200.0);
            }
        }

        // Increment f(xn)
        fxn = fxCoords.get(xCoords.indexOf(b));

        sum += fxn;
        xGraphValues.add(Math.round(b * 100)/100.0);
        fxGraphValues.add(Math.round(fxn * 100)/100.0);

        return h * sum / 2;
    }
    private double simpsonsRule(ArrayList<Double> xCoords, ArrayList<Double> fxCoords, int n, double h){
        double sum = 0, a = xCoords.get(0), b = xCoords.get(xCoords.size()-1), fxo, fxk, fxn, xk;
        int scalar;

        // Increment f(a)
        fxo = fxCoords.get(xCoords.indexOf(a));

        sum += fxo;
        xGraphValues.add(Math.round(a * 100)/100.0);
        fxGraphValues.add(Math.round(fxo * 100)/100.0);

        // Increment 2 * ∑(fx2k) for i ∈ [1, (N-2)-1]
        for (double k = 1; k < n; k++){
            xk = a + k*h;
            scalar = (k % 2 == 0) ? 2 : 4;

            if (xCoords.contains(xk))
                fxk = scalar * fxCoords.get(xCoords.indexOf(xk));
            else
                fxk = scalar * getFunctionValue(xCoords, fxCoords, xk);

            sum += fxk;
            
            if (k % modulusDividend == 0){
                xGraphValues.add(Math.round(xk * 100)/100.0);
                fxGraphValues.add(Math.round(fxk * 100)/200.0);
            }
        }

        // Increment f(b)
        fxn = fxCoords.get(xCoords.indexOf(b));

        sum += fxn;
        xGraphValues.add(Math.round(b * 100)/100.0);
        fxGraphValues.add(Math.round(fxn * 100)/100.0);

        return h * sum / 3;
    }

    // Function will return the fx value of the x value entered. Quadratic interpolation is performed when necessary,
    private double getFunctionValue(ArrayList<Double> xCoords, ArrayList<Double> fxCoords, double x){
        int firstIndexOfSubset;

        if (xCoords.contains(x))
            return fxCoords.get(xCoords.indexOf(x));
        else{
            firstIndexOfSubset = getFirstIndexOfSubset(xCoords, x);
            ArrayList<Double> newXVec = new ArrayList<>(xCoords.subList(firstIndexOfSubset, firstIndexOfSubset+3));
            ArrayList<Double> newFXVec = new ArrayList<>(fxCoords.subList(firstIndexOfSubset, firstIndexOfSubset+3));
            return lagrangeInterpolation(newXVec, newFXVec, x);
        }
    }

    // Gets the first index of the subset of 3 data points for quadratic interpolation
    private int getFirstIndexOfSubset(ArrayList<Double> xCoords, double x){
        if (xCoords.size() >= 3){
            int n = xCoords.size();

            if (x < xCoords.get(0))
                return 0;
            if (x > xCoords.get(n-1))
                return n-3;

            int index = 0;
            double leftVal, rightVal;
            for (int i = 0; i < n-1; i++){
                leftVal = xCoords.get(i);
                rightVal = xCoords.get(i+1);
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
