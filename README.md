# Numerical Integration Calculator

**Introduction**

According to the Fundamental Theorem of Calculus, if a function f is continuous on the closed interval [a, b] and F is an antiderivative of f on the interval [a, b], then

![alt text](https://www.shitpostbot.com/img/sourceimages/fundamental-theorem-of-calculus-59fe98066b4d4.png)

Although we may not always have the formula of a function to compute its antiderivative, we can find the area under the curve using numerical methods, such as the 
Trapezoidal Rule and Simpson’s Rule. 

This is a software system created in Java which uses the Java Swing graphical user interface. 


**Theory**

In numerical computation, two of the most popular methods for approximating the definite integral on the interval [a, b] are the Trapezoidal Rule and Simpson's Rule, both of which use the following parameters: 

<ul>
  <li>a = x<sub>0</sub> (First element of x-vector)</li>
  <li>b = x<sub>n</sub> (Last element of x-vector)</li>
  <li>h = <sup>(b-a)</sup>/<sub>n</sub> (Width of each sub-interval)</li>
  <li>x<sub>i</sub> = a + ih (Left position of each sub-interval)</li>
  <li>n = the number of sub-intervals</li>
</ul>

*(1) Trapezoidal Rule*

The Trapezoidal Rule consists of approximating the area under a curve using a trapezoid. To improve the approximation, the trapezoidal rule can be applied multiple times to obtain a more accurate result. This can be done by increasing the number of sub-intervals, n, which will also decrease the width of each sub-interval, h.

<img src="https://i.ibb.co/zPVsFyN/trapRule.png" width="512">
<img src="https://i.ibb.co/3z4pwf3/trap-Rule-Picture.png" width="512">

*(2) Simpson's Rule*

Simpson's Rule consists of approximating the area under a curve by integrating a Quadratic Lagrange Polynomial. While computing the integral in this manner may become computationally costly, the formula below will produce the same result (Simpson's Rule typically gives an exact solution for quadratic and cubic functions). 

<img src="https://i.ibb.co/KN4xwXP/simpsons-Rule.png" width="512">
<img src="https://i.ibb.co/9pz5G1M/Screen-Shot-2020-12-17-at-4-51-06-PM.png" width="512">

**Software System**
