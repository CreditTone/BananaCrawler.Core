package com.banana.common.master;

import java.rmi.Remote;
import java.util.List;

import com.banana.common.NodeStatus;
import com.banana.request.BasicRequest;

public interface ICrawlerMasterServer extends Remote{

	void addDownloadNode(String host,int port) throws java.rmi.RemoteException;
	
	void startTask() throws java.rmi.RemoteException;
	
	Object getTaskPropertie(String taskName,String name) throws java.rmi.RemoteException;
	
	void pushTaskRequests(String taskName,List<BasicRequest> requests) throws java.rmi.RemoteException;
	
}
