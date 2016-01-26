/**
  * Created by miguel on 11/24/15.
  * based on http://danielasfregola.com/2015/02/23/how-to-build-a-rest-api-with-spray/
  */
import akka.actor._
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import spray.can.Http

import scala.concurrent.duration._

object Main extends App {
  //val config = ConfigFactory.load()
  val host = "128.227.176.46"
  val port = 8080

  implicit val system = ActorSystem("facebook-service")
  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(10 seconds)

  val api = system.actorOf(Props(new RestInterface))

  IO(Http).ask(Http.Bind(listener = api, interface = host, port = port))
    .mapTo[Http.Event]
    .map {
      case Http.Bound(address) =>
        println(s"REST interface bound to $address")
      case Http.CommandFailed(cmd) =>
        println("REST interface could not bind to " +
          s"$host:$port, ${cmd.failureMessage}")
        system.shutdown()
    }
}
