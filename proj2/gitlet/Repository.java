package gitlet;

import java.io.File;
import java.util.Date;
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
        writeHead(head);
        writeStagedForFile(stagedForFile);
        writeRmForFile(rmForFile);
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
            rmForFile = new TreeMap<>();
            stagedForFile = new TreeMap<>();
            head = newCommit;
            writeHead(head);
            writeStagedForFile(stagedForFile);
            writeRmForFile(rmForFile);
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


}
