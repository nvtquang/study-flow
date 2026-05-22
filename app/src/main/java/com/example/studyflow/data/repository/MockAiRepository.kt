package com.example.studyflow.data.repository

import kotlinx.coroutines.delay

class MockAiRepository : AiRepository {
    override suspend fun ask(question: String): String {
        delay(900L)
        val normalized = question.lowercase()

        return when {
            "summarize" in normalized || "tóm tắt" in normalized || "tom tat" in normalized -> {
                "Tóm tắt nhanh:\n• Xác định ý chính của tài liệu.\n• Ghi lại 3-5 luận điểm quan trọng.\n• Kết thúc bằng phần cần ôn lại."
            }
            "explain" in normalized || "giải thích" in normalized || "giai thich" in normalized -> {
                "Giải thích dễ hiểu:\n• Chia khái niệm thành từng bước nhỏ.\n• Tìm ví dụ gần với bài học.\n• Tự kiểm tra bằng một câu hỏi ngắn sau mỗi phần."
            }
            "quiz" in normalized || "tạo câu hỏi" in normalized || "tao cau hoi" in normalized -> {
                "Bộ câu hỏi luyện tập:\n1. Khái niệm chính của phần này là gì?\n2. Vì sao nó quan trọng?\n3. Hãy nêu một ví dụ áp dụng thực tế."
            }
            "key points" in normalized || "ý chính" in normalized || "y chinh" in normalized -> {
                "Ý chính cần nhớ:\n• Mục tiêu học tập.\n• Thuật ngữ trọng tâm.\n• Công thức/quy trình quan trọng.\n• Lỗi thường gặp khi làm bài."
            }
            else -> {
                "Mình có thể hỗ trợ bạn tóm tắt, giải thích, tạo câu hỏi ôn tập hoặc rút ý chính. Hãy gửi nội dung hoặc câu hỏi cụ thể hơn để mình gợi ý kế hoạch học phù hợp."
            }
        }
    }
}
