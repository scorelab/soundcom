package soundcom.scorelab.org.soundcom;

/**
 * Created by user on 20-06-2018.
 */

public class Message {
    private String text; //message data
    private MemberData data; //username and user color
    private boolean belongsToCurrentUser; //check if message is transmitted or received

    public Message(String text, MemberData data, boolean belongsToCurrentUser) {
        this.text = text;
        this.data = data;
        this.belongsToCurrentUser = belongsToCurrentUser;
    }

    public String getText() {
        return text;
    }

    public MemberData getData() {
        return data;
    }

    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }
}
