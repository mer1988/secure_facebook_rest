package entities

import scala.collection.mutable.Map
import scala.collection.immutable

/**
  * Created by miguel on 11/29/15.
  */
case class Post(var id: Int,
                //createdTime: Timestamp,

                var likes: List[Int],
                var from: Int,
                to: immutable.Map[String,String],
                var message: String,
                val signature:String
               )

object posts{
  var posts:Map[Int,Post] = Map()
  var index = 0
}