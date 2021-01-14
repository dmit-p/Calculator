package com.pet.calc;

public class Calc{
    public static void main(String[] args) {
        //String text = "(+1.2E+10)*-1.3E+10";
        String expression = "1+1";
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
