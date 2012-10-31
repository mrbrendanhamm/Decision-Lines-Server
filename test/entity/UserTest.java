package entity;

import junit.framework.TestCase;

public class UserTest extends TestCase
{
	private User user;
	protected void setUp() throws Exception
	{
		user = new User();
	}

	public void testHashCode()
	{
		user.setUser("A");
		int re = user.hashCode();
		assert(re == user.getUser().hashCode() + 31);
	}

	public void testUserStringStringInt()
	{
		user = new User("A","123",1);
		assert(user.getUser() == "A");
		assert(user.getPassword() == "123");
		assert(user.getPosition() == 1);
	}

	public void testGetUser()
	{
		user.setUser("A");
		assert(user.getUser() == "A");
	}

	public void testGetPassword()
	{
		user.setPassword("123");
		assert(user.getPassword() == "123");
	}

	public void testGetPosition()
	{
		user.setPosition(1);
		assert(user.getPosition() == 1);
	}

	public void testEqualsObject()
	{
		User user1 = new User("A","123",1);
		User user2 = new User("B","123",1);
		assert(user.equals(user1));
		assert(!user.equals(user2));
	}

}
