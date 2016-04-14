namespace java com.daqsoft.schedule.common.face
service TransServer{
	string sendTask(1:string task);
	string getStatus();
	list<string> getAllRunning();
}