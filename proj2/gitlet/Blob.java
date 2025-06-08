package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.join;
import static gitlet.Utils.writeObject;

public class Blob implements Serializable {
    private String id;
    private byte[] bytes;
    // 存储Blob对应的源文件
    private File filename;
    private String filepath;
    // 这里存储哈希之后的文件
    private File savefilename;


    public Blob(File filename) {
        this.filename = filename;
        this.filepath = filename.getPath();
        this.bytes = Utils.readContents(filename);
        this.id = generateid();
        this.savefilename = generateSaveFilename();
    }
    public String generateid() {
        return Utils.sha1(filepath, bytes);
    }
    public File generateSaveFilename() {
        return join(Repository.GITLET_OBJECT_DIR, id);
    }
    public void save() {
        writeObject(savefilename, this);
    }


    // 常用方法
    public String getId() {
        return id;
    }
    public String getFilepath() {
        return filepath;
    }
    public boolean compareBlob(Blob other) {
        // 判断Blob两边内容的哈希值是否相等
        return Utils.sha1(bytes).equals(Utils.sha1(other.bytes));
    }
}
