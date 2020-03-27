self.sayNext("You've successfully defeated the #o100121#s and brought me a #t4000483#. That's very impressive! #bYou earn 3 Skill Points every time you level up, after you officially become a knight, that is. Keep following the arrow to the left, and you'll meet #b#p1102007##k, who will guide you through the next step.\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0# \r\n#fUI/UIWindow.img/QuestIcon/8/0# 40 exp")

nRet = self.inventoryExchange(0, 4000483, -1)
if not nRet: self.say("You don't have the items")
else:
    self.userIncEXP(40, False)
    self.questRecordSetState(20012, 2)
    self.questEndEffect()