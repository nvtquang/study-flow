# StudyFlow

**StudyFlow** là ứng dụng quản lý học tập thông minh trên nền tảng Android, được xây dựng bằng **Kotlin**, **Jetpack Compose**, **Firebase** và kiến trúc **MVVM**.

Tên đề tài:

> Phát triển ứng dụng quản lý học tập thông minh StudyFlow trên nền tảng Android, sử dụng Firebase và kiến trúc MVVM.

---

## 1. Giới thiệu

StudyFlow giúp sinh viên quản lý quá trình học tập một cách khoa học và hiệu quả hơn thông qua các chức năng:

- Đăng ký, đăng nhập tài khoản
- Quản lý lịch học
- Quản lý deadline
- Theo dõi mục tiêu học tập
- Chế độ tập trung Pomodoro
- Kho tài liệu học tập
- Nhóm học tập
- Thảo luận nhóm
- Trợ lý AI hỗ trợ học tập
- Hồ sơ cá nhân
- Cài đặt thông báo

Ứng dụng được thiết kế theo phong cách hiện đại, thân thiện, sử dụng màu xanh dương chủ đạo kết hợp tím lavender, giao diện dạng card bo góc lớn, phù hợp với nhu cầu học tập của sinh viên.

---

## 2. Công nghệ sử dụng

### Nền tảng

- Android Native
- Kotlin

### Giao diện

- Jetpack Compose
- Material 3
- Navigation Compose

### Kiến trúc

- MVVM
- Repository Pattern
- StateFlow / LiveData
- Coroutines

### Backend

- Firebase Authentication
- Cloud Firestore
- Firebase Storage
- Firebase Cloud Messaging *(dự kiến mở rộng)*

### Công cụ phát triển

- Android Studio
- Gradle
- Firebase Console

---

## 3. Kiến trúc hệ thống

Ứng dụng sử dụng kiến trúc **MVVM** nhằm tách biệt rõ ràng giữa giao diện, xử lý logic và dữ liệu.

```text
UI Screen / Composable
        ↓
ViewModel
        ↓
Repository
        ↓
Firebase / Local Data
```

Nguyên tắc chính:

- UI không xử lý logic nghiệp vụ trực tiếp.
- UI chỉ gọi ViewModel.
- ViewModel quản lý state và gọi Repository.
- Repository chịu trách nhiệm làm việc với Firebase.
- Dữ liệu được truyền lên UI thông qua StateFlow hoặc LiveData.

---

## 4. Cấu trúc thư mục đề xuất

```text
app/
└── src/main/java/com/example/studyflow/
    ├── MainActivity.kt
    ├── StudyFlowApp.kt
    │
    ├── navigation/
    │   ├── AppNavGraph.kt
    │   ├── Routes.kt
    │   └── BottomNavItem.kt
    │
    ├── ui/
    │   ├── theme/
    │   │   ├── Color.kt
    │   │   ├── Theme.kt
    │   │   └── Type.kt
    │   │
    │   ├── components/
    │   │   ├── StudyFlowTopBar.kt
    │   │   ├── PrimaryButton.kt
    │   │   ├── StudyCard.kt
    │   │   ├── StatCard.kt
    │   │   ├── ScheduleCard.kt
    │   │   ├── GoalCard.kt
    │   │   ├── MessageBubble.kt
    │   │   └── FileItem.kt
    │   │
    │   └── screens/
    │       ├── auth/
    │       ├── home/
    │       ├── planner/
    │       ├── goals/
    │       ├── focus/
    │       ├── files/
    │       ├── groups/
    │       ├── chat/
    │       ├── ai/
    │       ├── profile/
    │       └── settings/
    │
    ├── data/
    │   ├── model/
    │   ├── repository/
    │   ├── firebase/
    │   └── local/
    │
    ├── viewmodel/
    │
    └── util/
```

---

## 5. Các màn hình chính

### 5.1. Đăng nhập / Đăng ký

Người dùng có thể tạo tài khoản và đăng nhập bằng email/password thông qua Firebase Authentication.

Chức năng:

- Đăng ký tài khoản mới
- Đăng nhập tài khoản
- Validate email, mật khẩu
- Lưu thông tin người dùng vào Firestore
- Điều hướng vào ứng dụng sau khi đăng nhập thành công

---

### 5.2. Trang chủ / Dashboard

