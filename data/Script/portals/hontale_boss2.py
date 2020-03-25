quest = self.fieldSet("Hontale3")

if quest.getVariable("boss2") != "1":
    self.fieldBroadcastMessage(6, "The enormous creature is approaching from the deep cave.")
    #quest.setReactorState( 1, "tremble2", 0, 0 );
    self.fieldSummonMob(-350, 220, 2100032)
    quest.setVariable("boss2", "1")