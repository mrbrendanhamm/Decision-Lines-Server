package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import server.ClientState;
import server.IProtocolHandler;
import xml.Message;


import entity.DecisionLineEvent;
import entity.Edge;
import entity.Model;
import entity.User;

public class RemoveUserController implements IProtocolHandler {
	public RemoveUserController()
	{
		
	}

	public synchronized boolean removeUser(User user, String uniqueId)
	{
		DecisionLineEvent DLE = Model.getInstance().getDecisionLineEvent(uniqueId);
		HashMap<User,ArrayList<Edge>> usersAndEdges = DLE.getUsersAndEdges();
		Iterator<Entry<User, ArrayList<Edge>>> it = usersAndEdges.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry<User, ArrayList<Edge>> entry= it.next();
			if(entry.getKey().equals(user))
			{
				it.remove();
				return true;
			}
		}
		return false;
	}
	@Override
	public synchronized Message process(ClientState state, Message request) {
		// TODO Auto-generated method stub
		return null;
	}
}
