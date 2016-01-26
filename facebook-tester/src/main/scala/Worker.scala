/**
  * Created by miguel on 11/30/15.
  */


import java.security.{PublicKey, KeyPair}
import javax.crypto.SecretKey
import group.ids
import akka.actor.{ActorSystem, Cancellable, Actor, ActorRef}
import crypto.RSA
import spray.json.DefaultJsonProtocol
import spray.httpx.SprayJsonSupport._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{Await, Future}
import scala.util.Random
import scala.concurrent.duration._
import spray.client.pipelining._
import spray.http._
import crypto.AES


class Worker(climaster :ActorRef, nActors:Int, mode:String) extends Actor{
  var friends = Map[Int,PublicKey]()
  var pages = ArrayBuffer[Int]()
  val rand= new Random
  var id = 0
  val rsa:RSA = new RSA()
  val aes:AES = new AES()
  val keyPair:KeyPair = rsa.generateKey()
  val server = "128.227.176.46:8080"
  var serverPublickey:PublicKey = null
  var timer: Cancellable =null
  val nFriends = rand.nextInt(nActors -1)
  implicit val executor =context.system.dispatcher


  object UserProtocol extends DefaultJsonProtocol {
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
      case class UserResponse(
                           success:Boolean,
                           id:Int,
                           publicKey:String
                         )
    case class UserFeed(
                             feed:List[Map[String,String]]

                           )


      implicit val UserFormat = jsonFormat10(User)
      implicit val UsereResponseFormat = jsonFormat3(UserResponse)
      implicit val UserFeedFormat = jsonFormat1(UserFeed)
  }

  import UserProtocol._

  object PageProtocol extends DefaultJsonProtocol {
    case class Page(var id: Int,
                   owner:Int,
                    description: String,
                    name: String,
                    website: String,
                    likes: Int,
                    var feed: List[Int]
                   )
    case class PageResponse(
                           success:Boolean,
                           id:Int
                           )
    implicit val PageFormat = jsonFormat7(Page)
    implicit val PageResponseFormat = jsonFormat2(PageResponse)
  }
  import PageProtocol._

  object FriendListProtocol extends DefaultJsonProtocol {
    case class FriendList(var id: Int,
                          name: String,
                          owner: Int,
                          var members: List[Int],
                          var ran:String,
                          var signature:String)

    implicit val FriendListFormat = jsonFormat6(FriendList)

  }
  import FriendListProtocol._

  object FPostProtocol extends DefaultJsonProtocol {
    case class FPost(var id: Int,
                    //createdTime: Timestamp,
                    var likes: List[Int],
                    var from: Int,
                    to: Map[String, String],
                    message: String,
                    signature: String
                   )

    implicit val FPostFormat = jsonFormat6(FPost)

  }
  import FPostProtocol._

  object FriendProtocol extends DefaultJsonProtocol {
    case class Friend(var id: Int, var signature:String)
    implicit val FriendFormat = jsonFormat2(Friend)

  }
  import FriendProtocol._


  val powerUserTask= new Runnable{
    def run(){
      val num = rand.nextInt(15)

      if(num == 0){
        createPost()
      }
      if(num == 1){
        getFriends()
      }
      if(num == 2){
        getFriendLists()
      }
      if(num == 3){
        getFriendFeed()
      }
      if(num == 4){
        getFriendFeed()
      }
      if(num == 5){
        getFriendFeed()
      }
      if(num == 6){
        getPage()
      }
      if(num == 7){
        createPage()
      }
      if(num == 8){
        createPost()
      }
      if(num == 9){
        createPost()
      }
      if(num == 10){
        createPost()
      }

      if(num == 11){
        getFriend()

      }
      if(num == 12){
        addFriend()
      }
      if(num == 13){
        addFriend()
      }
      if(num == 14){
        createFriendList()
      }
    }
  }

  val moderateUserTask= new Runnable{
    def run(){
      val num = rand.nextInt(112)

      if(num == 0){
        getFriend()
      }
      if(num == 1){
        getFriends()
      }
      if(num == 2){
        getFriendLists()
      }
      if(num == 3){
        getFriendFeed()
      }
      if(num == 4){
        getFriendFeed()
      }
      if(num == 5){
        getPage()
      }
      if(num == 6){
        getPage()
      }
      if(num == 7){
        createPage()
      }
      if(num == 8){
        createPost()
      }
      if(num == 10){
        createPost()
      }
      if(num == 9){
        addFriend()
      }
      if(num == 11){
        createFriendList()
      }
    }
  }


  val lazyUserTask= new Runnable{
    def run(){
      val num = rand.nextInt(6)

      if(num == 0){
        getFriend()
      }

      if(num == 1){
        getFriends()
      }

      if(num == 2){
        getFriendLists()
      }

      if(num == 3){
        getFriendFeed()
      }

      if(num == 4){
        addFriend()
      }
      if(num == 5){
        createFriendList()
      }

    }
  }

