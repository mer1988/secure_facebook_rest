package services

import entities._
import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}
import crypto.{RSA, KeyRing}
/**
  * Created by miguel on 11/24/15.
  * based on http://danielasfregola.com/2015/02/23/how-to-build-a-rest-api-with-spray/
  */
class UserService (implicit val executionContext: ExecutionContext){

  def createUser(user: User): Future[Option[Object]] = Future {

    user.id = users.index
    users.index += 1

    if(!users.users.contains(user.id)){
      users.users += (user.id -> user)
      Some(Map("success" -> true, "id" -> user.id, "publicKey" -> RSA.publicKeyAsString(KeyRing.RSAkeyPair.getPublic)))
    }else{
      Some(Map("success" -> false, "id" -> -1, "publicKey" -> ""))
    }

  }

  def addUserFriendList(my_id:String, friend_list: FriendList): Future[Option[Object]] = Future {




        val my_id_int = my_id.toInt
        val friend_list_id_int = friend_list.id
        if(users.users.contains(my_id_int) ){
          if(RSA.checkSignature(friend_list.signature, friend_list.ran+"", RSA.stringAsPublicKey(users.users(my_id_int).publicKey))){

            friend_list.id = friendLists.index
            friendLists.index += 1

            if(!friendLists.friendLists.contains(friend_list.id)){
              friendLists.friendLists += (friend_list.id -> friend_list)
            }


            users.users(my_id_int).friendLists = friendLists.friendLists(friend_list_id_int).id :: users.users(my_id_int).friendLists
            Some(("success" -> true))


          }else{Some(Map("success" -> false, "msg" -> "Permision denied"))}
        }
        else{Some(("success" -> false))}

  }

  def getUserFriendlists(id: String): Future[Option[Object]] = Future {
    val id_int = id.toInt
    if(users.users.contains(id_int)){
      var data = new ListBuffer[Map[String,Any]]()
      for(fl <- users.users(id_int).friendLists){
        if(friendLists.friendLists.contains(fl)){
          val flo:FriendList = friendLists.friendLists(fl)
          data += Map("id"->flo.id, "name"->flo.name, "members"->flo.members)
        }
      }
      Some(("data" -> data))
    }else
    Some(("data" -> List()))


  }

  def addFriendToFriendList(my_id: String, friend_id:String, friend_list_id:String): Future[Option[Object]] = Future {
//    val my_id_int = my_id.toInt
//    val friend_list_id_int = friend_list_id.toInt
//    val friend_id_int = friend_id.toInt
//    if(users.users.contains(my_id_int) && users.users.contains(friend_id_int)){
//      if(friendLists.friendLists.contains(friend_list_id_int)){
//        if(friendLists.friendLists(friend_list_id_int).owner == my_id){
//          friendLists.friendLists(friend_list_id_int).members = friend_id :: friendLists.friendLists(friend_list_id_int).members
//          Some(("success" -> false))
//        }else
//          Some(("success" -> false))
//      }else
//        Some(("success" -> false))
//    }else
    Some(("success" -> false))

  }

  def addUserFriend(my_id:String, friend_id: ID): Future[Option[Object]] = Future {
    //Auth missing!! friend request need to be authenticated before performed
    //dig signature check required for this operation!

      val my_id_int = my_id.toInt
      val friend_id_int = friend_id.id

      if (users.users.contains(my_id_int) && users.users.contains(friend_id_int) && my_id != friend_id_int) {
        if(RSA.checkSignature(friend_id.signature, friend_id.id+"", RSA.stringAsPublicKey(users.users(my_id_int).publicKey))) {
          val u = users.users(my_id_int)
          val f = users.users(friend_id.id)

          u.friends = friend_id_int :: u.friends

          Some(Map("success" -> true, "id" -> friend_id_int, "publicKey" -> f.publicKey))
        } else
          Some(Map("success" -> false, "id" -> "", "publicKey" -> ""))

      }else{Some(Map("success" -> false, "id" -> "", "publicKey" -> ""))}
  }

