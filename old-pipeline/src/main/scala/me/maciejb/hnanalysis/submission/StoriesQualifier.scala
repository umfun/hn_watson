package me.maciejb.hnanalysis.submission


object StoriesQualifier {

  val MaxCommentSize = 1024 * 512
//  val MaxCommentSize = 1024 * 64

  def qualified(stories: String) : String = {
    stories.takeRight(math.min(stories.length, MaxCommentSize))
  }

}
