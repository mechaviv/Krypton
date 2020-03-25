def zakum_clearReg():
    quest = self.fieldSet( "ZakumEnter" )
    quest.setVariable("enter", "")
    quest.setVariable("dName", "")

def zakum_reset():
    quest = self.fieldSet( "ZakumEnter" )
    eTime = self.fieldSetGetQuestTime("ZakumEnter")
    if eTime > 302:
        if quest.getVariable("reset") != "1":
            zakum_clearReg()
            quest.setVariable("reset", "1")


quest = self.fieldSet( "ZakumEnter" )
zakum_reset()
