package eu.alkismavridis.articleanalyser.io

import com.fasterxml.jackson.databind.ObjectMapper
import eu.alkismavridis.articleanalyser.analyse.PostData
import eu.alkismavridis.articleanalyser.analyse.PostDataProvider
import java.net.URL
import java.util.stream.Stream

class RedditProviderException(message: String, cause: Throwable) : Exception(message, cause)

class RedditPostDataProvider : PostDataProvider {
    companion object {
        val MAPPER = ObjectMapper()
    }

    override fun getPosts(path: String): Stream<PostData> {
        val text = this.getResponseBodyFor(path)
        return this.extractPostData(text).stream().filter { it.body.isNotBlank() }
    }

    private fun extractPostData(response: String): List<PostData> {
        // In this case, I found easier to work with JsonNodes instead of modeling the response into classes and use jackson-reflection magic.
        // Of course, in other circumstances modeling the response could be the correct way to go.
        try {
            val json = MAPPER.readTree(response)
            return json.path("data").path("children").map {
                PostData(
                        it.path("data").path("title").asText(),
                        it.path("data").path("selftext").asText()
                )
            }
        } catch (e: Exception) {
            throw RedditProviderException("Response is not a valid json or does not have the expected format", e)
        }
    }

    private fun getResponseBodyFor(path: String): String {
        try {
            return URL(path).readText()
        } catch (e: Exception) {
            throw RedditProviderException("Could not fetch posts from $path", e)
        }
    }
}
