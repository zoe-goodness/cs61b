package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.TreeMap;

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
    /** The .gitlet/temp direcory. 包括 head信息，stagedForFile信息， rmForFile信息
     */
    public static final File TEMP_DIR = join(GITLET_DIR, "temp");

    /* TODO: fill in the rest of this class. */
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
            if (head.getBlobReference().containsKey(fileName) && head.getBlobReference().get(fileName).equals(stagedForFile.get(fileName))) {
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
        if (stagedForFile.containsKey(fileName) && stagedForFile.get(fileName).equals(sha1ForFileNameForCWD(fileName))) {
            //第一种情况
            String sha1ValueForFile = stagedForFile.get(fileName);
            stagedForFile.remove(fileName);
            stagedForFile = deleteStagedAreaForCWD(fileName, stagedForFile);
            writeStagedForFile(stagedForFile);
        } else if (head.getBlobReference().containsKey(fileName) && head.getBlobReference().get(fileName).equals(sha1ForFileNameForCWD(fileName))) {
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
            Utils.error("No changes added to the commit.");
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
     * 1.java gitlet.Main checkout -- [文件名]
     * 从当前 head（即当前分支的最新提交）中取出指定文件的版本，放到工作目录中。
     * 如果该文件已经存在，则覆盖它。注意：此操作不会将文件加入暂存区（staging area）。
     * 2.java gitlet.Main checkout [提交ID] -- [文件名]
     * 从指定提交（通过 commit id 指定）中取出文件的版本，并放入工作目录，覆盖当前版本。
     * 该文件同样不会被加入暂存区。
     * 3.java gitlet.Main checkout [分支名]
     * 获取指定分支head提交中的所有文件（注意这个head是类似master这种，而不是真正的head），
     * 并把他们放在working directory中（覆盖已存在的文件版本如果存在的话）。此外，head需要指向
     * 这个指定分支。
     * 第三种情况的注意点：1.在当前分支中（head）跟踪（即commit指向的文件）但不存在于checkout分支中的
     * 任何文件都将被删除
     * 2.暂存区会被情况（除非你检出的是当前分支）（即rmForFile和stagedForFile）
     *
     * */
    public static void checkout01(String fileName) {
        head = getHead();
        if (!head.getBlobReference().containsKey(fileName)) {
            Utils.error("File does not exist in that commit.");
        } else {
            String sha1 = head.getBlobReference().get(fileName);
            byte[] bytes = Utils.getBlobFileBySha1Value(sha1);
            Utils.writeContents(join(CWD, fileName), bytes);
        }
    }
    public static void checkout02(String commitID, String fileName) {
        head = getHead();
        int sha1Length = commitID.length();
        Commit tempCommit = head;
        String fullCommitID = null;
        if (sha1Length == 40) {
            fullCommitID = commitID;
            if (!join(OBJECTS_DIR, fullCommitID.substring(0, 2)).exists()) {
                Utils.error("No commit with that id exists.");
            }
            if (!join(join(OBJECTS_DIR, fullCommitID.substring(0, 2)), fullCommitID.substring(2)).exists()) {
                Utils.error("No commit with that id exists.");
            }
        } else {
            ArrayList<String> commitIDList = new ArrayList<>();
            while (tempCommit.getFirstParentString() != null) {
                if (sha1ForCommit(tempCommit).substring(0, sha1Length).equals(commitID)) {
                    if (commitIDList.size() != 0) {
                        Utils.error("No commit with that id exists.");
                    }
                    commitIDList.add(sha1ForCommit(tempCommit));
                }
                tempCommit = getCommitByCommitSha1(tempCommit.getFirstParentString());
            }
            if (sha1ForCommit(tempCommit).substring(0, sha1Length).equals(commitID)) {
                if (commitIDList.size() != 0) {
                    Utils.error("No commit with that id exists.");
                }
                commitIDList.add(sha1ForCommit(tempCommit));
            }
            if (commitIDList.size() != 1) {
                Utils.error("No commit with that id exists.");
            }
            fullCommitID = commitIDList.get(0);
        }
        Commit commit = getCommitByCommitSha1(fullCommitID);
        if (!commit.getBlobReference().containsKey(fileName)) {
            Utils.error("File does not exist in that commit.");
        }
        String sha1ForFile = commit.getBlobReference().get(fileName);
        byte[] bytes = Utils.getBlobFileBySha1Value(sha1ForFile);
        Utils.writeContents(join(CWD, fileName), bytes);

    }
    public static void checkout03(String branchName) {

    }
    public static void global_log() {
        commitList = getCommitFile();
        for (int i = 0; i < commitList.size(); i++) {
            String sha1 = commitList.get(i);
            Commit commit = getCommitByCommitSha1(sha1);
            printCommitInfo(commit);
        }
        
    }


}
