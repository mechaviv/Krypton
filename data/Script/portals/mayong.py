fset = self.fieldSet( "Mayong" );
users = fset.getUserCount();

if users < 6:
    #target.playPortalSE;
    self.registerTransferField( 240020402, "" )
else: self.userScriptMessage( "You may not enter Manon's Forest." )