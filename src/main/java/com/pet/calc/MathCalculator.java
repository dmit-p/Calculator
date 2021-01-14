package com.pet.calc;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MathCalculator{
	private boolean debug;
	MathCalculator()
	{
		debug = false;
	}
	
	public void setDebug(boolean debug) {
		this.debug=debug;
	}
	
	private List<Token> InfixToPostfix(List<Token> inTokens) throws MathCalcException
	{
	        Stack<Token>  opStack  = new Stack<>();
	        List<Token> outTokens = new ArrayList<>();
	        int i =0;
	        for(Token t : inTokens) {
	        	int type = t.getType();
	        	switch (type)
				{
					case Token.NUMBER: 
						outTokens.add(t);
						break;
	        		case Token.OPERATOR: 
	        			boolean isUMinus = false;
	        			if (i==0) {
	        				if(t.strEq("-")) {
	        					isUMinus=true;
	        					t.putString(Operator.UMINUS.getString());
	        				}
	        				if(t.strEq("+")) {
	        					break;
	        				}
	        			} else { 
	        				Token prevToken = inTokens.get(i-1);
	        				if (t.strEq("-") && (prevToken.strEq("*") || prevToken.strEq("/"))) {
	        					isUMinus=true;
	        					t.putString(Operator.UMINUS.getString());
	        				}
	        			}
	        			
	        			if (opStack.isEmpty()) {
	        				opStack.push(t);
	        			}else 
	       				if (opStack.peek().isLeftBracket()) {
	       					opStack.push(t);
	       				}else 
	       				{
	            			Operator currentOperator = OperatorsList.find(t.getString());
	            			if (isUMinus)
	            			{
		            			//for operator unary minus
	                			while ( !opStack.isEmpty()) {
	                    			Operator stOperator = OperatorsList.find(opStack.peek().getString());
	                				if (stOperator.getPriority() <= currentOperator.getPriority() ){
	                					break;
	                				}
	                				outTokens.add(opStack.pop());
	                			}
	            			}
	            			else
	            			{
		            			//for operators: '+', '-', '*', '/'
	                			while ( !opStack.isEmpty()) {
	                    			Operator stOperator = OperatorsList.find(opStack.peek().getString());
	                				if (stOperator.getPriority() < currentOperator.getPriority() ){
	                					break;
	                				}
	                				outTokens.add(opStack.pop());
	                			}
	            			}
	            			opStack.push(t);
	       				}
	        			break;
	        			
	        		case Token.BRACKET:	
	        			if (t.isLeftBracket()) {
	        				opStack.push(t);
	        			}
	        			else {
		        			boolean isLeftBracket = false;
	        				while ( !opStack.isEmpty()  ) {
		        				if (opStack.peek().isLeftBracket()) {
		        					isLeftBracket=true;
		        					opStack.pop();
		        					
			        				//If word equals name function
			        				if (!opStack.empty()&&FunctionsList.isFunction(opStack.peek().getString())){
			        					outTokens.add(opStack.pop());
			        				}
		        					break;
		        				}
		        				outTokens.add(opStack.pop());
		        			}
	        				if (isLeftBracket == false) {
	        					throw new MathCalcException("Unpaired brackets");
	        				}
	        			}
	        			break;
	        		case Token.COMMA:
	        			while ( !opStack.isEmpty()  ) {
	        				if (!opStack.peek().isLeftBracket()) {
	        					outTokens.add(opStack.pop());
	        				}
	        			}
	        			break;
	        		case Token.WORD:
	        				if(FunctionsList.isFunction(t.getString())) {
	        					opStack.push(t);
	        					break;
	        				}
        					throw new MathCalcException("Unknown word: "+t.getString());
	        			
	        		default:
	        				throw new MathCalcException("Unknown error");
				}
	        	i++;
	        }
	        
	        // unload everything from the stack
	        while( !opStack.isEmpty()) {
				Token t = opStack.pop();
				if (!t.isOperator())
				{
					throw new MathCalcException("Unpaired brackets");
				}
	        	outTokens.add(t);
			}
			return outTokens;
	}
    
    public double evalute(String exp) throws MathCalcException{
        List<Token> tokens = Parser.parseString(exp);
        // for debug print tokens
        if (debug) {
        	System.out.println("Function evalute list tokens:");
        	tokens.stream().forEach((value) -> System.out.println(value));
        	System.out.println("list tokens end");
        }
		List<Token> postfixTokens = InfixToPostfix(tokens);
		Stack<Double>  stack  = new Stack<>();
		for(Token t : postfixTokens)
		{
			if (t.isEualType(Token.NUMBER)) {
				stack.push(Double.valueOf(t.getString()));
			}
			else {
				Double op1=0.0;
				Double op2=0.0;
				String str = t.getString();
				if (t.isEualType(Token.OPERATOR)) {
					Operator op = OperatorsList.find(str);
					if (op == null) {
						throw new MathCalcException("Unknown error");
					}
					if (op != Operator.UMINUS){
						op2=stack.pop();
					}
					op1=stack.pop();
					Double res=op.execute(op1,op2);
					stack.push(res);										
				}
				else {
					Function fun = FunctionsList.find(str);
					if (fun==null) {
						throw new MathCalcException("Unknown error");
					}
					int numArgs=fun.getNumArgs();
					if (numArgs==2)	{
						op2=stack.pop();
					}
					op1=stack.pop();
					Double res=fun.execute(op1,op2);
					stack.push(res);										
				}
			}
		}
		Double res = stack.pop();
		return res;
	}
}


