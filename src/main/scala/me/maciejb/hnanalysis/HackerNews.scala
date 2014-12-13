package me.maciejb.hnanalysis

import com.softwaremill.thegarden.lawn.io.Resources


object HackerNews {
  lazy val leaders = Resources.readToString("leaders.txt").split("\n").toSeq
}