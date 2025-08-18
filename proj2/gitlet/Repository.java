package gitlet;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static gitlet.Utils.*;
import static gitlet.Utils.getCommitByCommitSha1;


/** Represents a gitlet repository.
 *
 *  does at a high level.
 *
 *  @author SYX
 */
public class Repository {
    /**
     *
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    public static String currentBranch;
    public static Commit head;
    public static ArrayList<String> commitList = new ArrayList<>();
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The .gitlet/objects directory. */
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    public static TreeMap<String, String> stagedForFile = new TreeMap<>();
    public static TreeMap<String, String> rmForFile = new TreeMap<>();
    /** The .gitlet/temp direcory. 包括 head信息，stagedForFile信息， rmForFile信息,commitList信息
     */
    public static final File TEMP_DIR = join(GITLET_DIR, "temp");
    public static final File BRANCHES_DIR = join(GITLET_DIR, "branches");
    //包括currentBranch信息和各个branch的信息

    public static void init() {
        GITLET_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        Commit initialCommit = new Commit("initial commit", new Date(0), new TreeMap<String, String>(), null, null, null, null);
        writeCommit(initialCommit);
        head = initialCommit;
        TEMP_DIR.mkdir();
        commitList.add(sha1ForCommit(initialCommit));
        writeHead(head);
        writeStagedForFile(stagedForFile);
        writeRmForFile(rmForFile);
        writeCommitFile(commitList);
        BRANCHES_DIR.mkdir();
        writeCommitBranch(head, "master");
        currentBranch = "master";
        writeCurrentBranch(currentBranch);
    }

    /**
     * 真实 git 中可以同时添加多个文件，Gitlet 中每次只能添加一个文件。
     * 1.如果该文件不存在，则打印File does not exist.并退出
     * 2.如果该文件之前被标记为“待删除”（见 `gitlet rm`），则现在不再被标记为待删除。
     * 3.如果该文件是第一次被添加，即在暂存区中没有对应的fileName：
     *      第一种：如果当前head没有对应的filename或者有对应的filename但内容不相同，则加入到暂存区中
     *      第二种：如果当前head有对应的filename且内容相同，则不加入到暂存区
     * 4.如果该文件不是第一次被添加，即在暂存区中有对应的filename：
     *      第一种：如果当前暂存区的filename和要加入的filename内容相同，则不做事情
     *      第二种：如果当前暂存区的filename和要加入的filename内容不同，则
     *          第一种：如果当前head没对应的filename，则把新的filename加入到暂存区，且删除之前暂存区的filename
     *          第二种：如果当前head有对应的filename，且新的filename和head不一样，则把新的filename加入到暂存区，且删除之前暂存区的filename（和第一个一样的效果，可以一起做）
     *          第三种：如果当前head有对应的filename，且新的filename和head一样，则删除之前暂存区的filename，
     *          且不对新的做出change
     *
     * @param fileName
     */
    public static void add(String fileName) {
        head = getHead();
        rmForFile = getRmForFile();
        stagedForFile = getStagedForFile();
        //第一种情况
        if (!join(CWD, fileName).exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        } else if (rmForFile.containsKey(fileName)) {
            //第二种情况
            rmForFile.remove(fileName);
            writeRmForFile(rmForFile);
        } else if (!stagedForFile.containsKey(fileName)) {
            //第三种情况
            //第三种情况的第二种
            if (head.getBlobReference().containsKey(fileName) && head.getBlobReference().get(fileName).equals(sha1ForFileNameForCWD(fileName))) {
                return;
            } else {
                //第三种情况的第一种
                writeBlob(fileName);
                String sha1ForFileName = sha1ForFileNameForCWD(fileName);
                stagedForFile.put(fileName, sha1ForFileName);
                writeStagedForFile(stagedForFile);
            }
        } else if (stagedForFile.containsKey(fileName)) {
            //第四种情况
            //第四种情况的第一种情况
            if (stagedForFile.get(fileName).equals(sha1ForFileNameForCWD(fileName))) {
                return;
            } else {
                //第四种情况的第二种情况
                if (head.getBlobReference().containsKey(fileName) && head.getBlobReference().get(fileName).equals(sha1ForFileNameForCWD(fileName))) {
                    //第四种情况的第二种情况的第三情况
                    stagedForFile = deleteStagedAreaForNotCWD(fileName, stagedForFile);
                    writeStagedForFile(stagedForFile);
                } else {
                    //第四种情况的第二种情况的第一+二情况
                    stagedForFile = deleteStagedAreaForNotCWD(fileName, stagedForFile);
                    stagedForFile = writeBlobForStagedArea(fileName, stagedForFile);
                    writeStagedForFile(stagedForFile);
                }
            }
        }
    }

