val = self.questRecordExGet(23007, "vel01")

if val == "":
    self.sayNext("The experiment is going well, quite well. The endless supply of Rue is certainly speeding things along. Joining the Black Wings was a wise decision, a wise decision indeed. Muahaha!", 0, 0x1)
    self.sayNext("I say, you have great foresight about these things.", 2159008, 0x5)
    self.sayNext("The android the Black Wings wanted will be completed soon. Oh yes, very soon. Then, the next stage will begin! I will conduct an experiment wilder than their wildest dreams!", 0, 0x1)
    self.sayNext("Pardon? The next stage?", 2159008, 0x5)
    self.sayNext("Teeheehee, do you still not comprehend what I'm trying to create? Look around! Here's a clue: it's eons more interesting than a simple android. Eons more interesting.", 0, 0x1)
    self.sayNext("What?? All these test subjects... I say, sir, just what are you planning to do?", 2159008, 0x5)
    self.sayNext("Now, now, you may not understand the grandness of my experiments. I don't expect you to. No, I don't expect you to. Just focus on your job and make sure none of the test subjects run away.", 0, 0x1)
    self.sayNext("Hey... Did you hear that?", 0, 0x1)
    self.sayNext("Huh? Well... Now that you mention it, I do hear something. Yes, I do hear something...", 2159008, 0x5)
    self.questRecordExSet(23007, "vel01", "1")

    self.userSetDirectionMode(True, 0)
    self.fieldEffectTremble(0, 500)#, 30 in the future
    self.userReservedEffect("Effect/Direction4.img/Resistance/TalkInLab")
elif val == "1":
    self.registerTransferField(931000012, "sp")