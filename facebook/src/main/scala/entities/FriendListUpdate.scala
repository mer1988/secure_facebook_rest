package entities
import scala.collection.mutable.Map
/**
  * Created by miguel on 11/29/15.
  */
case class FriendListUpdate(name: Option[String],
                            owner: Option[String],
                            members: Option[List[Int]]
                           )