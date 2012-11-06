package entity;

import junit.framework.TestCase;

public class EdgeTest extends TestCase
{
	private Edge edge;
	private Choice leftChoice = new Choice("When to meet", 1, 1);
	private Choice rightChoice = new Choice("When to eat", 2, 2);
	protected void setUp() throws Exception
	{
		edge = new Edge();
	}

	public void testEdgeChoiceChoiceInt()
	{
		edge = new Edge(leftChoice,rightChoice,1);
		assert(edge.getLeftChoice().equals(leftChoice));
		assert(edge.getRightChoice().equals(rightChoice));
		assert(edge.getHeight() == 1);
	}

	public void testGetLeftChoice()
	{
		edge.setChoices(leftChoice, rightChoice);
		assert(edge.getLeftChoice().equals(leftChoice));
	}

	public void testGetRightChoice()
	{
		edge.setChoices(leftChoice, rightChoice);
		assert(edge.getRightChoice().equals(rightChoice));
	}

	public void testGetHeight()
	{
		edge.setHeight(1);
		assert(edge.getHeight() == 1);
	}
	
	public void testHasChoice(){
		edge.setChoices(leftChoice, rightChoice);
		assertTrue(edge.hasChoice(1));
		assertTrue(edge.hasChoice(2));
		assertTrue(!(edge.hasChoice(3)));
	}

}
