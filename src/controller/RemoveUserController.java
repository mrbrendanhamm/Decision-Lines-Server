package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


import entity.DecisionLineEvent;
import entity.Edge;
import entity.Model;
import entity.User;

public class RemoveUserController {
	private Model myModel;
	public RemoveUserController()
	{
		
	}
	public RemoveUserController(Model myModel)
	{
		this.myModel = myModel;
	}
	public void setMyModel(Model myModel)
	{
		this.myModel = myModel;
	}
	public Model getMyModel()
	{
		return this.myModel;
	}
	public synchronized boolean removeUser(User user, String uniqueId)
	{
		DecisionLineEvent DLE = this.myModel.getDecisionLineEvent(uniqueId);
		HashMap<User,ArrayList<Edge>> usersAndEdges = DLE.getUsersAndEdges();
		Iterator<Entry<User, ArrayList<Edge>>> it = usersAndEdges.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry<User, ArrayList<Edge>> entry= it.next();
			if(entry.getKey().equal(user))
			{
				it.remove();
				return true;
			}
		}
		return false;
	}
}
