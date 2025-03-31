Shift Scheduling Optimization

This project is a shift scheduling optimization tool designed to assign workers to shifts based on a variety of constraints. The goal is to create an efficient schedule while respecting worker preferences and availability. The advantage of the program is that it enables the owner of the business to easily generate shifts while keeping as many of the workers preferences as possible

Features

Efficient scheduling algorithm with customizable constraints

Uses JavaFX for a graphical user interface

Automatically generates a visual representation of the schedule as an image

Supports various worker restrictions and shift types

Recursive depth-first search to find optimal scheduling solutions


Getting Started

Prerequisites

Java SE 9 or higher

Eclipse IDE (recommended)

Installation

Clone the repository:

git clone https://github.com/Kedem-Yomtov/Automatic-Shift-Scheduler.git
cd shift-scheduling

Open the project in Eclipse.

Ensure that your Java build path is correctly set up.

Running the Project

To run the scheduling algorithm, execute the Main.java class in Eclipse. The program will print debug information and the final scheduling output to the console.

Project Structure

src/ - Contains the source code files.

Main.java - Entry point of the program.

Cube.java - Represents a set of shifts grouped together.

Shift.java - Represents a single shift, including worker assignment.

Worker.java - Represents an individual worker with preferences and restrictions.

Schedule.java - Represents the entire Schedule, built of cubes for each grouped shift

Restrition.java - represents a restriction belonging to a worker

Usage

Customize worker and shift data in the source files, then run the project to generate optimized schedules. You can adjust the restrictions and preferences as needed.

Schedule File Format

The schedule file defines the structure of shifts for each day and the roles needed. It follows this format:
[Schedule Name]
[Role 1] [Role 2] [Role 3] ...
Cube:
[Cube Number] [Start Time] [End Time]
[Role] [Start Time] [End Time]
[Role] [Start Time] [End Time]
...

Example:
Bar
bartender kitchen waitress
Cube:
1 1745 0200
Bartender 1745 0200
Kitchen 1830 0100
Waitress 2000 0200
Cube:
2 1745 0200
Bartender 1745 0200
Kitchen 1830 0100
Waitress 2000 0200

Shift File Format:
[Worker Name]
[Roles]
[r or p] [Restriction Type] [Restriction Weight] [Variables]

Example:
Ked
Bartender
r daysOfWeek 0 2 3 5 7
r workDays 0 1 2

Barb
waitress Bartender kitchen
p workDays 0 1 1
r daysOfWeek 0 2 3 4 5 7


Troubleshooting

If you encounter unexpected results, check the console for debug messages.

Ensure that all necessary dependencies and Java versions are properly configured.

Contributing

Contributions are welcome! Feel free to open issues or submit pull requests to enhance functionality or fix bugs.

License

This project is licensed under the MIT License.

