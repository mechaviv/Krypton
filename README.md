# Krypton
Krypton - A Nexon Replica Emulator Project (Based on OrionAlpha)

----------------------------------------------------------------------
## Requirements/Dependencies
 * Java JDK (11 or higher)
 * javax.json (or Java EE)
 * Netty (4.1.31 or higher)
 * HikariCP (3.1.0 or higher)
 * MariaDB Connector/J (2.2.3 or higher)
 * slf4j (1.7.4 or higher)
 * Jython (2.7.1 or higher)
 * favr.lib.BCrypt (0.9.0 or higher)
 ----------------------------------------------------------------------
 ## Architecture
 The Krypton Emulator is split up into two parts: *Login*, and *Game*, each executing on their own thread. 
 
 **Login** is the central server which will have connectivity to each world and can migrate you back and forth. 
 
 **Game** is designed to be each world, and takes the JVM argument `-DgameID=X` to define which world it is. Each Game JVM that controls the world will also control all of its channels (thus, no multi-jvm here).
 
 ----------------------------------------------------------------------
 ## Server Configuration
 Located within the root of the emulator are a few configuration files:
  * Game0, Game1, etc is used to configure each World.
  * Login is used to configure the Login.
  * Shop is used to configure the Cash Shop.
  * Database is used to configure the connection to the database.
  
  The configuration is done within JSON; each property is defined as a key (string), and a value (int or string).
  Below will further explain the defintions of most of the properties used.
  
  Login/Game/Shop:
  * `port` -> The port to be binded for the server's connection acceptor.
  * `centerPort` -> The private port of your login server that connects JVMs together.
  * `PublicIP` -> The public IP address users will connect to.
  * `PrivateIP` -> The private IP address that connects the JVMs together.
  
  Database:
  * `dbPort` -> The port to connect to your database.
  * `dbGameWorld` -> The Schema name of the active database that the emulator connects to.
  * `dbGameWorldSource` -> The IP/hostname to connect to your database.
  * `dbGameWorldInfo` -> The username/password of your database, separated by comma. (e.g "root,password")
  
  Game:
  * `gameWorldId` -> The ID of the world (0 = Scania, 1 = Bera, etc).
  * `channelNo` -> The number of channels for the world.
  * `incExpRate` -> The server's Experience Rate modifier. We use Nexon's standard. (e.g 100 = 1x, 250 = 2.5x, etc)
  * `incMesoRate` -> The server's Meso Rate modifier (Nexon standard).
  * `incDropRate` -> The server's Drop Rate modifier (Nexon standard).
  * `worldName` -> The name of the world. This is sent to and displayed in the login server.
  ----------------------------------------------------------------------
  
