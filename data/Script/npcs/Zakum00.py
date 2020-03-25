# Adobis
def zakum_takeawayitem():
    nItem1 = self.inventoryGetItemCount(4001015)
    nItem2 = self.inventoryGetItemCount(4001016)
    nItem3 = self.inventoryGetItemCount(4001018)
    if nItem1 > 0:self.inventoryExchange(0, 4001015, -nItem1)
    if nItem2 > 0:self.inventoryExchange(0, 4001016, -nItem2)
    if nItem3 > 0:self.inventoryExchange(0, 4001018, -nItem3)


def zakum_master():
    self.sayNext( "Hello GM~ For your better GM life, you can clear the zakum quest by stages." )
    v0 = self.askMenu( "What can I do for you?\r\n#b#L0# Clear 1st stage of Zakum#l\r\n#L1# Clear 2nd Stage of Zakum#l\r\n#L2# Clear 3rd stage of zakum and get the items#l\r\n#L3# Return to the uncleared status#l\r\n#L4# Normal proceed#l\r\n#L5# Set up other characters#l\r\n#L6# Cancel setting availability#l" )
    if v0 == 0:
        self.questRecordSet(7000, "end")
        self.say( "You have been set to clear the 1st stage." )
    elif v0 == 1:
        self.questRecordSet(7001, "end")
        self.say( "You have been set to clear the 2nd stage." )
    elif v0 == 2:
        self.questRecordSet(7002, "end")
        self.say( "You have been set to clear the 3nd stage." )
        ret = self.inventoryExchange(0, 4001017, 1)
        if not ret: self.say( "You have clear the quest but couldn't receive the items due to lack of space in inventory." )
    elif v0 == 3:
        self.questRecordSet(7000, "")
        self.questRecordSet(7001, "")
        self.questRecordSet(7002, "")
    elif v0 == 4:
        return
    elif v0 == 5 or v0 == 6:
        self.say("Under construction ... Please wait ...")


zakum_master()