import akka.actor.{ActorRef, Actor,Props}
import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class Master (numActors: Int, str:String) extends Actor{

  val actors = new Array[ActorRef](numActors)
  val rand= new Random()
  val ids:List[Int] = List()
  var c_users = 0

  val m = (numActors * 0.3).toInt
  val p = (numActors *0.2).toInt + m
  val l = (numActors *0.5).toInt + m + p

  var j = 0
  var i = 0
  //Create FB Users and Ask the to subscribe

  //actors(0)
  for(i <- 0 until numActors){

    actors(i) = context.actorOf(Props(new Worker(self, numActors, str)))
    //actors(i) ! Connect
    //if((i % 25) == 0)
      //Thread sleep 333


  }

  actors(0) ! Connect

  println("asasas")



//  context.stop(self)
//  context.system.shutdown()
  //Start working!!!



  // Messages from workers
  def receive = {
    case str: String =>
      println(str)

    case connected(id) =>



      group.ids = id :: group.ids
      if(j < m){
        sender ! Start("moderate")
      }
      if(j > m && j < p){
        sender ! Start("power")
      }
      if(j > p ){
        sender ! Start("lazy")
      }
      j += 1

      if (i < numActors){
        actors(i) ! Connect
        Thread sleep 333
      }
      i += 1

    case reconnect =>
      sender ! Connect

  }


}

object group{
  var ids:List[Int] = List()
}