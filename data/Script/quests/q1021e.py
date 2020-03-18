if self.inventoryGetItemCount(2010007) == 0:
    if self.userGetHP() == self.userGetMHP():
        file = "#fUI/UIWindow.img/QuestIcon/"
        self.sayNext("How easy is it to consume the item? Simple, right? You can set a #bhotkey#k on the right bottom slot. Haha you didn't know that! right? Oh, and if you are a beginner, HP will automatically recover itself as time goes by. Well it takes time but this is one of the strategies for the beginners.")
        self.sayNext("Alright! Now that you have learned alot, I will give you a present. This is a must for your travel in Maple World, so thank me! Please use this under emergency cases!")
        self.say("Okay, this is all I can teach you. I know it's sad but it is time to say good bye. Well take care if yourself and Good luck my friend!\r\n\r\n" + file + "4/0#\r\n#v2010000# 3 #t2010000#\r\n#v2010009# 3 #t2010009#\r\n\r\n" + file + "8/0# 10 exp")

        if self.inventoryExchange(0, [2010000, 3, 2010009, 3]) == False:
            self.sayNext("Your inventory is full...")
        else:
            self.userIncEXP(10, False)
            self.questRecordSetState(1021, 2)
            self.questEndEffect()
    else:
        self.say("Hey, your HP is not fully recovered yet. Did you take all the #t2010007# that I gave you? Are you sure?")
else:
    self.say("Look... I told you to take every #r#t2010007 ##k that I gave you. Open the Items Window and click on the #bUSE#k tab. There you will see #t2010007#, double click on it for use.")