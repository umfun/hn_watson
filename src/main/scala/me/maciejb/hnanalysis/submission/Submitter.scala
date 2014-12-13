package me.maciejb.hnanalysis.submission

import akka.actor.ActorSystem
import spray.client.pipelining._
import spray.http._

import scala.concurrent.{ExecutionContext, Await, Future}

class Submitter(implicit val system: ActorSystem, val ec: ExecutionContext) {

  val WatsonUri = Uri("http://watson-um-demo.mybluemix.net/")

  def submitText(text : String): Future[String] = {
    val pipeline: HttpRequest => Future[HttpResponse] =
      addHeader(
        "User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/600.1.25" +
          " (KHTML, like Gecko) Version/8.0 Safari/600.1.25") ~>
        addHeader("Accept", "text/html") ~>
        sendReceive

    val formData = FormData(Map("content" -> text))

    pipeline(Post(WatsonUri, formData)) map (_.entity.asString)
  }

}
