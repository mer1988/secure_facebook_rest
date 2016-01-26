package entities

import java.sql.Timestamp
import scala.collection.mutable.Map
import scala.collection.immutable
/**
  * Created by miguel on 11/29/15.
  */
case class PostUpdate(
                      message: Option[String],
                      to: Option[immutable.Map[String,String]]
                     )