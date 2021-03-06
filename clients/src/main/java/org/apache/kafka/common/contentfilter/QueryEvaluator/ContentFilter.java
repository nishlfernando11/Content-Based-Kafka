package org.apache.kafka.common.contentfilter.QueryEvaluator;

/**
 * Created with IntelliJ IDEA.
 * User: Nish
 * Date: 10/20/16
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */

import org.apache.kafka.common.contentfilter.ahocorasick.trie.Trie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Content IFilter function for consumer
 */
public class ContentFilter{

    /**
     * Function that builds Interest Trie for positive-only or negative-only interests
     * positive-only interests represent a WhiteList which requests messages which satisfy given interests
     * negative-only interests represent a BlackList which requests messages which does not contain given interests
     * @param contentArray List of interests specified by the consumer
     * @return interest Trie for the given set of interests
     */
    public static Trie buildInterestTrie(ArrayList<String> contentArray){
        Trie.TrieBuilder trieBuilder =  new Trie.TrieBuilder();
        trieBuilder.onlyWholeWords().caseInsensitive().patternCount(contentArray.size());
        //.removeOverlaps()

        // build the Trie for each consumer interests
        for (String aContentArray : contentArray) {
            System.out.println(aContentArray);
            trieBuilder.addKeyword(aContentArray);
        }
        return trieBuilder.build();
    }

    /**
     * Find the first match of at least one of given interests
     * WhiteList match and filter only expected interests specified by the consumer
     * @param trie Trie built for the interests
     * @param text message to be scanned
     * @return true if one match found, otherwise false
     */
    public static boolean orMatch(Trie trie, CharSequence text){
        return trie.containsMatch(text);
    }

    /**
     * Find matches for all submitted interests
     * @param trie Trie built for the interests
     * @param text message to be scanned
     * @return true if all matches found, otherwise false
     */
    public static boolean andMatch(Trie trie, CharSequence text){
        return trie.parseTextforAnd(text);
    }

    /**
     * Find messages that does not satisfy given interests
     * BlackList filter unexpected interests specified by the consumer
     * @param trie Trie built for the interests
     * @param text message to be scanned
     * @return true if given matches are not, otherwise false
     */
    public static boolean notMatch(Trie trie, CharSequence text){
        boolean isMatched = orMatch(trie,text);
        return !isMatched ;
        }

    public static void main(String[] args){
        String[] values = { "hot","hot chocolate"};
        Collection<String> contentArray = new ArrayList<String>();
        Collections.addAll(contentArray, values);

        //String text2 = "hot as hot choco want hot chocolate hello hot we are hot chocollate";
        String text2 = "hot chocolate green ";
        //hot chocolate ";

        long curtime = System.currentTimeMillis();
        long endtime;
//
        Trie trie = Trie.builder()
                //.removeOverlaps()
                .caseInsensitive()
                        // .onlyWholeWordsWhiteSpaceSeparated()
                .onlyWholeWords()
                .patternCount(values.length)
                .addKeyword(" hot ".trim())
                .addKeyword(" hot chocolate ".trim())
                .build();

        endtime = System.currentTimeMillis();
        System.out.println(endtime - curtime + " millisecs");

        //Trie mytrie = buildInterestTrie((ArrayList)contentArray);

        // or match applied
        boolean match = orMatch(trie,text2);

        if(match){
            System.out.println("matched :" + text2 );
        }
    }
}
