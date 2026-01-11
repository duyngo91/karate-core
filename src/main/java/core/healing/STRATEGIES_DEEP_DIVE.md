# 🕵️ Deep Dive: Chi tiết 9 Chiến thuật Healing

Tài liệu này giải thích "thuật toán" bên trong từng Strategy để bạn hiểu code đang làm gì khi đọc lại sau này.

---

### 1. ExactAttributeStrategy (Mỏ neo thuộc tính)
*   **Logic**: Quét qua tất cả các bộ thuộc tính (id, name, class...) và so sánh độ giống nhau của chuỗi (String Similarity). 
*   **Điểm đặc biệt**: Nếu tìm thấy bất kỳ một thuộc tính nào (ví dụ `id`) giống hệt 100%, hoặc Text giống 100%, nó sẽ trả về điểm tuyệt đối ngay lập tức (**Early Exit**).
*   **Ưu điểm**: Cực nhanh và chính xác nhất cho các thay đổi nhỏ.
*   **Ví dụ**: `id="login_v1"` đổi thành `id="login_v2"`.

### 2. KeyBasedStrategy (Chuyên gia giải mã tên biến)
*   **Logic**: Sử dụng bộ lọc `KeyNormalizer` để loại bỏ các tiền tố kỹ thuật (prefix) như `btn-`, `txt_`, `lbl-` và chuyển về `lowercase`.
*   **Điểm đặc biệt**: Nó hiểu rằng `btnSubmit`, `submit_button` và `SUBMIT` thực chất là cùng một ý nghĩa.
*   **Ưu điểm**: Vượt qua được việc thay đổi quy tắc đặt tên (Naming Convention) của Developer.

### 3. TextBasedStrategy (Người đọc nội dung)
*   **Logic**: Tập trung 100% vào nội dung hiển thị cho User. Nó gán trọng số cho từng loại thẻ (Tag Weight).
*   **Điểm đặc biệt**: Thẻ `<label>` và `<span>` được ưu tiên cao hơn thẻ `<div>` vì chúng thường chứa text định nghĩa cho ô nhập liệu.
*   **Ưu điểm**: Cực kỳ hiệu quả cho các Element không có thuộc tính ID ổn định nhưng có Text cố định.

### 4. CrossAttributeStrategy (Kẻ hoán đổi thuộc tính)
*   **Logic**: Sử dụng `AttributeGroup` để so khớp "chéo". 
*   **Điểm đặc biệt**: Nó kiểm tra xem giá trị `name` cũ có đang nằm trong `id` mới hay không, hoặc `data-testid` cũ có nhảy sang `alt` mới không.
*   **Ưu điểm**: Xử lý tốt khi Dev cấu trúc lại code (Refactor) khiến giá trị thuộc tính bị nhảy từ chỗ này sang chỗ kia.

### 5. RagHealingStrategy (Bộ não AI Vector)
*   **Logic**: Chuyển toàn bộ ngữ cảnh của phần tử (Tag + Text + Attr + Neighbor) thành một mảng số (**Vector Embedding**).
*   **Điểm đặc biệt**: Sử dụng toán học **Cosine Similarity** để đo khoảng cách giữa 2 Vector. 
*   **Ưu điểm**: Đây là Strategy thông minh nhất. Nó hiểu "ngữ cảnh" sống của phần tử đó thay vì chỉ nhìn vào mặt chữ.

### 6. SemanticValueStrategy (Thông thái ngôn ngữ)
*   **Logic**: Sử dụng NLP (Natural Language Processing) để so sánh ý nghĩa của Label, Placeholder.
*   **Điểm đặc biệt**: Nó biết `Search` và `Tìm kiếm` hoặc `Login` và `Đăng nhập` (trong đa ngôn ngữ) là tương đồng.
*   **Ưu điểm**: Cứu được các case thay đổi nội dung chữ nhưng vẫn mang cùng chức năng.

### 7. NeighborStrategy (Người hàng xóm tốt bụng)
*   **Logic**: Nhìn vào phần tử đứng trước (Previous Sibling).
*   **Điểm đặc biệt**: Nếu một ô Input bị mất sạch ID, nhưng nó vẫn nằm cạnh một Label tên là "Mật khẩu", Strategy này sẽ khẳng định đó chính là ô nhập mật khẩu.
*   **Ưu điểm**: "Mỏ neo" ngữ cảnh cực mạnh cho các phần Form nhập liệu.

### 8. StructuralStrategy (Xương sống DOM)
*   **Logic**: Kiểm tra "gia phả" của phần tử: cùng thẻ Tag, cùng Form cha, cùng độ sâu trong cây DOM, cùng vị trí index.
*   **Điểm đặc biệt**: Nó có một "Boost score" cực mạnh nếu khoảng cách vị trí (indexDistance) chỉ là 1 đơn vị so với cũ.
*   **Ưu điểm**: Là cứu cánh khi mọi thuộc tính text/attr đều bị thay đổi hoàn toàn nhưng layout web vẫn giữ nguyên.

### 9. VisualHealingStrategy (Nhận diện khuôn mặt)
*   **Logic**: So sánh ảnh chụp thực tế của phần tử bằng thuật toán SSIM (Structural Similarity Index).
*   **Điểm đặc biệt**: Hoạt động theo cơ chế **Lazy Capture**. Nó chỉ chụp ảnh khi thực sự cần thiết để không làm chậm hiệu năng.
*   **Ưu điểm**: Là lớp phòng thủ cuối cùng. Nếu code đổi, text đổi, nhưng "nhìn" vẫn giống cái nút đó thì nó vẫn chọn đúng.

---
*Hy vọng tài liệu này giúp bạn tự tin bảo trì và nâng cấp hệ thống trong tương lai!*