  def getUserFriends(id: String): Future[Option[Object]] = Future {
    val id_int = id.toInt
    if(users.users.contains(id_int)){
      var data = new ListBuffer[Object]()
      for(friend <- users.users(id_int).friends){
        val f  = users.users(friend)
        data += Map("id" -> f.id, "firstName"->f.firstName, "lastName"->f.lastName)
      }
      Some(("friends" -> data))
    }else
    Some(("friends" -> List()))
  }

  //Profile!
  def getUser(id: String): Future[Option[Object]] = Future {
    val id_int=id.toInt
    val u = users.users.get(id_int)
    if(u != None){
      Some(Map("about"->u.get.about, "bio"->u.get.bio, "email"->u.get.email, "firtName"->u.get.firstName, "lastName"->u.get.lastName))
    }else
      Some(None)

  }

  def updateUser(id: String, update: UserUpdate): Future[Option[User]] = {
    val id_int = id.toInt

    def updateEntity(user: User): User = {
      val bio = update.bio.getOrElse(user.bio)
      val about = update.about.getOrElse(user.about)
      val email = update.email.getOrElse(user.email)
      val firstName = update.firstName.getOrElse(user.firstName)
      val lastName = update.lastName.getOrElse(user.firstName)
      val publicKey = user.publicKey
      val friendLists = user.friendLists
      val feed = user.feed
      val friends = user.friends
      User(id_int, bio, about, email, firstName, lastName, publicKey, feed, friends, friendLists)
    }

    if(users.users.contains(id_int)){
      users.users -= id_int
      users.users += (id_int -> updateEntity(users.users(id_int)))
      Future { None }
    }else
    Future { None }

  }

  def createPost(post: Post, id:String): Future[Option[Object]] = Future {
    //Requires digitial signature to veryfi idenetity of who the poster is!

      val id_int = id.toInt

      if (users.users.contains(id_int)) {
        val u = users.users(id_int)
        if(RSA.checkSignature(post.signature, post.message+"", RSA.stringAsPublicKey(users.users(id_int).publicKey))){
          if (id_int == post.from) {
            post.id = posts.index
            posts.index += 1

            u.feed = post.id :: users.users(id_int).feed

            for ((fid, key) <- post.to) {
              if (users.users.contains(fid.toInt) && u.friends.contains(fid)) {
                val f = users.users(fid.toInt)
                f.feed = post.id :: f.feed
              }
            }
            posts.posts += (post.id -> post)
            Some("success" -> true)

          } else
            Some(("success" -> false))
        }else
          Some(Map("success" -> false, "msg" -> "Permision denied"))
      } else
        Some(("success" -> false))


  }

  def getFeed(id:String, req_id:String): Future[Option[Object]] = Future{
    val id_int = id.toInt
    val req_id_int = req_id.toInt

    if(users.users.contains(id_int)){
      var data = new ListBuffer[Object]()
      for(p <- users.users(id_int).feed){
        val post = posts.posts.get(p)
        if(post != None){
          val key = post.get.to.get(req_id)
          if(key != None || post.get.to.size == 0 ){
            if(key == None){
              data += Map("message"->post.get.message, "key"->"")
            }else
            data += Map("message"->post.get.message, "key"->key)
          }
        }
      }
      Some(("feed" -> data))
  }else
  Some(("feed" -> List()))
//
  }

  def createFriendList(friendList: FriendList, id:String): Future[Option[Object]] = Future {
    val id_int = id.toInt
    friendList.id = friendLists.index
    friendLists.index += 1

    if(!friendLists.friendLists.contains(friendList.id) && users.users.contains(id_int)){
      friendLists.friendLists += (friendList.id -> friendList)
      users.users(id_int).friendLists = friendList.id :: users.users(id_int).friendLists
      Some(("success" -> true))
    }else
    Some("success" -> false)

  }



}
