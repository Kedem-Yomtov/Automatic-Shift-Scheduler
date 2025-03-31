package question2;

import java.util.ArrayList;

public class Worker {
	String name;
	ArrayList<String> type;
	ArrayList<Restriction> restrictions = new ArrayList<Restriction>();
	ArrayList<Restriction> preferences = new ArrayList<Restriction>();
	
	Worker(String name, ArrayList<String> type)
	{
		this.name = name;
		
		this.type = (ArrayList<String>) type.clone();
	}
	
	public String toString()
	{
		String ret = "Name: " + name + "\nWork Type: ";
		for(String typ : type)
		{
			ret+= typ + ", ";
		}
		ret += "\nRestrictions:\n";
		for(Restriction res : restrictions)
		{
			ret+= res.toString() + "\n";
		}
		ret += "preferences:\n";
		for(Restriction res : preferences)
		{
			ret+= res.toString() + "\n";
		}
		
		return ret;
	}
}
