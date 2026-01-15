package core.mcp.record.steps;

public class KarateStep {

    public enum Keyword {
        GIVEN, WHEN, THEN, AND, STAR
    }

    private final Keyword keyword;
    private final String expression;

    private KarateStep(Keyword keyword, String expression) {
        this.keyword = keyword;
        this.expression = expression;
    }

    public static KarateStep given(String exp) {
        return new KarateStep(Keyword.GIVEN, exp);
    }

    public static KarateStep when(String exp) {
        return new KarateStep(Keyword.WHEN, exp);
    }

    public static KarateStep and(String exp) {
        return new KarateStep(Keyword.AND, exp);
    }

    public static KarateStep star(String exp) {
        return new KarateStep(Keyword.STAR, exp);
    }

    public String render() {
        String k = keyword == Keyword.STAR ? "*" : keyword.name();
        return "  " + k + " " + expression;
    }
}
