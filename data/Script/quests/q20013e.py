self.sayNext("Did you bring me a Building Stone and a Drape? Let's see. Ah, these are just what I need! They indeed are a #t4032267# and a #t4032268#! I'll make you a Chair right away.")
self.sayNext("Here it is, a #t3010060#. What do you think? Nifty, huh? You can #bquickly recover your HP by sitting in this Chair#k. It will be stored in the #bSet-up#k window in your Inventory, so confirm that you've received the chair and head over to #b#p1102008##k. You'll see him if you keep following the arrow to the left. \r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0# \r\n#i3010060# 1 #t3010060# \r\n#fUI/UIWindow.img/QuestIcon/8/0# 95 exp")
nRet = self.inventoryExchange(0, 4032267, -1, 4032268, -1, 3010060, 1)
if not nRet: self.say("Your inventory is full")
else:
    self.userIncEXP(95, False)
    self.userAvatarOriented("UI/tutorial.img/10")
    self.questRecordSetState(20013, 2)
    self.questEndEffect()