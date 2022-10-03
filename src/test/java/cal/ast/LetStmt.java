package cal.ast;

public class LetStmt implements Stmt {
    private IdExp idExp;
    private Exp exp;

    public LetStmt(IdExp idExp, Exp exp) {
        this.idExp = idExp;
        this.exp = exp;
    }

    public IdExp getIdExp() {
        return idExp;
    }

    public Exp getExp() {
        return exp;
    }

    @Override
    public String debugString() {
        return "(%s = %s)".formatted(idExp.debugString(), exp.debugString());
    }
}
