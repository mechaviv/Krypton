map = 910220000
if self.userGetJob() != 100 or self.userGetLevel() >= 21:
    self.say("You are not a warrior or you are not under level 21! You may not enter the warrior training center!")
else:
    menu = "Would you like to go into the Training Center?"
    for i in range(5):
        menu += "\r\n#b#L{0}#Training Center {1} ({2}/5)#l#k".format(i, i+1, self.fieldGetUserCount(map + i))
    ret = self.askMenu(menu)
    if (self.fieldGetUserCount(map + ret) >= 5):
        self.say("This training center is full.")
    else:
        self.registerTransferField(map + ret, "")