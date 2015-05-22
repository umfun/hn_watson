package me.maciejb.hnanalysis.spark


case class ParsingException(line: String, causeOpt: Option[Throwable] = None) extends Exception
