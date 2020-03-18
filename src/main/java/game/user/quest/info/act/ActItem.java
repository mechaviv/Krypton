package game.user.quest.info.act;

import game.user.quest.info.QuestItemInfo;
import game.user.quest.info.QuestItemOption;

/**
 * Created by MechAviv on 1/21/2020.
 */
public class ActItem {
    private QuestItemInfo info;
    private QuestItemOption option;

    public QuestItemInfo getInfo() {
        return info;
    }

    public void setInfo(QuestItemInfo info) {
        this.info = info;
    }

    public QuestItemOption getOption() {
        return option;
    }

    public void setOption(QuestItemOption option) {
        this.option = option;
    }
}
