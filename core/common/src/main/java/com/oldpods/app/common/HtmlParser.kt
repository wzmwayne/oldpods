package com.oldpods.app.common

import android.text.Html
import android.text.Spanned
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.graphics.Color
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

    fun parseCurrentVersion(html: String): String {
        return extractFirstMatch(html, "当前版本：([^<]+)")
    }

    fun parseDeveloper(html: String): String {
        return extractFirstMatch(html, "开发者：([^<]+)")
    }

    fun parseUpdateDate(html: String): String {
        return extractFirstMatch(html, "更新日期：([^<]+)")
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

    fun parseDownloadLinks(html: String): List<com.oldpods.app.model.DownloadLink> {
        val links = mutableListOf<com.oldpods.app.model.DownloadLink>()

        // Pattern 1: <a class="but c-blue file-download-btn" href="URL">NAME</a>
        val btnPattern = Pattern.compile(
            """<a[^>]+class="but c-blue file-download-btn"[^>]+href="([^"]+)"[^>]*>([^<]*)</a>"""
        )
        val btnMatcher = btnPattern.matcher(html)
        while (btnMatcher.find()) {
            val url = btnMatcher.group(1)?.trim() ?: ""
            val name = btnMatcher.group(2)?.trim() ?: "下载"
            if (url.isNotEmpty()) {
                links.add(com.oldpods.app.model.DownloadLink(
                    name = name,
                    url = url,
                    type = classifyLink(url)
                ))
            }
        }

        // Pattern 2: golink URLs (Base64 encoded)
        val golinkPattern = Pattern.compile(
            """href="(https?://[^"]*golink[^"]*)" """
        )
        val golinkMatcher = golinkPattern.matcher(html)
        while (golinkMatcher.find()) {
            val url = golinkMatcher.group(1)?.trim() ?: ""
            if (url.isNotEmpty() && links.none { it.url == url }) {
                links.add(com.oldpods.app.model.DownloadLink(
                    name = "高速下载",
                    url = url,
                    type = com.oldpods.app.model.DownloadLink.LinkType.GOLINK
                ))
            }
        }

        // Pattern 3: Lanzou links
        val lanzouPattern = Pattern.compile(
            """href="(https?://[^"]*lanzou[^"]*)" """
        )
        val lanzouMatcher = lanzouPattern.matcher(html)
        while (lanzouMatcher.find()) {
            val url = lanzouMatcher.group(1)?.trim() ?: ""
            if (url.isNotEmpty() && links.none { it.url == url }) {
                links.add(com.oldpods.app.model.DownloadLink(
                    name = "蓝奏云",
                    url = url,
                    type = com.oldpods.app.model.DownloadLink.LinkType.LANZOU
                ))
            }
        }

        // Pattern 4: Baidu links
        val baiduPattern = Pattern.compile(
            """href="(https?://[^"]*pan\.baidu[^"]*)" """
        )
        val baiduMatcher = baiduPattern.matcher(html)
        while (baiduMatcher.find()) {
            val url = baiduMatcher.group(1)?.trim() ?: ""
            if (url.isNotEmpty() && links.none { it.url == url }) {
                links.add(com.oldpods.app.model.DownloadLink(
                    name = "百度网盘",
                    url = url,
                    type = com.oldpods.app.model.DownloadLink.LinkType.BAIDU
                ))
            }
        }

        // Pattern 5: Quark links
        val quarkPattern = Pattern.compile(
            """href="(https?://[^"]*quark[^"]*)" """
        )
        val quarkMatcher = quarkPattern.matcher(html)
        while (quarkMatcher.find()) {
            val url = quarkMatcher.group(1)?.trim() ?: ""
            if (url.isNotEmpty() && links.none { it.url == url }) {
                links.add(com.oldpods.app.model.DownloadLink(
                    name = "夸克网盘",
                    url = url,
                    type = com.oldpods.app.model.DownloadLink.LinkType.QUARK
                ))
            }
        }

        // If no links found via patterns, try the old method
        if (links.isEmpty()) {
            val url1 = parseDownloadUrl(html)
            if (url1.isNotEmpty()) {
                links.add(com.oldpods.app.model.DownloadLink("下载", url1, classifyLink(url1)))
            }
            val url2 = parseDownloadUrl2(html)
            if (url2.isNotEmpty()) {
                links.add(com.oldpods.app.model.DownloadLink("备用下载", url2, classifyLink(url2)))
            }
        }

        return links
    }

    private fun classifyLink(url: String): com.oldpods.app.model.DownloadLink.LinkType {
        return when {
            url.contains("golink") -> com.oldpods.app.model.DownloadLink.LinkType.GOLINK
            url.contains("lanzou") -> com.oldpods.app.model.DownloadLink.LinkType.LANZOU
            url.contains("pan.baidu") -> com.oldpods.app.model.DownloadLink.LinkType.BAIDU
            url.contains("quark") -> com.oldpods.app.model.DownloadLink.LinkType.QUARK
            else -> com.oldpods.app.model.DownloadLink.LinkType.DIRECT
        }
    }

    fun decodeGolink(golinkUrl: String): String {
        return try {
            val parts = golinkUrl.split("#")
            if (parts.size >= 2) {
                val encoded = parts[1]
                val decoded = String(android.util.Base64.decode(encoded, android.util.Base64.DEFAULT))
                // Extract URL from decoded JSON: {"url":"...","code":"..."}
                val urlPattern = Pattern.compile(""""url"\s*:\s*"([^"]+)"""")
                val matcher = urlPattern.matcher(decoded)
                if (matcher.find()) {
                    matcher.group(1) ?: golinkUrl
                } else {
                    decoded
                }
            } else {
                golinkUrl
            }
        } catch (e: Exception) {
            golinkUrl
        }
    }

    fun parseScreenshots(html: String): List<String> {
        val screenshots = mutableListOf<String>()
        val pattern = Pattern.compile("""<img[^>]+src="(https?://[^"]+\.(jpg|jpeg|png|webp)[^"]*)"""", Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(html)
        while (matcher.find()) {
            val url = matcher.group(1)?.trim() ?: ""
            if (url.isNotEmpty() && !url.contains("avatar") && !url.contains("icon") && !screenshots.contains(url)) {
                screenshots.add(url)
            }
        }
        return screenshots
    }

    fun parseDescription(html: String): String {
        // Remove <style> blocks
        var clean = html.replace(Regex("<style[^>]*>[\\s\\S]*?</style>"), "")
        // Remove script blocks
        clean = clean.replace(Regex("<script[^>]*>[\\s\\S]*?</script>"), "")
        // Remove pay-box div
        clean = clean.replace(Regex("<div class=\"pay-box[\\s\\S]*?</div>"), "")
        // Remove download buttons
        clean = clean.replace(Regex("<a[^>]*class=\"but[\\s\\S]*?</a>"), "")
        // Remove all HTML tags
        clean = clean.replace(Regex("<[^>]+>"), " ")
        // Decode HTML entities
        clean = clean.replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#039;", "'")
            .replace("&nbsp;", " ")
        // Collapse whitespace
        clean = clean.replace(Regex("\\s+"), " ").trim()
        return clean
    }

    fun parseDescriptionSpans(html: String): Spanned {
        return try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                Html.fromHtml(parseDescription(html), Html.FROM_HTML_MODE_LEGACY)
            } else {
                @Suppress("DEPRECATION")
                Html.fromHtml(parseDescription(html))
            }
        } catch (e: Exception) {
            SpannableStringBuilder(parseDescription(html))
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
