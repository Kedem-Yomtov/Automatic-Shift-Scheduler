package question2;

import java.util.ArrayList;

public class Restriction 
{
	boolean level; // 1- restriction, 0 - preference
	boolean activated;
	String type;
	ArrayList<String> variables;
	int weight;
	
	Restriction(boolean level, String type, ArrayList<String> variables, int weight)
	{
		this.level = level;
		this.activated = true;
		this.type = type;
		this.type.toLowerCase();
		this.variables = variables;
		this.weight = weight;
	}
	
	public String toString()
	{
		switch(this.type)
		{
		case "nobacktoback":
			return "Can't work back to back shifts";
		case "either":
			return "Can work either " + numToDay(Integer.parseInt(variables.get(0))) + " or " +  numToDay(Integer.parseInt(variables.get(1)));
		case "workdays":
			return "Can only work between " + variables.get(0) + " and " + variables.get(1) + " days"; 
		case "daysofweek":
			String ret = "Can't work on ";
			for(String s : variables)
			{
				ret += numToDay(Integer.parseInt(s)) + ", ";
			}
			return ret;
		case "badpairing":
			return "Can't work with " + variables.get(0);
		case "shifttype":
			return "Can't work as "  + variables.get(0);
		default:
			return "error";
		}
	}


	public static String numToDay(int num)
	{
		switch(num)
		{
		case 1:
			return "Sunday";
		case 2:
			return "Monday";
		case 3:
			return "Tuesday";
		case 4:
			return "Wednesday";
		case 5:
			return "Thursday";
		case 6:
			return "Friday";
		case 7:
			return "Saturday";
		}
		
		return "errorrr";
	}
}