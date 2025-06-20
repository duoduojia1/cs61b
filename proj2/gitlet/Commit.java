package gitlet;

// TODO: any imports you need here

import edu.princeton.cs.algs4.StdOut;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /**
     * TODO：在此添加实例变量。
     *
     * 在这里列出 Commit 类的所有实例变量，并在每个变量上方写一条有用的注释，
     * 描述该变量代表什么，以及该变量是如何被使用的。
     * 我们已经为 `message` 提供了一个示例。
     */

    /** The message of this Commit. */
    private String message;
    // 用来存储文件名和blob的映射
    private Map<String, String> fileToBlob = new HashMap<String, String>();
    private List<String> parent;
    private Date currentTime;
    private String id;
    private String timeStamp;
    private File saveCommitName;

    public Commit() {
        //初始化commit
        this.currentTime = new Date(0);
        this.timeStamp = DateToTimeStamp(this.currentTime);
        this.message = "initial commit";
        this.parent = new ArrayList<String>();
        this.id = generateId();
        this.saveCommitName = generateSaveCommitName();
    }

    public Commit(Commit other, String message) {
        this.currentTime = new Date();
        this.timeStamp = DateToTimeStamp(this.currentTime);
        this.message = message;
        this.parent = new ArrayList<String>();
        this.parent.add(other.id);
        this.fileToBlob = new HashMap<>(other.fileToBlob);
        this.id = generateId();
        this.saveCommitName = generateSaveCommitName();
    }

    public String generateId() {
        return Utils.sha1(message, parent.toString(), timeStamp, fileToBlob.toString());
    }

    public String DateToTimeStamp(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String formatted = formatter.format(date);
        return formatted;
    }

    public File generateSaveCommitName() {
        return Utils.join(Repository.GITLET_OBJECT_DIR, id);
    }
    public void save() {
        Utils.writeObject(saveCommitName, this);
    }

    public String getId() {
        return id;
    }

    public boolean isExistBlob(String filePath) {
        return fileToBlob.containsKey(filePath);
    }

    public String getBlobId(String filePath) {
        if(!isExistBlob(filePath)) {
            System.out.println("File not found");
            System.exit(0);
        }
        return fileToBlob.get(filePath);
    }

    public void put(String key, String value) {
        fileToBlob.put(key, value);
    }

    public String getDate() {
        return timeStamp;
    }
    public List<String> getParent() {
        return parent;
    }
    public String getMessage() {
        return message;
    }

    public boolean isExist(String filePath) {
        return fileToBlob.containsKey(filePath);
    }

    public void remove(String filePath) {
        fileToBlob.remove(filePath);
    }


    public Set<Map.Entry<String, String>> entrySet() {
        return fileToBlob.entrySet();
    }

    // check map
    public void check() {
        for (String key : fileToBlob.keySet()) {
            String value = fileToBlob.get(key);
            System.out.println(key + "->" + value);
        }
    }
    /* TODO: fill in the rest of this class. */
}
