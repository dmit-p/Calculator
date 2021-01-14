package com.pet.calc;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class MathCalculatorTests {
	MathCalculator calc = new MathCalculator();
	
    @Test
    public void expr1Test() {
     	try {
     		assertTrue(calc.evalute("1+2")==(1+2));
     	}catch(MathCalcException e) {
     		fail("Should not have thrown any exception");
     	}
    }
    
    @Test
    public void expr2Test() {
     	try {
     	assertTrue(calc.evalute("1-2")==(1-2));
     	}catch(MathCalcException e) {
     		fail("Should not have thrown any exception");
     	}
    }
    @Test
    public void expr3Test() {
    	
     	try {
     	assertTrue(calc.evalute("1*2")==(1.0*2.0));
     	}catch(MathCalcException e) {
     		fail("Should not have thrown any exception");
     	}
    }
    @Test
    public void expr4Test() {
    	try {
     	assertTrue(calc.evalute("1/2")==(1.0/2.0));
     	}catch(MathCalcException e) {
     		fail("Should not have thrown any exception");
     	}
    }
    
    @Test
    public void expr5Test() {
    	try {
     	assertTrue(calc.evalute("34.0/45.0+124.0-67.23E+01")==(34.0/45.0+124.0-67.23E+01));
     	}catch(MathCalcException e) {
     		fail("Should not have thrown any exception");
     	}
    }
    @Test
    public void expr6Test() {
    	try {
     	assertTrue(calc.evalute("10.0*-2")==(10.0*-2));
     	}catch(MathCalcException e) {
     		fail("Should not have thrown any exception");
     	}
    }
    @Test
    public void expr7Test() {
    	try {
     	assertTrue(calc.evalute("1+cube(2)+2")==(1+(2*2*2)+2));
     	}catch(MathCalcException e) {
     		fail("Should not have thrown any exception");
     	}
    }
    @Test
    public void expr8Test() {
    	try {
     	assertTrue(calc.evalute("1+cube(2)/2.0")==(1+(2*2*2)/2.0));
     	}catch(MathCalcException e) {
     		fail("Should not have thrown any exception");
     	}
    }
    @Test
    public void expr9Test() {
    	try {
     	assertTrue(calc.evalute("10/cube(2)/2.0")==(10.0/(2*2*2)/2.0));
     	}catch(MathCalcException e) {
     		fail("Should not have thrown any exception");
     	}
    }
    @Test
    public void expr10Test() {
    	try {
     	assertTrue(calc.evalute("abs(20)+abs(-10)")==(30));
     	}catch(MathCalcException e) {
     		fail("Should not have thrown any exception");
     	}
    }
    @Test
    public void expr11_Test() {
    	try {
     	assertTrue(calc.evalute("sqrt(2)")==(Math.sqrt(2)));
     	}catch(MathCalcException e) {
     		fail("Should not have thrown any exception");
     	}
    }
    @Test
    public void expr12_Test() {
    	try {
     	calc.evalute("sqrt(-2)");
     	fail("Expected MathCalcException");
     	}catch(MathCalcException e) {
     		Assert.assertEquals("Function sqrt: arg<0", e.getMessage());
     	}
    }
    @Test
    public void expr13_Test() {
    	try {
     	calc.evalute("1/0");
     	fail("Expected MathCalcException");
     	}catch(MathCalcException e) {
     		Assert.assertEquals("Division by zero", e.getMessage());
     	}
    }
    @Test
    public void expr14_Test() {
    	try {
     	calc.evalute("2+2)/2");
     	fail("Expected MathCalcException");
     	}catch(MathCalcException e) {
     		Assert.assertEquals("Unpaired brackets", e.getMessage());
     	}
    }
    @Test
    public void expr15_Test() {
    	try {
     	calc.evalute("(2+2/2");
     	fail("Expected MathCalcException");
     	}catch(MathCalcException e) {
     		Assert.assertEquals("Unpaired brackets", e.getMessage());
     	}
    }

    @Test
    public void expr16_Test() {
    	try {
     	calc.evalute("unknown(1)");
     	fail("Expected MathCalcException");
     	}catch(MathCalcException e) {
     		Assert.assertEquals("Unknown word: unknown", e.getMessage());
     	}
    }
    
    @Test
    public void expr17_Test() {
    	try {
     	assertTrue(calc.evalute("pow(2,3)")==(Math.pow(2,3)));
     	}catch(MathCalcException e) {
     		fail("Should not have thrown any exception");
     	}
    }
    
    @Test
    public void expr18_Test() {
    	try {
     	assertTrue(calc.evalute("exp(2)")==(Math.exp(2)));
     	}catch(MathCalcException e) {
     		fail("Should not have thrown any exception");
     	}
    }

    @Test
    public void expr19_Test() {
    	try {
     	assertTrue(calc.evalute("log(10)")==(Math.log(10)));
     	}catch(MathCalcException e) {
     		fail("Should not have thrown any exception");
     	}
    }

    @Test
    public void expr20_Test() {
    	try {
     	assertTrue(calc.evalute("log10(10)")==(Math.log10(10.0)));
     	}catch(MathCalcException e) {
     		fail("Should not have thrown any exception");
     	}
    }

    @Test
    public void expr21_Test() {
    	try {
     	assertTrue(calc.evalute("-(1+1)")== -2.0);
     	}catch(MathCalcException e) {
     		fail("Should not have thrown any exception");
     	}
    }

    @Test
    public void expr22_Test() {
    	try {
     	assertTrue(calc.evalute("+(1+1)")== 2.0);
     	}catch(MathCalcException e) {
     		fail("Should not have thrown any exception");
     	}
    }

}
