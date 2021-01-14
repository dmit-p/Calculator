package com.pet.calc;
/*
 * Allowed operators: '+', '-', '*', '/'
 * 
 * Allowed the format number is defined by a regular expression
 * "[-+]?([0-9]*[.])?[0-9]+([eE][-+]?\\d+)?"
 * 
 * Allowed functions cube(x), abs(x), sqrtx(), sin(x), cos(x), pow(x,y), exp(x), log(x), log10(x)
 * 
 * */
public class Calc{
    public static void main(String[] args) {
        String expression = "-(-1.3E+1+15)-pow(2,3)";
        MathCalculator calc = new MathCalculator();
        double res=0;
        try {
		res = calc.evalute(expression);
        } catch (MathCalcException e) {
        	System.out.println(e.getMessage());
        }
		System.out.println("res: "+res);
    }
}
