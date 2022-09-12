package crawler

import akka.actor
import akka.actor.{Actor, Props, ReceiveTimeout, Terminated}
import crawler.Controller.{Check, Result}

import scala.concurrent.duration.{Duration, TimeUnit}

object Controller {
  case class Check(url : String, depth : Int)
  case class Result(cache : Set[String])
}
class Controller extends Actor {

  def getterProp(url :String, depth : Int) = Props (new Getter(url, depth))

  context.setReceiveTimeout(Duration(10, "second"))

  var cache = Set.empty[String]

  def receive : Receive = {

    case Check(url, depth) =>
      if (!cache(url) && depth > 0)
          context.watch(context.actorOf(getterProp(url, depth)))
      cache += url
    case Terminated(_) =>
      if(context.children.isEmpty)
          context.parent ! Result(cache)
    case ReceiveTimeout =>
      context.children foreach context.stop
  }
}
