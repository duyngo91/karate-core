package core.healing.model;

import core.healing.HealingConfig;
import core.healing.model.ElementNode;
import core.healing.model.HealingResult;
import core.healing.model.StrategyMatch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ElementScore {
    public double roleScore = 1.0;

    public final ElementNode element;

    public List<StrategyMatch> matches = new ArrayList<>();

    public int passCount = 0;
    public int totalStrategies = 0;

    public double totalWeightedScore = 0;
    public double bestRawScore = 0;

    private double confidence = -1;

    public ElementScore(ElementNode element) {
        this.element = element;
    }

    /**
     * Tính toán độ tin cậy (Confidence) tổng hợp của ứng cử viên [0.0 - 1.0].
     *
     * CÔNG THỨC 45 - 35 - 20 (Hybrid Confidence):
     * -------------------------------------------
     * 1. 45% - Normalized Weighted (Sức mạnh tập thể):
     *    Điểm trung bình của tất cả các strategy đã chạy.
     *    => Mục đích: Đảm bảo phần tử "tốt đều" ở nhiều khía cạnh (ID, Tag, Cấu trúc...).
     *
     * 2. 35% - Best Raw Score (Đỉnh cao cá nhân):
     *    Điểm số cao nhất mà một strategy bất kỳ đạt được.
     *    => Mục đích: "Cứu" ứng cử viên nếu chỉ còn duy nhất 1 dấu hiệu đúng (Vd: chỉ placeholder còn đúng).
     *
     * 3. 20% - Pass Ratio (Sự đồng thuận):
     *    Tỷ lệ bao nhiêu strategy vượt ngưỡng HEALING_THRESHOLD.
     *    => Mục đích: Tăng niềm tin nếu nhiều strategy cùng "gật đầu" chọn ứng cử viên này.
     *
     * CUỐI CÙNG: Nhân với roleScore.
     *    => Nhằm "loại bỏ" những phần tử sai vai trò (Vd: healing từ Button sang Input).
     *
     * VÍ DỤ: 4 strategy chạy, KeyBased chấm 0.9 (PASS), các cái khác chấm 0.1 (FAIL).
     *   - normalizedWeighted = (0.9 + 0.1 + 0.1 + 0.1) / 4 = 0.3
     *   - bestRawScore = 0.9
     *   - passRatio = 1 / 4 = 0.25
     *   => Confidence = (0.3 * 0.45) + (0.9 * 0.35) + (0.25 * 0.20) = 0.50 (Trung bình).
     *
     * HƯỚNG DẪN TUNING (Khi nào đổi 45% và 35%):
     * -------------------------------------------
     * - Ưu tiên 45% Mean / 35% Max (Mặc định - AN TOÀN): 
     *      Dùng khi Web khá ổn định. Hệ thống chỉ heal khi phần tử "giống về tổng thể". 
     *      => Giảm thiểu tối đa việc chọn nhầm phần tử (False Positives).
     *
     * - Ưu tiên 35% Mean / 45% Max (LIỀU LĨNH): 
     *      Dùng khi Web thay đổi quá nhanh (Refactor mạnh). Hệ thống sẽ cực kỳ tin vào
     *      chỉ một dấu hiệu duy nhất còn sót lại.
     *      => Cứu được nhiều case khó nhưng dễ chọn nhầm nếu có các phần tử giống nhau.
     *
     * VÍ DỤ THỰC TẾ:
     * -------------------------------------------
     * Case A (Chọn 45% Mean - An toàn): Trang Web có 10 cái nút "Mua hàng" giống hệt nhau. 
     *      Nếu bạn dùng Max (45%), chỉ cần 1 cái nút bất kỳ trùng 1 thuộc tính nhỏ, AI sẽ 
     *      chọn nhầm ngay. Dùng Mean (45%) sẽ bắt AI phải kiểm tra cả vị trí, hàng xóm 
     *      để chọn đúng cái nút thứ 3 chẳng hạn.
     *
     * Case B (Chọn 45% Max - Liều lĩnh): Ô nhập "Username" bị Dev đổi sạch từ ID, Class 
     *      đến vị trí DOM do chuyển từ Angular sang React. Chỉ còn duy nhất thuộc tính 
     *      placeholder="Username" là giữ nguyên. Lúc này phải tăng Max (45%) để AI dám 
     *      tin vào cái placeholder đó mà "cứu" case test.
     */
    public double getConfidence() {
        if (confidence >= 0) return confidence;

        double normalizedWeighted =
                totalStrategies == 0 ? 0 :
                        totalWeightedScore / totalStrategies;

        double passRatio =
                totalStrategies == 0 ? 0 :
                        (double) passCount / totalStrategies;

        // Tính công thức Hybrid dựa trên mode
        String mode = HealingConfig.getInstance().getHealingMode();
        if ("RECKLESS".equals(mode)) {
            // Mode "Liều lĩnh" (35-45-20): Tin vào bestRawScore hơn (nhạy bén hơn nhưng dễ sai hơn)
            confidence =
                    0.35 * clamp(normalizedWeighted) +
                    0.45 * clamp(bestRawScore) +
                    0.20 * clamp(passRatio);
        } else {
            // Mode "An toàn" (45-35-20): Tin vào normalizedWeighted hơn (ổn định hơn)
            confidence =
                    0.45 * clamp(normalizedWeighted) +
                    0.35 * clamp(bestRawScore) +
                    0.20 * clamp(passRatio);
        }

        // 👇 SCALE CUỐI – Kiểm tra tư cách phần tử (Role/Tag)
        confidence *= clamp(roleScore);

        return confidence;
    }

    public HealingResult getHealingResult() {
        element.genLocator();
        return new HealingResult(
                element,
                getConfidence(),
                getBestStrategyName()
        );
    }

    private String getBestStrategyName() {
        return matches.stream()
                .max(Comparator.comparingDouble(StrategyMatch::getWeightedScore))
                .map(m -> m.getStrategy().getName())
                .orElse("Unknown");
    }

    private double clamp(double v) {
        return Math.max(0, Math.min(1, v));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("\n================ ELEMENT SCORE ================\n");

        // Element summary
        sb.append(element);

        sb.append("\nSummary:\n");
        sb.append("  passCount          : ").append(passCount).append("\n");
        sb.append("  confidence       : ").append(getConfidence()).append("\n");
        sb.append("  bestRawScore       : ").append(format(bestRawScore)).append("\n");
        sb.append("  totalWeightedScore : ").append(format(totalWeightedScore)).append("\n");

        sb.append("\nStrategy Breakdown:\n");

        for (StrategyMatch m : matches) {
            sb.append("  - ")
                    .append(m.getStrategy().getName())
                    .append(" | raw=")
                    .append(format(m.getRawScore()))
                    .append(" | weighted=")
                    .append(format(m.getWeightedScore()));

            if (m.getRawScore() >= HealingConfig.HEALING_THRESHOLD) {
                sb.append("  ✔ PASS");
            }

            sb.append("\n");
        }

        sb.append("===============================================\n");
        return sb.toString();
    }
    private String format(double v) {
        return String.format("%.3f", v);
    }

    private String truncate(String s, int max) {
        if (s == null) return "null";
        return s.length() > max ? s.substring(0, max) + "..." : s;
    }
}
