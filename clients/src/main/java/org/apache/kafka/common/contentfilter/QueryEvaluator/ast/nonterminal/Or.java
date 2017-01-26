package org.apache.kafka.common.contentfilter.QueryEvaluator.ast.nonterminal;

import org.apache.kafka.common.contentfilter.QueryEvaluator.ast.NonTerminal;
import org.apache.kafka.common.contentfilter.QueryEvaluator.ast.Terminal;
import org.apache.kafka.common.contentfilter.ahocorasick.trie.Trie;
import org.apache.kafka.common.contentfilter.QueryEvaluator.ContentFilter;

import java.util.Map;


public class Or extends NonTerminal {

	public String interpret() {
       return  "( " +left.interpret() + " || " + right.interpret()+ " )";
	}

    public boolean evaluate(String message, Map<Integer, Trie> list){

        boolean lt=false,rt=false;

        if((left instanceof NonTerminal) || (right instanceof NonTerminal)) {
            if((left instanceof NonTerminal) && (right instanceof NonTerminal)) {
                lt=left.evaluate(message, list);
                if(lt) return true;
                rt=right.evaluate(message, list);
            }
            else if((left instanceof Terminal) && (right instanceof NonTerminal))  {
                lt = ContentFilter.orMatch(list.get(this.toString().hashCode()), message);
                if(lt)return true;
                rt = right.evaluate(message, list);

            }
            else if((left instanceof NonTerminal)&& (right instanceof Terminal))  {
                lt=left.evaluate(message, list);
                if(lt)return true;
                rt =    ContentFilter.orMatch(list.get(this.toString().hashCode()), message);
            }
        }
        else{
            boolean b = ContentFilter.orMatch(list.get(this.toString().hashCode()), message);
            return b;
        }
        return (rt || lt);
    }


    public String toString() {
		return String.format("(%s | %s)", left, right);
	}
}
