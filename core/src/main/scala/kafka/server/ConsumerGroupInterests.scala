package kafka.server

import scala.collection.mutable
import org.apache.kafka.common.contentfilter.ahocorasick.trie.Trie
import org.apache.kafka.common.contentfilter.QueryEvaluator.QueryEvaluator
import org.apache.kafka.common.contentfilter.QueryEvaluator.ast.StringExpression
import scala.collection.JavaConversions._
import org.apache.kafka.common.contentfilter.FilteringContent


/**
 * Singleton object that has consumer interests based on client id
 */
object ConsumerGroupInterests {

  //hold ConsumerInterest object based on client id
  var interestContainer :Map[String, FilteringContent] = Map()

  def setGroupInterest(connectionId :String, conInterests :FilteringContent){
    interestContainer = interestContainer + (connectionId -> conInterests)
  }

  def getGroupInterests(connectionId :String): FilteringContent = {
    return interestContainer(connectionId)
  }

  def isContentFilteringEnabled(connectionId :String):Boolean ={
    if(interestContainer.contains(connectionId)){
      return true
    }else{
      return false
    }
  }

  def processConsumerInterests(connectionId:String, clientId:String,interests:String){
    println("process "+connectionId+" "+clientId+" "+interests)
    val stringExpression = QueryEvaluator.parseExpression(interests)
    val tries = QueryEvaluator.generateTries(stringExpression)
    setGroupInterest(connectionId, new FilteringContent(stringExpression,tries))

  }


}

/**
 * Class used to hold clients and their interest
 * every group had a Consumer Interest object
 */
class ConsumerInterests(clientId :String,
                        stringExpression :StringExpression,
                        consumerTrieList : Map[Integer, Trie]){

  //holds clients and their interests
//  var interests = mutable.Map[String, Map[Integer, Trie]]()

  def getClientId():String={
    return this.clientId
  }

  def getTireList():Map[Integer, Trie]={
    return this.consumerTrieList
  }
//
//  def getConsumerInterests(clientId :String):Option[String]={
//    return this.interests.get(clientId)
//  }
}