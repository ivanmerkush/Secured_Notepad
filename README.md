# Secured_Notepad

-----------------
Secured_Notepad
Implement encoding and decoding textfiles using algorithm IDEA, CFB
Sending keys and files was implemented using sockets.
-----------------
**task**

*Server*:
- stores files.
- generates random session key on client request.
- sends to the client encoded with a public key RSA session key.
- sends to the client encoded with a session key file.

*Client*:
- generates RSA and sends public RSA key to the serveer.
- sends request with  the file name to the server.
- decodes session key with a private key RSA.
- decodes received file with a session key and and displays it on the screen.
