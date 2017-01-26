package org.apache.kafka.common.contentfilter.QueryEvaluator;

import org.apache.kafka.common.contentfilter.QueryEvaluator.ast.StringExpression;
import org.apache.kafka.common.contentfilter.QueryEvaluator.lexer.Lexer;
import org.apache.kafka.common.contentfilter.QueryEvaluator.parser.RecursiveDescentParser;
import org.apache.kafka.common.contentfilter.ahocorasick.trie.Trie;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class QueryEvaluator {
    // interest Trie List
    public Map<Integer,Trie> TList;

    public QueryEvaluator(){
        TList = new HashMap<Integer,Trie>();
    }

    public static StringExpression parseExpression(String expression){
        Lexer lexer = new Lexer(new ByteArrayInputStream(expression.getBytes()));
        RecursiveDescentParser parser = new RecursiveDescentParser(lexer);
        StringExpression ast = parser.build();
        return ast;
    }

    public static Map<Integer,Trie> generateTries(StringExpression ast){
        final QueryEvaluator qe = new QueryEvaluator();
        qe.TList = ast.generateTrieList(qe.TList);
        return qe.TList;
    }

    public static boolean filter(String text, StringExpression ast, Map<Integer,Trie> trieList ){
//        System.out.println(String.format("AST: %s", ast));
//        System.out.println(String.format("RES: %s", ast.interpret()));
//        String sent  = "america is good with china";
        boolean b = ast.evaluate(text, trieList);
//        System.out.println(String.format("RES: %s : %b",text,b ));
        return b;
    }

	public static void main(String[] args) throws InterruptedException {

        //Pattern pattern = Pattern.compile("[^A-Za-z0-9]*\\w.*", Pattern.CASE_INSENSITIVE);
        //Matcher matcher = pattern.matcher(line);

        Scanner sc = new Scanner((System.in));
		String expression = "";
		if(args.length > 0 && args[0].equals("-f")) {
			while(sc.hasNextLine()) expression += sc.nextLine(); System.out.println(expression);
		} else {
			System.out.println("Insert an expression:");
			expression = sc.nextLine();
		}

		Lexer lexer = new Lexer(new ByteArrayInputStream(expression.getBytes()));
		RecursiveDescentParser parser = new RecursiveDescentParser(lexer);
        StringExpression ast = parser.build();

        final QueryEvaluator qa = new QueryEvaluator();
        qa.TList = ast.generateTrieList(qa.TList);

        System.out.println(String.format("AST: %s", ast));
//		System.out.println(String.format("RES: %s", ast.interpret()));
        String sent  = "america1 is good with china 11/01/2011 HIV/AIDS 'Mein Kampf'";
        boolean b = ast.evaluate(sent, qa.TList);

        System.out.println(String.format("RES: %s : %b", sent, b));
    }
}
