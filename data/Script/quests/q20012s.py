self.sayNext("I've been waiting for you, #h0#. My name is #p1102006# and I'm the third brother you are going to meet. So, you've learned about using Regular Attacks, correct? Well, next you'll be learning about your #bSkills#k, which you will find very helpful in Maple World.")
self.sayNext("You earn Skill Points every time you level up, which means you probably have a few saved up already. Press the #bK key#k to see your skills. Invest your Skill Points in the skill you wish to strengthen and don't forget to #bplace the skill in a Quick Slot for easy use#k.")

nRet = self.askAccept("Time to practice before you forget. You will find a lot of #o100121#s in this area. Why don't you hunt #r3 #o100121#s#k using your #bThree Snails#b skill and bring me 1 #b#t4000483##k as proof? I'll wait for you here.")

if nRet == 1:
    self.questRecordSetState(20012, 1)
    self.userAvatarOriented("UI/tutorial.img/8")