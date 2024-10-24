package com.example.quick_reply.data.repo

import com.example.quick_reply.data.local.MainSharedPreferences
import com.example.quick_reply.util.StringUtils.VIETNAMESE_PHONE_NUMBER_PATTERN
import com.example.quick_reply.util.StringUtils.convertToLowercaseNonAccent

class MainRepo(
    private val mainSharedPreferences: MainSharedPreferences
) {

    fun getExclusionList(): List<String> {
        val list = mutableListOf<String>()
        if (mainSharedPreferences.isSkipPleaseChange()) {
            list.addAll(getSkipPleaseChangeList())
        }
        if (mainSharedPreferences.isSkip7Seats()) {
            list.addAll(getSkip7Seats())
        }
        return list.format()
    }

    private fun getSkipPleaseChangeList() = listOf(
        "*cần*lấy*",
        "*đổi*lấy*",
        "*Cần*đổi*",
        "*Cần*Trả*",
        "*Đang*chia sẻ*",
        "*Có mặt*xin*",
        "*cần*gửi lại*",
        "*khách*sđt*",
        "*cần*sđt*",
        "*mặt*khách*",
    )

    private fun getSkip7Seats() = listOf(
        "7c",
        "xe 7",
        "xe7",
        "x7",
        "7 chỗ",
        "7cho"
    )

    fun getAutoAcceptList(): List<String> {
        return listOf(
            "*đà lạt*Nha trang*"
        ).format()
    }

    private fun List<String>.format() = map { text ->
        var lowercaseNonAccent = convertToLowercaseNonAccent(text).replace("*sdt*", "*${VIETNAMESE_PHONE_NUMBER_PATTERN}*")
        if (!lowercaseNonAccent.startsWith("*")) {
            lowercaseNonAccent = "*$lowercaseNonAccent"
        }
        if (!lowercaseNonAccent.endsWith("*")) {
            lowercaseNonAccent = "$lowercaseNonAccent*"
        }
        return@map lowercaseNonAccent.replace("*", "(.|\\n)*")
    }
}