class Parser{
	private static final Pattern regexpSingeleChar;
	private static final Pattern regexpNumber;
	private static final List<String> bracketList;
	static {
		//to search for single-character separators (including operators and brackets)
		//add when adding operators
		regexpSingeleChar = Pattern.compile("[\\s\\t\\x28-\\x2B\\x2C\\x2D\\x2F]");
    	//to search number
		regexpNumber = Pattern.compile("[-+]?([0-9]*[.])?[0-9]+([eE][-+]?\\d+)?");
		bracketList = Arrays.asList("(", ")");
	}

	
	   private static void helperParser(String str, List<Token> tokens)
	   {
	        if (str.length()>0){ 
	        Matcher m = regexpSingeleChar.matcher(str);
	        int pos = 0;
	        while (m.find()) {
	            int start  = m.start();
	            int end    = m.end();
	            if(start>pos){
	                tokens.add(new Token(str.substring(pos, start), Token.WORD));
	            }
	            if(end>start){
	                String substr = str.substring(start, end);
	                if (OperatorsList.isOperator(substr)) {
	                	tokens.add(new Token(substr,Token.OPERATOR));
	                }
	                else 
	                if (bracketList.contains(substr)) {
	            		tokens.add(new Token(substr,Token.BRACKET));
	            	}
	                // else if  \s or \t then skip char 
	            }
	            pos=end;
	        }
	        if(str.length()>pos){
	        	tokens.add(new Token(str.substring(pos, str.length()),Token.WORD));
	        }
	    }
	}
	public static List<Token> parseString(String text) {
	
    	List<Token> tokens= new LinkedList<>(); 
        Matcher m = regexpNumber.matcher(text);
        int pos = 0;
        while (m.find()) {
	        int start  = m.start();
	        int end    = m.end();
	        
        	if ((start>0) && (text.substring(start-1, start+1).matches("\\w[0-9]"))) {
        		// not a number found
        		helperParser(text.substring(pos, end), tokens);
        	}
        	else {
    	        helperParser(text.substring(pos, start), tokens);
    	        if (start>0 && (start==pos || text.charAt(start-1)==')')) {
    	        	// found something and a number
    	       		tokens.add(new Token(text.substring(start, start+1),Token.OPERATOR));
    	       		tokens.add(new Token(text.substring(start+1, end),Token.NUMBER));
    	        }
    	        else
    	        {
    	        	// found a number
    	        	tokens.add(new Token(text.substring(start, end),Token.NUMBER));
    	        }
        	}
	        pos=end;
        }
        helperParser(text.substring(pos, text.length()),  tokens);
        return tokens;
	}
}

/* Контайнер для иинимальной синтаксичиской 
 * единицы получаемой при разборе строки */
class Token{
	static final int UNKNOWN  = 0;
	static final int NUMBER   = 1;
	static final int WORD     = 2;
	static final int OPERATOR = 3;
	static final int BRACKET  = 4;
	static final int COMMA    = 5;
	
	private String str;
	private int type;
	
	Token(String str){
		this.str = str;
		type = Token.UNKNOWN;
	}

	Token(String str, int type){
		this.str = str;
		this.type = type;
	}

	public String toString() {
		String strType=null;
		switch(type) {
		case NUMBER:
			strType="NUMBER";
			break;
		case WORD:
			strType="WORD";
			break;
		case OPERATOR:
			strType="OPERATOR";
			break;
		case BRACKET:
			strType="BRACKET";
			break;
			
		default:
			strType="UNKNOWN";
		}
		return "Token type: "+strType+" \""+str+"\"";
	}
	
	public String getString() {
		return str;
	}
	public void putString(String str) {
		this.str=str;
	}	
	
	public int getType() {
		return type;
	}
	