  def startWorking(behavior:String){

    if(mode == "full"){
      if(behavior == "power" ){
        println("Start Power")
        timer=context.system.scheduler.schedule(initialDelay = Duration(0, MILLISECONDS),interval = Duration(1500, MILLISECONDS),runnable=powerUserTask )(executor)
      }
      if(behavior == "moderate"){
        println("Star Moderate")
        timer=context.system.scheduler.schedule(initialDelay = Duration(0, MILLISECONDS),interval = Duration(1000, MILLISECONDS),runnable=moderateUserTask )(executor)
      }
      if(behavior == "lazy"){
        println("Star lazy")
        timer=context.system.scheduler.schedule(initialDelay = Duration(0, MILLISECONDS),interval = Duration(500, MILLISECONDS),runnable=lazyUserTask )(executor)
      }

    }else{
      if(behavior == "power" ){
        println("Start Power")
        timer=context.system.scheduler.schedule(initialDelay = Duration(0, MILLISECONDS),interval = Duration(2000, MILLISECONDS),runnable=powerUserTask )(executor)
      }
      if(behavior == "moderate"){
        println("Star Moderate")
        timer=context.system.scheduler.schedule(initialDelay = Duration(0, MILLISECONDS),interval = Duration(1500, MILLISECONDS),runnable=moderateUserTask )(executor)
      }
      if(behavior == "lazy"){
        println("Star lazy")
        timer=context.system.scheduler.schedule(initialDelay = Duration(0, MILLISECONDS),interval = Duration(1000, MILLISECONDS),runnable=lazyUserTask )(executor)
      }

    }




  }


  def getFriend(): Unit ={
    if(friends.size > 0){
      val pipeline: HttpRequest => Future[String] = sendReceive ~> unmarshal[String]
      val friend_id = friends.keySet.toVector(rand.nextInt(friends.size))
      val f: Future[String] = pipeline(Get(s"http://"+server+s"/user/${friend_id}"))
      val result = Await.result(f, 5.seconds)
      println(s"${result}")
    }
  }

  def getFriends(): Unit ={
    if(friends.size > 0){
      val pipeline: HttpRequest => Future[String] = sendReceive ~> unmarshal[String]
      val friend_id = friends.keySet.toVector(rand.nextInt(friends.size))
      val f: Future[String] = pipeline(Get(s"http://"+server+s"/user/${friend_id}/friends"))
      val result = Await.result(f, 5.seconds)
      println(s"${result}")
    }
  }

  def getFriendLists(): Unit ={
    if(friends.size > 0){
      val pipeline: HttpRequest => Future[String] = sendReceive ~> unmarshal[String]
      val friend_id = friends.keySet.toVector(rand.nextInt(friends.size))
      val f: Future[String] = pipeline(Get(s"http://"+server+s"/user/${friend_id}/friendlists"))
      val result = Await.result(f, 5.seconds)
      println(s"${result}")
    }
  }

  def createFriendList(): Unit ={

    var friendList:List[Int] = List()
    var nf:Int = 0
    if (friends.size > 0){
      nf = rand.nextInt(1+friends.size)
    }

    val key:String = aes.keyToString(aes.obtainSecretKey());

    var i = 0
    for((f, pk) <- friends){
      if(i<nf)
        friendList =  f.toInt :: friendList
      i += 1
    }

    val newFriendList = FriendList(0, rand.alphanumeric.take(1+rand.nextInt(100)).mkString, id, friendList, key,rsa.getSignature(key,keyPair.getPrivate))
      val pipeline: HttpRequest => Future[String] = sendReceive ~> unmarshal[String]
      val f: Future[String] = pipeline(Post(s"http://"+server+s"/user/${id}/friendlists", newFriendList))
      val result = Await.result(f, 15.seconds)
      println(s" Create friendlist ${result}")

  }


  def getFriendFeed(): Unit ={
    if(friends.size > 0){
      val pipeline: HttpRequest => Future[UserFeed] = sendReceive ~> unmarshal[UserFeed]
      val friend_id = friends.keySet.toVector(rand.nextInt(friends.size))
      val f: Future[UserFeed] = pipeline(Get(s"http://"+server+s"/user/${id}/feed/${friend_id}"))
      val response = Await.result(f, 25.seconds)
      var r:List[String] = List()
      for (d <- response.feed){
        if(d("key") == ""){
          r = d("message")+"\n" :: r
        }else
          r = aes.decrypt(d("message"), aes.stringToKey(rsa.decrypt(d("key"),keyPair.getPrivate)))+"\n" :: r
      }
      println(s" feed of ${friend_id} viewd by ${id} ${r}")
    }
  }



