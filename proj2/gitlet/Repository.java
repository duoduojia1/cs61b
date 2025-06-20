package gitlet;

import edu.princeton.cs.algs4.StdOut;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;

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
    public static final File GITLET_REMOVE_STAGE = join(GITLET_DIR, "removeStage");
    private static Commit current_commit;
    private static Stage current_stage = new Stage();
    private static String branchName;

    private static Stage removal_stage = new Stage();



    public static void restore() {
        // 把可持久化内容写进内存
        // 先读取当前是在哪个分支， 再读取分支最新的commit指向哪里
        branchName = Utils.readContentsAsString(GITLET_HEAD);
        String commit_id = Utils.readContentsAsString(join(GITLET_heads_DIR, branchName));
        current_commit = readObject(join(GITLET_OBJECT_DIR, commit_id), Commit.class);
        current_stage = Utils.readObject(GITLET_stage, Stage.class);
        removal_stage = Utils.readObject(GITLET_REMOVE_STAGE, Stage.class);
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
        Utils.writeObject(GITLET_REMOVE_STAGE, removal_stage);
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
            current_stage.saveStage();
        }
    }

    public static File getFromPath(String filePath) {
        return Paths.get(filePath).isAbsolute() ? new File(filePath) : join(CWD, filePath);
    }

    public static void commit(String message) {
        // 如果message为空
        if(message.isEmpty()) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        if(current_stage.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        Commit newCommit = new Commit(current_commit, message);

        // 遍历暂存区的内容，同时比较currentCommit
        // 因为在加入暂存区已经判断过了，Blob必然是最新的，加入即可
        Set<Map.Entry<String, String>> set = current_stage.entrySet();
        for (Map.Entry<String, String> entry : set) {
            String key = entry.getKey();
            String value = entry.getValue();
            newCommit.put(key, value);
        }

        // 这里还要处理remove暂存区的内容
        while(!removal_stage.removal_isEmpty()) {
            String blobId = removal_stage.getRemovalPollFirst();
            Blob blob = Utils.readObject(join(GITLET_OBJECT_DIR, blobId), Blob.class);
            if(current_commit.isExistBlob(blob.getFilepath())) {
                // 不能直接删除掉Blob，可能被多个commit关联
                current_commit.remove(blob.getFilepath());
            }
        }

        // 修改当前的Head到为最新的Commit下, 覆盖掉当前的分支即可
        newCommit.save();
        moveHead(newCommit);
        // 清空暂存区
        current_stage.clear();
        removal_stage.clear();
        current_stage.saveStage();
        removal_stage.saveRemovalStage();
    }

    public static void moveHead(Commit commit) {
        File heads = join(GITLET_heads_DIR, branchName);
        writeContents(heads, commit.getId());
    }

    public static void rm(String filePath) {
        // remove暂存区只保存当前已跟踪的文件，commit逻辑还得改
        // 如果当前暂存区已经存在该文件，直接删除掉即可
        // 记得把相对路径切换到绝对路径
        File file = getFromPath(filePath);
        filePath = file.getAbsolutePath();
        if(current_stage.isExist(filePath)) {
            current_stage.remove(filePath);
        }
        // 当前追踪了该文件，但不在暂存区中, 删除工作区的文件， 加入到remove暂存区中
        // 下一次commit删除
        else if(current_commit.isExistBlob(filePath)) {
            // 可以重复加入，但是只删除一次
            removal_stage.removal_add(filePath);
            // 删除掉工作区的文件
            if(file.exists()) {
                file.delete();
            }
        }
        else {
            System.out.println("no reason to remove to file");
        }
        // 可持久化暂存区
        current_stage.saveStage();
        removal_stage.saveRemovalStage();
    }


    public static void log() {
        Commit cur_log = current_commit;
        do {
            System.out.println("===");
            System.out.println("commit" + " " + cur_log.getId());
            System.out.println("Date:" + " " + cur_log.getDate());
            System.out.println(cur_log.getMessage());
            System.out.println();
            if(cur_log.getParent().isEmpty()) {
                break;
            }
            String commit_id = cur_log.getParent().get(0);
            cur_log = readObject(join(GITLET_OBJECT_DIR, commit_id), Commit.class);
        }while(true);
    }

    public static void global_log() {
        // 因为都存在同一个目录下，所以没法用Util工具，写个递归得了
        helper_global_log(current_commit);
    }

    public static void helper_global_log(Commit commit) {
        System.out.println("===");
        System.out.println("commit" + " " + commit.getId());
        System.out.println("Date:" + " " + commit.getDate());
        System.out.println(commit.getMessage());
        System.out.println();
        if(commit.getParent().isEmpty()) {
            return;
        }
        for(int i = 0; i < commit.getParent().size(); i++) {
            String commit_id = commit.getParent().get(i);
            Commit next_commit = readObject(join(GITLET_OBJECT_DIR, commit_id), Commit.class);
            helper_global_log(next_commit);
        }
    }

    public static void find(String message) {
        int is_exist = helper_find(message, current_commit);
        if(is_exist == 0) {
            System.out.println("Found no commit with that message.");
        }
    }

    public static int helper_find(String message, Commit commit) {
        int res = 0;
        if(message.equals(commit.getMessage())) {
            System.out.println(commit.getId());
            res += 1;
        }
        if(commit.getParent().isEmpty()) {
            return res;
        }
        for(int i = 0; i < commit.getParent().size(); i++) {
            String commit_id = commit.getParent().get(i);
            Commit next_commit = readObject(join(GITLET_OBJECT_DIR, commit_id), Commit.class);
            res += helper_find(message, next_commit);
        }
        return res;
    }

    public static void status() {
        /**
         * 1. 列出当前的分支
         * 2. 暂存区的内容
         * 3. 待删除的内容
         */
        System.out.println("=== Branches ===");
        List<String> all_branch = Utils.plainFilenamesIn(GITLET_heads_DIR);
        for(String branch : all_branch) {
            if(branch.equals(branchName)) {
                System.out.println("*" + branch);
            }
            else {
                System.out.println(branch);
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        List<String> keys = new ArrayList<>();
        for (Map.Entry<String, String> entry : current_stage.entrySet()) {
            keys.add(entry.getKey());
        }

        Collections.sort(keys);

        for (String key : keys) {
            System.out.println(key);
        }
        System.out.println();

        keys.clear();
        System.out.println("=== Removed Files ===");
        for (Map.Entry<String, String> entry : removal_stage.entrySet()) {
            keys.add(entry.getKey());
        }

        Collections.sort(keys);

        for (String key : keys) {
            System.out.println(key);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();

        System.out.println("=== Untracked Files ===");
        System.out.println();

    }

    public static void checkout(String flag, String filename) {
        // flag就是个占位符, 先判断最新的一个提交有没有filename
        File file = getFromPath(filename);
        if(!current_commit.isExist(file.getAbsolutePath())) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String blobId = current_commit.getBlobId(file.getAbsolutePath());
        Blob blob = readObject(join(GITLET_OBJECT_DIR, blobId), Blob.class);
        Utils.writeContents(file, blob.getBytes());
    }

    public static void checkout(String id, String flag, String filename) {
        File file = getFromPath(filename);
        // 先判断commit是否存在
        File commitTarget = join(GITLET_OBJECT_DIR, id);
        if(!commitTarget.exists()) {
            System.out.println("No commit with that id exists");
            System.exit(0);
        }
        Commit historyCommit = readObject(commitTarget, Commit.class);
        if(!historyCommit.isExist(file.getAbsolutePath())) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String blobId = historyCommit.getBlobId(file.getAbsolutePath());
        Blob blob = readObject(join(GITLET_OBJECT_DIR, blobId), Blob.class);
        Utils.writeContents(file, blob.getBytes());
    }

    public static void checkout(String branchname) {
        // 切换分支名，并且保存对应分支的commit
        File branchTarget = join(GITLET_heads_DIR, branchname);
        if(!branchTarget.exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        if(branchname.equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        // 可能需要考虑下，如果被add了但是没有被commit也是不被允许的
        // 考虑内容被覆盖的情况
        if(!targetCurdiff(branchname)) {
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            System.exit(0);
        }
        // 这一部分用来找出工作目录需要删除的文件
        for(File file : targetCurRemove(branchname)) {
            file.delete();
        }
        HelperDiffAdd(branchname);
        current_stage.clear();
        removal_stage.clear();

        // 保存一下分支信息

        Utils.writeContents(GITLET_HEAD, branchname);
        current_stage.saveStage();
        removal_stage.saveStage();

    }

    public static boolean targetCurdiff(String branchname) {
        String targetCommitId = Utils.readContentsAsString(join(GITLET_heads_DIR, branchname));
        Commit targetCommit = readObject(join(GITLET_OBJECT_DIR, targetCommitId), Commit.class);
        for(String fileName: Utils.plainFilenamesIn(CWD)) {
            File file = getFromPath(fileName);
            // 判断当前文件是否未被跟踪，即既没被add和commit, 但是目标分支存在这个文件
            if(!current_stage.isExist(file.getAbsolutePath()) && !current_commit.isExist(file.getAbsolutePath())) {
                if(targetCommit.isExist(file.getAbsolutePath())) {
                    return false;
                }
            }
        }
        return true;
    }

    public static List<File> targetCurRemove(String branchname) {
        List<File> res = new ArrayList<>();
        String targetCommitId = Utils.readContentsAsString(join(GITLET_heads_DIR, branchname));
        Commit targetCommit = readObject(join(GITLET_OBJECT_DIR, targetCommitId), Commit.class);
        for(String fileName: Utils.plainFilenamesIn(CWD)) {
            File file = getFromPath(fileName);
            // 先判断是否是未跟踪，未跟踪的不能删除
            if(current_stage.isExist(file.getAbsolutePath()) || current_commit.isExist(file.getAbsolutePath())) {
                if (!targetCommit.isExist(file.getAbsolutePath())) {
                    // 如果目标Commit不存在则要删掉
                    res.add(file);
                }
            }
        }
        Collections.sort(res);
        return res;
    }

    public static void HelperDiffAdd(String branchname) {
        List<File> res = new ArrayList<>();
        String targetCommitId = Utils.readContentsAsString(join(GITLET_heads_DIR, branchname));
        Commit targetCommit = readObject(join(GITLET_OBJECT_DIR, targetCommitId), Commit.class);

        for(Map.Entry<String, String> entry : targetCommit.entrySet()) {
            File file = getFromPath(entry.getKey());
            Blob blob = readObject(join(GITLET_OBJECT_DIR, entry.getValue()), Blob.class);
            Utils.writeContents(file, blob.getBytes());
        }
    }

    public static void branch(String branchname) {
        // 先检查是否存在同名的情况, 先读取所有的目录
        List<String> branchSets = Utils.plainFilenamesIn(GITLET_REFS_DIR);
        if(branchSets.contains(branchname)) {
            System.out.println("branch with that name already exists.");
            System.exit(0);
        }

        File newbranch = join(GITLET_heads_DIR, branchname);
        Utils.writeContents(newbranch, current_commit.getId());
    }

    public static void rm_branch(String branchname) {
        List<String> branchSets = Utils.plainFilenamesIn(GITLET_REFS_DIR);
        if(!branchSets.contains(branchname)) {
            System.out.println("A branch with that name does not exist.");
        }
        if(branchName.equals(branchname)) {
            System.out.println("Cannot remove the current branch.");
        }
        File removeBranch = join(GITLET_heads_DIR, branchname);
        removeBranch.delete();
    }


    public static void reset(String commitId) {
        // 这里是把当前的branchHead强行改成了commitId
        // 判断一下是否存在这个commit

        List<String> commitIdSets = Utils.plainFilenamesIn(GITLET_OBJECT_DIR);
        if(!commitIdSets.contains(commitId)) {
            System.out.println("No commit with that id exists.");
        }
        if(!targetCommitCurdiff(commitId)) {
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
        }
        for(File file: targetCommitCurRemove(commitId)) {
            file.delete();
        }
        HelperCommitDiffAdd(commitId);
        current_stage.clear();
        removal_stage.clear();
        current_stage.saveStage();
        removal_stage.saveStage();
    }

    public static boolean targetCommitCurdiff(String commitId) {
        Commit targetCommit = readObject(join(GITLET_OBJECT_DIR, commitId), Commit.class);
        for(String fileName: Utils.plainFilenamesIn(CWD)) {
            File file = getFromPath(fileName);
            // 判断当前文件是否未被跟踪，即既没被add和commit, 但是目标分支存在这个文件
            if(!current_stage.isExist(file.getAbsolutePath()) && !current_commit.isExist(file.getAbsolutePath())) {
                if(targetCommit.isExist(file.getAbsolutePath())) {
                    return false;
                }
            }
        }
        return true;
    }

    public static List<File> targetCommitCurRemove(String commitId) {
        List<File> res = new ArrayList<>();
        Commit targetCommit = readObject(join(GITLET_OBJECT_DIR, commitId), Commit.class);
        for(String fileName: Utils.plainFilenamesIn(CWD)) {
            File file = getFromPath(fileName);
            // 先判断是否是未跟踪，未跟踪的不能删除
            if(current_stage.isExist(file.getAbsolutePath()) || current_commit.isExist(file.getAbsolutePath())) {
                if (!targetCommit.isExist(file.getAbsolutePath())) {
                    // 如果目标Commit不存在则要删掉
                    res.add(file);
                }
            }
        }
        Collections.sort(res);
        return res;
    }

    public static void HelperCommitDiffAdd(String commitId) {
        List<File> res = new ArrayList<>();
        Commit targetCommit = readObject(join(GITLET_OBJECT_DIR, commitId), Commit.class);

        for(Map.Entry<String, String> entry : targetCommit.entrySet()) {
            File file = getFromPath(entry.getKey());
            Blob blob = readObject(join(GITLET_OBJECT_DIR, entry.getValue()), Blob.class);
            Utils.writeContents(file, blob.getBytes());
        }
    }
}
