package resources

import routing.MyHttpService
import services.{PostService, PageService}
import spray.routing.Route
import entities.{transactions, Page, PageUpdate, Post}
/**
 * Created by Migue on 11/29/15.
 */
trait PageResource extends MyHttpService{

  val pageService: PageService


  def pageRoutes: Route = pathPrefix("page") {
    pathEnd {
      post {
        entity(as[Page]) { page =>
          transactions.transactions += 1
          complete(pageService.createPage(page))
        }
      }
    } ~
      path(Segment) { id =>
        get {
          transactions.transactions += 1
          complete(pageService.getPage(id))
        } ~
          put {
            entity(as[PageUpdate]) { update =>
              transactions.transactions += 1
              complete(pageService.updatePage(id, update))
            }
          } ~
          delete {
            //transactions.transactions += 1
            complete(204, pageService.deletePage(id))
          }
      }~
        path(Segment / "feed"){ id =>
          post{
            entity(as[Post]) { post =>
              transactions.transactions += 1
              complete(pageService.createPost(post,id))
            }
          }~
          get{
            transactions.transactions += 1
            complete(pageService.getFeed(id))
          }
        }
  }
}
