package com.banana.common.download;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.banana.common.NodeStatus;
import com.banana.request.BasicRequest;

public interface IDownload extends Remote{
	
	boolean dowloadLink(String taskName,BasicRequest request) throws RemoteException;
	
	boolean loadJar(String filename,byte[] jar) throws RemoteException;
	
	NodeStatus getStatus() throws RemoteException;
}
