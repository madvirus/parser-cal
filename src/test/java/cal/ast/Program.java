package cal.ast;

public class Program implements Node {
    private Stmt[] stmts;

    public Program(Stmt[] stmts) {
        this.stmts = stmts;
    }

    public Stmt[] getStmts() {
        return stmts;
    }

    @Override
    public String debugString() {
        StringBuilder sb = new StringBuilder();
        for (Stmt stmt : stmts) {
            sb.append(stmt.debugString());
        }
        return sb.toString();
    }
}
