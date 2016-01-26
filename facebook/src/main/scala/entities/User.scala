package entities

import scala.collection.mutable.Map
//
// import scala.collection.concurrent.TrieMap
/**
  * Created by miguel on 11/24/15.
  */
case class User(var id: Int,
                about: String,
                bio: String,
                //birthday: Date,
                email: String,
                firstName: String,
                lastName: String,
                publicKey: String,
                var feed: List[Int],
                var friends: List[Int],
                var friendLists: List[Int]
               )

object users{
  var users:Map[Int,User] = Map()
  var index = 0
}