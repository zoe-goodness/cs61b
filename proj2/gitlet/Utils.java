package gitlet;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;


/** Assorted utilities.
 *
 * Give this file a good read as it provides several useful utility functions
 * to save you some time.
 *
 *  @author P. N. Hilfinger
 */
class Utils {

    /**我自己方便用来写代码的注释 //TODO： 最后commit的时候要删除掉
     * serialize：将对象序列化为字节数组，即把commit序列化为字节数组
     * writeCommit:将可序列化对象写入文件,即把commit对象写入文件
     * sha1ForCommit:得到commit的sha1
     * sha1ForCommitPrefix:得到commit的sha1的前两个字符串
     * sha1ForCommitsuffix：得到commit的sha1的除了前两个字符串剩余的字符串
     * writeHead
     * getHead
     * getRmForFile
     * writeRmForFile
     * getStagedForFile
     * writeStagedForFile
     * writeBlob:把Blob写入到.gitlet/objects中（即把工作目录中的filename写到.gitlet/objects中）
     * sha1ForFileNameForCWD:得到的是CWD中filename的sha1，注意是CWD，不是objects中的sha1
     * deleteStagedAreaForCWD:删除暂存区的filename（包括stagedforfile和那个文件夹和那个文件）,注意暂存区此时的filename的sha1要和cwd中filename的sha1要相同
     * deleteStagedAreaForNotCWD:删除暂存区的filename（包括stagedforfile和那个文件夹和那个文件）,注意暂存区此时的filename的sha1要和cwd中filename的sha1是不同
     * getCommitByCommitSha1:通过给commit的sha1value，得到在objects中的这个Commit
     * printCommitInfo:用于log输出commit信息
     */





    /** The length of a complete SHA-1 UID as a hexadecimal numeral. */
    static final int UID_LENGTH = 40;

    /* SHA-1 HASH VALUES. */

