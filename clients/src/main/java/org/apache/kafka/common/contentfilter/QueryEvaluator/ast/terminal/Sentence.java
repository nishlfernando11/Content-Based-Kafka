package org.apache.kafka.common.contentfilter.QueryEvaluator.ast.terminal;

import org.apache.kafka.common.contentfilter.QueryEvaluator.ast.Terminal;
import org.apache.kafka.common.contentfilter.ahocorasick.trie.Trie;
import org.apache.kafka.common.contentfilter.QueryEvaluator.ContentFilter;

import java.util.ArrayList;
import java.util.Map;


/**
 * Created with IntelliJ IDEA.
 * User: Nish
 * Date: 12/8/16
 * Time: 7:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class Sentence extends Terminal {
    public Sentence(String sentence) {
        super(sentence);
    }

    public Map<Integer,Trie> generateTrieList(Map<Integer, Trie> list){
        ArrayList<String> interests = new ArrayList<String>(1);
        interests.add(this.sentence);
        Trie trie = ContentFilter.buildInterestTrie(interests);
        int key = this.toString().hashCode();
        list.put(key,trie);
        return list;
    }

    public String interpret() {
        return sentence;
    }

    public boolean evaluate(String msg, Map<Integer, Trie> list){
        return ContentFilter.orMatch(list.get(this.toString().hashCode()), msg);
    }
}