  def getPageFeed(): Unit ={
    if(pages.length > 0){
      val pipeline: HttpRequest => Future[String] = sendReceive ~> unmarshal[String]
      val page_id = pages(rand.nextInt(pages.length))
      val f: Future[String] = pipeline(Get(s"http://"+server+s"/page/${page_id}/feed"))
      val result = Await.result(f, 5.seconds)
      println(s"${result}")
    }
  }

  def getPage(): Unit ={
    if(pages.length > 0){
      val pipeline: HttpRequest => Future[String] = sendReceive ~> unmarshal[String]
      val page_id = pages(rand.nextInt(pages.length))
      val f: Future[String] = pipeline(Get(s"http://"+server+s"/page/${page_id}/feed"))
      val result = Await.result(f, 5.seconds)
      println(s"${result}")
    }
  }

  def createPage(): Unit ={
    val newPage = Page(0, id, rand.alphanumeric.take(1+rand.nextInt(100)).mkString, rand.alphanumeric.take(1+rand.nextInt(100)).mkString, rand.alphanumeric.take(1+rand.nextInt(10)).mkString, 0, List())
    val pipeline: HttpRequest => Future[PageResponse] = sendReceive ~> unmarshal[PageResponse]
    val f: Future[PageResponse] = pipeline(Post(s"http://"+server+s"/page", newPage))
    val response = Await.result(f, 5.seconds)
    pages.append(response.id)
    println(s"Create Page! ${response}")
  }

  def connect(): Unit ={

    //println(s"User Joined! ${response}")
    try{
      val newUser = User(0, rand.alphanumeric.take(1+rand.nextInt(100)).mkString, rand.alphanumeric.take(1+rand.nextInt(100)).mkString, rand.alphanumeric.take(1+rand.nextInt(10)).mkString, rand.alphanumeric.take(1+rand.nextInt(10)).mkString, rand.alphanumeric.take(1+rand.nextInt(10)).mkString, rsa.publicKeyAsString(keyPair.getPublic),List(),List(),List())
      val pipeline: HttpRequest => Future[UserResponse] = sendReceive ~> unmarshal[UserResponse]
      val f: Future[UserResponse] = pipeline(Post(s"http://"+server+s"/user", newUser))
      val response = Await.result(f, 25.seconds)

      if(response.success){
        id = response.id
        serverPublickey = RSA.stringAsPublicKey(response.publicKey)
        println(s"User Joined! with id: ${id}")
        climaster ! connected(id)
      }
      else {
        println(s"Reconnect! ${response}")
        climaster ! reconnect
      }
    }catch{
      case e:Exception =>
        e.printStackTrace()
        println(s"Reconnect! failture")
        climaster ! reconnect
    }
  }

  def addFriend(): Unit = {
    if (friends.size < nFriends) {
      var fid = -1
      while (fid == -1) {
        val r = ids(rand.nextInt(ids.length))
        if (!friends.contains(r))
          fid = r
      }

      val newFriend = Friend(fid, rsa.getSignature(fid+"", keyPair.getPrivate))
      val pipeline: HttpRequest => Future[UserResponse] = sendReceive ~> unmarshal[UserResponse]
      val f: Future[UserResponse] = pipeline(Post(s"http://" + server + s"/user/${id}/friends", newFriend))
      val response = Await.result(f, 10.seconds)
      if(response.success){
        friends += (response.id -> RSA.stringAsPublicKey(response.publicKey))
      }
      println(s"User ${id} Added Friend! ${fid}: ${response.success}")

    }
  }
  def createPost(): Unit ={

    var toList:Map[String,String] = Map()
    var nTo:Int = 0
    if (friends.size > 0){
      nTo = rand.nextInt(1+friends.size)
    }


    val key:SecretKey = aes.obtainSecretKey();

    var i = 0
    for((f, pk) <- friends){
      if(i<nTo)
        toList +=  (f+"" -> rsa.encrypt(aes.keyToString(key),pk))
      i += 1
    }
    var msg = ""
    if (nTo == 0){
      msg = "Public: " + rand.alphanumeric.take(1+rand.nextInt(100)).mkString
    }
    else{
      msg = aes.encrypt( "Private: " + rand.alphanumeric.take(1+rand.nextInt(100)).mkString, key)
    }

    val sig = rsa.getSignature(msg, keyPair.getPrivate)

    var post = FPost(0, List(), id, toList, msg, sig)

    val pipeline: HttpRequest => Future[String] = sendReceive ~> unmarshal[String]

    val f: Future[String] = pipeline(Post(s"http://"+server+s"/user/${id}/feed", post))
    val result = Await.result(f, 10.seconds)
    if (nTo == 0){
      println(s"Create Public Post! from ${id}:  ${result}")
    }else{
      println(s"Create Private Post! from ${id} to ${toList.keySet}: ${result}")
    }


  }


  def receive = {


    case Start(behavior) =>
      startWorking(behavior)

    case str: String =>
      println(str)

    case Connect =>
      connect()

  }

}