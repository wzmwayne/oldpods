package com.oldpods.app.model

object CategoryMapping {
    data class CategoryDef(
        val id: Int,
        val name: String,
        val endpoint: String,
        val wpCategoryId: Int
    )

    val categories = listOf(
        CategoryDef(1, "聊天社交", "ltsjcategory", 0),
        CategoryDef(2, "影音视听", "yystcategory", 0),
        CategoryDef(3, "图书阅读", "tsydcategory", 0),
        CategoryDef(4, "效率办公", "xlbgcategory", 0),
        CategoryDef(5, "实用工具", "sygjcategory", 0),
        CategoryDef(6, "便捷生活", "bjshcategory", 0),
        CategoryDef(7, "学习教育", "xxjycategory", 0),
        CategoryDef(8, "主题美化", "ztmhcategory", 0),
        CategoryDef(9, "游戏", "yxcategory", 0),
        CategoryDef(10, "特殊设备", "tssbcategory", 0)
    )

    // Known topics from website
    data class TopicDef(val id: Int, val name: String, val postCount: Int)

    val topics = listOf(
        TopicDef(202, "百分之千专区", 85),
        TopicDef(212, "开罗游戏", 33),
        TopicDef(203, "战火英雄", 39),
        TopicDef(312, "Minecraft", 21),
        TopicDef(210, "E社游戏", 10),
        TopicDef(211, "愤怒的小鸟", 12),
        TopicDef(310, "GAMELOFT游戏合集", 15)
    )
}
