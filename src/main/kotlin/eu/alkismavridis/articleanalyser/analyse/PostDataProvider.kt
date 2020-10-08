package eu.alkismavridis.articleanalyser.analyse

import java.util.stream.Stream

/**
 * A post might come from the web, a database, a filesystem or any other IO device.
 * The business logic is the same in all those cases, so it should not depend on IO specifics.
 * This interface intends to abstract out those IO-specifics.
 * */
interface PostDataProvider {
    fun getPosts(path: String) : Stream<PostData>
}
