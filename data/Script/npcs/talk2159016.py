val = self.questRecordExGet(23007, "exp4")
if val == "1": self.sayNext("D'oh! You found me. But I'm tiny! Are you a professional at this game or something?")
else:
    self.sayNext("D'oh! You found me. But I'm tiny! Are you a professional at this game or something?\r\n\r\n\r\n\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 3 exp")
    self.userIncEXP(3, False)
    self.questRecordExSet(23007, "exp4", "1")