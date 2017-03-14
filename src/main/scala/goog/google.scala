package goog

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

import scala.concurrent.Future

import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

import scala.io.StdIn
import scala.util.{ Failure, Success }

final case class Item(name: String, pos: Int)

// collect your json format instances into a support trait:
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val itemFormat: RootJsonFormat[Item] = jsonFormat2(Item)
}

object google extends App with JsonSupport {
  implicit val system = ActorSystem()
  import system.dispatcher
  implicit val materializer = ActorMaterializer()

  import akka.http.scaladsl.server.Directives._

  val lines = scala.io.Source.fromFile("google.json", "UTF-8").mkString

  val route: Route =
    get {
      path("query") {
        //        val r = List(Item("thing", 42), Item("thing", 42), Item("thing", 21))
        //        println("Sending: " + r.mkString(""))
        //        complete(r)

        println("GOOGLE")

        complete(lines)

      }
    }
  /*def handler(request: HttpRequest): Future[HttpResponse] = request match {
    case HttpRequest(HttpMethods.GET, Uri.Path("/abc"), _, _, _) ⇒
      Future.successful(HttpResponse(entity = "Hello world"))
  }*/

  Http().bindAndHandleAsync(Route.asyncHandler(route), "localhost", 9080)
    .onComplete {
      case Success(_) ⇒
        println("Server started on port 9080. Type ENTER to terminate.")
        StdIn.readLine()
        system.terminate()
      case Failure(e) ⇒
        println("Binding failed.")
        e.printStackTrace()
        system.terminate()
    }
}
