import akka.actor.{ActorRef, Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
/**
  * Created by miguel on 11/30/15.
  */
object Main{


  def main(args: Array[String]){
    var numActors=100
    var str = "full"
    if(args.length < 2){
      numActors=100
      str = "full"
    }
    else{
      numActors = args(0).toInt
      str = args(1)
    }

    println(numActors)
    val system = ActorSystem("TestSystem",ConfigFactory.load(ConfigFactory.parseString("""
        akka {
           log-dead-letters = off
        }
                                                                                         """)))
    // create the master
    val master = system.actorOf(Props(new Master(numActors, str)), name = "master")
  }
}