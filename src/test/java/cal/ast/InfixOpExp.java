package cal.ast;

public class InfixOpExp implements Exp {
    private String operator;
    private Exp left;
    private Exp right;

    public InfixOpExp(String operator, Exp left, Exp right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public String getOperator() {
        return operator;
    }

    public Exp getLeft() {
        return left;
    }

    public Exp getRight() {
        return right;
    }

    @Override
    public String debugString() {
        return "(%s %s %s)".formatted(left.debugString(), operator, right.debugString());
    }
}
