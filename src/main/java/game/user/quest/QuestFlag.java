package game.user.quest;

/**
 * Created by MechAviv on 1/22/2020.
 */
public class QuestFlag {
    public static final int
            QuestReq_LostItem = 0,
            QuestReq_AcceptQuest = 1,
            QuestReq_CompleteQuest = 2,
            QuestReq_ResignQuest = 3,
            QuestReq_OpeningScript = 4,
            QuestReq_CompleteScript = 5,

    QuestRes_Start_QuestTimer = 6,
            QuestRes_End_QuestTimer = 7,
            QuestRes_Start_TimeKeepQuestTimer = 8,
            QuestRes_End_TimeKeepQuestTimer = 9,
            QuestRes_Act_Success = 10,
            QuestRes_Act_Failed_Unknown = 11,
            QuestRes_Act_Failed_Inventory = 12,
            QuestRes_Act_Failed_Meso = 13,
            QuestRes_Act_Failed_Pet = 14,
            QuestRes_Act_Failed_Equipped = 15,
            QuestRes_Act_Failed_OnlyItem = 16,
            QuestRes_Act_Failed_TimeOver = 17,
            QuestRes_Act_Reset_QuestTimer = 18;
}
