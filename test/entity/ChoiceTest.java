package entity;

import junit.framework.TestCase;

public class ChoiceTest extends TestCase
{
	private Choice choice;
	protected void setUp() throws Exception
	{
		choice = new Choice();
	}

	public void testChoiceStringInt()
	{
		choice = new Choice("When to meet", 1);
		assert(choice.getName() == "When to meet");
		assert(choice.getOrder() == 1);
	}

	public void testChoiceStringIntInt()
	{
		choice = new Choice("When to meet", 1, 1);
		assert(choice.getName() == "When to meet");
		assert(choice.getOrder() == 1);
		assert(choice.getFinalDecisionOrder() == 1);
	}

	public void testGetOrder()
	{
		choice.setOrder(1);
		assert(choice.getOrder() == 1);
	}

	public void testGetName()
	{
		choice.setName("When to meet");
		assert(choice.getName() == "When to meet");
	}

	public void testGetFinalDecisionOrder()
	{
		choice.setFinalDecisionOrder(1);
		assert(choice.getFinalDecisionOrder() == 1);
	}

	public void testEqualsObject()
	{
		choice = new Choice("When to meet", 1, 1);
		Choice choice1 = new Choice("When to meet", 1, 1);
		Choice choice2 = new Choice("When to meet", 1, 1);
		assert(choice.equals(choice1));
		assert(!choice.equals(choice2));
	}

}
