package banktransfer

import akka.actor.{Actor, ActorLogging, ActorRef}

object WireTransfer {

  case class Transfer(from : ActorRef, to: ActorRef, amount: BigInt)

  case class Done(message : String)

  case class Failed(message : String)
}

class WireTransfer extends Actor with ActorLogging {

  import WireTransfer._

  def receive : Receive = {

    case Transfer(from, to, amount) => {
        from ! BankAccount.Withdraw(amount)
        context.become(waitForWithdrawal(to,amount,sender))
    }
  }

  def waitForWithdrawal(to: ActorRef, amount: BigInt, sender: ActorRef) : Receive = {

    case Done(message) => {
        println(message)
        to ! BankAccount.Deposit(amount)
        context.become(waitForDeposit(sender))
    }
    case Failed(message) => {
        println(message)
        sender ! BankAccount.Failed("message")
        context.stop(self)
    }
  }

  def waitForDeposit(sender: ActorRef) : Receive = {
    case Done(message) => {
      println(message)
      sender ! Done("Successfully transfered")
      context.stop(self)
    }
  }
}