	public boolean isEualType(int type) {
			return this.type==type;
	}
	public boolean isNumber() {
		return this.type==NUMBER;
	}
	public boolean isWord() {
		return this.type==WORD;
	}
	public boolean isOperator() {
		return this.type==OPERATOR;
	}
	public boolean isBracket() {
		return this.type==BRACKET;
	}
	public boolean isLeftBracket() {
		return this.type==BRACKET && str.equals("(");
	}
	public boolean isRightBracket() {
		return this.type==BRACKET && str.equals(")");
	}
	public boolean strEq(String str) {
		return this.str.equals(str);
	}
}

// for expand the list of operators
enum TypeOperator {LEFT_ASSOCIATIVE_UNARY, RIGHT_ASSOCIATIVE_UNARY, LEFT_ASSOCIATIVE_BINARY, RIGHT_ASSOCIATIVE_BINARY}

enum Operator {
	PLUS("+",1, TypeOperator.LEFT_ASSOCIATIVE_BINARY){
		double execute(double op1, double op2) {
			return op1 + op2;
		}
	}, 
	MINUS("-", 1, TypeOperator.LEFT_ASSOCIATIVE_BINARY){
		double execute(double op1, double op2) {
			return op1 - op2;
		}
	}, 
	MUL("*", 2, TypeOperator.LEFT_ASSOCIATIVE_BINARY){
		double execute(double op1, double op2) {
			return op1 * op2;
		}
	}
	, 
	DIV("/",2, TypeOperator.LEFT_ASSOCIATIVE_BINARY){
		double execute(double op1, double op2) throws MathCalcException {
			if (op2==0) {
				throw new MathCalcException("Division by zero");
			}
			return op1 / op2;
		}
	}
	, 
	UMINUS("u-",3, TypeOperator.RIGHT_ASSOCIATIVE_UNARY){
		double execute(double op1, double op2) {
			return op1 * (-1);
		}		
	};
	private int priority;
	private String str;
	private TypeOperator type;// for expand the list of operators
	Operator(String str, int priority, TypeOperator type){
		this.str = str;
		this.priority = priority;
		this.type=type;
	}
	int getPriority() {
		return this.priority;
	}
	String getString() {
		return str;
	}
	TypeOperator getType() {
		return type;
	}

	void   execute() throws MathCalcException {}
	double execute(double op1, double op2) throws MathCalcException{
		return 0;
	}
}

class OperatorsList{
	private static Map<String,Operator> map; 
	static {
		map = new HashMap<>();
		Operator[] operators= Operator.values();
		for(Operator op: operators) {
			map.put(op.getString(), op);
		}
	}
	
	public static boolean  isOperator (String str){
		boolean isOperator = map.containsKey(str);
		return isOperator;
	}
	
	public static Operator find(String str){
		Operator op = map.get(str);
		return op;
	}
}

enum Function {
	CUBE("cube",1){
		double execute(double op1, double op2) {
			return op1*op1*op1;
		}
	},
	ABS("abs",1){
		double execute(double op1, double op2) {
			if (op1>=0) {
				return op1;
			}
			else {
				return -1.0*op1;
			}
		}
	},
	SQRT("sqrt",1){
		double execute(double op1, double op2) throws MathCalcException{
			if (op1>=0) {
				return Math.sqrt(op1);
			}
			else {
				throw new MathCalcException("Function sqrt: arg<0");
			}
		}
	},
	SIN("sin",1){
		double execute(double op1, double op2) {
			return Math.sin(op1);
		}
	},
	COS("cos",1){
		double execute(double op1, double op2) {
			return Math.cos(op1);
		}
	},
	POW("pow",2){
		double execute(double op1, double op2) {
			return Math.pow(op1,op2);	
		}
	},
	EXP("exp",1){
		double execute(double op1, double op2) {
			return Math.exp(op1);	
		}
	},
	LOG("log",1){
		double execute(double op1, double op2) {
			return Math.log(op1);	
		}
	},
	LOG10("log10",1){
		double execute(double op1, double op2) {
			return Math.log10(op1);	
		}
	}
	;
	
	private int numArgs;
	private String str;
	
	Function(String str, int numArgs){
		this.str = str;
		this.numArgs = numArgs;
	}
	
	String getString() {
		return str;
	}
	
	int getNumArgs()
	{
		return numArgs;
	}
	
	void   execute() {}
	
	double execute(double op1, double op2) throws MathCalcException {
		return 0;
	}
}

class FunctionsList{
	private static Map<String,Function> map; 
	static {
		map = new HashMap<>();
		Function[] functions= Function.values();
		for(Function fn: functions) {
			map.put(fn.getString(), fn);
		}
	}
	
	public static boolean isFunction (String str){
		boolean isFunction = map.containsKey(str);
		return isFunction;
	}
	
	public static Function find(String str){
		Function fn = map.get(str);
		return fn;
	}
}
