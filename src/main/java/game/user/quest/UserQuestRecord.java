package game.user.quest;

import common.user.DBChar;
import game.user.User;
import game.user.WvsContext;
import util.FileTime;

/**
 * Created by MechAviv on 1/22/2020.
 */
public class UserQuestRecord {
    public static final int
            QUEST_STATE_INVALID = -1,
            QUEST_STATE_NONE = 0,
            QUEST_STATE_PERFORM = 1,
            QUEST_STATE_COMPLETE = 2;

    private static boolean isInvalidQuestID(int key) {
        return key == 0 || key < 1000 || key > 30000;
    }

    public static String get(User user, int key) {
        if (isInvalidQuestID(key)) {
            return null;
        }
        if (user.lock()) {
            try {
                if (!user.getCharacter().getQuestRecord().containsKey(key)) {
                    return null;
                }
                return user.getCharacter().getQuestRecord().get(key);
            } finally {
                user.unlock();
            }
        }
        return null;
    }

    public static int getState(User user, int key) {
        if (isInvalidQuestID(key)) {
            return QUEST_STATE_INVALID;
        }
        if (user.lock()) {
            try {
                if (user.getCharacter().getQuestRecord().getOrDefault(key, null) != null) {
                    return QUEST_STATE_PERFORM;
                }
                if (user.getCharacter().getQuestComplete().getOrDefault(key, null) !=  null) {
                    return QUEST_STATE_COMPLETE;
                }
            } finally {
                user.unlock();
            }
        }
        return QUEST_STATE_NONE;
    }

    public static boolean remove(User user, int key, boolean complete) {
        if (isInvalidQuestID(key)) {
            return false;
        }
        boolean success = false;
        if (user.lock()) {
            try {
                if (complete) {
                    success = user.getCharacter().getQuestComplete().remove(key) != null;
                } else {
                    success = user.getCharacter().removeQuest(key);
                }
                if (success) {
                    user.addCharacterDataMod(complete ? DBChar.QuestComplete : DBChar.QuestRecord);
                    user.sendPacket(WvsContext.onQuestRecordMessage(key, 0, false));
                }
            } finally {
                user.unlock();
            }
        }
        return success;
    }

    public static boolean set(User user, int key, String info) {
        if (isInvalidQuestID(key) || info != null && info.length() > 16) {
            return false;
        }
        if (user.lock()) {
            try {
                user.getCharacter().setQuest(key, info);
                user.addCharacterDataMod(DBChar.QuestRecord);
                user.sendPacket(WvsContext.onQuestRecordMessage(key, 1, info));
            } finally {
                user.unlock();
            }
        }
        return true;
    }

    public static boolean setComplete(User user, int key) {
        if (isInvalidQuestID(key)) {
            return false;
        }
        boolean success = false;
        if (user.lock()) {
            try {
                success = user.getCharacter().removeQuest(key);
                if (success) {
                    user.addCharacterDataMod(DBChar.QuestRecord);
                    FileTime ftEnd = FileTime.systemTimeToFileTime();
                    user.getCharacter().getQuestComplete().put(key, ftEnd);
                    user.addCharacterDataMod(DBChar.QuestComplete);
                    user.sendPacket(WvsContext.onQuestRecordMessage(key, 2, ftEnd));
                }
            } finally {
                user.unlock();
            }
        }
        return success;
    }
}
