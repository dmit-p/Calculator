package com.pet.calc;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MathCalculator{
    private OperatorsList opList = new OperatorsList();  
    
    
	private List<Token> InfixToPostfix(List<Token> inTokens)
	{
	        OperatorsList opList = new OperatorsList();
	        FunctionList  funList = new FunctionList();
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
	        			if (i>0) 
	        			{
	        				Token prevToken = inTokens.get(i-1);
	        				if (t.getString().equals("-") && (prevToken.getString().equals("*") || prevToken.getString().equals("/")))
	        				{
	        					isUMinus=true;
	        					t.putString("u-");
	        				}
	        			}
	        			
	        				
	        			
	        			if (opStack.isEmpty()) {
	        				opStack.push(t);
	        			}else 
	       				if (opStack.peek().isLeftBracket()) {
	       					opStack.push(t);
	       				}else 
	       				{
	            			Token stackToken = opStack.peek(); 
	       					Operator stackOperator = opList.find(stackToken.getString());
	            			Operator currentOperator = opList.find(t.getString()); 
	            			
	            			
	            			if (isUMinus)
	            			{
		            			//для u
		       					if (stackOperator.getPriority() <= currentOperator.getPriority()) {
		            				opStack.push(t);
		            			}
		       					else 
		            			{
		                			while ( !opStack.isEmpty()) {
		                    			Operator stOperator = opList.find(opStack.peek().getString());
		                				if (stOperator.getPriority() <= currentOperator.getPriority() ){
		                					break;
		                				}
		                				outTokens.add(opStack.pop());
		                			}
		                			opStack.push(t);
		            			}
	            				
	            			}
	            			else
	            			{
		            			//для + - * /
		       					if (stackOperator.getPriority() < currentOperator.getPriority()) {
		            				opStack.push(t);
		            			}
		       					else 
		            			{
		                			while ( !opStack.isEmpty()) {
		                    			Operator stOperator = opList.find(opStack.peek().getString());
		                				if (stOperator.getPriority() < currentOperator.getPriority() ){
		                					break;
		                				}
		                				outTokens.add(opStack.pop());
		                			}
		                			opStack.push(t);
		            			}
	            				
	            			}
	       				}
	        			break;
	        			
	        		case Token.BRACKET:	
	        			if (t.isLeftBracket())
	        			{
	        				opStack.push(t);
	        			}
	        			else
	        			{
		        			while ( !opStack.isEmpty()  ) {
		        				
		        				if (opStack.peek().isLeftBracket()) {
		        					opStack.pop();
		        					
			        				// непонятно нужно или нет
			        				if (!opStack.empty()&&funList.isFunction(opStack.peek().getString())){
			        					outTokens.add(opStack.pop());
			        				}
			        				//
		        					break;
		        				}
		        				outTokens.add(opStack.pop());
		        			}
	        			}
	        			break;
	        		case Token.WORD:
	        				if(funList.isFunction(t.getString())) {
	        					opStack.push(t);
	        				}
	        			break;
	        		default:
				}
	        	i++;
	        }
	        // выгрузка
			while( !opStack.isEmpty())
			{
				outTokens.add(opStack.pop());
			}
			return outTokens;
		
	}
    
    public double evalute(String exp) {
    	FunctionList  funList = new FunctionList();
    	
    	Parser parser = new Parser();
        List<Token> tokens = parser.parseString(exp);
        
		System.out.println("***Execute expression***");
		tokens.stream().forEach((value) -> System.out.println(value));
		
		List<Token> postfixTokens = InfixToPostfix(tokens);
		Stack<Double>  stack  = new Stack<>();
		for(Token t : postfixTokens)
		{
			if (t.isEualType(Token.NUMBER))
			{
				stack.push(Double.valueOf(t.getString()));
			}
			else
			{
				if (t.isEualType(Token.OPERATOR)) {
					String str = t.getString();
					Operator op = opList.find(str);
					if (op == null)
					{
						System.out.println("ERROR: Operator.UNKNOWN");
					}
					else
					{
						if (op == Operator.UMINUS)
						{
							Double op2=0.0;
							Double op1=stack.pop();
							Double res=op.execute(op1,op2);
							stack.push(res);										
							
						}
						else
						{
							Double op2=stack.pop();
							Double op1=stack.pop();
							Double res=op.execute(op1,op2);
							stack.push(res);										
						}

					}

				}
				else
				{
					String str = t.getString();
					Function fun = funList.find(str);
					if (fun!=null) {
						int numArgs=fun.getNumArgs();
						if (numArgs==1) {
							Double op2=0.0;
							Double op1=stack.pop();
							Double res=fun.execute(op1,op2);
							stack.push(res);										
						}
						if (numArgs==2)	{
							Double op2=stack.pop();
							Double op1=stack.pop();
							Double res=fun.execute(op1,op2);
							stack.push(res);										
						}
							
					}
					else
					{
						//Возможно это неизвестная функция!!!
						System.out.println("ERROR");						
					}

				}
			}
		}
		Double res = stack.pop();
		return res;
	}
}





