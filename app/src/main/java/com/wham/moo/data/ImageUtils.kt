package com.wham.moo.data

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.UUID

/**
 * 图片工具类
 * 将 content:// URI 的图片复制到 App 内部存储，保证重启后仍可访问
 */
object ImageUtils {

    /**
     * 将一张图片从 content URI 复制到 App 内部存储的 diary_images 目录
     * @return 本地存储的绝对路径 (file:// 格式字符串)
     */
    fun copyImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null

            // 创建 diary_images 目录
            val imageDir = File(context.filesDir, "diary_images")
            if (!imageDir.exists()) {
                imageDir.mkdirs()
            }

            // 用 UUID 生成唯一文件名，保留原始扩展名
            val fileName = "${UUID.randomUUID()}.${getExtension(context, uri)}"
            val destFile = File(imageDir, fileName)

            // 复制文件
            FileOutputStream(destFile).use { output ->
                inputStream.use { input ->
                    input.copyTo(output)
                }
            }

            // 返回 file:// 格式的路径
            Uri.fromFile(destFile).toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 批量复制图片到内部存储
     * @return 复制成功后的本地路径列表
     */
    fun copyImagesToInternalStorage(context: Context, uris: List<String>): List<String> {
        return uris.mapNotNull { uriStr ->
            try {
                val uri = Uri.parse(uriStr)
                copyImageToInternalStorage(context, uri)
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * 获取 URI 对应的文件扩展名
     */
    private fun getExtension(context: Context, uri: Uri): String {
        val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
        return when {
            mimeType.contains("png") -> "png"
            mimeType.contains("gif") -> "gif"
            mimeType.contains("webp") -> "webp"
            else -> "jpg"
        }
    }

    /**
     * 删除日记关联的本地图片文件
     */
    fun deleteImages(context: Context, imageUris: String) {
        if (imageUris.isBlank()) return
        imageUris.split(",").filter { it.isNotBlank() }.forEach { uriStr ->
            try {
                val uri = Uri.parse(uriStr)
                if (uri.scheme == "file") {
                    val file = File(uri.path!!)
                    if (file.exists()) file.delete()
                }
            } catch (_: Exception) { }
        }
    }
}
