package resources

import routing.MyHttpService
import entities._
import services.UserService
import spray.routing._

/**
  * Created by miguel on 11/24/15.
  * based on http://danielasfregola.com/2015/02/23/how-to-build-a-rest-api-with-spray/
  */
trait UserResource extends MyHttpService {

  val userService: UserService

  def userRoutes: Route = pathPrefix("user") {
    pathEnd {
      post {
        entity(as[User]) { user =>
            user.feed = List()
            user.friendLists = List()
            user.friends = List()
            transactions.transactions += 1
            complete(userService.createUser(user))
        }
      }
    } ~
      path(Segment) { id =>
        get {
          transactions.transactions += 1
          complete(userService.getUser(id))
        } ~
        put {
          entity(as[UserUpdate]) { update =>
            transactions.transactions += 1
            complete(userService.updateUser(id, update))
          }
        }

      } ~
        path(Segment/ "friendlists"){ id =>
          get{
            transactions.transactions += 1
            complete{userService.getUserFriendlists(id)}
          }~
          put{
            entity(as[FriendList]) { friend_list_id =>
              transactions.transactions += 1
              complete(userService.addUserFriendList(id, friend_list_id))
            }
          }~
          post{
            entity(as[FriendList]) { friend_list =>
              transactions.transactions += 1
              complete(userService.createFriendList(friend_list, id))
            }
          }

        }~
          path(Segment/ "friends"){ id =>
            get{
              transactions.transactions += 1
              complete{userService.getUserFriends(id)}
            }~
            post{
              entity(as[ID]) { friend_id =>
                transactions.transactions += 1
                complete(userService.addUserFriend(id, friend_id))
              }
            }
          }~
            path(Segment / "feed"){ id =>
              post {
                entity(as[Post]) { post =>
                  post.likes = List()
                  transactions.transactions += 1
                  complete(userService.createPost(post, id))
                }
              }
            }~
              path(Segment / "feed" / Segment) { (req_id, id) =>
                get {
                  transactions.transactions += 1
                  complete(userService.getFeed(id, req_id))
                }
              }
  }
}
