package eu.alkismavridis.articleanalyser

import eu.alkismavridis.articleanalyser.analyse.PostListAnalyser
import eu.alkismavridis.articleanalyser.analyse.TextMetadataProvider
import eu.alkismavridis.articleanalyser.io.RedditPostDataProvider

private const val DEFAULT_TARGET_URL = "https://www.reddit.com/r/webdev.json"
fun main(args: Array<String>) {
    val urlToUse = if (args.isEmpty()) DEFAULT_TARGET_URL else args[0]
    val analyser = PostListAnalyser(RedditPostDataProvider(), TextMetadataProvider())
    val postDataWithMostWords = analyser.analyse(urlToUse)

    print("\n\nPost title with most words: ${postDataWithMostWords.title}\nMost common word of that post: ${postDataWithMostWords.mostCommonWord}\n\n")
}
