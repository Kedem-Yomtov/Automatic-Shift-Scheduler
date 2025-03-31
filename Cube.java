package question2;

import java.util.ArrayList;

public class Cube {
	int startHour;
	int endHour;
	int dayOfWeek; //0 - sunday, 1 - monday..
	ArrayList<Shift> shifts;
	ArrayList<Worker> workers = new ArrayList<Worker>();
	Cube(int startHour, int endHour, int dayOfWeek)
	{
		this.startHour = startHour;
		this.endHour = endHour;
		this.dayOfWeek = dayOfWeek;
		shifts = new ArrayList<Shift>();
	}
	
	void addShift(Shift s)
	{
		shifts.add(s);
	}
	
	public String toString()
	{
		String ret = "Start: " + startHour + ", End: " + endHour + ", Day of Week: " + dayOfWeek + "\nShifts:\n";
		for(Shift s : shifts)
		{
			ret += s.toString() + "\n";
		}
		return ret;
	}
}
