package com.oldpods.app.common

import android.text.Html
import android.text.Spanned
import android.text.SpannableStringBuilder
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

    fun parseDownloadLinks(html: String): List<com.oldpods.app.model.DownloadLink> {
        val links = mutableListOf<com.oldpods.app.model.DownloadLink>()

        // Strategy: match each file-download-box block
        // Structure:
        // <div class="...file-download-box...">
        //   <div class="file-download-name">百度网盘</div>
        //   <a class="but c-blue file-download-btn" href="URL">下载</a>
        // </div>
        val boxPattern = Pattern.compile(
            """<div[^>]*class="[^"]*file-download-box[^"]*"[^>]*>([\s\S]*?)</div>\s*</div>""",
            Pattern.CASE_INSENSITIVE
        )
        val namePattern = Pattern.compile(
            """file-download-name[^>]*>([^<]+)<"""
        )
        val btnPattern = Pattern.compile(
            """class="but c-blue file-download-btn"[^>]+href="([^"]+)""""
        )

        val boxMatcher = boxPattern.matcher(html)
        while (boxMatcher.find()) {
            val boxContent = boxMatcher.group(1) ?: continue

            val nameMatcher = namePattern.matcher(boxContent)
            val name = if (nameMatcher.find()) nameMatcher.group(1)?.trim() ?: "" else ""

            val btnMatcher = btnPattern.matcher(boxContent)
            if (btnMatcher.find()) {
                val rawUrl = btnMatcher.group(1)?.trim() ?: ""
                if (rawUrl.isNotEmpty()) {
                    val decodedUrl = decodeGolink(rawUrl)
                    val displayName = if (name.isNotEmpty()) name else identifyDriveName(decodedUrl)
                    links.add(com.oldpods.app.model.DownloadLink(
                        name = "${displayName}下载",
                        url = decodedUrl,
                        type = classifyLink(decodedUrl)
                    ))
                }
            }
        }

        // Fallback: if no file-download-box found, try direct button pattern
        if (links.isEmpty()) {
            val fallbackBtn = Pattern.compile(
                """<a[^>]+class="but c-blue file-download-btn"[^>]+href="([^"]+)"[^>]*>([^<]*)</a>"""
            )
            val fallbackMatcher = fallbackBtn.matcher(html)
            while (fallbackMatcher.find()) {
                val rawUrl = fallbackMatcher.group(1)?.trim() ?: ""
                val btnName = fallbackMatcher.group(2)?.trim() ?: ""
                if (rawUrl.isNotEmpty()) {
                    val decodedUrl = decodeGolink(rawUrl)
                    val driveName = identifyDriveName(decodedUrl)
                    val displayName = if (btnName.isNotEmpty()) btnName else "${driveName}下载"
                    links.add(com.oldpods.app.model.DownloadLink(
                        name = displayName,
                        url = decodedUrl,
                        type = classifyLink(decodedUrl)
                    ))
                }
            }
        }

        return links
    }

    fun decodeGolink(url: String): String {
        return try {
            // Format: https://oldpods.cn/?golink=BASE64_ENCODED_URL&nonce=XXX
            if (url.contains("golink=")) {
                val marker = "golink="
                val start = url.indexOf(marker) + marker.length
                val end = url.indexOf("&", start).let { if (it == -1) url.length else it }
                val encoded = url.substring(start, end)
                val decoded = String(android.util.Base64.decode(encoded, android.util.Base64.DEFAULT))
                // decoded is the direct URL, e.g. https://pan.baidu.com/s/xxx
                return decoded
            }
            // Fallback: old format with # fragment
            if (url.contains("#")) {
                val parts = url.split("#")
                if (parts.size >= 2) {
                    val decoded = String(android.util.Base64.decode(parts[1], android.util.Base64.DEFAULT))
                    // Try JSON format: {"url":"...","code":"..."}
                    val urlPattern = Pattern.compile(""""url"\s*:\s*"([^"]+)"""")
                    val matcher = urlPattern.matcher(decoded)
                    if (matcher.find()) {
                        return matcher.group(1) ?: url
                    }
                    return decoded
                }
            }
            url
        } catch (e: Exception) {
            url
        }
    }

    private fun identifyDriveName(url: String): String {
        return when {
            url.contains("pan.baidu") -> "百度网盘"
            url.contains("quark") -> "夸克网盘"
            url.contains("lanzou") -> "蓝奏云"
            url.contains("189.cn") || url.contains("ctyun") -> "天翼云盘"
            url.contains("115.com") -> "115网盘"
            url.contains("caiyun") -> "彩云网盘"
            url.contains("weiyun") -> "微云"
            url.contains("lanzoui") || url.contains("lanzoux") -> "蓝奏云"
            else -> "网盘"
        }
    }

    private fun classifyLink(url: String): com.oldpods.app.model.DownloadLink.LinkType {
        return when {
            url.contains("pan.baidu") -> com.oldpods.app.model.DownloadLink.LinkType.BAIDU
            url.contains("quark") -> com.oldpods.app.model.DownloadLink.LinkType.QUARK
            url.contains("lanzou") -> com.oldpods.app.model.DownloadLink.LinkType.LANZOU
            url.contains("189.cn") || url.contains("ctyun") -> com.oldpods.app.model.DownloadLink.LinkType.DIRECT
            url.contains("115.com") -> com.oldpods.app.model.DownloadLink.LinkType.DIRECT
            else -> com.oldpods.app.model.DownloadLink.LinkType.DIRECT
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
        var clean = html.replace(Regex("<style[^>]*>[\\s\\S]*?</style>"), "")
        clean = clean.replace(Regex("<script[^>]*>[\\s\\S]*?</script>"), "")
        clean = clean.replace(Regex("<div class=\"pay-box[\\s\\S]*?</div>"), "")
        clean = clean.replace(Regex("<div class=\"muted-box file-download-box[\\s\\S]*?</div>\\s*</div>"), "")
        clean = clean.replace(Regex("<a[^>]*class=\"but[\\s\\S]*?</a>"), "")
        clean = clean.replace(Regex("<[^>]+>"), " ")
        clean = clean.replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#039;", "'")
            .replace("&nbsp;", " ")
            .replace("&#8236;", "")
            .replace("&#8203;", "")
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
}
