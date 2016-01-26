package entities
import scala.collection.mutable.Map
/**
 * Created by Migue on 11/29/15.
 */
case class Page(var id: Int,
                owner: Int,
                description: String,
                name: String,
                website: String,
                likes: Int,
                var feed: List[Int]
               )

object pages{
  var pages:Map[Int,Page] = Map()
  var index = 0
}