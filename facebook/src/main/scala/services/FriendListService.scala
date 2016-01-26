package services

import scala.concurrent.{Future, ExecutionContext}
import entities.{FriendList, User, friendLists}
/**
 * Created by Migue on 11/29/15.
 */
class FriendListService (implicit val executionContext: ExecutionContext){

  def createFriendList(friendList: FriendList): Future[Option[Object]] = Future {
    friendList.id = friendLists.index
    friendLists.index += 1

    if(!friendLists.friendLists.contains(friendList.id)){
      friendLists.friendLists += (friendList.id -> friendList)
      Some(Map("success" -> true, "id"->friendList.id))
    }else
    Some(("success" -> false, "id"->""))

//    friendLists.friendLists.find(_.id == friendList.id) match {
//      case Some(friendList) => Some(("success" -> false)) // Conflict! id is already taken
//      case None =>
//        friendLists.friendLists = friendLists.friendLists :+ friendList
//        Some(("success" -> true))
//    }
  }

  def getFriendList(id: String): Future[Option[FriendList]] = Future {
//    friendLists.friendLists.find(_.id == id)
    val id_int = id.toInt
    if(friendLists.friendLists.contains(id_int))
      Some(friendLists.friendLists(id_int))
    else
      None
  }


}