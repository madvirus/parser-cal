package cal.eval;

import cal.ast.*;

public class Evaluator {
    public EvalResult eval(Program program, EvalEnv env) {
        EvalResult result = null;
        Stmt[] stmts = program.getStmts();
        for (Stmt stmt : stmts) {
            result = evalStmt(stmt, env);
        }
        return result;
    }

    private EvalResult evalStmt(Stmt stmt, EvalEnv env) {
        if (stmt instanceof ExpStmt expStmt) {
            return evalExpStmt(expStmt, env);
        } else if (stmt instanceof LetStmt letStmt) {
            return evalLetStmt(letStmt, env);
        }
        return null;
    }

    private EvalResult evalLetStmt(LetStmt letStmt, EvalEnv env) {
        String id = letStmt.getIdExp().getId();
        EvalResult result = evalExp(letStmt.getExp(), env);
        env.put(id, result);
        return null;
    }

    private EvalResult evalExpStmt(ExpStmt expStmt, EvalEnv env) {
        return evalExp(expStmt.getExp(), env);
    }

    private EvalResult evalExp(Exp exp, EvalEnv env) {
        if (exp instanceof NumberExp numberExp) {
            return EvalResult.of(numberExp.getValue());
        } else if (exp instanceof InfixOpExp infixOpExp) {
            return evalInfixOpExp(infixOpExp, env);
        } else if (exp instanceof PrefixOpExp prefixOpExp) {
            return evalPrefixOpExp(prefixOpExp, env);
        } else if (exp instanceof IdExp idExp) {
            return evalIdExp(idExp, env);
        }
        return null;
    }

    private EvalResult evalInfixOpExp(InfixOpExp infixOpExp, EvalEnv env) {
        EvalResult left = evalExp(infixOpExp.getLeft(), env);
        EvalResult right = evalExp(infixOpExp.getRight(), env);
        return switch (infixOpExp.getOperator()) {
            case "+" -> new EvalResult(left.getValue().add(right.getValue()));
            case "-" -> new EvalResult(left.getValue().subtract(right.getValue()));
            case "*" -> new EvalResult(left.getValue().multiply(right.getValue()));
            case "/" -> new EvalResult(left.getValue().divide(right.getValue()));
            case "**" -> new EvalResult(left.getValue().pow(right.getValue().intValue()));
            default -> null;
        };
    }

    private EvalResult evalPrefixOpExp(PrefixOpExp prefixOpExp, EvalEnv env) {
        EvalResult result = evalExp(prefixOpExp.getExp(), env);
        return switch(prefixOpExp.getOperator()) {
            case "-" -> new EvalResult(result.getValue().negate());
            default -> null;
        };
    }

    private EvalResult evalIdExp(IdExp idExp, EvalEnv env) {
        String id = idExp.getId();
        return env.get(id);
    }

}
