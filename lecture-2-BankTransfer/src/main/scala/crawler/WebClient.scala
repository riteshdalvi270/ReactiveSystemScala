package crawler

import scala.concurrent.{Future, Promise}
import com.ning.http.client.AsyncHttpClient

import java.util.concurrent.{Executors, TimeUnit}

object WebClient {

  val client = new AsyncHttpClient

  def get(url : String) : Future[String] = {
    val future = client.prepareGet(url).execute();
    val promise =  Promise[String]()
    future.addListener(new Runnable {
      override def run(): Unit = {
        val response = future.get(10, TimeUnit.SECONDS)
        if(response.getStatusCode < 400)
          promise.success(response.getResponseBodyExcerpt(131072))
        else promise.failure(new RuntimeException(response.getStatusCode.toString))
      }
    }, Executors.newCachedThreadPool())

    promise.future
  }

  def shutdown : Unit = {
    client.close()
  }
}

object HttpClient extends App {
  import scala.concurrent.ExecutionContext.Implicits.global
  WebClient get  "http://www.google.com.br" map println andThen {case _ => WebClient shutdown}
}
