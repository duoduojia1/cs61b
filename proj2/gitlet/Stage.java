package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Stage implements Serializable {
    private Map<String, String> pathToblobID = new HashMap<String, String>();

    public void add(Blob blob) {
        pathToblobID.put(blob.getFilepath(), blob.getId());
    }

    public boolean isEmpty() {
        return pathToblobID.isEmpty();
    }

    public Set<Map.Entry<String, String>> entrySet() {
        return pathToblobID.entrySet();
    }

    public void clear() {
        pathToblobID.clear();
    }

}
