package me.maciejb.hnanalysis.samples

import java.nio.file.Files

import me.maciejb.hnanalysis.infrastructure.Beans
import org.apache.commons.io.Charsets

import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * @author Maciej Bilas
 * @since 13/12/14 15:37
 */
object SubmitHipsterIpsumApp extends App {
  import Beans._

  val s = Await.result(submitter.submitText(HipsterIpsums.Text4Paragraphs), 10.seconds)

  Files.write(outputDirectory.resolve("hipsum.html"), s.getBytes(Charsets.UTF_8))

  shutdownHandler.shutdown()
}
