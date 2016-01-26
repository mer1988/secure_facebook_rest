package services

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Future, ExecutionContext}
import entities._

/**
 * Created by Migue on 11/29/15.
 */


class PageService (implicit val executionContext: ExecutionContext){


  def createPage(page: Page): Future[Option[Object]] = Future {
    page.id = pages.index
    pages.index += 1


//    pages.pages.find(_.id == page.id) match {
//      case Some(q) => Some(Map("success" -> false, "id"->"")) // Conflict! id is already taken
//      case None =>
//        pages.pages = pages.pages :+ page
//        Some(Map("success" -> true, "id" -> page.id))
//    }

    if(!pages.pages.contains(page.id)){
      pages.pages += (page.id -> page)
      Some(Map("success" -> true, "id" -> page.id))
    }
    else
      Some(Map("success" -> false, "id"-> -1))
  }

  def getPage(id: String): Future[Option[Page]] = Future {
    val id_int = id.toInt
    if(pages.pages.contains(id_int)){
      Some(pages.pages(id_int))
    }
    else
      None
  }

  def updatePage(id: String, update: PageUpdate): Future[Option[Object]] = {
    val id_int = id.toInt

    def updateEntity(page: Page): Page = {
      val description = update.description.getOrElse(page.description)
      val name = update.name.getOrElse(page.name)
      val website = update.website.getOrElse(page.website)
      val likes = page.likes
      val feed = page.feed
      val owner = page.owner
      Page(id_int, owner, description, name, website, likes, feed)
    }

    if(pages.pages.contains(id_int)){
      pages.pages -= id_int
      pages.pages += (id_int -> updateEntity(pages.pages(id_int)))
      Future { None }
    }
    else{
      Future { None }
    }


//    getPage(id).flatMap { maybePage =>
//      maybePage match {
//        case None => Future { None } // No page found, nothing to update
//        case Some(page) =>
//          val updatedPage = updateEntity(page)
//          deletePage(id).flatMap { _ =>
//            createPage(updatedPage).map(_ => Some(updatedPage))
//          }
//      }
//    }
  }

  def deletePage(id: String): Future[Unit] = Future {
    val id_int = id.toInt
    if(pages.pages.contains(id_int)){
      pages.pages -= id_int
      Some("success" -> true)
    }else
    Some("success" -> false)
//    pages.pages = pages.pages.filterNot(_.id == id)
  }

  def createPost(post: Post, id:String): Future[Option[Object]] = Future {

//    pages.pages.find(_.id == id) match {
//      case Some(page) =>
//        if(page.id != post.from){
//          Some(("success" -> false))
//        }else{
//
//          post.id = posts.index.toString
//          posts.index += 1
//          posts.posts = posts.posts :+ post
//          page.feed = page.feed :+ post.id
//
//          for(id <- post.to){
//            if(id.startsWith("u")){
//              users.users.find(_.id == id) match{
//                case Some(to_user) =>
//                  to_user.feed = to_user.feed :+ post.id
//                case None => None
//              }
//            }
//            if(id.startsWith("p")){
//              pages.pages.find(_.id == id) match{
//                case Some(to_page) =>
//                  to_page.feed = to_page.feed :+ post.id
//                case None => None
//              }
//            }
//          }
//
//
//
//          Some(("success" -> true))
//
//        }
//
//      case None => Some(("success" -> false))
//    }

    Some("success" -> false)
  }

  def getFeed(id:String): Future[Option[Object]] = Future{
    val id_int = id.toInt

    if(pages.pages.contains(id_int)){
      var data = new ListBuffer[Post]()
      for(p <- pages.pages(id_int).feed){
        if(posts.posts.contains(p)){
          data += posts.posts(p)
        }
      }
      Some(("feed" -> data))
    }else
    Some(("feed" -> List()))
//      pages.pages.find(_.id == id) match {
//        case Some(page) =>
//          var data = new ListBuffer[Post]()
//          for(p <- page.feed){
//            posts.posts.find(_.id == p) match {
//              case Some(post) => data += post
//              case None => None
//            }
//          }
//          Some(("feed" -> data))
//        case None => Some(("feed" -> List()))
//      }
  }

}
