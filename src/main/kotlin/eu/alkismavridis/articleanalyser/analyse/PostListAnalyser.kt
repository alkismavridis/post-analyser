package eu.alkismavridis.articleanalyser.analyse

import java.util.stream.Collectors.toList


class AnalysisResult(val title: String, val mostCommonWord: String)


/** Analyses a list of posts, returns a report about the post with the most words. */
class PostListAnalyser(private val provider: PostDataProvider, private val textMetadataProvider: TextMetadataProvider) {
    fun analyse(path: String): AnalysisResult {
        val posts = this.provider.getPosts(path)
                .map { PostWithMetadata(it, this.textMetadataProvider.analyse(it.body)) }
                .collect(toList())

        val postWithMostWords = this.findPostWithMostWords(posts)
                ?: throw AnalyserException("Post data provider did not fetch any posts")

        return AnalysisResult(postWithMostWords.post.title, postWithMostWords.textMetadata.mostCommonWord)
    }

    internal fun findPostWithMostWords(posts: List<PostWithMetadata>): PostWithMetadata? {
        var postWithMostWords: PostWithMetadata? = null

        posts.forEach {
            if (postWithMostWords == null || postWithMostWords!!.textMetadata.wordCount <= it.textMetadata.wordCount) {
                postWithMostWords = it
            }
        }

        return postWithMostWords
    }
}

/** Encapsulates a post, along with the results of our analysis about that post */
internal class PostWithMetadata(val post: PostData, val textMetadata: TextMetadata)
