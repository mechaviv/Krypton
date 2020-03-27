self.sayNext("There are a number of ways to hunt, but the most basic way is with your #bRegular Attack#k. All you need is a weapon in your hand, since it's a simple matter of just swinging your weapon at monsters.")
self.sayNext("Press the #bCtrl#k to use your Regular Attack. Usually the Ctrl is located #bat the bottom left of the keyboard#k, but you don't need me to tell you that, right? Find the Ctrl and try it out!")

nRet = self.askAccept("Now that you've tried it, we've got to test it out. In this area, you can find the weakest #r#o100120##ks in Ereve, which is perfect for you. Try hunting #r1#k. I'll give you a reward when you get back.")

if nRet == 0:self.say("You don't want to? It's not even that hard, and you'll receive special equipment as a reward! Well, give it some thought and let me know if you change your mind.")
else:
    self.questRecordSetState(20011, 1)
    self.userAvatarOriented("UI/tutorial.img/4")