    /**
     * 1.如果该文件当前已经被暂存用于添加，则将其从暂存区中取消（unstage）。
     * 2.如果该文件在当前提交中是被追踪的，**则标记其为待删除**，并将其从工作目录中删除
     * （前提是用户没有手动删除）。不需要将blob删除，因为之前的commit需要用到它
     * 3.如果该文件**没有被追踪，也没有被暂存**，则打印No reason to remove the file.
     */
    public static void rm(String fileName) {
        head = getHead();
        stagedForFile = getStagedForFile();
        rmForFile = getRmForFile();
        if (stagedForFile.containsKey(fileName)) {
            //第一种情况
            stagedForFile.remove(fileName);
            stagedForFile = deleteStagedAreaForCWD(fileName, stagedForFile);
            writeStagedForFile(stagedForFile);
        } else if (head.getBlobReference().containsKey(fileName)) {
            //第二种情况
            rmForFile.put(fileName, head.getBlobReference().get(fileName));
            writeRmForFile(rmForFile);
            if (join(CWD, fileName).exists()) {
                join(CWD, fileName).delete();
            }
        } else {
            //第三种情况
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
    }

    /**
     * heaed，rmForFile，stagedForFile
     * 1.如果没有任何文件被暂存，则中止并打印No changes added to the commit.(即rmForFile，stagedForFile都没有东西）
     * 2.平时情况
     * 补充说明
     * 1.提交后，暂存区会被清空。
     * 2.新提交将成为“当前提交”，`head` 指针也会指向它。前一个 `head` 成为它的父提交。
     */
    public static void commit(String commitMessage) {

        head = getHead();
        rmForFile = getRmForFile();
        stagedForFile = getStagedForFile();
        commitList = getCommitFile();
        if (rmForFile.size() == 0 && stagedForFile.size() == 0) {
            System.out.println("No changes added to the commit.");
            System.exit(0);

        } else {
            TreeMap<String, String> newBlobReference = new TreeMap<>(head.getBlobReference());
            for (String s : rmForFile.keySet()) {
                newBlobReference.remove(s);
            }
            for (String s : stagedForFile.keySet()) {
                newBlobReference.put(s, stagedForFile.get(s));
            }
            Commit newCommit = new Commit(commitMessage, new Date(), newBlobReference, sha1ForCommit(head), null, null, null);
            writeCommit(newCommit);
            commitList.add(sha1ForCommit(newCommit));
            rmForFile = new TreeMap<>();
            stagedForFile = new TreeMap<>();
            head = newCommit;
            writeHead(head);
            writeStagedForFile(stagedForFile);
            writeRmForFile(rmForFile);
            writeCommitFile(commitList);
            String currentBranchName = getCurrentBranch();
            writeCommitBranch(head, currentBranchName);
        }
    }

    public static void log() {
        head = getHead();
        Commit tempCommit = head;
        while (tempCommit.getFirstParentString() != null) {
            printCommitInfo(tempCommit);
            tempCommit = getCommitByCommitSha1(tempCommit.getFirstParentString());
        }
        printCommitInfo(tempCommit);
    }

    /**
     * java gitlet.Main checkout -- [文件名]
     * 从当前 head（即当前分支的最新提交）中取出指定文件的版本，放到工作目录中。
     * 如果该文件已经存在，则覆盖它。注意：此操作不会将文件加入暂存区（staging area）。
     *
     * */
    public static void checkout01(String fileName) {
        head = getHead();
        if (!head.getBlobReference().containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);

        } else {
            String sha1 = head.getBlobReference().get(fileName);
            byte[] bytes = Utils.getBlobFileBySha1Value(sha1);
            Utils.writeContents(join(CWD, fileName), bytes);
        }
    }

