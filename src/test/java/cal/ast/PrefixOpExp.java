package cal.ast;

public class PrefixOpExp implements Exp {
    private String operator;
    private Exp exp;

    public PrefixOpExp(String operator, Exp exp) {
        this.operator = operator;
        this.exp = exp;
    }

    public String getOperator() {
        return operator;
    }

    public Exp getExp() {
        return exp;
    }

    @Override
    public String debugString() {
        return "(%s%s)".formatted(operator, exp.debugString());
    }
}
