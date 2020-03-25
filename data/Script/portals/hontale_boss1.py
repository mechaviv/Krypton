quest = self.fieldSet("Hontale3")

if quest.getVariable("boss1") != "1":
    self.fieldBroadcastMessage(6, "The enormous creature is approaching from the deep cave.")
    #quest.setReactorState( 0, "tremble1", 0, 0 );
    self.fieldSummonMob(880, 220, 2100031)
    quest.setVariable("boss1", "1")