# Numerical Integration Calculator

## Table of Contents
[1. Introduction](#introduction)

[2. Theory](#theory)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[a. Trapezoidal Rule](#1-trapezoidal-rule)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[b. Simpson's Rule](#2-simpsons-rule)

[3. Software System](#software-system)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[a. Graphical User Interface Design](#graphical-user-interface-design)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[b. Input Panel](#input-panel)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[c. Data Panel](#data-panel)

[4. Example](#example)

## Introduction

According to the Fundamental Theorem of Calculus, if a function f is continuous on the closed interval [a, b] and F is an antiderivative of f on the interval [a, b], then

<img src="https://user-images.githubusercontent.com/54899441/102685629-1bb79e00-41b0-11eb-968d-6fb7c97da441.png"></img>

Although we may not always have the formula of a function to compute its antiderivative, we can find the area under the curve using numerical methods, such as the 
Trapezoidal Rule and Simpson’s Rule. 

This open-source project is a software system created in Java that uses the Java Swing graphical user interface. 

[Back to Top](#numerical-integration-calculator)

## Theory

In numerical computation, two of the most popular methods for approximating the definite integral on the interval [a, b] are the Trapezoidal Rule and Simpson's Rule, both of which use the following parameters: 

<ul>
  <li>a = x<sub>0</sub> (First element of x-vector)</li>
  <li>b = x<sub>n</sub> (Last element of x-vector)</li>
  <li>h = <sup>(b-a)</sup>/<sub>n</sub> (Width of each sub-interval)</li>
  <li>x<sub>i</sub> = a + ih (Left position of each sub-interval)</li>
  <li>n = the number of sub-intervals</li>
</ul>

[Back to Top](#numerical-integration-calculator)

##### (1) Trapezoidal Rule

The Trapezoidal Rule consists of approximating the area under a curve using a trapezoid. Although using one trapezoid will yield a large error (especially for non-linear data), the Trapezoidal Rule can be applied multiple times to obtain a more accurate result. In fact, using more trapezoids and reducing the width of each one will produce a better approximation. When increasing the number of sub-intervals, n, each sub-interval width, h, will become smaller. 

<img src="https://user-images.githubusercontent.com/54899441/102685601-ee6af000-41af-11eb-9237-004921d45376.png" width="512">
<img src="https://user-images.githubusercontent.com/54899441/102685594-e743e200-41af-11eb-9e51-9a05875f3486.png" width="512">

[Back to Top](#numerical-integration-calculator)

##### (2) Simpson's Rule

Simpson's Rule consists of approximating the area under a curve by integrating a Quadratic Lagrange Polynomial. While computing the integral in this manner may become computationally costly, the formula below will produce the same result (Simpson's Rule typically gives an exact solution for quadratic and cubic functions). 

<img src="https://user-images.githubusercontent.com/54899441/102685599-ea3ed280-41af-11eb-9a9c-5589c331a6b0.png" width="512">
<img src="https://user-images.githubusercontent.com/54899441/102685586-e0b56a80-41af-11eb-911e-ac0208a46185.png" width="512">

[Back to Top](#numerical-integration-calculator)

## Software System

##### Graphical User Interface Design

Below is an image of the GUI design of the software system. The logo on the left side is one that we 3-D modeled using Maple 2020. To the right of the logo is the Input Panel, where the user will enter h and n values and select methods for integrating and importing data. The objects on the right pertain to the Data Panel; in the Data Panel, the user can import data into the table or upload a file with a .csv or .txt extension.

<img src="https://user-images.githubusercontent.com/54899441/102685584-de531080-41af-11eb-981a-37e5550de129.png"></img>

[Back to Top](#numerical-integration-calculator)

##### Input Panel

<ol>
    <li><span style="font-weight: bold;">Import Option: </span>The user can enter data on the table or upload a .csv or .txt file.</li>
    <li><span style="font-weight: bold;">H-value: </span>A positive integer, decimal, or fraction (e.g., 9, pi/4, 2pi).</li>
    <li><span style="font-weight: bold;">N-value: </span>A positive integer that must be even if, and only if, Simpson’s Rule is chosen.</li>
    <li><span style="font-weight: bold;">Integration Option: </span>A dropdown menu for choosing either the Trapezoidal Rule or Simpson’s Rule.</li>
    <li><span style="font-weight: bold;">Area Button & Field: </span>The button will find the area and the area will be displayed in the field.</li>
    <li><span style="font-weight: bold;">Graph Button: </span>The button will find the area and the area will be displayed in the field.</li>
</ol>

<img src="https://user-images.githubusercontent.com/54899441/102686123-83231d00-41b3-11eb-99af-f28092324fa3.png" width="625"></img>

[Back to Top](#numerical-integration-calculator)

##### Data Panel

<ol>
    <li><span style="font-weight: bold;">Add: </span>Adds a row to the end of the table. If a row is selected, then a row will be added immediately under that row.</li>
    <li><span style="font-weight: bold;">Delete: </span>Deletes the last row. If a row is selected, then that row is will be deleted.</li>
    <li><span style="font-weight: bold;">Push Down: </span>Pushes all the rows down once. </li>
    <li><span style="font-weight: bold;">Clear: </span>Clears all the data entered.</li>
    <li><span style="font-weight: bold;">Browse File: </span>This button will open the user’s file explorer and ask for a file to be selected.</li>
</ol>

<img src="https://user-images.githubusercontent.com/54899441/102686122-828a8680-41b3-11eb-9922-03a5bf587029.png" width="640"></img>

[Back to Top](#numerical-integration-calculator)

## Example

In this example, the data is representative of the function f(x)=x<sup>2</sup> on the interval [1,5]. There are 32 sub-intervals and the width of each one is 4/32 = 0.125. The area calculated is 41.33333, which is actually the exact answer.

<img src="https://user-images.githubusercontent.com/54899441/102689402-ca1d0c80-41cb-11eb-972e-04970fb72f85.png">
<img src="https://user-images.githubusercontent.com/54899441/102689401-c9847600-41cb-11eb-9e93-adcf304817f1.png">

[Back to Top](#numerical-integration-calculator)
