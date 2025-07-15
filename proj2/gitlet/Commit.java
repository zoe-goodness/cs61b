package gitlet;



import java.io.Serializable;
import java.util.Date;
import java.util.TreeMap;

/** Represents a gitlet commit object.
 *
 *  does at a high level.
 *
 *  @author syx
 */
public class Commit implements Serializable {
    /**
     *
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private Date timestamp;
    private TreeMap<String, String> blobReference;
    private String firstParentString;
    private transient Commit firstParentObject;
    private String secondParentString;
    private transient Commit secondParentObject;

    public Commit(String message, Date timestamp, TreeMap<String, String> blobReference, String firstParentString, Commit firstParentObject, String secondParentString, Commit secondParentObject) {
        this.message = message;
        this.timestamp = timestamp;
        this.blobReference = blobReference;
        this.firstParentString = firstParentString;
        this.firstParentObject = firstParentObject;
        this.secondParentString = secondParentString;
        this.secondParentObject = secondParentObject;
    }

    public Commit getFirstParentObject() {
        return firstParentObject;
    }

    public Commit getSecondParentObject() {
        return secondParentObject;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getFirstParentString() {
        return firstParentString;
    }

    public String getMessage() {
        return message;
    }

    public String getSecondParentString() {
        return secondParentString;
    }

    public TreeMap<String, String> getBlobReference() {
        return blobReference;
    }

    public void setFirstParentObject(Commit firstParentObject) {
        this.firstParentObject = firstParentObject;
    }

    public void setSecondParentObject(Commit secondParentObject) {
        this.secondParentObject = secondParentObject;
    }
}
