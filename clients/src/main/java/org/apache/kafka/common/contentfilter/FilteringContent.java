package org.apache.kafka.common.contentfilter;

import org.apache.kafka.common.contentfilter.QueryEvaluator.ast.StringExpression;
import org.apache.kafka.common.contentfilter.ahocorasick.trie.Trie;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Irosha
 * Date: 12/31/16
 * Time: 4:34 PM
 */
public class FilteringContent {

    StringExpression expression;

    Map<Integer,Trie> trieList;

    public FilteringContent(StringExpression stringExpression, Map<Integer,Trie> list){
        expression = stringExpression;
        trieList = list;
    }

    public Map<Integer, Trie> getTrieList() {
        return trieList;
    }

    public void setTrieList(Map<Integer, Trie> trieList) {
        this.trieList = trieList;
    }

    public StringExpression getExpression() {
        return expression;
    }

    public void setExpression(StringExpression expression) {
        this.expression = expression;
    }


}
