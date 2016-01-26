/**
  * Created by miguel on 11/30/15.
  */
import akka.actor.{ActorRef, Actor}
import scala.collection.mutable.ArrayBuffer


case class Start(behavior:String)
case class Friends(friends:ArrayBuffer[Int])
case class connected(id:Int)
case object Connect
case object reconnect