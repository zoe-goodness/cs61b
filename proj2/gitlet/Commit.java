package gitlet;



import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
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
    public Commit() {

    }

    public void setBlobReference(TreeMap<String, String> blobReference) {
        this.blobReference = blobReference;
    }

    public void setFirstParentString(String firstParentString) {
        this.firstParentString = firstParentString;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setSecondParentString(String secondParentString) {
        this.secondParentString = secondParentString;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Commit commit = (Commit) o;
        return Objects.equals(message, commit.message) && Objects.equals(timestamp, commit.timestamp) && Objects.equals(blobReference, commit.blobReference) && Objects.equals(firstParentString, commit.firstParentString) && Objects.equals(secondParentString, commit.secondParentString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, timestamp, blobReference, firstParentString, secondParentString);
    }
}