Hiển thị tổng quan học tập của người dùng.

Bao gồm:

- Lịch học tiếp theo
- Deadline khẩn cấp
- Tiến độ học tập
- Hoạt động gần đây
- Tổng số nhiệm vụ
- Gợi ý bắt đầu phiên tập trung

---

### 5.3. Lịch trình học tập

Cho phép người dùng xem lịch học theo ngày.

Chức năng:

- Xem lịch theo từng ngày
- Hiển thị bài giảng, thực hành, kiểm tra, deadline
- Chọn ngày bằng thanh lịch ngang
- Thêm lịch học mới
- Thêm deadline mới

---

### 5.4. Quản lý mục tiêu

Giúp người dùng tạo và theo dõi mục tiêu học tập.

Chức năng:

- Thêm mục tiêu mới
- Xem mục tiêu đang thực hiện
- Xem mục tiêu đã hoàn thành
- Đánh dấu hoàn thành mục tiêu
- Xóa mục tiêu
- Tính tiến độ tổng thể

---

### 5.5. Chế độ tập trung

Cung cấp Pomodoro timer giúp người dùng tập trung học tập.

Chức năng:

- Bắt đầu phiên tập trung
- Tạm dừng
- Tiếp tục
- Đặt lại
- Lưu phiên học vào Firestore
- Ghi nhận phiên hoàn thành hoặc bị gián đoạn

---

### 5.6. Kho tài liệu

Quản lý tài liệu học tập của người dùng.

Chức năng:

- Hiển thị danh sách tài liệu
- Tìm kiếm tài liệu
- Sắp xếp theo gần đây, tên, kích thước
- Upload tài liệu lên Firebase Storage
- Lưu metadata vào Firestore

---

### 5.7. Nhóm học tập

Cho phép người dùng tham gia hoặc tạo nhóm học tập.

Chức năng:

- Xem danh sách nhóm đã tham gia
- Tìm kiếm nhóm
- Tạo nhóm mới
- Xem thông tin nhóm
- Mở màn hình thảo luận nhóm

---

### 5.8. Thảo luận nhóm

Chat realtime giữa các thành viên trong nhóm.

Chức năng:

- Gửi tin nhắn văn bản
- Hiển thị tin nhắn realtime bằng Firestore
- Hiển thị người gửi, thời gian, nội dung
- Cập nhật tin nhắn cuối cùng của nhóm

---

### 5.9. Trợ lý AI

Màn hình hỗ trợ học tập bằng AI.

Chức năng bản đầu:

- Chat với trợ lý AI
- Trả lời mock theo từ khóa
- Hỗ trợ các yêu cầu như:
  - tóm tắt nội dung
  - giải thích khái niệm
  - tạo câu hỏi ôn tập
  - rút ý chính

Kiến trúc được thiết kế để dễ thay `MockAiRepository` bằng API thật như Gemini hoặc OpenAI sau này.

---

### 5.10. Hồ sơ cá nhân

Hiển thị thông tin cá nhân và thống kê học tập.

Bao gồm:

- Avatar
- Họ tên
- Email
- Chuỗi học tập hiện tại
- Tổng giờ học
- Điểm tập trung
- Mục tiêu của tôi
- Nhóm của tôi
- Cài đặt ứng dụng
- Đăng xuất

---

## 6. Cài đặt Firebase

### Bước 1: Tạo Firebase project

Truy cập Firebase Console và tạo project mới tên **StudyFlow**.

### Bước 2: Thêm Android app

Điền package name của app, ví dụ:

```text
com.example.studyflow
```

### Bước 3: Tải file cấu hình

Tải file:

```text
google-services.json
```

Sau đó đặt vào thư mục:

```text
app/google-services.json
```

### Bước 4: Bật Authentication

Vào:

```text
Firebase Console > Authentication > Sign-in method
```

Bật:

```text
Email/Password
```

### Bước 5: Tạo Firestore Database

Vào:

```text
Firebase Console > Firestore Database
```

Chọn:

```text
Create database
```

Có thể bắt đầu bằng test mode trong quá trình phát triển.

### Bước 6: Bật Firebase Storage

Vào:

```text
Firebase Console > Storage
```

Chọn:

```text
Get started
```

---

## 7. Cách chạy project

### Bước 1: Clone project

```bash
git clone <repository-url>
cd StudyFlow
```

