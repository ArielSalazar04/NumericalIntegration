package main.java;

public class SimpsonsRuleException extends Exception{
    public SimpsonsRuleException(){
        super("N must be even when applying Simpson's Rule.");
    }
}

