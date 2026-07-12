package com.oldpods.app.common

import java.net.URLDecoder
import java.util.regex.Pattern

object HtmlParser {

    private fun extractFirstMatch(html: String, regex: String): String {
        return try {
            val pattern = Pattern.compile(regex)
            val matcher = pattern.matcher(html)
            if (matcher.find()) matcher.group(1)?.trim() ?: "" else ""
        } catch (e: Exception) {
            ""
        }
    }

    fun parseCompatibleVersion(html: String): String {
        return extractFirstMatch(html, "兼容版本：([^<]+)")
    }

    fun parsePackageSize(html: String): String {
        return extractFirstMatch(html, "安装包大小：([^<]+)")
    }

    fun parseDownloadUrl(html: String): String {
        return extractFirstMatch(html, """<a[^>]+class="but c-blue file-download-btn"[^>]+href="([^"]+)"""")
    }

    fun parseDownloadUrl2(html: String): String {
        return try {
            val pattern = Pattern.compile("""<a[^>]+class="but c-blue file-download-btn"[^>]+href="([^"]+)"""")
            val matcher = pattern.matcher(html)
            var count = 0
            var lastMatch = ""
            while (matcher.find()) {
                count++
                if (count == 2) {
                    lastMatch = matcher.group(1)?.trim() ?: ""
                    break
                }
            }
            lastMatch
        } catch (e: Exception) {
            ""
        }
    }

    fun isApkUrl(url: String): Boolean {
        return try {
            val decoded = URLDecoder.decode(url, "UTF-8")
            decoded.contains(".apk") && (decoded.endsWith(".apk") || decoded.matches(Regex(".*\\.apk[?&].*")))
        } catch (e: Exception) {
            false
        }
    }

    fun formatHtmlToPlainText(html: String): String {
        return html.replace(Regex("<[^>]+>"), "")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#039;", "'")
            .replace("&nbsp;", " ")
            .trim()
    }
}