### Bước 2: Mở bằng Android Studio

Chọn:

```text
File > Open > Chọn thư mục project
```

### Bước 3: Sync Gradle

Chờ Android Studio tải dependencies và sync Gradle.

### Bước 4: Thêm Firebase config

Đặt file `google-services.json` vào thư mục `app/`.

### Bước 5: Chạy ứng dụng

Chọn emulator hoặc thiết bị thật, sau đó bấm:

```text
Run
```

---

## 8. Lộ trình phát triển theo phase

### Phase 0: Project setup

- Tạo project Android
- Cấu hình Kotlin, Compose
- Tạo theme
- Tạo Navigation Compose
- Tạo bottom navigation
- Tạo placeholder screens

### Phase 1: Design system

- Tạo component dùng lại
- Tạo màu sắc, typography
- Tạo card, button, top bar, progress ring
- Áp dụng visual style theo mockup

### Phase 2: Firebase Auth

- Tích hợp Firebase Authentication
- Login
- Register
- Auth state
- Điều hướng theo trạng thái đăng nhập

### Phase 3: Home/Dashboard

- Tạo dashboard
- Đọc user profile
- Hiển thị dữ liệu học tập tổng quan
- Mock fallback nếu chưa có data

### Phase 4: Planner

- Xem lịch học theo ngày
- Thêm lịch học
- Thêm deadline
- Lưu dữ liệu vào Firestore

### Phase 5: Goals

- Thêm mục tiêu
- Xem mục tiêu
- Đánh dấu hoàn thành
- Xóa mục tiêu
- Tính tiến độ

### Phase 6: Focus Mode

- Pomodoro timer
- Start, pause, resume, reset
- Lưu phiên học

### Phase 7: Files

- Hiển thị kho tài liệu
- Upload file
- Lưu metadata
- Search/sort tài liệu

### Phase 8: Groups & Chat

- Quản lý nhóm học tập
- Tạo nhóm
- Chat realtime

### Phase 9: AI Assistant

- Chat UI
- Mock AI response
- Chuẩn bị kiến trúc thay API thật

### Phase 10: Profile & Settings

- Hồ sơ cá nhân
- Cài đặt thông báo
- Logout
- Final polish

---

## 9. Tính năng hoàn thiện trong MVP

MVP nên ưu tiên:

- Đăng ký / đăng nhập
- Trang chủ
- Lịch học
- Thêm lịch học / deadline
- Mục tiêu học tập
- Pomodoro focus mode
- Hồ sơ cá nhân
- Firebase Firestore cơ bản

Các tính năng có thể mở rộng sau:

- Chat realtime nâng cao
- Upload tài liệu đầy đủ
- AI thật
- Push notification
- Đồng bộ offline
- TODO còn lại
  - Chưa có notification thật bằng WorkManager/AlarmManager.
  - AI vẫn là mock, chưa nối Gemini/OpenAI.
  - File message trong chat vẫn là placeholder.
  - Chưa có UI join nhóm bằng mã/link.
  - Chưa có rules/security Firestore/Storage trong repo.

---

## 10. Cách test toàn bộ app

  1. Đăng ký hoặc đăng nhập.
  2. Home:
      - Kiểm tra dashboard.
      - Bấm Học ngay, Mục tiêu, Nhóm học tập.
  3. Planner:
      - Thêm lịch học/deadline.
      - Kiểm tra dữ liệu trong Firestore.
  4. AI:
      - Gửi câu hỏi tóm tắt, giải thích, quiz, ý chính.
  5. Focus:
      - Start/pause/resume/reset.
      - Kiểm tra focusSessions.
  6. Profile:
      - Vào tab Hồ sơ.
      - Chỉnh sửa tên.
      - Mở Mục tiêu, Nhóm, Kho tài liệu, Cài đặt ứng dụng.
  7. Notification Settings:
      - Bật/tắt các toggle.
      - Đổi giờ báo cáo.
      - Kiểm tra Firestore collection notificationSettings.
  8. Files:
      - Upload file từ Profile > Kho tài liệu.
  9. Groups/Chat:
      - Tạo nhóm, mở chat, gửi tin nhắn.
  10. Logout:

---

## 11. Tác giả

Dự án được phát triển cho mục tiêu học tập và đồ án Android.

**StudyFlow** — Smart study management app for students.

**nvtquang**
