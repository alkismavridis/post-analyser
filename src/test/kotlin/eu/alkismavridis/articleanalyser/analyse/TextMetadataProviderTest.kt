package eu.alkismavridis.articleanalyser.analyse

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class TextMetadataProviderTest {
    private val provider = TextMetadataProvider()


    /// splitWords tests
    @Test
    fun shouldSplitOnWhiteSpaces() {
        assertThat(provider.splitWords("This is  the   \ttext\n  \t to split   ")).containsExactly(
                "This",
                "is",
                "the",
                "text",
                "to",
                "split",
                ""
        )
    }

    @Test
    fun shouldSplitOnSpecialCharacters() {
        assertThat(provider.splitWords("{This}, !is!  <the>,   (text)  [to] split$ \n more? characters| \$to split* \"quotes\" 'single'")).containsExactly(
                "",
                "This",
                "is",
                "the",
                "text",
                "to",
                "split",
                "more?",
                "characters",
                "to",
                "split",
                "quotes",
                "single",
                ""
        )
    }

    @Test
    fun shouldConsiderUrlsAsSingleWords() {
        assertThat(provider.splitWords("This here http://jkorpela.fi/ftpurl.html?query=hi%3Cthere%3E%21%201%2B2%24%26#hash is a url")).containsExactly(
                "This",
                "here",
                "http://jkorpela.fi/ftpurl.html?query=hi%3Cthere%3E%21%201%2B2%24%26#hash",
                "is",
                "a",
                "url"
        )
    }


    /// isRealWord tests
    @Test
    fun lettersShouldBeRealWords() {
        assertThat(provider.isRealWord("This")).isTrue()
        assertThat(provider.isRealWord("is")).isTrue()
        assertThat(provider.isRealWord("a")).isTrue()
        assertThat(provider.isRealWord("test")).isTrue()
    }

    @Test
    fun numbersShouldNotBeRealWords() {
        assertThat(provider.isRealWord("2")).isFalse()
        assertThat(provider.isRealWord("-3")).isFalse()
        assertThat(provider.isRealWord("2.5")).isFalse()
        assertThat(provider.isRealWord("-2.718281828")).isFalse()
        assertThat(provider.isRealWord("20%")).isFalse()
        assertThat(provider.isRealWord("20.55%")).isFalse()
        assertThat(provider.isRealWord("-20%")).isFalse()
        assertThat(provider.isRealWord("-20.55%")).isFalse()
    }

    @Test
    fun mixtureOfNumbersAndLettersShouldBeRealWords() {
        assertThat(provider.isRealWord("23andMe")).isTrue()
        assertThat(provider.isRealWord("internet4you")).isTrue()
        assertThat(provider.isRealWord("123abc456")).isTrue()
        assertThat(provider.isRealWord("abc123def")).isTrue()
    }

    @Test
    fun absoluteUrlsShouldNotBeRealWords() {
        assertThat(provider.isRealWord("https://www.reddit.com/r/webdev.json")).isFalse()
        assertThat(provider.isRealWord("https://mvnrepository.com/artifact/org.assertj/assertj-core/3.6.1")).isFalse()
        assertThat(provider.isRealWord("ftp://ftp.funet.fi/pub/standards/RFC/rfc959.txt")).isFalse()
        assertThat(provider.isRealWord("http://jkorpela.fi/ftpurl.html?query=hi%3Cthere%3E%21%201%2B2%24%26#hash")).isFalse()
    }

    @Test
    fun urlsWithoutProtocolShouldNotBeRealWords() {
        assertThat(provider.isRealWord("www.google.com")).isFalse()
        assertThat(provider.isRealWord("www.foo.bar/path?query=param&other-query=value")).isFalse()
    }

    @Test
    fun blankStringsShouldNotBeRealWords() {
        assertThat(provider.isRealWord("")).isFalse()
        assertThat(provider.isRealWord(" ")).isFalse()
    }


    /// extractWordTests
    @Test
    fun shouldRemoveLeadingCharacters() {
        assertThat(provider.extractWord("\$fOo")).isEqualTo("foo")
        assertThat(provider.extractWord("#hashTag")).isEqualTo("hashtag")
    }

    @Test
    fun shouldRemoveTrailingCharacters() {
        assertThat(provider.extractWord("endOfSentence.")).isEqualTo("endofsentence")
        assertThat(provider.extractWord("lookAtThis!")).isEqualTo("lookatthis")
        assertThat(provider.extractWord("queSTion?")).isEqualTo("question")
        assertThat(provider.extractWord("exampLe:")).isEqualTo("example")
        assertThat(provider.extractWord("semicoLon;")).isEqualTo("semicolon")
    }

    @Test
    fun shouldRemoveTrailingAndLeadingCharacters() {
        assertThat(provider.extractWord("#hashTag.")).isEqualTo("hashtag")
        assertThat(provider.extractWord("\$lookAtThis!")).isEqualTo("lookatthis")
    }

    @Test
    fun shouldNotRemoveLettersAndNumbers() {
        assertThat(provider.extractWord("helLo123")).isEqualTo("hello123")
        assertThat(provider.extractWord("123Hello")).isEqualTo("123hello")
    }


    /// analyse tests
    @Test
    fun shouldDetectWordsEvenIfPunctuationFollows() {
        val result = provider.analyse("  I and, [you] AND! he #anD? I ")
        assertThat(result.mostCommonWord).isEqualTo("and")
        assertThat(result.wordCount).isEqualTo(7)
    }

    @Test
    fun shouldIgnoreNumbers() {
        val result = provider.analyse("  abc123 aBc123    22.44 22.44 22.44   100% 100% 100% 100% 100% ")
        assertThat(result.mostCommonWord).isEqualTo("abc123")
        assertThat(result.wordCount).isEqualTo(2)
    }

    @Test
    fun shouldIgnoreUrls() {
        val result = provider.analyse("  hello http://jkorpela.fi/me/me/me/me.html?query=hi%3Cthere%3E%21%201%2B2%24%26#me \t ")
        assertThat(result.mostCommonWord).isEqualTo("hello")
        assertThat(result.wordCount).isEqualTo(1)
    }
}
