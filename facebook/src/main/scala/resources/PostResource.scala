package resources

import routing.MyHttpService
import services.PostService
import spray.routing.Route
import entities.{transactions, PostUpdate}
/**
  * Created by miguel on 11/30/15.
  */
trait PostResource extends MyHttpService{

  val postService: PostService

  def postRoutes: Route = pathPrefix("post" / Segment) {id =>
    pathEnd {

        put {
          entity(as[PostUpdate]) { update =>
            transactions.transactions += 1
            complete(postService.updatePost(id, update))
          }
        } ~
        delete {
          //transactions.transactions += 1
          complete(204, postService.deletePost(id))
        }
    } ~
      path(Segment){ req_id =>
        get {
        complete(postService.getPost(id, req_id))
        }
      }
  }
}