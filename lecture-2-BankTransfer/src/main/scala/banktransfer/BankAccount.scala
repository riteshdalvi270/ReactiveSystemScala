package banktransfer

import akka.actor.Actor

object BankAccount {

  case class Deposit(amount: BigInt) {
    require(amount > 0)
  }

  case class Withdraw(amount: BigInt) {
    require(amount > 0)
  }

  case class Done(message : String)
  case class Failed(message : String)
}

class BankAccount extends Actor {
  import BankAccount._

  var balance : BigInt = 0

  def receive : Receive = {

    case Deposit(amount) => {
      balance+=amount
      sender ! Done("balance updated")
    }

    case Withdraw(amount : BigInt) => {
      balance-=amount
      sender ! Done("balance withdrawn")
    }

    case _  => {
      sender ! Failed(" Operation not supported")
    }
  }
}
