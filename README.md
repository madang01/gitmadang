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
(3-1) "SinnoriAntBuildUtil" is a utility making ANT build environment.<br/>
(3-2) "SinnoriSourceBuilderUtil" is a utility creating 5 type sources using a message infomation file.<br/>
(3-2-1) 5 type source
1. Message : a message is a value object and is similar to a POJO.<br/>
2. Encoder : transfer a message to binary stream
3. Decoder : transfer a binary stream to a message
4. ServerCodec : define whether the message can be sent to client and can receive from client 
5. ClientCodec : define whether the message can be sent to server and can receive from server<br/>

&gt;&gt; sample project list<br/>
(1) sample_test<br/>
(2) sample_fileupdown<br/>
(2-1) sync file uploading/downloading<br/>
(2-2) asyn file uploading/downloading<br/>
(3) sample_db<br/>
