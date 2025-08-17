package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author syx
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                if (args.length != 1) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                if (Utils.initialized()) {
                    System.out.println("A Gitlet version-control system already exists in the current directory.");
                    System.exit(0);
                }
                Repository.init();
                break;
            case "add":

                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                if (!Utils.initialized()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }
                Repository.add(args[1]);
                break;
            case "rm":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                if (!Utils.initialized()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }
                Repository.rm(args[1]);
                break;
            case "commit":
                if (args.length != 2) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                if (!Utils.initialized()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }
                Repository.commit(args[1]);
                break;
            case "log":
                if (args.length != 1) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                if (!Utils.initialized()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }
                Repository.log();
                break;
            case "checkout":
                if (args.length <= 1 || args.length >= 5) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                if (!Utils.initialized()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }
                if (args.length == 3) {
                    Repository.checkout01(args[2]);
                } else if (args.length == 4) {
                    if (!args[2].equals("--")) {
                        System.out.println("Incorrect operands.");
                        System.exit(0);
                    }
                    Repository.checkout02(args[1], args[3]);
                } else if (args.length == 2) {
                    Repository.checkout03(args[1]);
                }
                break;
            case "global-log":
                if (args.length != 1) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.global_log();
                break;
            case "find":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.find(args[1]);
                break;
            case "branch":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.branch(args[1]);
                break;
            case "status":
                if (args.length != 1) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.status();
                break;
            case "rm-branch":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.rm_branch(args[1]);
                break;
            case "reset":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.reset(args[1]);
                break;
            case "merge":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.merge(args[1]);
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
                break;

        }
    }
}
