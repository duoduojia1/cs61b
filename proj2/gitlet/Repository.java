package gitlet;

import edu.princeton.cs.algs4.StdOut;

import java.io.File;
import java.nio.file.Paths;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /**
     * TODO: 在此处添加实例变量。
     *
     * 在这里列出 Repository（仓库）类的所有实例变量，并在每个变量上方添加有用的注释，
     * 描述该变量代表什么，以及它是如何被使用的。我们已经为你提供了两个示例。
     */
    /** The current working directory. */

    public static final File CWD = new File(System.getProperty("user.dir"));

    /*
     *   .gitlet
     *      |--objects
     *      |     |--commit and blob
     *      |--refs
     *      |    |--heads  记录每个分支指向哪个commit
     *      |         |--master
     *      |--HEAD  记录当前在哪个分支
     *      |--stage
     */

    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File GITLET_OBJECT_DIR = join(GITLET_DIR, "object");
    public static final File GITLET_REFS_DIR = join(GITLET_DIR, "refs");
    public static final File GITLET_heads_DIR = join(GITLET_REFS_DIR, "heads");

    public static final File GITLET_HEAD = join(GITLET_DIR, "HEAD");
    public static final File GITLET_stage = join(GITLET_DIR, "stage");

    private static Commit current_commit;
    private static Stage current_stage = new Stage();
    private static String branchName;



    public static void restore() {
        // 把可持久化内容写进内存
        // 先读取当前是在哪个分支， 再读取分支最新的commit指向哪里
        branchName = Utils.readContentsAsString(GITLET_HEAD);
        current_commit = Utils.readObject(join(GITLET_heads_DIR, branchName), Commit.class);
        current_stage = Utils.readObject(GITLET_stage, Stage.class);
    }

    public static void isExistRepository() {
        if(!GITLET_DIR.exists()) {
            System.out.println("GITLET directory does not exist");
            System.exit(0);
        }
    }

    public static void init() {
        if(GITLET_DIR.exists()) {
            System.out.println("GITLET Repository already exists");
            System.exit(0);
        }
        GITLET_DIR.mkdir();
        GITLET_OBJECT_DIR.mkdir();
        GITLET_REFS_DIR.mkdir();
        GITLET_heads_DIR.mkdir();

        commit_init();
        HEAD_init();
        heads_init();
        stage_init();

        branchName = "master";
    }


    /* TODO: fill in the rest of this class. */
    public static void commit_init() {
        // 保存初始化节点
        Commit init_commit = new Commit();
        current_commit = init_commit;
        current_commit.save();
    }

    public static void HEAD_init() {
        writeContents(GITLET_HEAD, "master");
    }

    public static void heads_init() {
        File heads = join(GITLET_heads_DIR, "master");
        writeContents(heads, current_commit.getId());
    }

    public static void stage_init() {
        // 可持久化暂存区
        Utils.writeObject(GITLET_stage, current_stage);
    }


    public static void add(String filePath) {
        File file = getFromPath(filePath);
        if(!file.exists()) {
            System.out.println("File does not exist");
            System.exit(0);
        }
        Blob fileToBlob = new Blob(file);

        boolean flag = true;
        // 判断当前的commit中是否存在该Blob

        if(current_commit.isExistBlob(fileToBlob.getFilepath())) {
            // 如果存在则需要判断内容是否更改
            String id = current_commit.getBlobId(fileToBlob.getFilepath());
            // 反序列化出Blob内容
            Blob oldBlob = Utils.readObject(join(GITLET_OBJECT_DIR, id), Blob.class);
            // 判断和旧的内容是否相等
            if(fileToBlob.compareBlob(oldBlob)) {
                flag =false;
            }
        }
        if(flag) {
            // commit的时候直接复制原先的commit，只有是新的Blob才需要被重新加入
            fileToBlob.save();
            current_stage.add(fileToBlob);
        }
    }

    public static File getFromPath(String filePath) {
        return Paths.get(filePath).isAbsolute() ? new File(filePath) : join(CWD, filePath);
    }





}
