package resources

import org.json4s.native.Json
import routing.MyHttpService
import services.FriendListService
import spray.routing.PathMatchers.Segment
import spray.routing._
import entities.{transactions, FriendList}
/**
  * Created by miguel on 11/29/15.
  */

trait FriendListResource extends MyHttpService{

  val friendListService: FriendListService

  def friendListRoutes: Route = pathPrefix("friendList") {
    pathEnd {
        post {
          entity(as[FriendList]) { friendlist =>
            transactions.transactions += 1
            complete(friendListService.createFriendList(friendlist))
          }
        }
      }~
      path(Segment) { id =>
        get {
          transactions.transactions += 1
          complete(friendListService.getFriendList(id))
        }
      }
  }

}
