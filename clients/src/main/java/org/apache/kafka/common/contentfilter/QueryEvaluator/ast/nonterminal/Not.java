package org.apache.kafka.common.contentfilter.QueryEvaluator.ast.nonterminal;

import org.apache.kafka.common.contentfilter.QueryEvaluator.ast.NonTerminal;
import org.apache.kafka.common.contentfilter.QueryEvaluator.ast.StringExpression;
import org.apache.kafka.common.contentfilter.ahocorasick.trie.Trie;
import org.apache.kafka.common.contentfilter.QueryEvaluator.ContentFilter;

import java.util.ArrayList;
import java.util.Map;


public class Not extends NonTerminal {
	public void setChild(StringExpression child) {
		setLeft(child);
	}

	public void setRight(StringExpression right){
		throw new UnsupportedOperationException();
	}

	public String interpret() {
		return  "!" +left.interpret();
	}


    public boolean evaluate(String message, Map<Integer, Trie> list){

        if(left instanceof NonTerminal) {
            boolean b = left.evaluate(message, list);
            return !b;
        }
        else {
            boolean b = ContentFilter.notMatch(list.get(this.toString().hashCode()), message);
            return b;
        }
    }

    public Map<Integer,Trie> generateTrieList(Map<Integer, Trie> list){

        if(left instanceof NonTerminal) {
            left.generateTrieList(list);
        }
        else {
            ArrayList<String> interests = new ArrayList<String>(1);
            interests.add(left.interpret());
            Trie trie = ContentFilter.buildInterestTrie(interests);
            list.put(this.toString().hashCode(),trie);
        }
        return list;
    }


    public String toString() {
		return String.format("!%s", left);
	}
}