    /** Returns the SHA-1 hash of the concatenation of VALS, which may
     *  be any mixture of byte arrays and Strings. */
    static String sha1(Object... vals) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            for (Object val : vals) {
                if (val instanceof byte[]) {
                    md.update((byte[]) val);
                } else if (val instanceof String) {
                    md.update(((String) val).getBytes(StandardCharsets.UTF_8));
                } else {
                    throw new IllegalArgumentException("improper type to sha1");
                }
            }
            Formatter result = new Formatter();
            for (byte b : md.digest()) {
                result.format("%02x", b);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException excp) {
            throw new IllegalArgumentException("System does not support SHA-1");
        }
    }

    /** Returns the SHA-1 hash of the concatenation of the strings in
     *  VALS. */
    static String sha1(List<Object> vals) {
        return sha1(vals.toArray(new Object[vals.size()]));
    }

    /* FILE DELETION */

    /** Deletes FILE if it exists and is not a directory.  Returns true
     *  if FILE was deleted, and false otherwise.  Refuses to delete FILE
     *  and throws IllegalArgumentException unless the directory designated by
     *  FILE also contains a directory named .gitlet. */
    static boolean restrictedDelete(File file) {
        if (!(new File(file.getParentFile(), ".gitlet")).isDirectory()) {
            throw new IllegalArgumentException("not .gitlet working directory");
        }
        if (!file.isDirectory()) {
            return file.delete();
        } else {
            return false;
        }
    }

    /** Deletes the file named FILE if it exists and is not a directory.
     *  Returns true if FILE was deleted, and false otherwise.  Refuses
     *  to delete FILE and throws IllegalArgumentException unless the
     *  directory designated by FILE also contains a directory named .gitlet. */
    static boolean restrictedDelete(String file) {
        return restrictedDelete(new File(file));
    }

    /* READING AND WRITING FILE CONTENTS */

    /** Return the entire contents of FILE as a byte array.  FILE must
     *  be a normal file.  Throws IllegalArgumentException
     *  in case of problems. */
    static byte[] readContents(File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("must be a normal file");
        }
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /** Return the entire contents of FILE as a String.  FILE must
     *  be a normal file.  Throws IllegalArgumentException
     *  in case of problems. */
    static String readContentsAsString(File file) {
        return new String(readContents(file), StandardCharsets.UTF_8);
    }

    /** Write the result of concatenating the bytes in CONTENTS to FILE,
     *  creating or overwriting it as needed.  Each object in CONTENTS may be
     *  either a String or a byte array.  Throws IllegalArgumentException
     *  in case of problems. */
    static void writeContents(File file, Object... contents) {
        try {
            if (file.isDirectory()) {
                throw
                    new IllegalArgumentException("cannot overwrite directory");
            }
            BufferedOutputStream str =
                new BufferedOutputStream(Files.newOutputStream(file.toPath()));
            for (Object obj : contents) {
                if (obj instanceof byte[]) {
                    str.write((byte[]) obj);
                } else {
                    str.write(((String) obj).getBytes(StandardCharsets.UTF_8));
                }
            }
            str.close();
        } catch (IOException | ClassCastException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /** Return an object of type T read from FILE, casting it to EXPECTEDCLASS.
     *  Throws IllegalArgumentException in case of problems. */
    static <T extends Serializable> T readObject(File file,
                                                 Class<T> expectedClass) {
        try {
            ObjectInputStream in =
                new ObjectInputStream(new FileInputStream(file));
            T result = expectedClass.cast(in.readObject());
            in.close();
            return result;
        } catch (IOException | ClassCastException
                 | ClassNotFoundException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /** Write OBJ to FILE. */
    static void writeObject(File file, Serializable obj) {
        writeContents(file, serialize(obj));
    }

    /* DIRECTORIES */

    /** Filter out all but plain files. */
    private static final FilenameFilter PLAIN_FILES =
        new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isFile();
            }
        };

    /** Returns a list of the names of all plain files in the directory DIR, in
     *  lexicographic order as Java Strings.  Returns null if DIR does
     *  not denote a directory. */
    static List<String> plainFilenamesIn(File dir) {
        String[] files = dir.list(PLAIN_FILES);
        if (files == null) {
            return null;
        } else {
            Arrays.sort(files);
            return Arrays.asList(files);
        }
    }

    /** Returns a list of the names of all plain files in the directory DIR, in
     *  lexicographic order as Java Strings.  Returns null if DIR does
     *  not denote a directory. */
    static List<String> plainFilenamesIn(String dir) {
        return plainFilenamesIn(new File(dir));
    }

    /* OTHER FILE UTILITIES */

    /** Return the concatentation of FIRST and OTHERS into a File designator,
     *  analogous to the {@link java.nio.file.Paths.#get(String, String[])}
     *  method. */
    static File join(String first, String... others) {
        return Paths.get(first, others).toFile();
    }

    /** Return the concatentation of FIRST and OTHERS into a File designator,
     *  analogous to the {@link java.nio.file.Paths.#get(String, String[])}
     *  method. */
    static File join(File first, String... others) {
        return Paths.get(first.getPath(), others).toFile();
    }


    /* SERIALIZATION UTILITIES */

    /** Returns a byte array containing the serialized contents of OBJ. */
    static byte[] serialize(Serializable obj) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(stream);
            objectStream.writeObject(obj);
            objectStream.close();
            return stream.toByteArray();
        } catch (IOException excp) {
            throw error("Internal error serializing commit.");
        }
    }



    /* MESSAGES AND ERROR REPORTING */

    /** Return a GitletException whose message is composed from MSG and ARGS as
     *  for the String.format method. */
    static GitletException error(String msg, Object... args) {
        return new GitletException(String.format(msg, args));
    }

    /** Print a message composed from MSG and ARGS as for the String.format
     *  method, followed by a newline. */
    static void message(String msg, Object... args) {
        System.out.printf(msg, args);
        System.out.println();
    }


    /* utils for mine */
    static boolean initialized() {
        if (Repository.GITLET_DIR.exists()) {
            return true;
        } else {
            return false;
        }
    }

    //    * sha1ForCommit:得到commit的sha1
    static String sha1ForCommit(Commit commit) {
        return sha1(serialize(commit));
    }
    //     * sha1ForCommitPrefix:得到commit的sha1的前两个字符串
    static String sha1ForCommitPrefix(Commit commit) {
        return sha1(serialize(commit)).substring(0, 2);
    }
    //     * sha1ForCommitsuffix：得到commit的sha1的除了前两个字符串剩余的字符串
    static String sha1ForCommitSuffix(Commit commit) {
        return sha1(serialize(commit)).substring(2);
    }
    //写commit
    static void writeCommit(Commit commit) {
        File firstDirectory = join(Repository.OBJECTS_DIR, sha1ForCommitPrefix(commit));
        if (!firstDirectory.exists()) {
            firstDirectory.mkdir();
        }
        writeObject(join(firstDirectory, sha1ForCommitSuffix(commit)), commit);
    }
    //写head
    static void writeHead(Commit commit) {
        writeObject(join(Repository.TEMP_DIR, "head"), commit);
    }
    //读取head
    static Commit getHead() {
        return readObject(join(Repository.TEMP_DIR, "head"), Commit.class);
    }
    //读取stagedforfile
    static TreeMap getStagedForFile() {
        return readObject(join(Repository.TEMP_DIR, "stagedForFile"), TreeMap.class);
    }
    //读取rmforfile
    static TreeMap getRmForFile() {
        return readObject(join(Repository.TEMP_DIR, "rmForFile"), TreeMap.class);
    }
    //写stagedforfile
    static void writeStagedForFile(TreeMap stagedForFile) {
        writeObject(join(Repository.TEMP_DIR, "stagedForFile"), stagedForFile);
    }
    //写rmforfile
    static void writeRmForFile(TreeMap rmForFile) {
        writeObject(join(Repository.TEMP_DIR, "rmForFile"), rmForFile);
    }
    //把Blob写入到.gitlet/objects中（即把工作目录中的filename写到.gitlet/objects中）
    static void writeBlob(String fileName) {
        String sha1ForFileName = sha1(readContentsAsString(join(Repository.CWD, fileName)));
        if (!join(Repository.OBJECTS_DIR, sha1ForFileName.substring(0, 2)).exists()) {
            join(Repository.OBJECTS_DIR, sha1ForFileName.substring(0, 2)).mkdir();
        }
        writeContents(join(join(Repository.OBJECTS_DIR, sha1ForFileName.substring(0, 2)), sha1ForFileName.substring(2)), readContents(join(Repository.CWD, fileName)));

    }
