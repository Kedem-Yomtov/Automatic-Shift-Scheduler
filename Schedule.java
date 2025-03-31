package question2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Schedule {
	String scheduleName;
	ArrayList<Cube> cubes = new ArrayList<Cube>();
	ArrayList<Worker> workers = new ArrayList<Worker>();
	ArrayList<Restriction> generalRestrictions = new ArrayList<Restriction>();
	ArrayList<Restriction> restrictions = new ArrayList<Restriction>();
	ArrayList<Restriction> preferences = new ArrayList<Restriction>();
	ArrayList<String> shiftTypes = new ArrayList<String>(); //bar, kitchen, etx
	String Schedstring = "";
	int maxFoundDepth = 0;
	Schedule(File scheduleFile, File shiftsFile)
	{
		String scheduleDir = scheduleFile.getAbsolutePath();
		String shiftsDir = shiftsFile.getAbsolutePath();
		//read from scheduleFile
		
		try (BufferedReader reader = new BufferedReader(new FileReader(scheduleDir)))
		{
	         String line;
	     
			 scheduleName = reader.readLine(); // first line contains schedule name
			 //next line contains list of shift types
			 
			 String[] wordsArray = reader.readLine().toLowerCase().split(" ");

		      // Convert the array to an ArrayList
		     shiftTypes = new ArrayList<>(Arrays.asList(wordsArray));
		     reader.readLine(); //skip first cube definition
		     //now start collecting cubes
	         while ((line = reader.readLine()) != null)
	         {
	        	 //get first line after "cube";
	        	 line = line.toLowerCase();
	        	 if(line.trim() == "")
	        		{
	        			continue;
	        		}
			      // Convert the array to an ArrayList
	        	 String[] cubeData = line.split(" ");
	        	int dayofweek = Integer.valueOf(cubeData[0]) - 1;
	        	int startHour = Integer.valueOf(cubeData[1]);
	        	int endHour = Integer.valueOf(cubeData[2]);
	        	if(endHour < startHour)
	        	{
	        		endHour+= 24;//if reaches into next day
	        	}
	        	Cube newCube = new Cube(startHour, endHour, dayofweek);
	        	while ((line = reader.readLine()) != null)//get shifts for cube
	    	    {
	        		if(line.trim() == "")
	        		{
	        			continue;
	        		}
	        		line = line.toLowerCase();
	        		if (line.trim().compareTo("cube:") == 0)//found next cube
	        		{
	        			break;
	        		}
		        	String[] shiftData = line.split(" ");
	        		//shifttype startshift endshift

	        		Shift newShift = new Shift(Integer.valueOf(shiftData[1]), Integer.valueOf(shiftData[2]), shiftData[0]);
	        		newCube.addShift(newShift);
	        	}
	            cubes.add(newCube);
	                
	        }
	    } catch (IOException e) {
	    e.printStackTrace();
	    }
		
		
		
		//read from shifts file

		try (BufferedReader reader = new BufferedReader(new FileReader(shiftsDir)))
		{
			String line;
			String name;
			ArrayList<String> jobTypes = new ArrayList<String>();
			Worker newWorker = null;
			while((line = reader.readLine()) != null)
			{
				if (line == null || line.trim().isEmpty()) {
				    continue; // Skip empty lines
				}
				
				name = line.toLowerCase();//get name
				String[] jobTypesArray = reader.readLine().toLowerCase().split(" ");//get job types
				jobTypes = new ArrayList<>(Arrays.asList(jobTypesArray));
				newWorker = new Worker(name, jobTypes);//init new worker
				while((line = reader.readLine()) != null)//collect restrictions
				{
						line = line.toLowerCase();
						if (line == null || line.trim().isEmpty()) {
						    break; // Skip empty lines
						}
						
						if(!((line.charAt(0) == 'r' || line.charAt(0) == 'p') && line.charAt(1) == ' '))
						{
							//initialize name for next person and continue
							name = line;
							break;
						}
						//else add restrictions
						
						Restriction newRestriction = buildRestriction(line);
						newWorker.restrictions.add(newRestriction); // add to restrictions list
				}
				workers.add(newWorker);
			}
			
			
			
		}catch (IOException e) {
		    e.printStackTrace();
		    }
		
		for(Worker c : workers)
		{
			if(c != null)
				Schedstring += c.toString() + "\n";
		}
	}
	
	Restriction buildRestriction(String line)
	{
		line = line.toLowerCase();
		boolean level; // 1- restriction, 0 - preference
		String type;
		ArrayList<String> variables = new ArrayList<String>() ;
		int weight;
		
		String[] parse = line.split(" ");
		if(parse.length < 3)
		{
			System.out.println("Error at preference line: " + line);
			return null;
		}
		if(parse[0].equals("r"))
		{
			level = true;
		}
		else
		{
			level = false;
		}
		
		type = parse[1];
		weight = Integer.parseInt(parse[2]);
		for(int i = 3; i < parse.length; i++)
		{
			variables.add(parse[i]);
		}
		
		return new Restriction(level, type, variables, weight);
	}
	
	void disableAllPreferences()
	{
		for(Worker w : workers)
		{
			for(Restriction r : w.restrictions)
			{
				if(r.level == false)
				{
					r.activated = false;
				}
			}
		}
	}
	String runScheduleAlgorithm() throws InterruptedException
	{
		maxFoundDepth = 0;
		definitelyEmptyShifts(); // will find shifts that definitely cant be filled
		disableAllPreferences();
		System.out.println("Running Algorithm\n");
		System.out.println(getMaxDepth(0, 0, 0, true));
		resetCubes();
		
		String scheduleStatus = buildSchedule(false);
		if(scheduleStatus.equals("No solution"))
		{
			System.out.println(getMaxDepth(0, 0, 0, true));
			System.out.println("Max found depth was: " + maxFoundDepth);
			scheduleStatus = buildSchedule(true);
			return scheduleStatus;
		}
		else
		{
			return scheduleStatus;
		}
		
	}
	
	String buildSchedule(boolean noPerfectShift) throws InterruptedException
 {
		resetCubes();
    	System.out.println("trying to build schedule with activated preferences - " );
    	for(int i = 0; i< workers.size(); i++)
		{
			reactivatePreferences(workers.get(i).restrictions);
		}
    	
    	boolean a = getMaxDepth(0, 0, 0, noPerfectShift);
    	System.out.println("tried to build schedule with activated preferences " );

		if(getNumOfFullShifts() >= maxFoundDepth && numOfMissingShifts() == 0)
		{
			if(noPerfectShift == false && getNumOfShifts() == maxFoundDepth)
			{
				System.out.println("Returned with full schedule");
				return "Solution Found!";
				
			}
			else
			{
				//reactivate preferences
				
				String retStr = "No Perfect Solution\nMissing " + (getNumOfShifts() - maxFoundDepth) + " Shifts\n" +  getRelaxedPreferencesSummary(workers);
				for(Cube c : cubes)
				{
					for(Shift s : c.shifts)
					{
						if(s.worker == null || s.worker.name.equals("-") || s.worker.name.equals("Missing"))
						{
							retStr+= "Missing a " + s.type + " on " + Restriction.numToDay(c.dayOfWeek+1) + "\n";
						}
					}
				}
				System.out.println("Returned with no perfect solution");

				return retStr;
			}

		}

		else
		{
			System.out.println("AAAAAAAAAAA");
			
			for(int i = 0; i < workers.size(); i++)
			{
				reactivatePreferences(workers.get(i).restrictions);
			}
			
			System.out.println("Max found depth - " + maxFoundDepth);
			List<Restriction> allPreferences = new ArrayList<>();
		    
		    // Collect all preferences that can be relaxed
		    for (Worker worker : workers) 
		    {
		        for (Restriction restriction : worker.restrictions) {
		            if (!restriction.level) { // If it's a preference and activated
		                allPreferences.add(restriction);
		            }
		        }
		    }
		 // Sort preferences by weight (ascending) for priority relaxation
		    Collections.shuffle(allPreferences);
	        allPreferences.sort(Comparator.comparingInt(r -> r.weight));
	        
		    int totalPreferences = allPreferences.size();
		    
		    // Iterate by number of relaxed preferences (Hamming weight)
		    for (int relaxCount = 0; relaxCount <= totalPreferences; relaxCount++) 
		    {
		        // Generate all combinations with exactly `relaxCount` preferences relaxed
		        List<Integer> masks = generateMasksWithHammingWeight(totalPreferences, relaxCount);
		        
		        for (int mask : masks) 
		        {
		            // Apply the current combination of preferences
		        	
		        	
		            applyPreferenceRelaxation(allPreferences, mask);
	            	//System.out.println("trying to build schedule with deactivated preferences - trying \n" + getRelaxedPreferencesSummary(workers));

		            // Try building the schedule with the current setup
		            //System.out.println("Trying a solution");
		           // resetCubes();
		            
		        	System.out.println("trying to build schedule with Deactivated preferences - " + getRelaxedPreferencesSummary(workers));
		        	getMaxDepth(0, 0, 0, noPerfectShift);
		            if (getNumOfFullShifts() >= maxFoundDepth)
					{
		            	
		                // If successful, return the result
		            	String retStr = "No Perfect Solution\nMissing " + (getNumOfShifts() + numOfMissingShifts() - maxFoundDepth) + " Shifts\n";
						for(Cube c : cubes)
						{
							for(Shift s : c.shifts)
							{
								if(s.worker == null || s.worker.name.equals("Missing"))
								{
									retStr+= "Missing a " + s.type + " on " + Restriction.numToDay(c.dayOfWeek+1) + "\n";
								}
								else if(s.worker.name.equals("-"))
								{
									retStr+= "Missing a " + s.type + " on " + Restriction.numToDay(c.dayOfWeek+1) + "\n";
								}
							}
						}		
						System.out.println("Returned with preferences not kept");
						return retStr  + "Preferences Not Kept:\n" +
		                       getRelaxedPreferencesSummary(workers);
		            }
		            
		        }
		    }
			System.out.println("Returned with no solution");
		return "No solution";
	}
 }
	
	
	void definitelyEmptyShifts()
	{
		for(int i = 0; i < cubes.size(); i++)
		{
			for(int j = 0; j < cubes.get(i).shifts.size(); j++)
			{
				boolean found = false;
				for (Worker w : workers)
				{
					if(!isRestrictedDayOfWeek(i, w) && w.type.contains(cubes.get(i).shifts.get(j).type))
					{
						found = true;
					}

				}
				if(found == false)
				{
					cubes.get(i).shifts.get(j).worker = new Worker("Missing", new ArrayList<>(Collections.singletonList("-")));
				}
			}
		}
		
	}
	//turn on and off relevant preferences
	void applyPreferenceRelaxation(List<Restriction> preferences, int mask) {
	    for (int i = 0; i < preferences.size(); i++) {
	        // If the bit is set in the mask, deactivate the preference
	        preferences.get(i).activated = (mask & (1 << i)) == 0;
	    }
	}


	//get message for output hich preferences werent met
	String getRelaxedPreferencesSummary(List<Worker> workers) {
	    StringBuilder summary = new StringBuilder();

	    for (Worker worker : workers) {
	        for (Restriction restriction : worker.restrictions) {
	            if (!restriction.activated) { // Only include deactivated preferences
	                summary.append(worker.name.substring(0, 1).toUpperCase() + worker.name.substring(1) + ": ").append(restriction.toString())
	                       .append("\n");
	            }
	        }
	    }

	    return summary.toString();
	}
	void reactivatePreferences(List<Restriction> preferences)
	{
		for(int i = 0; i < preferences.size(); i++)
			{
				preferences.get(i).activated = true;
			}
	}
	List<Integer> generateMasksWithHammingWeight(int totalPreferences, int hammingWeight) {
	    List<Integer> masks = new ArrayList<>();
	    generateMasksHelper(0, 0, totalPreferences, hammingWeight, masks);
	    return masks;
	}

	void generateMasksHelper(int currentMask, int index, int totalPreferences, int remainingWeight, List<Integer> masks) {
	    if (remainingWeight == 0) {
	        masks.add(currentMask); // Add mask when the required weight is achieved
	        return;
	    }
	    if (index >= totalPreferences) {
	        return; // Stop if we've exhausted all preferences
	    }
	    // Include the current bit
	    generateMasksHelper(currentMask | (1 << index), index + 1, totalPreferences, remainingWeight - 1, masks);
	    // Exclude the current bit
	    generateMasksHelper(currentMask, index + 1, totalPreferences, remainingWeight, masks);
	}
	
	void resetCubes()
	{
		for(int i = 0; i < cubes.size(); i++)
		{
			cubes.get(i).workers.clear();
			for(int j = 0; j < cubes.get(i).shifts.size(); j ++)
			{
				if(cubes.get(i).shifts.get(j).worker != null && !cubes.get(i).shifts.get(j).worker.name.equals("Missing"))
					cubes.get(i).shifts.get(j).worker = null;
			}
		}
	}
	//main function of this program
	boolean addShiftToSchedule(int cubeIndex, int shiftIndex, int depth, boolean noPerfectShift) throws InterruptedException //recursive function
	{
		if(depth > maxFoundDepth)
		{
			System.out.println("EEEEEEEEEEEEEEEE");
			maxFoundDepth = depth;
		}
		
		//end recursion conditions
		if(cubeIndex >= cubes.size()) //if passed last cube, means we found everyone
		{
			System.out.println(" ---- 2 ---- ");
			if(noPerfectShift == true && depth == maxFoundDepth-1)
			{
				return workersPassedMinWorkDays() && depth == maxFoundDepth-1;
			}
			return workersPassedMinWorkDays();
		}
		if(cubeIndex == cubes.size() -1 && shiftIndex >= cubes.get(cubeIndex).shifts.size() ) //if reached last shift
		{			
			//check workers have shifts in correct range
			if(noPerfectShift)
			{
				//System.out.println(" ---- 9 ---- " +  cubes.size() + " " + cubes.get(cubeIndex).shifts.size()  + " " + depth);
				//System.out.println(" ---- 3 ---- " +  cubes.size() + " " + cubes.get(cubeIndex).shifts.size()  + " " + depth);
				//System.out.println(" ---- 3---- " + cubeIndex + " " + shiftIndex + " ");
				return (maxFoundDepth == depth) && workersPassedMinWorkDays();
			}
			return workersPassedMinWorkDays();
		}
		if(cubes.get(cubeIndex).shifts.size() <= shiftIndex)// reached last shift within local cube
		{
			//System.out.println(" ---- 5 ---- " +  cubes.size() + " " + cubes.get(cubeIndex).shifts.size()  + " " + depth);
			return addShiftToSchedule(cubeIndex+1, 0, depth, noPerfectShift); //call recursion for next cube at first shift
		}
		if(cubes.get(cubeIndex) == null)
		{
			System.out.println("null Cube!");
			return false;
		}
		if(cubes.get(cubeIndex).shifts.get(shiftIndex) == null)
		{
			System.out.println("null Shift!");
			return false;
		}
		
		
		//place workers in shift
		String currentShiftType = cubes.get(cubeIndex).shifts.get(shiftIndex).type;
		boolean foundWorker = false;
		//loop through all workers and see which one we can place
		
		
			if(cubes.get(cubeIndex).shifts.get(shiftIndex).worker != null && cubes.get(cubeIndex).shifts.get(shiftIndex).worker.name.equals("Missing"))// if a worker, or missing worker have already been assigned here
			{
				System.out.println("BAH + " + cubes.get(cubeIndex).dayOfWeek);
				return addShiftToSchedule(cubeIndex, shiftIndex+1, depth+1, noPerfectShift);// found a good path using this worker
				
			}
		//within each shift check we want to randomize the workers we look through
		List<Worker> shuffledWorkers = new ArrayList<>(workers);
		Collections.shuffle(shuffledWorkers);
		for(Worker w : shuffledWorkers)
		{
			//System.out.println("Attempting worker " + w.name + "for cube and shift: " + cubeIndex + " " + shiftIndex);
			//if this worker can work this type of shift and is not working in the cube
			if(w.type.contains(currentShiftType) && !workerIsInCube(cubeIndex, w))
			{
				if(workerHasRestriction(w, "daysofweek")) //check for specific days that cant work
				{
					if(isRestrictedDayOfWeek(cubeIndex, w))
					{
						continue;//check next worker because we cant place this worker
					}
				}				
				//test restrictions
				if(workerHasRestriction(w, "backtoback"))//no back to back shifts
				{
					if(isBacktoBack(cubeIndex, w))
					{
						continue;//check next worker because we cant place this worker
					}
				}
				if(workerHasRestriction(w, "workdays")) //defines work day amount range
				{
					if(reachedMaxWorkDays(w))
					{
						continue;//check next worker because we cant place this worker
					}
				}
				
				if(workerHasRestriction(w, "either"))//checks if working is working at most 1 of 2 specific days
				{
					if(isRestrictedEither(w, cubes.get(cubeIndex).dayOfWeek))
					{
						continue;//check next worker because we cant place this worker
					}
				}
				if(workerHasRestriction(w, "badpairing"))
				{
					if(badWorkPairing(cubes.get(cubeIndex), w))
					{
						continue;
					}
				}
				if(workerHasRestriction(w, "shifttype"))
				{
					if(isRestrictedShiftType(currentShiftType, w))
					{
						continue;
					}
				}
				
				//set worker to shift
				cubes.get(cubeIndex).shifts.get(shiftIndex).worker = w;
				cubes.get(cubeIndex).workers.add(w);
				//go into recursion
				//if recursion returns true we keep this worker and return true
				//otherwise try next worker


				if(addShiftToSchedule(cubeIndex, shiftIndex+1, depth+1, noPerfectShift) == true)// found a good path using this worker
				{
					if(noPerfectShift) System.out.println("AAA found worker for shift " + w.name);
					foundWorker = true;
					return true;
				}
				else
				{
					//remove worker from cube
					cubes.get(cubeIndex).shifts.get(shiftIndex).worker = null; // Remove placeholder
					removeWorkerFromCube(cubeIndex, w);
					
				}
				
			}
		}
		
		if(foundWorker == true)
		{
			return true;
		}
		else
		{
			
			if(noPerfectShift)//if we know theres no perfect shift we keep trying but dont incrament depth
			{
				cubes.get(cubeIndex).shifts.get(shiftIndex).worker = new Worker("-", new ArrayList<>(Collections.singletonList("-")));
				
				if(addShiftToSchedule(cubeIndex, shiftIndex+1, depth, noPerfectShift))
				{
					System.out.println("A");
					return true;
				}
				cubes.get(cubeIndex).shifts.get(shiftIndex).worker = null; // Remove placeholder
				return false;
			}
			else
			{
				cubes.get(cubeIndex).shifts.get(shiftIndex).worker = null; // Remove placeholder
				return false;
			}
			
		}
		
		 //if weve gone throgh all the workers for a certain shift and 
			//havent found anything that works we return false
			//make sure to cancel any changes we made
	}

	
	boolean getMaxDepth(int cubeIndex, int shiftIndex, int depth, boolean noPerfectShift) throws InterruptedException //recursive function
	{
		if(depth > maxFoundDepth)
		{
			maxFoundDepth = depth;
		}
		
		//end recursion conditions
		if(cubeIndex >= cubes.size()) //if passed last cube, means we found everyone
		{
		 return workersPassedMinWorkDays();
		}
		if(cubeIndex == cubes.size() -1 && shiftIndex >= cubes.get(cubeIndex).shifts.size() ) //if reached last shift
		{			
			//check workers have shifts in correct range
			return workersPassedMinWorkDays();

		}
		if(cubes.get(cubeIndex).shifts.size() <= shiftIndex)// reached last shift within local cube
		{
			//System.out.println(" ---- 5 ---- " +  cubes.size() + " " + cubes.get(cubeIndex).shifts.size()  + " " + depth);
			return getMaxDepth(cubeIndex+1, 0, depth, noPerfectShift); //call recursion for next cube at first shift
		}
		if(cubes.get(cubeIndex) == null)
		{
			System.out.println("null Cube!");
			return false;
		}
		if(cubes.get(cubeIndex).shifts.get(shiftIndex) == null)
		{
			System.out.println("null Shift!");
			return false;
		}
		
			if(cubes.get(cubeIndex).shifts.get(shiftIndex).worker != null && cubes.get(cubeIndex).shifts.get(shiftIndex).worker.name.equals("Missing"))// if a worker, or missing worker have already been assigned here
			{
				return getMaxDepth(cubeIndex, shiftIndex+1, depth+1, noPerfectShift);// found a good path using this worker
				
			}
		
		
		//place workers in shift
		String currentShiftType = cubes.get(cubeIndex).shifts.get(shiftIndex).type;
		boolean foundWorker = false;
		//loop through all workers and see which one we can place
		
		//within each shift check we want to randomize the workers we look through
		List<Worker> shuffledWorkers = new ArrayList<>(workers);
		Collections.shuffle(shuffledWorkers);
	
		
		int attemptedWorkers = 0;//this will show if theres definitely no one to work this shift

		for(Worker w : shuffledWorkers)
		{
			//if this worker can work this type of shift and is not working in the cube
			if(w.type.contains(currentShiftType) && !workerIsInCube(cubeIndex, w))
			{
				if(workerHasRestriction(w, "daysofweek")) //check for specific days that cant work
				{
					if(isRestrictedDayOfWeek(cubeIndex, w))
					{
						continue;//check next worker because we cant place this worker
					}
				}				
				//test restrictions
				if(workerHasRestriction(w, "backtoback"))//no back to back shifts
				{
					if(isBacktoBack(cubeIndex, w))
					{
						continue;//check next worker because we cant place this worker
					}
				}
				if(workerHasRestriction(w, "workdays")) //defines work day amount range
				{
					if(reachedMaxWorkDays(w))
					{
						continue;//check next worker because we cant place this worker
					}
				}
				
				if(workerHasRestriction(w, "either"))//checks if working is working at most 1 of 2 specific days
				{
					if(isRestrictedEither(w, cubes.get(cubeIndex).dayOfWeek))
					{
						continue;//check next worker because we cant place this worker
					}
				}
				if(workerHasRestriction(w, "badpairing"))
				{
					if(badWorkPairing(cubes.get(cubeIndex), w))
					{
						continue;
					}
				}
				if(workerHasRestriction(w, "shifttype"))
				{
					if(isRestrictedShiftType(currentShiftType, w))
					{
						continue;
					}
				}
				attemptedWorkers++;
				//System.out.println("Found worker " + w.name + "for cube and shift: " + cubeIndex + " " + shiftIndex);

				
				//set worker to shift
				cubes.get(cubeIndex).shifts.get(shiftIndex).worker = w;
				cubes.get(cubeIndex).workers.add(w);
				//go into recursion
				//if recursion returns true we keep this worker and return true
				//otherwise try next worker

				if(getMaxDepth(cubeIndex, shiftIndex+1, depth+1, noPerfectShift) == true)// found a good path using this worker
				{
					//System.out.println("found worker for shift " + w.name);
					foundWorker = true;
					return true;
				}
				else
				{
					//remove worker from cube
					removeWorkerFromCube(cubeIndex, w);
					
				}
				
			}
		}
		if(foundWorker == true)
		{
			return true;
		}
		
			//*******************************************
		if(noPerfectShift)//if we know theres no perfect shift we keep trying but dont incrament depth
		{
			cubes.get(cubeIndex).shifts.get(shiftIndex).worker = new Worker("-", new ArrayList<>(Collections.singletonList("-")));
			return getMaxDepth(cubeIndex, shiftIndex+1, depth, noPerfectShift);
		}
		else
		{
			return false;
		}
			
		
		 //if weve gone throgh all the workers for a certain shift and 
			//havent found anything that works we return false
			//make sure to cancel any changes we made
	}
	int getAvailableShifts(Worker w) {
	    int count = 0;
	    int i = 0;
	    for (Cube c : cubes) {
	        for (Shift s : c.shifts) {
	            if (isRestrictedDayOfWeek(i, w) && w.type.contains(s.type)) {
	                count++;
	            }
	        }
	    	i++;

	    }
	    return count;
	}
	int getNumOfShifts()
	{
		int ret = 0;
		for(Cube c : cubes)
		{
			ret += c.shifts.size();
		}
		return ret;
	}
	
	int numOfMissingShifts()
	{
		int ret = 0;
		for(Cube c : cubes)
		{
			for(Shift s : c.shifts)
			{
				if (s.worker != null && s.worker.name.equals("Missing"))
				{
					ret++;
				}
			}
		}
		return ret;
	}
	int getNumOfFullShifts()
	{
		int ret = 0;
		for(Cube c : cubes)
		{
			for(Shift s : c.shifts)
			{
				if(s.worker != null)
				{
					if(!s.worker.name.equals("-"))
					{
						ret++;
					}
				}
			}
		}
		return ret;
	}
	boolean workerIsInCube(Cube c, Worker w)
	{
		for(int i = 0; i < c.workers.size(); i++)
		{
			if(w.name.equals(c.workers.get(i).name))
			{
				return true;
			}
		}
		return false;
	}

	boolean workerIsInCube(int cubeIndex, Worker w)
	{
		for(int i = 0; i < cubes.get(cubeIndex).workers.size(); i++)
		{
			if(w.name.equals(cubes.get(cubeIndex).workers.get(i).name))
			{
				return true;
			}
		}
		return false;
	}
	void removeWorkerFromCube(int cubeIndex, Worker w)
	{
		for(int i = 0; i < cubes.get(cubeIndex).workers.size(); i++)
		{
			if(w.name.equals(cubes.get(cubeIndex).workers.get(i).name))
			{
				cubes.get(cubeIndex).workers.remove(i);
				return;
			}
		}
	}
	boolean workerHasRestriction(Worker w, String restrictionType)
	{
		restrictionType = restrictionType.toLowerCase();
		for(Restriction r : w.restrictions)
		{
			if(r.type.equals(restrictionType) && r.activated)
			{
				return true;
			}
		}
		
		return false;
	}
	boolean isBacktoBack(int cubeIndex, Worker worker)
	{
		//find if is activated
		for(Restriction r : worker.restrictions)
		{
			if(r.type.equals("nobacktoback"))
			{
				if(!r.activated)
				{
					return false;
				}
			}
					
		}
		if(cubeIndex == 0)//if is first shift of the week
		{
			return false;
		}
		//return whether the current worker appears in the last cubes shifts
		int currentDayofWeek = cubes.get(cubeIndex).dayOfWeek;
		for(Cube c : cubes)
		{
			if(c.dayOfWeek == currentDayofWeek -1 && workerIsInCube(c, worker))
			{
				return true;
			}
		}
		return false;
	}
	boolean isRestrictedEither(Worker worker, int dayOfWeek)
	{
		//find either restriction
		for(Restriction r : worker.restrictions)
		{
			if(r.type.equals("either") && r.activated)
			{
				int firstDay = Integer.parseInt(r.variables.get(0))-1;
				int secondDay = Integer.parseInt(r.variables.get(1))-1;
				
				if(dayOfWeek == firstDay)
				{
					if(workerIsWorkingOnCertainDay(worker, secondDay))
					{
						return true;
					}
				}
				if(dayOfWeek == secondDay)
				{
					if(workerIsWorkingOnCertainDay(worker, firstDay))
					{
						return true;
					}
				}
				
			}
		
		}
		return false;
	}
	boolean workerIsWorkingOnCertainDay(Worker worker, int dayOfWeek)
	{
		for(Cube c : cubes)
		{
			if(c.dayOfWeek == dayOfWeek)
			{
				if(workerIsInCube(c, worker))
				{
					return true;
				}
			}
		}
		return false;
	}
	boolean isRestrictedShiftType(String shiftType, Worker worker)
	{
		for(Restriction r : worker.restrictions)
		{
			if(r.type.equals("shifttype") && r.activated)
			{
				if(r.variables.get(0).equals(shiftType))
				{
					return true;
				}
			}
		}
		return false;
	}
	boolean isRestrictedDayOfWeek(int cubeIndex, Worker worker)
	{
		
		//get days of week
		for(Restriction r : worker.restrictions)
		{
			if(r.type.equals("daysofweek") && r.activated)
			{

				if(stringIsInList(String.valueOf(cubes.get(cubeIndex).dayOfWeek + 1), r.variables))
				{
					return true;
				}
			}
		}
		return false;

	}
	boolean badWorkPairing(Cube c, Worker worker)
	{
		for(Restriction r : worker.restrictions)
		{
			if(r.type.equals("badpairing") && r.activated)
			{
				String badPairName = r.variables.get(0);
				for(Worker temp : workers)
				{
					if(temp.name.equals(badPairName))
					{
						if(workerIsInCube(c, temp))
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	boolean stringIsInList(String str, ArrayList<String> list)
	{
		for(String temp : list)
		{
			if(str.equals(temp.trim()))
				
			{
				return true;
			}
		}
		
		return false;
	}
	boolean reachedMaxWorkDays(Worker worker)
	{
		int maxWorkDays = 1000;
		//get maxdays
		for(Restriction r : worker.restrictions)
		{
			if(r.type.equals("workdays") && r.activated)
			{
				int varMax =  Integer.parseInt(r.variables.get(1));			
				maxWorkDays = Math.min(varMax, maxWorkDays);
			}
		}
		int workDays = 0;
		for(Cube c : cubes)
		{
			if(workerIsInCube(c, worker)) 
			{
				workDays++;
			}
		}
		return (workDays >= maxWorkDays);
		
	}
	boolean workersPassedMinWorkDays() throws InterruptedException
	{
		//function gets called if we found a good path that matches all other criteria
		for(int i = 0; i < workers.size(); i++)//check for each worker
		{
		//	Thread.sleep(500);
			Worker w = workers.get(i);
			
			int minWorkDays = 0;
			//get maxdays
			for(Restriction r : w.restrictions)
			{
				if(r.type.equals("workdays") && r.activated)
				{
					int varMin =  Integer.parseInt(r.variables.get(0));
					minWorkDays = Math.max(minWorkDays, varMin);

				}
			}
			int workDays = 0;
			//check for each cube if the worker is working there
			for(Cube c : cubes)
			{
				for(Worker tempWorker : c.workers)
				{

					if(tempWorker.name.equals(w.name)) 
					{
						workDays++;
					}
				}
				
			}
			if(workDays < minWorkDays)
			{
				//if we found someone who isnt working enough days
				return false;
			}
		}
		//if we didnt find anyone not working enough

		return true;
	}
}
