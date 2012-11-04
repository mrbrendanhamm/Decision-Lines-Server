package entity;

import java.util.ArrayList;

import junit.framework.TestCase;

public class ModelTest extends TestCase
{
	private Model model;
	private DecisionLineEvent DLE = new DecisionLineEvent("1");
	protected void setUp() throws Exception
	{
		model = Model.getInstance();
	}

	public void testGetInstance()
	{
		model = Model.getInstance();
		assert(model != null);
	}

	public void testGetDecisionLineEvents()
	{
		ArrayList<DecisionLineEvent> DLEs = Model.getInstance().getDecisionLineEvents();
		assert(DLEs != null);
	}

	public void testRemoveDecisionLineEvent()
	{
		Model.getInstance().getDecisionLineEvents().add(DLE);
		assert(Model.getInstance().getDecisionLineEvents().size() == 1);
		Model.getInstance().removeDecisionLineEvent(DLE);
		assert(Model.getInstance().getDecisionLineEvents().size() == 0);
	}

	public void testGetDecisionLineEvent()
	{
		Model.getInstance().getDecisionLineEvents().add(DLE);
		DecisionLineEvent re = Model.getInstance().getDecisionLineEvent("1");
		assert(re.equals(DLE));
	}

}
