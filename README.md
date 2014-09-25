I don't speak English very well.

Sinnori framework is a program that helps to make a message oriented server / client application.

>> model

Function(request message) {
  do business logic
  return response message
}

clinet : send a request message to server 
server : (1) receive a request message from client
(2) do business logic
(3) send response message to client
client : receive a response message from server


>> 3 part
(1) Sinnori Server
(2) server connection API
(3) utility
(3-1) utility to make ANT build environment.
(3-2) utility to create a source associated with the message

>> sample project list
(1) sample_test
(2) sample_fileupdown
(2-1) sync file uploading/downloading
(2-2) asyn file uploading/downloading
(3) sample_db