class Parser{
	private static OperatorsList operatorsList; 
	private static Pattern regexpSingeleChar;
	private static Pattern regexpNumber;
	{
		operatorsList = new OperatorsList();
		regexpSingeleChar = Pattern.compile("[\\s\\t\\x28-\\x2B\\x2D\\x2F]");
    	regexpNumber = Pattern.compile("[-+]?([0-9]*[.])?[0-9]+([eE][-+]?\\d+)?");
	}
	
	Parser(){}

	private static final List<String> bracketList = Arrays.asList("(", ")");
	   private void helperParser(String str, List<Token> tokens)
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
	                
	                if (operatorsList.isOperator(substr)) {
	                	tokens.add(new Token(substr,Token.OPERATOR));
	                }
	                else 
	                if (bracketList.contains(substr)) {
	            		tokens.add(new Token(substr,Token.BRACKET));
	            	}
	            	
	            }
	            pos=end;
	        }
	        if(str.length()>pos){
	        	tokens.add(new Token(str.substring(pos, str.length()),Token.WORD));
	        }
	    }
	}
	public List<Token> parseString(String text) {
	
    	List<Token> tokens= new LinkedList<>(); 
        Matcher m = regexpNumber.matcher(text);
        int pos = 0;
        while (m.find()) {
	        int start  = m.start();
	        int end    = m.end();
	        helperParser(text.substring(pos, start), tokens);
	        if (start>0 && (start==pos || text.charAt(start-1)==')')) {
	       		tokens.add(new Token(text.substring(start, start+1),Token.OPERATOR));
	       		tokens.add(new Token(text.substring(start+1, end),Token.NUMBER));
	        }
	        else
	        {
	        	tokens.add(new Token(text.substring(start, end),Token.NUMBER));
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
}

//left_associative binary
//right_associative_unary 


enum TypeOperator {LEFT_ASSOCIATIVE_UNARY, RIGHT_ASSOCIATIVE_UNARY, LEFT_ASSOCIATIVE_BINARY, RIGHT_ASSOCIATIVE_BINARY}

enum Operator {
/*	UNKNOWN("", 0){
		void execute() {}
	}, */
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
		double execute(double op1, double op2) {
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
	private TypeOperator type;
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
	void   execute() {}
	double execute(double op1, double op2) {
		return 0;
	}
}

class OperatorsList{
	private Map<String,Operator> map; 
	
	OperatorsList(){
		map = new HashMap<>();
		Operator[] operators= Operator.values();
		for(Operator op: operators) {
			map.put(op.getString(), op);
		}
	}
	boolean isOperator (String str){
		boolean isOperator = map.containsKey(str);
		return isOperator;
	}

	
	public Operator find(String str){
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
		double execute(double op1, double op2) {
			if (op1>=0) {
				return Math.sqrt(op1);
			}
			else
			{
				System.out.println("ERROR: arg<0");
				return 0;
			}

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
	double execute(double op1, double op2) {
		return 0;
	}
}

class FunctionList{
	private Map<String,Function> map; 
	
	FunctionList(){
		map = new HashMap<>();
		Function[] functions= Function.values();
		for(Function fn: functions) {
			map.put(fn.getString(), fn);
		}
	}
	boolean isFunction (String str){
		boolean isFunction = map.containsKey(str);
		return isFunction;
	}

	
	public Function find(String str){
		Function fn = map.get(str);
		return fn;
	}
}
