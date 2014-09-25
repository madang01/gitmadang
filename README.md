I don't speak English very well.

Sinnori framework is a program that helps to make a message oriented server / client application.

&gt;&gt; model

Function(request message) {
  do business logic
  return response message
}

clinet : send a request message to server 
server : (1) receive a request message from client
(2) do business logic
(3) send response message to client
client : receive a response message from server


&gt;&gt; 3 part<br/>
(1) Sinnori Server<br/>
(2) server connection API<br/>
(3) utility<br/>
(3-1) utility to make ANT build environment.<br/>
(3-2) utility to create a source associated with the message<br/>

&gt;&gt; sample project list<br/>
(1) sample_test<br/>
(2) sample_fileupdown<br/>
(2-1) sync file uploading/downloading<br/>
(2-2) asyn file uploading/downloading<br/>
(3) sample_db<br/>
