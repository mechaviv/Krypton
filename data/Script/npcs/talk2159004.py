val = self.questRecordExGet(23007, "exp2")
if val == "1": self.sayNext("Have you found Jun and Von yet? Von's going to be pretty hard to find. Better keep your eyes open.")
else:
    self.sayNext("Haha, you found me. Guess I should've found a better hiding spot.")
    self.sayNext("Have you found Jun and Von yet? Von's going to be pretty hard to find. Better keep your eyes open.\r\n\r\n\r\n\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 5 exp")
    self.userIncEXP(5, False)
    self.questRecordExSet(23007, "exp2", "1")