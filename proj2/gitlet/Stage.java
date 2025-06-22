package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

public class Stage implements Serializable {
    private Map<String, String> pathToblobID = new HashMap<String, String>();
    private Deque<String> removal_queue = new ArrayDeque<>();

    public void saveStage() {
        Utils.writeObject(Utils.join(Repository.GITLET_DIR, "stage"), this);
    }

    public void saveRemovalStage() {
        Utils.writeObject(Utils.join(Repository.GITLET_DIR, "removeStage"), this);
    }


    // --------------removal_queue方法-----------------

    public void removal_add(String filePath) {
        removal_queue.add(filePath);
    }

    public boolean removal_isEmpty() {
        return removal_queue.isEmpty();
    }

    public String getRemovalPollFirst() {
        return removal_queue.pollFirst();
    }

    public void removal_clear() {
        removal_queue.clear();
    }

    public List<String> getRemovalSet() {
        List<String> Set = new ArrayList<>();
        for(String filePath : removal_queue) {
            Set.add(filePath);
        }
        return Set;
    }
    // --------------------------------------------------

    public void add(Blob blob) {
        pathToblobID.put(blob.getFilepath(), blob.getId());
    }

    public boolean isEmpty() {
        return pathToblobID.isEmpty();
    }

    public boolean isExist(String filePath) {
        return pathToblobID.containsKey(filePath);
    }

    public void remove(String filePath) {
        pathToblobID.remove(filePath);
    }

    public Set<Map.Entry<String, String>> entrySet() {
        return pathToblobID.entrySet();
    }

    public void clear() {
        pathToblobID.clear();
    }

}
