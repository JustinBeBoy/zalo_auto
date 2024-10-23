package com.example.quick_reply.repo

import com.example.quick_reply.util.StringUtils.VIETNAMESE_PHONE_NUMBER_PATTERN
import com.example.quick_reply.util.StringUtils.convertToLowercaseNonAccent

class MainRepo {

    fun getExclusionList(): List<String> {
        return listOf(
            "*cần*lấy*",
            "*đổi*lấy*",
            "*Cần*đổi*",
            "*Cần*Trả*",
            "*Đang*chia sẻ*",
            "*Có mặt*xin*",
            "*cần*gửi lại*",
            "*khách*sđt*",
            "*cần*sđt*",
            "7c",
            "xe 7",
            "xe7",
            "x7",
            "7 chỗ",
            "7cho"
        ).format()
    }

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