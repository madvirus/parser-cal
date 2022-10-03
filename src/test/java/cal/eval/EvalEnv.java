package cal.eval;

import java.util.HashMap;
import java.util.Map;

public class EvalEnv {
    private Map<String, EvalResult> values = new HashMap<>();

    public void put(String id, EvalResult result) {
        values.put(id, result);
    }

    public EvalResult get(String id) {
        return values.get(id);
    }
}