//    把Blob写入到.gitlet/objects中（即把工作目录中的filename写到.gitlet/objects中）
//    并把blob添加到stagedForFile中
    static TreeMap writeBlobForStagedArea(String fileName, TreeMap<String, String> stagedForFile) {
        writeBlob(fileName);
        stagedForFile.put(fileName, sha1ForFileNameForCWD(fileName));
        return stagedForFile;
    }

    //sha1ForFileNameForCWD:得到的是CWD中filename的sha1，注意是CWD，不是objects中的sha1
    static String sha1ForFileNameForCWD(String fileName) {
        return sha1(readContentsAsString(join(Repository.CWD, fileName)));
    }
    //deleteStagedAreaForCWD:删除暂存区的filename（包括stagedforfile和那个文件夹和那个文件）,注意暂存区此时的filename的sha1要和cwd中filename的sha1要相同
    static TreeMap deleteStagedAreaForCWD(String fileName, TreeMap stagedForFile) {
        String sha1ForFileName = sha1ForFileNameForCWD(fileName);
        stagedForFile.remove(fileName);
        if (join(Repository.OBJECTS_DIR, sha1ForFileName.substring(0, 2)).listFiles().length == 1) {
            join(join(Repository.OBJECTS_DIR, sha1ForFileName.substring(0, 2)), sha1ForFileName.substring(2)).delete();
            join(Repository.OBJECTS_DIR, sha1ForFileName.substring(0, 2)).delete();
        } else {
            join(join(Repository.OBJECTS_DIR, sha1ForFileName.substring(0, 2)), sha1ForFileName.substring(2)).delete();
        }
        return stagedForFile;
    }
    //deleteStagedAreaForNotCWD:删除暂存区的filename（包括stagedforfile和那个文件夹和那个文件）,注意暂存区此时的filename的sha1要和cwd中filename的sha1是不同
    static TreeMap deleteStagedAreaForNotCWD(String fileName, TreeMap<String, String> stagedForFile) {
        String sha1ForFileName = stagedForFile.get(fileName);
        stagedForFile.remove(fileName);
        if (join(Repository.OBJECTS_DIR, sha1ForFileName.substring(0, 2)).listFiles().length == 1) {
            join(join(Repository.OBJECTS_DIR, sha1ForFileName.substring(0, 2)), sha1ForFileName.substring(2)).delete();
            join(Repository.OBJECTS_DIR, sha1ForFileName.substring(0, 2)).delete();
        } else {
            join(join(Repository.OBJECTS_DIR, sha1ForFileName.substring(0, 2)), sha1ForFileName.substring(2)).delete();
        }
        return stagedForFile;
    }
//    getCommitByCommitSha1:通过给commit的sha1value，得到在objects中的这个Commit
    static Commit getCommitByCommitSha1(String sha1) {
        return readObject(join(join(Repository.OBJECTS_DIR, sha1.substring(0, 2)), sha1.substring(2)), Commit.class);
    }
    static void printCommitInfo(Commit commit) {
        System.out.println("===");
        System.out.println("commit " + sha1ForCommit(commit));

        if (commit.getSecondParentObject() != null) {
            System.out.println("Merge: " +
                    commit.getFirstParentString().substring(0, 7) + " " +
                    commit.getSecondParentString().substring(0, 7));
        }

        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getDefault());
        System.out.println("Date: " + sdf.format(commit.getTimestamp()));
        System.out.println(commit.getMessage());
        System.out.println();
    }
}
