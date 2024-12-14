package com.example.quick_reply.data.util

object StringUtils {

    const val VIETNAMESE_PHONE_NUMBER_PATTERN = "(84|0[3|5|7|8|9])+([0-9]{8})"

    fun convertToLowercaseNonAccent(text: String): String {
        // Chuyển các ký tự có dấu thành dạng cơ bản không dấu
        val normalizedText = noAccentVietnamese(text)
        // Chuyển toàn bộ văn bản thành chữ thường
        return normalizedText.lowercase()
    }

    private fun noAccentVietnamese(text: String): String {
        // Tạo chuỗi chứa tất cả các ký tự tiếng Việt có dấu
        val intab =
            "ạảãàáâậầấẩẫăắằặẳẵóòọõỏôộổỗồốơờớợởỡéèẻẹẽêếềệểễúùụủũưựữửừứíìịỉĩýỳỷỵỹđẠẢÃÀÁÂẬẦẤẨẪĂẮẰẶẲẴÓÒỌÕỎÔỘỔỖỒỐƠỜỚỢỞỠÉÈẺẸẼÊẾỀỆỂỄÚÙỤỦŨƯỰỮỬỪỨÍÌỊỈĨÝỲỶỴỸĐ"

        // Tạo chuỗi thay thế tương ứng các ký tự không dấu
        val outtab = "a".repeat(17) + "o".repeat(17) + "e".repeat(11) + "u".repeat(11) + "i".repeat(5) + "y".repeat(5) + "d" +
                "A".repeat(17) + "O".repeat(17) + "E".repeat(11) + "U".repeat(11) + "I".repeat(5) + "Y".repeat(5) + "D"

        // Tạo map để ánh xạ từ ký tự có dấu sang không dấu
        val replacesDict = intab.zip(outtab).toMap()

        // Thay thế các ký tự có dấu trong chuỗi bằng ký tự không dấu
        val result = StringBuilder()
        for (char in text) {
            result.append(replacesDict[char] ?: char)  // Nếu ký tự không có trong map thì giữ nguyên
        }

        return result.toString()
    }
}