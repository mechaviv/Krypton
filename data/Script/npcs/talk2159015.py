val = self.questRecordExGet(23007, "exp3")
if val == "1": self.sayNext("Hehehe... I should have hidden somewhere else.")
else:
    self.sayNext("Aw shucks. You found me. Wow, you're really good at this game!\r\n\r\n\r\n\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 3 exp")
    self.userIncEXP(3, False)
    self.questRecordExSet(23007, "exp3", "1")