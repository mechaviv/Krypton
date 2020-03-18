if self.userGetGender() == 0:
    self.sayNext("Hey, Man~  What's up? Haha!  I am Roger who can teach you adorable new Maplers lots of information.")
    self.sayNext("You are asking who made me do this?  Ahahahaha!  Myself!  I wanted to do this and just be kind to you new travellers.")
else:
    self.sayNext("Hey there, Pretty~ I am Roger who teaches you adorable new Maplers lots of information.")
    self.sayNext("I know you are busy! Please spare me some time~ I can teach you some useful information! Ahahaha!")

ret = self.askAccept("So..... Let me just do this for fun! Abaracadabra~!")
if ret == 0:
    self.say("I can't believe you have just turned down an attractive guy like me!")
else:
    if self.inventoryGetItemCount(2010007) >= 1:
        val = self.userGetHP() / 2
        self.userIncHP(-val, False)
        self.questRecordSetState(1021, 1)
        self.sayNext("Surprised? If HP becomes 0, then you are in trouble. Now, I will give you #r#t2010007##k. Please take it. You will feel stronger. Open the Item window and double click to consume. Hey, it's very simple to open the Item window. Just press #bI#k on your keyboard.")
        self.sayNext("Please take all #t2010007# that I gave you. You will be able to see the HP bar increasing. Please talk to me again when you recover your HP 100%.")
    else:
        if self.inventoryExchange(0, [2010007, 1]) == False:
            self.sayNext("Your use inventory must be full.")
        else:
            val = self.userGetHP() / 2
            self.userIncHP(-val, False)
            self.questRecordSetState(1021, 1)
            self.sayNext("Surprised? If HP becomes 0, then you are in trouble. Now, I will give you #r#t2010007##k. Please take it. You will feel stronger. Open the Item window and double click to consume. Hey, it's very simple to open the Item window. Just press #bI#k on your keyboard.")
            self.sayNext("Please take all #t2010007# that I gave you. You will be able to see the HP bar increasing. Please talk to me again when you recover your HP 100%.")