package eu.alkismavridis.articleanalyser.analyse

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.util.stream.Stream
import kotlin.test.Test

class PostListAnalyserTest {
    companion object {
        val URL_WITH_POSTS = "https://some.url.eu/with/posts/"
        val URL_WITHOUT_POSTS = "https://some.url.eu/without/posts/"
    }

    /// findPostWithMostWords tests
    @Test
    fun shouldReturnNullIfListIsEmpty() {
        val analyser = this.createDummyAnalyser()
        assertThat(analyser.findPostWithMostWords(emptyList())).isNull()
    }

    @Test
    fun shouldReturnEntryWithLargestWordCount() {
        val analyser = this.createDummyAnalyser()
        val posts = listOf(
                this.createPost(2, "I have two words"),
                this.createPost(60, "I have sixty"),
                this.createPost(7, "I have seven words")
        )

        val result = analyser.findPostWithMostWords(posts)
        assertThat(result).isNotNull()
        assertThat(result!!.textMetadata.wordCount).isEqualTo(60)
        assertThat(result.post.title).isEqualTo("I have sixty")
    }


    /// analyse tests
    @Test
    fun shouldReturnPostWithMostWords() {
        val analyser = this.createDummyAnalyser()

        val result = analyser.analyse(URL_WITH_POSTS)
        assertThat(result.title).isEqualTo("Large post")
        assertThat(result.mostCommonWord).isEqualTo("words")
    }

    @Test
    fun shouldThrowExceptionIfProviderFetchesNoPosts() {
        val analyser = this.createDummyAnalyser()

        assertThatExceptionOfType(AnalyserException::class.java)
                .isThrownBy { analyser.analyse(URL_WITHOUT_POSTS) }
                .withMessage("Post data provider did not fetch any posts")
    }


    /// UTILS
    private fun createDummyAnalyser(): PostListAnalyser {
        val provider = mock(PostDataProvider::class.java)
        `when`(provider.getPosts(URL_WITH_POSTS)).thenReturn(Stream.of(
                PostData("Small post", "I have a few words. This is A small post."),
                PostData("Large post", "I have 11 more 11 words. 11 WordS! 11 WorDs? 11 <wORds> 11. I should ignore 11 and url https://docs.gradle.org/current/userguide/userguide.html"),
                PostData("Middle post", "I am a middle post. post POST! poSt. POst, post? post! Post!")
        ))

        `when`(provider.getPosts(URL_WITHOUT_POSTS)).thenReturn(Stream.empty())
        return PostListAnalyser(provider, TextMetadataProvider())
    }

    private fun createPost(wordCount: Int, title: String): PostWithMetadata {
        return PostWithMetadata(PostData(title, ""), TextMetadata(wordCount, ""))
    }
}
