nRet = self.inventoryExchange(0, 1142065, 1)
if not nRet: self.say("Your inventory is full")
else:
    self.incExp(300, False)
    self.questRecordSetState(29905, 2)
    self.questEndEffect()