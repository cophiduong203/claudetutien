# Hytur Tu Tiên 3.0 (Fabric 1.20.1)

Addon tu tiên cho modpack HyturCraft RPG. **Đây là Đợt 1/6** của dự án.

## Đã có trong Đợt 1
- 10 đại cảnh giới / 100 tầng, đột phá **tự động** (không có bế quan/thiền), tu vi dư được giữ lại.
- Nhận tu vi khi diệt quái và Boss (Boss = quái có máu tối đa >= 100, cấu hình được).
- Chống farm: giảm tu vi khi giết quá nhanh, mỗi thực thể chỉ thưởng một lần.
- Tăng chỉ số theo tầng (máu, sát thương, giáp, toughness, tốc độ, kháng đẩy, hồi máu) bằng Attribute Modifier UUID cố định.
- Lưu dữ liệu theo người chơi trong world (`data/hytur_tu_tien.dat`).
- HUD nhỏ gọn, thông báo đột phá bằng tiếng Việt, hiệu ứng đột phá cơ bản (hạt + âm thanh).
- Lệnh `/canhgioi`, file cấu hình `config/hytur_tu_tien.json`, lang vi_vn + en_us.

## Chưa có (các đợt sau)
Đan dược + loot (Đợt 2), Phóng Kiếm/Vạn Kiếm/Thuấn Di (Đợt 3), hào quang + pháp luân + hiệu ứng đột phá điện ảnh (Đợt 4), Ngự Kiếm Phi Hành (Đợt 5), Thiên Kiếm Giáng Thế (Đợt 6).

## Build bằng GitHub Actions
1. Tạo repo GitHub, đẩy toàn bộ thư mục này lên nhánh `main`.
2. Vào tab **Actions** -> workflow **Build Hytur Tu Tien** sẽ tự chạy (hoặc bấm *Run workflow*).
3. Khi xong, tải artifact **HyturTuTien-3.0** -> bên trong là `hytur-tu-tien-3.0.0.jar`.
4. Bỏ jar vào thư mục `mods` của HyturCraft (cùng Fabric API 0.92.2+1.20.1 hoặc mới hơn cho 1.20.1).

Build local: `./gradlew clean build` (cần JDK 17). Jar nằm ở `build/libs/hytur-tu-tien-3.0.0.jar`.

## Dependency
- Minecraft 1.20.1, Fabric Loader >= 0.15.0, Fabric API (build với 0.92.2+1.20.1), Java 17.
- Mod cần cài ở **cả client và server**.

## Phím
| Phím | Chức năng |
|---|---|
| (chưa gán) | Bật/tắt HUD tu vi - gán trong Controls |

Phím H, I, Z và Space×2 sẽ được thêm ở các đợt sau. Phím R không bao giờ bị chiếm.

## Lệnh
| Lệnh | Quyền | Mô tả |
|---|---|---|
| `/canhgioi` | mọi người | Xem cảnh giới, tu vi |
| `/canhgioi help` | mọi người | Hướng dẫn |
| `/canhgioi settang <1-100>` | OP | Đặt tầng |
| `/canhgioi addqi <số>` | OP | Thêm tu vi |
| `/canhgioi reset` | OP | Đặt lại tiến độ |
| `/canhgioi test breakthrough` | OP | Xem thử hiệu ứng đột phá |

## Cân bằng
Công thức tu vi: `round(baseQi * levelGrowth^(L-1) * realmGrowth^floor((L-1)/10))`, mặc định `100`, `1.12`, `1.35`
(nhẹ hơn ví dụ 1.28/1.45 trong thiết kế vì với 1.28/1.45, tầng 100 cần khoảng 10^14 tu vi). Phần thưởng quái được nhân theo
`(yêu cầu(tầng)/yêu cầu(1))^0.8` để quái vẫn có ý nghĩa ở tầng cao. Tất cả chỉnh được trong config.

## Trạng thái kiểm thử
Chưa build/chạy thực tế (xem ghi chú bàn giao). Hãy chạy GitHub Actions và gửi log nếu có lỗi.
