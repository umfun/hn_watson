package me.maciejb.hnanalysis.tasks

import java.nio.charset.Charset
import java.nio.file.{Files, StandardOpenOption}

import me.maciejb.hnanalysis.HackerNews

/**
 * @author Maciej Bilas
 * @since 13/12/14 15:41
 */
object Top100Profiles extends App {
  import me.maciejb.hnanalysis.infrastructure.Beans._
  
  val outputFile = outputDirectory.resolve("top100profiles.txt")

  Files.deleteIfExists(outputFile)
  Files.createFile(outputFile)

  val top100Users = HackerNews.leaders

  for {
    user <- top100Users
    profile <- analyzerPipeline.profileOf(user)
  } {
    Files.write(outputFile, (profile + "\n").getBytes(Charset.defaultCharset()),
      StandardOpenOption.SYNC,
      StandardOpenOption.APPEND)
  }

  shutdownHandler.shutdown()
}
