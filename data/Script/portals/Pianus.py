fset = self.fieldSet( "Pianus" );
users = fset.getUserCount();

if users < 10:
    #target.playPortalSE;
    self.registerTransferField( 230040420, "" )
else: self.userScriptMessage( "The Cave of Pianus is currently full. Please come back later." )