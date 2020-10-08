package eu.alkismavridis.articleanalyser.analyse

import java.util.regex.Pattern

class TextMetadata(val wordCount: Int, val mostCommonWord: String)

class TextMetadataProvider {
    companion object {
        val WORD_SEPARATOR_PATTERN = Pattern.compile("[\\s,!|$*;\"'\\[\\]()<>{}]+")!!
        val IS_NUMBER_PATTERN = Pattern.compile("(([+|-]?([0-9]+)(\\.[0-9]+)?)|([+|-]?\\.?[0-9]+))%?")!!
        val IS_URL_PATTERN = Pattern.compile("^(?!mailto:)(?:(?:http|https|ftp)://)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)")!!
    }

    fun analyse(text: String): TextMetadata {
        val occurrences = mutableMapOf<String, Int>()

        val words = this.splitWords(text)
        var realWordCount = 0
        words.forEach {
            if (this.isRealWord(it)) {
                addToMap(this.extractWord(it), occurrences)
                realWordCount++
            }
        }

        val mostCommonWord = getMostCommonWordOf(occurrences)
        return TextMetadata(realWordCount, mostCommonWord)
    }

    internal fun splitWords(text: String): List<String> {
        return text.split(WORD_SEPARATOR_PATTERN)
    }

    /** Strips out training characters such as .?: and leading characters such as $#  */
    internal fun extractWord(wordText: String): String {
        val lastChar = wordText[wordText.lastIndex]
        val firstChar = wordText[0]
        val cutFirstCharacter = firstChar == '$' || firstChar == '#'
        val cutLastCharacter = lastChar == '.' || lastChar == '?' || lastChar == ':' || lastChar == '!'

        if (!cutFirstCharacter && !cutLastCharacter) {
            return wordText.toLowerCase()
        }

        return wordText.substring(
                if (cutFirstCharacter) 1 else 0,
                if (cutLastCharacter) wordText.lastIndex else wordText.lastIndex + 1
        ).toLowerCase()
    }


    /** Excludes URLS and numbers. */
    internal fun isRealWord(word: String): Boolean {
        return word.isNotBlank() && !IS_NUMBER_PATTERN.matcher(word).matches() && !IS_URL_PATTERN.matcher(word).matches()
    }


    private fun addToMap(wordToAdd: String, targetMap: MutableMap<String, Int>) {
        if (targetMap.containsKey(wordToAdd)) {
            targetMap[wordToAdd] = targetMap[wordToAdd]!! + 1
        } else {
            targetMap[wordToAdd] = 1
        }
    }

    private fun getMostCommonWordOf(map: Map<String, Int>): String {
        var result = ""
        var maxFound = 0

        map.entries.forEach {
            if (it.value > maxFound) {
                maxFound = it.value
                result = it.key
            }
        }

        return result
    }
}
