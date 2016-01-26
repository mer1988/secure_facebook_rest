package entities
import java.util.Date
/**
  * Created by miguel on 11/24/15.
  */
case class UserUpdate(
                      about: Option[String],
                      bio: Option[String],
                      //birthday: Date,
                      email: Option[String],
                      firstName: Option[String],
                      lastName: Option[String]

                     )
