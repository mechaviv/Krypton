if self.userGetLevel() < 15:
    self.say("You need to be at least Level 15 in order to use Gachapon.")
else:
    if self.inventoryGetItemCount(5220000) >= 0:
        ret1 = self.askYesNo( "You may use Gachapon. Would you like to use your Gachapon ticket?" )
        if ret1 == 0:
            self.say("Please come again!")
        else:
            'self.inventoryGetHoldCount()' 'self.inventoryGetSlotCount()'
            if self.inventoryGetSlotCount(1) > self.inventoryGetHoldCount(1) and self.inventoryGetSlotCount(2) > self.inventoryGetHoldCount(2) and self.inventoryGetSlotCount(4) > self.inventoryGetHoldCount(4):
                'ret2 = inventory.makeRandGachaponItem( 5220000, 2 );'
                ret2 = 5220000
                if ret2 >= 1:
                    self.say( "You have obtained #b#t" + str(ret2) + "##k." )
                else:
                    self.say("Please check your item inventory and see if you have the ticket, or if the inventory is full")
            else:
                self.say("Please make room on your item inventory and then try again.")
    else:
        self.say("You don't have Gachapon ticket")
'''
script "gachapon3" {
	if (target.nLevel < 15 ) self.say ( "You need to be at least Level 15 in order to use Gachapon.");
	else {
	
		inventory = target.inventory;
		if ( inventory.itemCount( 5220000 ) >= 1 ) {
			ret1 = self.askYesNo( "You may use Gachapon. Would you like to use your Gachapon ticket?" );
			if ( ret1 != 0 ) {
				if ( inventory.slotCount( 1 ) > inventory.holdCount( 1 ) and inventory.slotCount( 2 ) > inventory.holdCount( 2 ) and inventory.slotCount( 4 ) > inventory.holdCount( 4 )) {
					ret2 = inventory.makeRandGachaponItem( 5220000, 2 );
					if ( ret2 >= 1 ) self.say( "You have obtained #b#t" + ret2 + "##k." );
					else self.say( "Please check your item inventory and see if you have the ticket, or if the inventory is full" );
				}
				else self.say( "Please make room on your item inventory and then try again." );
	 		} 
			else self.say( "Please come again!" );
		}
		else self.say( "Here's Gachapon." );
	}
}
'''