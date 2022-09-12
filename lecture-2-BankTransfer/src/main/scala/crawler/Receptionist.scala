package crawler

import akka.actor.{Actor, ActorRef, Props}
import crawler.Receptionist.{Failed, Get, Job, Result}

object Receptionist {
  case class Get(url : String)
  case class Job(sender : ActorRef, url : String)
  case class Failed(url : String, message : String)
  case class Result(url : String, links : Set[String])
}

class Receptionist extends Actor  {
  var reqNo = 0

  def receive = waiting
  
  def waiting : Receive = {
    case Get(url) =>
      context.become(runNext(Vector(Job(sender, url))))
  }

  def running(queue : Vector[Job]) : Receive = {

    case Controller.Result(links) =>
      queue.head.sender ! Result(queue.head.url, links)
      context.stop(queue.head.sender)
      context.become(runNext(queue))
    case Get(url)  =>
      context.become(enqueueJob(queue, Job(sender, url)))
  }

  def runNext(queue : Vector[Job]) : Receive = {
    reqNo += 1
    if(queue.isEmpty) waiting
    else {
      val controller = context.actorOf(Props[Controller],s"c$reqNo")
      controller ! Controller.Check(queue.head.url, 2)
      running(queue)
    }
  }

  def enqueueJob(queue: Vector[Job], job: Job): Receive = {
    if (queue.size > 3) {
      sender ! Failed(job.url, "queue overflow")
      running(queue)
    }else running(queue :+ job)
  }

}
