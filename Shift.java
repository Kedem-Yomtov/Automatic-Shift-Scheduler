package question2;

public class Shift {
	Worker worker;
	int startHour;
	int endHour;
	String type;
	
	Shift( int startHour, int endHour, String type)
	{
		this.startHour = startHour;
		this.endHour = endHour;
		this.type = type;
		worker = null;
	}
	
	public String toString()
	{
		if(worker == null)
		{
			return ("Shift start :" + startHour + ", End: " + endHour + ", type: " + type);

		}
		return ("Shift start :" + startHour + ", End: " + endHour + ", type: " + type + "Worker: " + worker.name);
	}
	public String toStringforSchedule()
	{
		if(worker == null || worker.name.equals("-"))
		{
			return ("Missing Shift\n" + numberToHourString(startHour) + "-" + numberToHourString(endHour) + "\n" + type);

		}
		
		
		return ("" + numberToHourString(startHour) + "-" + numberToHourString(endHour) + "\n" + type + "\n" + worker.name.substring(0, 1).toUpperCase() + worker.name.substring(1));
	}
	
	public String  numberToHourString(int num)
	{
		return String.valueOf(num/1000) + String.valueOf((num/100)%10) + ":" + String.valueOf((num/10)%10) + String.valueOf(num%10);
	}
}
