package services

import crypto.{KeyRing, RSA, AES}
import entities.{Post, posts, PostUpdate, pages, users}

import scala.concurrent.{Future, ExecutionContext}

/**
  * Created by miguel on 11/30/15.
  */
class PostService (implicit val executionContext: ExecutionContext){


  def createPost(post: Post): Future[Option[Object]] = Future {

//    post.id = posts.index.toString
//    posts.index += 1
//
//    if(!posts.posts.contains(post.id)) {
//
//    }
//    posts.posts.find(_.id == post.id) match {
//      case Some(q) => Some(("success" -> false)) // Conflict! id is already taken
//      case None =>
//        var f = 0
//        pages.pages.find(_.id == post.from) match{
//          case Some(page) =>
//            page.feed = page.feed :+ post.id
//            f += 1
//        }
//        users.users.find(_.id == post.from) match {
//          case Some(user) =>
//            user.feed = user.feed :+ post.id
//            f += 1
//        }
//
//        if(f != 1){
//          Some(("success" -> false))
//        }
//        else{
//          posts.posts = posts.posts :+ post
//          Some(("success" -> true))
//        }
//    }
    Some(("success" -> false))
  }

  def getPost(id: String, req_id:String): Future[Option[Object]] = Future {
    val id_int = id.toInt
    val req_id_int = req_id.toInt

    val p = posts.posts.get(id_int)
    if(p != None){
      val key = p.get.to.get(req_id)
      if(key != None || p.get.to.size == 0){
        Some(Map("message"->p.get.message, "key"->key))
      }
      else{
        Some(Map("message"->"Permission denied", "key"->""))
      }
    }else
      Some(Map("message"->"Post does not exist", "key"->""))
//    if(posts.posts.contains(id_int)) {
//      Some(posts.posts(id_int))
//    }else
//      None
  }

  def updatePost(id: String, update: PostUpdate): Future[Option[Post]] = {
    val id_int = id.toInt

    def updateEntity(post: Post): Post = {
      //val createdTime = post.createdTime
      val likes = post.likes
      val from = post.from
      val to = update.to.getOrElse(post.to)
      val message = update.message.getOrElse(post.message)
      val signature = post.signature
      Post(id_int, likes, from, to, message, signature)
    }

    if(users.users.contains(id_int)){
      posts.posts -= id_int
      posts.posts += (id_int -> updateEntity(posts.posts(id_int)))
      Future { None }
    }else
    Future { None }

//    getPost(id).flatMap { maybePost =>
//      maybePost match {
//        case None => Future { None } // No page found, nothing to update
//        case Some(post) =>
//          val updatedPost = updateEntity(post)
//          deletePost(id).flatMap { _ =>
//            createPost(updatedPost).map(_ => Some(updatedPost))
//          }
//      }
//    }
  }

  def deletePost(id: String): Future[Unit] = Future {
    val id_int = id.toInt
    if(posts.posts.contains(id_int)){
      posts.posts -= id_int
      Some("success" -> true)
    }else
    Some("success" -> false)
//    posts.posts = posts.posts.filterNot(_.id == id)
  }

}
