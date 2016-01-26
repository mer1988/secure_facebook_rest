import java.security.{PrivateKey, KeyPair}

import akka.actor.Cancellable
import resources._
import services._
import spray.routing._
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps
import entities._
import crypto.RSA
/**
  * Created by miguel on 11/24/15.
  * based on http://danielasfregola.com/2015/02/23/how-to-build-a-rest-api-with-spray/
  */

class RestInterface(implicit val executionContext: ExecutionContext) extends HttpServiceActor with UserResources with PageResources with FriendListResources with PostResources{

  def receive = runRoute(routes)

  var timer: Cancellable =null
  implicit val executor =context.system.dispatcher

  val printTransactions= new Runnable{
    def run(): Unit ={
      println(posts.posts.size+","+users.users.size+","+pages.pages.size+","+transactions.transactions)
      transactions.transactions = 0
    }
  }

  val userService = new UserService
  val pageService = new PageService
  val friendListService = new FriendListService
  val postService = new PostService
  val routes: Route = userRoutes ~ pageRoutes ~ friendListRoutes ~ postRoutes

  timer =context.system.scheduler.schedule(initialDelay = Duration(0, MILLISECONDS),interval = Duration(1000, MILLISECONDS),runnable= printTransactions)(executor)
}

trait UserResources extends UserResource
trait PageResources extends PageResource
trait FriendListResources extends FriendListResource
trait PostResources extends PostResource

