package gitlet;

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
        }
    }
    private static void ValidArgs(String[] args, int num) {
        if(args.length != num) {
            System.out.println("args length is not equal to num");
            System.exit(0);
        }
    }
}
