package io.github.supermonster003.autojs6.plugin.threeterraplayer.update

import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

internal data class AppRelease(
    val version: String,
    val name: String,
    val notes: String,
    val pageUrl: String,
    val publishedAt: String,
)

internal object AppUpdateRepository {
    fun latestRelease(): Result<AppRelease> = runCatching {
        val connection = URL(LATEST_RELEASE_API).openConnection() as HttpURLConnection
        try {
            connection.connectTimeout = CONNECT_TIMEOUT_MS
            connection.readTimeout = READ_TIMEOUT_MS
            connection.instanceFollowRedirects = true
            connection.setRequestProperty("Accept", "application/vnd.github+json")
            connection.setRequestProperty("User-Agent", USER_AGENT)
            connection.setRequestProperty("X-GitHub-Api-Version", "2022-11-28")
            val responseCode = connection.responseCode
            require(responseCode == HttpURLConnection.HTTP_OK) { "HTTP $responseCode" }
            val text = connection.inputStream.bufferedReader(Charsets.UTF_8).use { reader ->
                val buffer = CharArray(RESPONSE_BUFFER_SIZE)
                val output = StringBuilder()
                while (output.length <= MAX_RESPONSE_CHARS) {
                    val count = reader.read(buffer)
                    if (count < 0) break
                    output.append(buffer, 0, count)
                }
                require(output.length <= MAX_RESPONSE_CHARS) { "Response is too large" }
                output.toString()
            }
            parseRelease(JSONObject(text))
        } finally {
            connection.disconnect()
        }
    }

    private fun parseRelease(json: JSONObject): AppRelease {
        val tag = json.optString("tag_name").trim().takeIf { AppVersionPolicy.parse(it) != null }
            ?: error("Missing release tag")
        val pageUrl = json.optString("html_url").trim()
            .takeIf { it.startsWith("https://github.com/$REPOSITORY/releases/") }
            ?: error("Invalid release URL")
        return AppRelease(
            version = tag.removePrefix("v").removePrefix("V"),
            name = json.optString("name").trim().ifEmpty { tag },
            notes = json.optString("body").trim().take(MAX_RELEASE_NOTES_CHARS),
            pageUrl = pageUrl,
            publishedAt = json.optString("published_at").trim(),
        )
    }

    const val REPOSITORY = "SuperMonster003/AutoJs6-Plugin-Three-Terra-Player"
    const val RELEASES_URL = "https://github.com/$REPOSITORY/releases"
    const val PROJECT_URL = "https://github.com/$REPOSITORY"

    private const val LATEST_RELEASE_API = "https://api.github.com/repos/$REPOSITORY/releases/latest"
    private const val USER_AGENT = "AutoJs6-Plugin-Three-Terra-Player-UpdateChecker"
    private const val CONNECT_TIMEOUT_MS = 10_000
    private const val READ_TIMEOUT_MS = 15_000
    private const val RESPONSE_BUFFER_SIZE = 4_096
    private const val MAX_RESPONSE_CHARS = 1_000_000
    private const val MAX_RELEASE_NOTES_CHARS = 8_000
}
