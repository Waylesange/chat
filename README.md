# chat
Server and client sides of the network chat, which may be used for exchanging messages between clients

USAGE

Compile and start Server.java using:

$ javac Server.java
$ java Server

and then the same for as many clients as you like:

$ javac Client.java
$ java Client
$ java Client
$ java Client
...

OR: you can run these files from within your favorite IDE, for example IntelliJ IDEA.

In each of these clients, you can enter one of the 10 records currently present in the database. They are of the form:

Login: login, login1, login2, ..., login9;
Password: password, password1, password2, ..., password9;

Having entered any of these 10 combinations when asked for in each client, you will be assigned one of the 10 nicknames of the form:

Nickname: nickname, nickname1, nickname2, ..., nickname9;

Now you are able to exchange messages between the clients.

EXIT

To exit any client, type "end" in the text field. You can then authorize again in that same client window, using any other or the same login/password combination.

To exit the server, press Ctrl+C.
