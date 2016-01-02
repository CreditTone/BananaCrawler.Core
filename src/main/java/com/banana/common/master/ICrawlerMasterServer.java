package com.banana.common.master;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.List;

import com.banana.request.BasicRequest;

public interface ICrawlerMasterServer extends Remote{

	void registerDownloadNode(String rmiAddress) throws java.rmi.RemoteException;
	
	void startTask() throws java.rmi.RemoteException;
	
	Object getTaskPropertie(String taskName,String name) throws java.rmi.RemoteException;
	
	void pushTaskRequests(String taskName,List<BasicRequest> requests) throws java.rmi.RemoteException;
	
	Object getStartContextAttribute(String taskName,String hashCode,String attribute) throws java.rmi.RemoteException;
	
}
