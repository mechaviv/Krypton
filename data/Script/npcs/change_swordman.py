if self.userGetJob() == 100 and self.userGetLevel() >= 30:
    self.say("1")
    self.registerTransferField(108000300, "")
elif self.userGetJob() == 100 and self.userGetLevel() < 30:
    self.say("2")
elif self.userGetJob() == 110 or self.userGetJob() == 120 or self.userGetJob() == 130:
    self.say("3")