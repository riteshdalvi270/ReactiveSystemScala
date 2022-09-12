package crawler

import akka.actor.Actor
import akka.pattern.pipe

import java.util.concurrent.Executor
import akka.actor.Status

import scala.concurrent.ExecutionContext
import org.jsoup.Jsoup

import scala.collection.JavaConverters._

class Getter(url : String, depth : Int) extends Actor {

  val client = WebClient

  client get url pipeTo(self)

  def receive : Receive = {
    case body: String =>
      for {
        link <- findLinks(body)
      } yield context.parent !
    case _ => context.stop(self)
  }

  def findLinks(body: String): Iterator[String] = {
    val document = Jsoup.parse(body, url)
    val links = document.select("a[href]")
    for {
      link <- links.iterator().asScala
    } yield link.absUrl("href")
  }
}
