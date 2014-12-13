package me.maciejb.hnanalysis.samples

import com.softwaremill.thegarden.lawn.io.Resources

/**
 * @author Maciej Bilas
 * @since 13/12/14 15:36
 */
object HipsterIpsums {
  lazy val Text4Paragraphs = Resources.readToString("hipsum4.txt")
}