fset = self.fieldSet( "Gryphius" );
users = fset.getUserCount();

if users < 6:
    #target.playPortalSE;
    self.registerTransferField( 240020101, "" )
else: self.userScriptMessage( "You may not enter the Griffey Forest." )