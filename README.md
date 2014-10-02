I don't speak English very well.<br/>

Sinnori framework is a program that helps to make a message oriented server / client application.<br/><br/>

&gt;&gt; model<br/>
Function(a request sync message) {<br/>
  do business logic<br/>
  return a response sync message<br/>
}<br/>

Function(a request asyn message) {<br/>
  do business logic<br/>
  return response asyn message(s)<br/>
}<br/>


clinet : send a request message to server<br/> 
server : (1) receive a request message from client<br/>
(2) do business logic<br/>
(3) send response message to client<br/>
client : receive a response message from server<br/><br/>


&gt;&gt; 3 part<br/>
(1) Sinnori Server<br/>
(2) server connection API<br/>
(3) utility<br/>
(3-1) utility making ANT build environment.<br/>
(3-2) utility creating a source associated with the message<br/><br/>

&gt;&gt; sample project list<br/>
(1) sample_test<br/>
(2) sample_fileupdown<br/>
(2-1) sync file uploading/downloading<br/>
(2-2) asyn file uploading/downloading<br/>
(3) sample_db<br/>
