package routing
import serializers.JsonSupport
import spray.http.HttpHeaders
import spray.routing._
import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by miguel on 11/24/15.
  * based on http://danielasfregola.com/2015/02/23/how-to-build-a-rest-api-with-spray/
  */

trait MyHttpService extends HttpService with JsonSupport {

  implicit val executionContext: ExecutionContext

  def completeWithLocationHeader[T](resourceId: Future[Option[T]], ifDefinedStatus: Int, ifEmptyStatus: Int): Route =
    onSuccess(resourceId) { maybeT =>
      maybeT match {
        case Some(t) => completeWithLocationHeader(ifDefinedStatus, t)
        case None => complete(ifEmptyStatus, None)
      }
    }

  def completeWithLocationHeader[T](status: Int, resourceId: T): Route =
    requestInstance { request =>
      val location = request.uri.copy(path = request.uri.path / resourceId.toString)
      respondWithHeader(HttpHeaders.Location(location)) {
        complete(status, None)
      }
    }

}
