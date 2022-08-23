package banktransfer

import akka.actor.{Actor, ActorSystem, Props}

object BankTransfer extends App {
  val system: ActorSystem = ActorSystem("akka-system")

  val bankTransfer = system.actorOf(Props[BankTransferMain], "bankTransfer")

  bankTransfer ! "wire-transfer"

}

  class BankTransferMain extends Actor {

    val accountA = context.actorOf(Props[BankAccount], "accountA")
    val accountB = context.actorOf(Props[BankAccount], "accountB")


    def receive : Receive = {
      case "wire-transfer" => {
        accountA ! BankAccount.Deposit(100)
        context.become(transfer())
      }
    }

    def transfer() : Receive = {
      case BankAccount.Done(message) => {
        println(message)
        val wireTransfer = context.actorOf(Props[BankAccount], "wiretransfer")
        wireTransfer ! WireTransfer.Transfer(accountA,accountB,50)
        context.become(awaitTransfer())
      }

      case BankAccount.Failed(message) => {
        println(message)
        context.stop(self)
      }
    }

    def awaitTransfer() : Receive = {
      case WireTransfer.Done(message) => {
        println(message)
        context.stop(self)
      }

      case  WireTransfer.Failed(message) => {
        println(message)
        context.stop(self)
      }
    }
}
