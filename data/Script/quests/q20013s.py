self.sayNext("#b(*clang clang*)#k")
self.sayNext("Whoa! Hey! You scared me. I didn't know I had a visitor. You must be the Noblesse #p1102006# was talking about. Welcome! I''m #p1102007#, and my hobby is making #bChairs#k. I'm thinking about making you one as a welcome present.")
self.sayNext("But wait, I can't make you one because I don''t have enough materials. Could you find me the materials I need? Around this area, you will find a lot of Boxes with items inside. Could you bring me back a #t4032267# and a #t4032268# found inside those Boxes?")
self.sayNext("Do you know how to get items from boxes? All you have to do is break the Boxes like you're attacking a monster. The difference is that you can attack monsters using your Skills, but you can #bonly use Regular Attacks to break Boxes#k.")

nRet = self.askAccept("Please bring me 1 #b#t4032267##k and 1 #b#t4032268##k found inside those Boxes. I'll make you an awesome Chair as soon as I have what I need. I'll wait here!")

if nRet == 0: self.say("Hmm, was that too much to ask? Is it because you don''t know how to break Boxes? I'll tell you how if you accept my Quest. Let me know if you change your mind.")
else:
    self.questRecordSetState(20013, 1)
    self.userAvatarOriented("UI/tutorial.img/9")