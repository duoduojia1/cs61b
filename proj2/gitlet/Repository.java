package gitlet;

import java.io.File;
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

    public static  Commit current_commit;

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
}
