import scala.language.reflectiveCalls

import java.io._

object Implicits {

  implicit class FileW(file: File) {
    def newReader(charset: String) = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset))
  }

  type Closer = {
    def close(): Unit
  }

  implicit class AutoCloser[A <: Closer](closer: A) {
    def use[B](f: A => B) = try {
      f(closer)
    } finally {
      closer.close
    }
  }

  val pass = () => Unit

}
