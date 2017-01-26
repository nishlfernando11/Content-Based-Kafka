package org.apache.kafka.common.contentfilter.QueryEvaluator.parser;

import org.apache.kafka.common.contentfilter.QueryEvaluator.ast.*;
import org.apache.kafka.common.contentfilter.QueryEvaluator.ast.nonterminal.And;
import org.apache.kafka.common.contentfilter.QueryEvaluator.ast.nonterminal.Not;
import org.apache.kafka.common.contentfilter.QueryEvaluator.ast.nonterminal.Or;
import org.apache.kafka.common.contentfilter.QueryEvaluator.ast.terminal.Sentence;
import org.apache.kafka.common.contentfilter.QueryEvaluator.lexer.Lexer;
import org.apache.kafka.common.contentfilter.ahocorasick.trie.Trie;

import java.util.ArrayList;

public class RecursiveDescentParser {
	private Lexer lexer;
	private int symbol;
    private StringExpression root;
    public ArrayList<Trie> interestTrieList;


    public RecursiveDescentParser(Lexer lexer) {
		this.lexer = lexer;
        this.interestTrieList = new ArrayList<Trie>();
	}

    public StringExpression build() {
        expression();
        return root;
    }

	private void expression() {
		term();
		while (symbol == Lexer.OR) {
			Or or = new Or();
			or.setLeft(root);
			term();
			or.setRight(root);
			root = or;
		}
	}

	private void term() {
		factor();
		while (symbol == Lexer.AND) {
			And and = new And();
			and.setLeft(root);
			factor();
			and.setRight(root);
			root = and;
		}
	}

	private void factor() {
		symbol = lexer.nextSymbol();
        if(symbol == Lexer.KEYWORD){
            root = new Sentence(lexer.getKeyword());
            symbol = lexer.nextSymbol();
        }
        else if (symbol == Lexer.NOT) {
			Not not = new Not();
			factor();
			not.setChild(root);
			root = not;
		} else if (symbol == Lexer.LEFT) {
			expression();
			symbol = lexer.nextSymbol(); // we don't care about ')'
		} else {
			throw new RuntimeException("Expression Malformed");
		}
	}
}
