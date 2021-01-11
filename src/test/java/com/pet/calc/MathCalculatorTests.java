package com.pet.calc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.jupiter.api.Test;

public class MathCalculatorTests {
	MathCalculator calc = new MathCalculator();

    @Test
    public void expr1Test() {
     	assertTrue(calc.evalute("1+2")==(1+2));
    }
    
    @Test
    public void expr2Test() {
     	assertTrue(calc.evalute("1-2")==(1-2));
    }
    @Test
    public void expr3Test() {
     	assertTrue(calc.evalute("1*2")==(1.0*2.0));
    }
    @Test
    public void expr4Test() {
     	assertTrue(calc.evalute("1/2")==(1.0/2.0));
    }
    
    @Test
    public void expr5Test() {
     	assertTrue(calc.evalute("34.0/45.0+124.0-67.23E+01")==(34.0/45.0+124.0-67.23E+01));
    }
    @Test
    public void expr6Test() {
     	assertTrue(calc.evalute("10.0*-2")==(10.0*-2));
    }
    @Test
    public void expr7Test() {
     	assertTrue(calc.evalute("1+cube(2)+2")==(1+(2*2)+2));
    }
    @Test
    public void expr8Test() {
     	assertTrue(calc.evalute("1+cube(2)/2.0")==(1+(2*2)/2.0));
    }
    @Test
    public void expr9Test() {
     	assertTrue(calc.evalute("10/cube(2)/2.0")==(10.0/(2*2)/2.0));
    }
    

}
