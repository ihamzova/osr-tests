package cucumber;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ScenarioContext {

    private static final String DEFAULT = "default";
    public Table<String, String, Object> scenarioContext;

    public ScenarioContext() {
        scenarioContext = HashBasedTable.create();
    }

    public void setContext(Context key, Object value) {
        scenarioContext.put(key.toString(), DEFAULT, value);
    }

    public void setContext(Context key, String alias, Object value) {
        scenarioContext.put(key.toString(), alias, value);
    }

    public void deleteContext(Context key) {
        scenarioContext.remove(key.toString(), DEFAULT);
    }

    public void deleteContext(Context key, String alias) {
        scenarioContext.remove(key.toString(), alias);
    }

    public Object getContext(Context key) {
        return scenarioContext.get(key.toString(), DEFAULT);
    }

    public Object getContext(Context key, String alias) {
        return scenarioContext.get(key.toString(), alias);
    }

    public List<Object> getAllContext(Context key) {
        final Map<String, Object> m = scenarioContext.row(key.toString());
        return Arrays.asList(m.values().toArray());
    }

    public Boolean isContains(Context key) {
        return scenarioContext.contains(key.toString(), DEFAULT);
    }

    public Boolean isContains(Context key, String alias) {
        return scenarioContext.contains(key.toString(), alias);
    }

}
