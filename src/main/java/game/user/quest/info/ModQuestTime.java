package game.user.quest.info;

import util.FileTime;

/**
 * Created by MechAviv on 1/20/2020.
 */
public class ModQuestTime {
    private int questID;
    private FileTime start;
    private FileTime end;

    public int getQuestID() {
        return questID;
    }

    public void setQuestID(int questID) {
        this.questID = questID;
    }

    public FileTime getStart() {
        return start;
    }

    public void setStart(FileTime start) {
        this.start = start;
    }

    public FileTime getEnd() {
        return end;
    }

    public void setEnd(FileTime end) {
        this.end = end;
    }
}
