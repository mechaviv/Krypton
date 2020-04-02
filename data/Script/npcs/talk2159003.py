val = self.questRecordExGet(23007, "exp1")
if val == "1": self.sayNext("Did you find Ulrika and Von yet? Von is really, really good at hiding.")
else:
    self.sayNext("Eep! You found me.")
    self.sayNext("Eh, I wanted to go further into the wagon, but my head wouldn't fit.")
    self.sayNext("Did you find Ulrika and Von yet? Von is really, really good at hiding.\r\n\r\n\r\n\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 5 exp")
    self.userIncEXP(5, False)
    self.questRecordExSet(23007, "exp1", "1")