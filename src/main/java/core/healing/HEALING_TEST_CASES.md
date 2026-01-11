# 🧪 Healing Test Cases: 9 Chiến thuật trong thực tế

Tài liệu này cung cấp 5 ví dụ cụ thể cho mỗi Strategy để bạn có thể dùng làm dữ liệu Test (Dataset) hoặc hiểu rõ các kịch bản thực tế.

---

### 1. ExactAttributeStrategy (So khớp tuyệt đối)
*Kịch bản: Dev thay đổi ID hoặc Class nhưng vẫn giữ lại các neo chính.*

1.  **Case 1**: `id="user_123"` ➔ `id="user_124"` (ID thay đổi nhẹ, tên vẫn tương đồng).
2.  **Case 2**: `name="email_addr"` ➔ `name="email_address"` (Thay đổi chính tả thuộc tính).
3.  **Case 3**: `data-testid="login-btn"` ➔ `data-testid="submit-btn"` (Nhưng text "Login" bên trong giữ nguyên).
4.  **Case 4**: Loại bỏ `id`, chỉ giữ lại `class="btn-primary login-action"`.
5.  **Case 5**: Đổi `class` từ `btn-large` sang `btn-small` nhưng giữ nguyên `name="save"`.

### 2. KeyBasedStrategy (Chuẩn hóa tên biến)
*Kịch bản: Thay đổi quy tắc đặt tên (Naming convention).*

1.  **Case 1**: `id="btnSubmit"` ➔ `id="submit-button"`.
2.  **Case 2**: `name="txt_username"` ➔ `name="userName"`.
3.  **Case 3**: `id="lbl_first_name"` ➔ `id="firstNameLabel"`.
4.  **Case 4**: `data-id="SEARCH_INPUT"` ➔ `data-id="searchInput"`.
5.  **Case 5**: `id="form-control-user"` ➔ `id="userControl"`.

### 3. TextBasedStrategy (Sức mạnh nội dung)
*Kịch bản: Thay đổi hoàn toàn code nhưng giữ nguyên chữ cho người dùng.*

1.  **Case 1**: `<button>Login</button>` ➔ `<a href="#">Login</a>` (Đổi Tag).
2.  **Case 2**: `<span title="Delete Item">🗑️</span>` ➔ `<button aria-label="Delete Item">Xóa</button>`.
3.  **Case 3**: `<label>User Name</label>` ➔ `<p>User Name:</p>`.
4.  **Case 4**: `text="Đăng ký ngay"` ➔ `text="Đăng ký tài liệu"` (Chữ thay đổi nhẹ).
5.  **Case 5**: `<input type="submit" value="Pay Now">` ➔ `<button>Pay Now</button>`.

### 4. CrossAttributeStrategy (Hoán đổi thuộc tính)
*Kịch bản: Giá trị thuộc tính bị nhảy từ chỗ này sang chỗ kia.*

1.  **Case 1**: `name="user_email"` ➔ `id="user_email"`.
2.  **Case 2**: `id="btn_save"` ➔ `data-testid="btn_save"`.
3.  **Case 3**: `placeholder="Tìm kiếm..."` ➔ `value="Tìm kiếm..."`.
4.  **Case 4**: `title="Close"` ➔ `aria-label="Close"`.
5.  **Case 5**: `formcontrolname="address"` ➔ `name="address"`.

### 5. RagHealingStrategy (AI Vector & Ngữ cảnh)
*Kịch bản: Thay đổi sâu sắc về cả chữ lẫn code, chỉ còn "ý nghĩa" là giống.*

1.  **Case 1**: `text="Login"` ➔ `text="Sign In"` (Từ đồng nghĩa).
2.  **Case 2**: `<input id="search">` ➔ `<input placeholder="Enter keywords to find products">`.
3.  **Case 3**: Nút "Thêm vào giỏ" nằm cạnh ảnh sản phẩm ➔ Nút "Mua ngay" nằm dưới giá tiền.
4.  **Case 4**: `id="f_name"` ➔ `label="First Name"`.
5.  **Case 5**: `text="Logout"` ➔ `title="Thoát khỏi hệ thống"`.

### 6. SemanticValueStrategy (Từ đồng nghĩa & Đa ngôn ngữ)
*Kịch bản: Hiểu ý nghĩa chuỗi văn bản.*

1.  **Case 1**: `Search` ➔ `Find`.
2.  **Case 2**: `Add to Cart` ➔ `Purchase`.
3.  **Case 3**: `Remove` ➔ `Delete` ➔ `Discard`.
4.  **Case 4**: `Previous` ➔ `Back`.
5.  **Case 5**: `Settings` ➔ `Options` ➔ `Configuration`.

### 7. NeighborStrategy (Dựa vào hàng xóm)
*Kịch bản: Phần tử mục tiêu bị mất hết info, nhưng phần tử bên cạnh thì không.*

1.  **Case 1**: Ô Input phía sau Label "Email".
2.  **Case 2**: Nút "X" nằm bên trong một Dialog có tiêu đề "Cảnh báo".
3.  **Case 3**: Checkbox nằm trước dòng chữ "Tôi đồng ý với điều khoản".
4.  **Case 4**: Icon nằm bên trong thẻ `<button>` có text là "Tải xuống".
5.  **Case 5**: Ô nhập mật khẩu nằm ngay dưới ô nhập tài khoản.

### 8. StructuralStrategy (Layout DOM)
*Kịch bản: Code ẩn danh hoàn toàn (như các framework tự sinh ID ngẫu nhiên).*

1.  **Case 1**: Ô input thứ 2 trong form thứ nhất.
2.  **Case 2**: Thẻ `<a>` nằm trong `div.header > div.nav`.
3.  **Case 3**: Phần tử có độ sâu DOM là 12, cùng cha với 4 phần tử khác.
4.  **Case 4**: Phần tử `<li>` cuối cùng trong danh sách `<ul>`.
5.  **Case 5**: Chuyển từ `div > span` sang `div > label` nhưng vẫn cùng cha.

### 9. VisualHealingStrategy (Hình ảnh)
*Kịch bản: Thay đổi code 100% nhưng giao diện mắt người nhìn vẫn thế.*

1.  **Case 1**: Nút "Login" đổi từ `background-color: blue` sang `background-color: darkblue`.
2.  **Case 2**: Icon Facebook ở Footer bị đổi thẻ từ `<i>` sang `svg`.
3.  **Case 3**: Banner quảng cáo đổi Text nhưng kích thước và vị trí giữ nguyên.
4.  **Case 4**: Toàn bộ hệ thống đổi CSS Framework (Vd: Material ➔ Ant Design) nhưng Layout giữ nguyên.
5.  **Case 5**: Nút "Giỏ hàng" có số lượng nhảy từ "1" sang "2" (Vẫn là cái nút đó).

---
*Gợi ý: Bạn có thể copy các kịch bản này vào file `healing_dataset.feature` để chạy thử nghiệm thực tế!*
