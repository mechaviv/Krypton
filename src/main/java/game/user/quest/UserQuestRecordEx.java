package game.user.quest;

import common.user.DBChar;
import game.user.User;
import game.user.WvsContext;

/**
 * Created by MechAviv on 3/31/2020.
 */
public class UserQuestRecordEx {
    public static String get(User user, int questID, String key) {
        if (user.lock()) {
            try {
                return user.getCharacter().getQuestEx(questID, key);
            } finally {
                user.unlock();
            }
        }
        return null;
    }

    public static boolean set(User user, int questID, String key, String info) {
        if (user.lock()) {
            try {
                user.getCharacter().setQuestEx(questID, key, info);
                user.addCharacterDataMod(DBChar.QuestRecordEx);
                user.sendPacket(WvsContext.onQuestRecordExMessage(questID, user.getCharacter().getQuestRecordEx().get(questID).getRawString()));
            } finally {
                user.unlock();
            }
        }
        return true;
    }
}
