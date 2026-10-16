# 🐍 Java Snake Game

Game Rắn Săn Mồi viết bằng **Java Swing/AWT**, có menu cấu hình, ba mức độ khó, bản đồ đổi màu theo cấp độ và bảng điểm.

---

## 🌟 Tính năng nổi bật

- **Màn hình Menu cấu hình:**
  - **Đặt tên người chơi:** Tùy chỉnh tên người chơi hiển thị trên bảng điểm.
  - **Tùy chọn độ khó:** 3 mức độ **EASY** (Thong thả), **MEDIUM** (Tiêu chuẩn), **HARD** (Thử thách) với tốc độ di chuyển riêng biệt.
  - **Tùy chọn màu sắc Rắn:** Cho phép chọn 5 tone màu thời trang (_Green, Cyan, Pink, Yellow, Orange_).

- **Hệ thống Map & Thời tiết thay đổi theo Level:**
  - **Level 1:** 🌱 _Green Valley (Ban ngày)_
  - **Level 2:** 🌙 _Dark Jungle (Rừng rậm ban đêm)_
  - **Level 3:** 🏜️ _Scorching Desert (Sa mạc nắng cháy)_
  - **Level 4+:** ❄️ _Cyber Ice Land (Vùng đất băng giá)_

- **Đồ họa & Trải nghiệm:**
  - Hiệu ứng caro chìm hiện đại.
  - Rắn bo góc mượt mà, có hiệu ứng mắt hướng theo chiều di chuyển.
  - Quả táo bo tròn có cuống lá.
  - Điểm số, Cấp độ và Kỷ lục (High Score) cập nhật thời gian thực.
  - Tạm dừng, phát hiện va chạm với tường/thân rắn và thông báo chiến thắng khi lấp đầy bàn chơi.

---

## 🎮 Cách điều khiển

### 1. Tại màn hình Menu

- **Phím `↑` / `↓`**: Di chuyển con trỏ chọn mục.
- **Phím `←` / `→`**: Thay đổi Độ khó hoặc Màu sắc Rắn.
- **Gõ phím chữ/số**: Nhập tên người chơi.
- **Phím `ENTER`**: Bắt đầu chơi.

### 2. Trong khi chơi

- **Phím `↑`, `↓`, `←`, `→`**: Điều khiển hướng di chuyển của Rắn.
- **Phím `P`**: Tạm dừng hoặc tiếp tục.
- Ăn 5 quả táo để lên một cấp; rắn tăng tốc nhẹ sau mỗi cấp.

### 3. Khi kết thúc ván

- **Phím `SPACE`**: Chơi lại ngay lập tức với cấu hình hiện tại.
- **Phím `M`**: Quay lại màn hình Menu để thay đổi cài đặt.

---

## 🛠️ Yêu cầu môi trường & Cách chạy

1. **Yêu cầu:** Java JDK 17+ (Đã thử nghiệm hoàn hảo trên JDK 24).
2. **Tải mã nguồn và chạy** (từ thư mục chứa `SnakeGame.java`):
   ```bash
   git clone https://github.com/vng-vuson102/Java-Snake-Game.git
   cd Java-Snake-Game
   javac SnakeGame.java GamePanel.java
   java SnakeGame
   ```
