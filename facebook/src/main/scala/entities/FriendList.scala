package entities

/**
 * Created by Migue on 11/29/15.
 */
case class FriendList(var id: Int, name: String, owner: Int, var members: List[Int], var ran:String, var signature:String)

object friendLists{
  var friendLists:Map[Int,FriendList] = Map()
  var index = 0
}