    /**
     *
     *  java gitlet.Main checkout [提交ID] -- [文件名]
     *从指定提交（通过 commit id 指定）中取出文件的版本，并放入工作目录，覆盖当前版本。
     * 该文件同样不会被加入暂存区。
     * @param commitID
     * @param fileName
     */
    public static void checkout02(String commitID, String fileName) {
        head = getHead();
        int sha1Length = commitID.length();
        Commit tempCommit = head;
        String fullCommitID = null;
        if (sha1Length == 40) {
            fullCommitID = commitID;
            if (!join(OBJECTS_DIR, fullCommitID.substring(0, 2)).exists()) {
                System.out.println("No commit with that id exists.");
                System.exit(0);

            }
            if (!join(join(OBJECTS_DIR, fullCommitID.substring(0, 2)), fullCommitID.substring(2)).exists()) {
                System.out.println("No commit with that id exists.");
                System.exit(0);
            }
        } else {
            ArrayList<String> commitIDList = new ArrayList<>();
            while (tempCommit.getFirstParentString() != null) {
                if (sha1ForCommit(tempCommit).substring(0, sha1Length).equals(commitID)) {
                    if (commitIDList.size() != 0) {
                        System.out.println("No commit with that id exists.");
                        System.exit(0);
                    }
                    commitIDList.add(sha1ForCommit(tempCommit));
                }
                tempCommit = getCommitByCommitSha1(tempCommit.getFirstParentString());
            }
            if (sha1ForCommit(tempCommit).substring(0, sha1Length).equals(commitID)) {
                if (commitIDList.size() != 0) {
                    System.out.println("No commit with that id exists.");
                    System.exit(0);
                }
                commitIDList.add(sha1ForCommit(tempCommit));
            }
            if (commitIDList.size() != 1) {
                System.out.println("No commit with that id exists.");
                System.exit(0);
            }
            fullCommitID = commitIDList.get(0);
        }
        Commit commit = getCommitByCommitSha1(fullCommitID);
        if (!commit.getBlobReference().containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);

        }
        String sha1ForFile = commit.getBlobReference().get(fileName);
        byte[] bytes = Utils.getBlobFileBySha1Value(sha1ForFile);
        Utils.writeContents(join(CWD, fileName), bytes);

    }

    /**
     * java gitlet.Main checkout [分支名]
     * 获取指定分支head提交中的所有文件（注意这个head是类似master这种，而不是真正的head），
     * 并把他们放在working directory中（覆盖已存在的文件版本如果存在的话）。此外，head需要指向
     * 这个指定分支。
     * 注意点：1.在当前分支中（head）跟踪（即commit指向的文件）但不存在于checkout分支中的任何文件都将被删除
     * 2.暂存区会被清除（除非你检出的是当前分支）（即rmForFile和stagedForFile）
     * @param branchName
     */
    public static void checkout03(String branchName) {
        if (!join(BRANCHES_DIR, branchName).exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);

        }
        head = getHead();
        stagedForFile = getStagedForFile();
        rmForFile = getRmForFile();
        String currentBranchName = getCurrentBranch();
        if (currentBranchName.equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        Commit commitBranch = getCommitBranch(branchName);
        List<String> files = plainFilenamesIn(CWD);
        for (String file : files) {
            if (!head.getBlobReference().containsKey(file) && !stagedForFile.containsKey(file) && commitBranch.getBlobReference().containsKey(file)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        writeAllBlobInWorkingDirectoryForCommit(commitBranch);
        head = commitBranch;
        writeHead(head);
        stagedForFile = new TreeMap<>();
        rmForFile = new TreeMap<>();
        writeRmForFile(rmForFile);
        writeStagedForFile(stagedForFile);
        writeCurrentBranch(branchName);
    }
    public static void global_log() {
        commitList = getCommitFile();
        for (int i = 0; i < commitList.size(); i++) {
            String sha1 = commitList.get(i);
            Commit commit = getCommitByCommitSha1(sha1);
            printCommitInfo(commit);
        }

    }
    public static void find(String commitMessage) {
        commitList = getCommitFile();
        int num = 0;
        for (int i = 0; i < commitList.size(); i++) {
            String sha1 = commitList.get(i);
            Commit commit = getCommitByCommitSha1(sha1);
            if (commit.getMessage().equals(commitMessage)) {
                num += 1;
                System.out.println(sha1);
            }
        }
        if (num == 0) {
            System.out.println("Found no commit with that message.");
        }
    }
    public static void branch(String branchName) {
        if (join(BRANCHES_DIR, branchName).exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        head = getHead();
        writeCommitBranch(head, branchName);
    }
    public static void status() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
        head = getHead();
        stagedForFile = getStagedForFile();
        rmForFile = getRmForFile();
        System.out.println("=== Branches ===");
        currentBranch = getCurrentBranch();
        List<String> branches = new ArrayList<>(plainFilenamesIn(BRANCHES_DIR));
        branches.remove("currentBranch");
        Collections.sort(branches);
        for (int i = 0; i < branches.size(); i++) {
            if (currentBranch.equals(branches.get(i))) {
                System.out.println("*" + branches.get(i));
            } else {
                System.out.println(branches.get(i));
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        if (stagedForFile.size() != 0) {
            for (String fileName : stagedForFile.keySet()) {
                System.out.println(fileName);
            }
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        if (rmForFile.size() != 0) {
            for (String fileName : rmForFile.keySet()) {
                System.out.println(fileName);
            }
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        TreeMap<String, String> tempIncludingDeletedModified = new TreeMap<>();
//        TreeSet<String> tempNotIncludingDeletedModified = new TreeSet<>();
        //first
        TreeMap<String, String> blobReference = head.getBlobReference();
        for (String fileName : blobReference.keySet()) {
            if (join(CWD, fileName).exists() && !sha1ForFileNameForCWD(fileName).equals(blobReference.get(fileName)) && !stagedForFile.containsKey(fileName)) {
//                System.out.println(fileName + " (modified)");
                tempIncludingDeletedModified.put(fileName, fileName + " (modified)");
//                tempNotIncludingDeletedModified.add(fileName);
            } else if (!join(CWD, fileName).exists() && !rmForFile.containsKey(fileName)) {
                //fourth
                tempIncludingDeletedModified.put(fileName, fileName + " (deleted)");
//                tempNotIncludingDeletedModified.add(fileName);
            }
        }
        for (String fileName : stagedForFile.keySet()) {
            //third
            if (!join(CWD, fileName).exists()) {
//                System.out.println(fileName + " (deleted)");
                tempIncludingDeletedModified.put(fileName, fileName + " (deleted)");
//                tempNotIncludingDeletedModified.add(fileName);
            } else {
                //second
                if (!stagedForFile.get(fileName).equals(sha1ForFileNameForCWD(fileName))) {
                    tempIncludingDeletedModified.put(fileName, fileName + " (modified)");
//                    tempNotIncludingDeletedModified.add(fileName);
                }
            }
        }
        for (String fileName : tempIncludingDeletedModified.keySet()) {
            System.out.println(tempIncludingDeletedModified.get(fileName));
        }


        System.out.println();
        System.out.println("=== Untracked Files ===");
        List<String> fileNames = plainFilenamesIn(CWD);
        for (String fileName : fileNames) {
            if (!head.getBlobReference().containsKey(fileName) && !stagedForFile.containsKey(fileName)) {
                System.out.println(fileName);
            }
        }
        System.out.println();
    }
    public static void rm_branch(String branchName) {
        if (!join(BRANCHES_DIR, branchName).exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        currentBranch = getCurrentBranch();
        if (currentBranch.equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        join(BRANCHES_DIR, branchName).delete();
    }
    public static void reset(String commitID) {
        stagedForFile = getStagedForFile();
        rmForFile = getRmForFile();
        head = getHead();
        currentBranch = getCurrentBranch();
        if (!join(OBJECTS_DIR, commitID.substring(0, 2)).exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        String[] commitNames = join(OBJECTS_DIR, commitID.substring(0, 2)).list();
        ArrayList<String> realCommitNames = new ArrayList<>();
        for (String commitName : commitNames) {
            if (commitName.substring(0, commitID.length() - 2).equals(commitID.substring(2))) {
                realCommitNames.add(commitName);
            }
        }
        if (realCommitNames.size() != 1) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit realCommit = getCommitByCommitSha1(commitID.substring(0, 2) + realCommitNames.get(0));
        List<String> files = plainFilenamesIn(CWD);
        for (String file : files) {
            if (!head.getBlobReference().containsKey(file)) {
                if (!stagedForFile.containsKey(file)) {
                    if (realCommit.getBlobReference().containsKey(file)) {
                        System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                        System.exit(0);
                    }
                }
            }
        }
        File[] cwdFiles = CWD.listFiles();
        if (cwdFiles != null) {
            for (File cwdFile : cwdFiles) {
                cwdFile.delete();
            }
        }
        TreeMap<String, String> newCWDFiles = realCommit.getBlobReference();
        for (String newCWDFile : newCWDFiles.keySet()) {
            writeContents(join(CWD, newCWDFile), readContents(join(join(OBJECTS_DIR, newCWDFiles.get(newCWDFile).substring(0, 2)), newCWDFiles.get(newCWDFile).substring(2))));
        }
        stagedForFile = new TreeMap<>();
        rmForFile = new TreeMap<>();
        writeStagedForFile(stagedForFile);
        writeRmForFile(rmForFile);
        head = realCommit;
        writeHead(head);
        writeCommitBranch(realCommit, currentBranch);
    }
    public static void merge(String branchName) {
        head = getHead();
        rmForFile = getRmForFile();
        stagedForFile = getStagedForFile();
        String currentBranchtemp = getCurrentBranch();
        ArrayList<File> untrackedFiles = new ArrayList<>();
        for (File file : CWD.listFiles()) {
            if ((!stagedForFile.containsKey(file.getName()) && !head.getBlobReference().containsKey(file.getName())) || (rmForFile.containsKey(file.getName()))) {
                untrackedFiles.add(file);
            }
        }
        Commit branchCommit = getCommitBranch(branchName);

        //find the split node
        Commit splittingNode = null;
        TreeSet<Commit> headAllCommits = new TreeSet<>();
        Commit temp = head;
        Queue<Commit> fringeForHead = new LinkedList<>();
        HashSet<Commit> markedForHead = new HashSet<>();
        fringeForHead.add(temp);
        markedForHead.add(temp);
        while (!fringeForHead.isEmpty()) {
            Commit v = fringeForHead.remove();
            if (v.getFirstParentString() != null) {
                Commit n = getCommitByCommitSha1(v.getFirstParentString());
                if (!markedForHead.contains(n)) {
                    fringeForHead.add(n);
                    markedForHead.add(n);
                }
            }
            if (v.getSecondParentString() != null) {
                Commit n = getCommitByCommitSha1(v.getSecondParentString());
                if (!markedForHead.contains(n)) {
                    fringeForHead.add(n);
                    markedForHead.add(n);
                }
            }
        }
        Queue<Commit> fringeForBranch = new LinkedList<>();
        HashSet<Commit> markedForBranch = new HashSet<>();
        fringeForBranch.add(branchCommit);
        markedForBranch.add(branchCommit);
        while (!fringeForBranch.isEmpty()) {
            Commit v = fringeForBranch.remove();
            if (markedForHead.contains(v)) {
                splittingNode = v;
                break;
            }
            if (v.getFirstParentString() != null) {
                Commit n = getCommitByCommitSha1(v.getFirstParentString());
                if (markedForHead.contains(n)) {
                    splittingNode = n;
                    break;
                }
                if (!markedForBranch.contains(n)) {
                    fringeForBranch.add(n);
                    markedForBranch.add(n);
                }
            }
            if (v.getSecondParentString() != null) {
                Commit n = getCommitByCommitSha1(v.getSecondParentString());
                if (markedForHead.contains(n)) {
                    splittingNode = n;
                    break;
                }
                if (!markedForBranch.contains(n)) {
                    fringeForBranch.add(n);
                    markedForBranch.add(n);
                }
            }
        }
        //写如果当前分支有未跟踪的文件会被合并覆盖或删除，输出
        //There is an untracked file in the way; delete it, or add and commit it first.
        //然后退出。注意：这个检查要在执行任何操作之前完成。

        if (rmForFile.size() != 0 || stagedForFile.size() != 0) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        if (!join(BRANCHES_DIR, branchName).exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        Commit givenBranchCommit = getCommitBranch(branchName);
        if (givenBranchCommit.equals(head)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        if (splittingNode.equals(branchCommit)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
        if (splittingNode.equals(head)) {
            checkout03(branchName);
            System.out.println("Current branch fast-forwarded.");
            writeCurrentBranch(currentBranchtemp);
            System.exit(0);
        }
        TreeMap<String, String> splittingNodeBlobReference = splittingNode.getBlobReference();
        TreeMap<String, String> branchCommitBlobReference = givenBranchCommit.getBlobReference();
        TreeMap<String, String> headCommitBlobReference = head.getBlobReference();
        Commit newCommit = new Commit();
        TreeMap<String, String> newCommitBlobReference = new TreeMap<>();
        //如果当前分支有未跟踪的文件会被合并覆盖或删除，输出
        //There is an untracked file in the way; delete it, or add and commit it first.
        //然后退出。注意：这个检查要在执行任何操作之前完成。
        //1
        for (String fileName : headCommitBlobReference.keySet()) {
            if (splittingNodeBlobReference.containsKey(fileName) && splittingNodeBlobReference.get(fileName).equals(headCommitBlobReference.get(fileName)) && branchCommitBlobReference.containsKey(fileName) && !branchCommitBlobReference.get(fileName).equals(headCommitBlobReference.get(fileName))) {
                if (untrackedFiles.contains(fileName)) {
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    System.exit(0);
                }
            }
        }
        //5
        for (String fileName : branchCommitBlobReference.keySet()) {
            if (!splittingNodeBlobReference.containsKey(fileName) && !headCommitBlobReference.containsKey(fileName)) {
                if (untrackedFiles.contains(fileName)) {
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    System.exit(0);
                }
            }
        }
        //6
        for (String fileName : splittingNodeBlobReference.keySet()) {
            if (headCommitBlobReference.containsKey(fileName) && headCommitBlobReference.get(fileName).equals(splittingNodeBlobReference.get(fileName)) && !branchCommitBlobReference.containsKey(fileName)) {
                if (untrackedFiles.contains(fileName)) {
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    System.exit(0);
                }
            }
        }







        for (String fileName : headCommitBlobReference.keySet()) {
            //1
            if (splittingNodeBlobReference.containsKey(fileName) && splittingNodeBlobReference.get(fileName).equals(headCommitBlobReference.get(fileName)) && branchCommitBlobReference.containsKey(fileName) && !branchCommitBlobReference.get(fileName).equals(headCommitBlobReference.get(fileName))) {
                checkout02(sha1ForCommit(givenBranchCommit), fileName);
                add(fileName);
            }
            //2
            if (splittingNodeBlobReference.containsKey(fileName) && branchCommitBlobReference.containsKey(fileName) && branchCommitBlobReference.get(fileName).equals(splittingNodeBlobReference.get(fileName)) && !headCommitBlobReference.get(fileName).equals(splittingNodeBlobReference.get(fileName))) {
                newCommitBlobReference.put(fileName, branchCommitBlobReference.get(fileName));
            }
            //3
            if (branchCommitBlobReference.containsKey(fileName) && branchCommitBlobReference.get(fileName).equals(headCommitBlobReference.get(fileName))) {
                newCommitBlobReference.put(fileName, branchCommitBlobReference.get(fileName));
            }
            //4
            if (!splittingNodeBlobReference.containsKey(fileName) && !branchCommitBlobReference.containsKey(fileName)) {
                newCommitBlobReference.put(fileName, headCommitBlobReference.get(fileName));
            }


        }
        //5
        for (String fileName : branchCommitBlobReference.keySet()) {
            if (!splittingNodeBlobReference.containsKey(fileName) && !headCommitBlobReference.containsKey(fileName)) {
                checkout02(sha1ForCommit(givenBranchCommit), fileName);
                add(fileName);
            }
        }
        //6
        for (String fileName : splittingNodeBlobReference.keySet()) {
            if (headCommitBlobReference.containsKey(fileName) && headCommitBlobReference.get(fileName).equals(splittingNodeBlobReference.get(fileName)) && !branchCommitBlobReference.containsKey(fileName)) {
                rm(fileName);
            }
        }
        //8
        boolean conflict = false;
        commitList = getCommitFile();
        ArrayList<String> conflictFiles = new ArrayList<>();
        for (String fileName : headCommitBlobReference.keySet()) {
            //两个分支都修改了文件内容，且结果不同于对方
            if (splittingNodeBlobReference.containsKey(fileName) && !splittingNodeBlobReference.get(fileName).equals(headCommitBlobReference.get(fileName)) && branchCommitBlobReference.containsKey(fileName) && !branchCommitBlobReference.get(fileName).equals(splittingNodeBlobReference.get(fileName)) && !branchCommitBlobReference.get(fileName).equals(headCommitBlobReference.get(fileName))) {
                conflict = true;
                conflictFiles.add(fileName);
            }
            //一个分支修改了文件，另一个分支删除了文件
            if (splittingNodeBlobReference.containsKey(fileName) && !splittingNodeBlobReference.get(fileName).equals(headCommitBlobReference.get(fileName))&& !branchCommitBlobReference.containsKey(fileName)) {
                conflict = true;
                conflictFiles.add(fileName);
            }
            //文件在分裂点（split point）中不存在，但在两个分支里都出现了，且内容不同
            if (!splittingNodeBlobReference.containsKey(fileName) && branchCommitBlobReference.containsKey(fileName) && !branchCommitBlobReference.get(fileName).equals(headCommitBlobReference.get(fileName))) {
                conflict = true;
                conflictFiles.add(fileName);
            }
        }
        for (String fileName : branchCommitBlobReference.keySet()) {
            if (splittingNodeBlobReference.containsKey(fileName) && !splittingNodeBlobReference.get(fileName).equals(branchCommitBlobReference.get(fileName)) && !headCommitBlobReference.containsKey(fileName)) {
                conflict = true;
                conflictFiles.add(fileName);
            }
        }
        newCommit.setMessage("Merged " + branchName + " into " + currentBranchtemp + ".");
        newCommit.setBlobReference(newCommitBlobReference);
        newCommit.setFirstParentString(sha1ForCommit(head));
        newCommit.setSecondParentString(sha1ForCommit(givenBranchCommit));
        for (String conflictFile : conflictFiles) {
            rm(conflictFile);
        }
        for (String s : rmForFile.keySet()) {
            newCommitBlobReference.remove(s);
        }
        for (String s : stagedForFile.keySet()) {
            newCommitBlobReference.put(s, stagedForFile.get(s));
        }
        newCommit.setTimestamp(new Date());
        writeCommit(newCommit);
        commitList.add(sha1ForCommit(newCommit));
        rmForFile = new TreeMap<>();
        stagedForFile = new TreeMap<>();
        head = newCommit;
        writeHead(head);
        writeStagedForFile(stagedForFile);
        writeRmForFile(rmForFile);
        writeCommitFile(commitList);
        if (conflict) {
            for (String conflictFile : conflictFiles) {
                String conflictContent = new String();
                conflictContent += "<<<<<<< HEAD\n";
                if (headCommitBlobReference.containsKey(conflictFile)) {
                    byte[] blob = getBlobFileBySha1Value(headCommitBlobReference.get(conflictFile));
                    String content = new String(blob, StandardCharsets.UTF_8);
                    conflictContent += content;
                } else {
                    conflictContent += "";
                }

                conflictContent += "=======\n";
                if (branchCommitBlobReference.containsKey(conflictFile)) {
                    byte[] blob = getBlobFileBySha1Value(branchCommitBlobReference.get(conflictFile));
                    String content = new String(blob, StandardCharsets.UTF_8);
                    conflictContent += content;
                } else {
                    conflictContent += "";
                }
                conflictContent += ">>>>>>>\n";
                writeContents(join(CWD, conflictFile), conflictContent);
                add(conflictFile);
            }
            System.out.println("Encountered a merge conflict.");
            writeStagedForFile(stagedForFile);
        }
    }

}
