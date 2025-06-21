package gitlet;

import java.time.temporal.ValueRange;
import java.util.ResourceBundle;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                ValidArgs(args, 1);
                Repository.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                ValidArgs(args, 2);
                Repository.isExistRepository();
                Repository.restore();
                Repository.add(args[1]);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                ValidArgs(args, 2);
                Repository.isExistRepository();
                Repository.restore();
                Repository.commit(args[1]);
                break;
            case "rm":
                ValidArgs(args, 2);
                Repository.isExistRepository();
                Repository.restore();
                Repository.rm(args[1]);
                break;
            case "log":
                ValidArgs(args, 1);
                Repository.isExistRepository();
                Repository.restore();
                Repository.log();
                break;
            case "global_log":
                ValidArgs(args, 1);
                Repository.isExistRepository();
                Repository.restore();
                Repository.global_log();
                break;
            case "find":
                ValidArgs(args, 2);
                Repository.isExistRepository();
                Repository.restore();
                Repository.find(args[1]);
                break;
            case "status":
                ValidArgs(args, 1);
                Repository.isExistRepository();
                Repository.restore();
                Repository.status();
                break;
            case "checkout":
                Repository.isExistRepository();
                Repository.restore();
                if(ValidIsNum(args, 3)) {
                    Repository.checkout(args[1], args[2]);
                }
                if(ValidIsNum(args, 4)) {
                    Repository.checkout(args[1], args[2], args[3]);
                }
                if(ValidIsNum(args, 2)) {
                    Repository.checkout(args[1]);
                }
                break;
            case "branch":
                ValidArgs(args, 2);
                Repository.isExistRepository();
                Repository.restore();
                Repository.branch(args[1]);
                break;
            case "rm-branch":
                ValidArgs(args, 2);
                Repository.isExistRepository();
                Repository.restore();
                Repository.rm_branch(args[1]);
            case "reset":
                ValidArgs(args, 2);
                Repository.isExistRepository();
                Repository.restore();
                Repository.reset(args[1]);
            case "merge":
                ValidArgs(args, 2);
                Repository.isExistRepository();
                Repository.restore();
                Repository.merge(args[1]);
        }
    }
    private static void ValidArgs(String[] args, int num) {
        if(args.length != num) {
            System.out.println("args length is not equal to num");
            System.exit(0);
        }
    }
    private static boolean ValidIsNum(String[] args, int num) {
        return args.length == num;
    }
}
