package com.pet.calc;

public class Calc{
    public static void main(String[] args) {
        //String text = "(+1.2E+10)*-1.3E+10";
        String expression = "10/(2+3)";
        MathCalculator calc = new MathCalculator();
		Double res = calc.evalute(expression);
		System.out.println("res: "+res);
    }
}