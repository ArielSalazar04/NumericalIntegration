# Numerical Integration Calculator

**Introduction**

According to the Fundamental Theorem of Calculus, if a function f is continuous on the closed interval [a, b] and F is an antiderivative of f on the interval [a, b], then

<img src="https://user-images.githubusercontent.com/54899441/102685629-1bb79e00-41b0-11eb-968d-6fb7c97da441.png"></img>

Although we may not always have the formula of a function to compute its antiderivative, we can find the area under the curve using numerical methods, such as the 
Trapezoidal Rule and Simpson’s Rule. 

This open-source project is a software system created in Java that uses the Java Swing graphical user interface. 

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

The Trapezoidal Rule consists of approximating the area under a curve using a trapezoid. Although using one trapezoid will yield a large error (especially for non-linear data), the Trapezoidal Rule can be applied multiple times to obtain a more accurate result. In fact, using more trapezoids and reducing the width of each one will produce a better approximation. When increasing the number of sub-intervals, n, each sub-interval width, h, will become smaller. 

<img src="https://user-images.githubusercontent.com/54899441/102685601-ee6af000-41af-11eb-9237-004921d45376.png" width="512">
<img src="https://user-images.githubusercontent.com/54899441/102685594-e743e200-41af-11eb-9e51-9a05875f3486.png" width="512">

*(2) Simpson's Rule*

Simpson's Rule consists of approximating the area under a curve by integrating a Quadratic Lagrange Polynomial. While computing the integral in this manner may become computationally costly, the formula below will produce the same result (Simpson's Rule typically gives an exact solution for quadratic and cubic functions). 

<img src="https://user-images.githubusercontent.com/54899441/102685599-ea3ed280-41af-11eb-9a9c-5589c331a6b0.png" width="512">
<img src="https://user-images.githubusercontent.com/54899441/102685586-e0b56a80-41af-11eb-911e-ac0208a46185.png" width="512">

**Software System**

