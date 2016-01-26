package crypto

import java.security.KeyPair
import java.security.PublicKey
import scala.collection.mutable.Map
/**
  * Created by miguel on 12/10/15.
  */
object KeyRing{
  val RSAkeyPair:KeyPair = RSA.generateKey()
  //val RSAPublicKeys: Map[Int,PublicKey] = Map()
}
