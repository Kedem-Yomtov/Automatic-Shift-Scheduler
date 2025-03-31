Shift Scheduling Optimization

This project is a shift scheduling optimization tool designed to assign workers to shifts based on a variety of constraints. The goal is to create an efficient schedule while respecting worker preferences and availability. The advantage of the program is that it enables the owner of the business to easily generate shifts while keeping as many of the workers preferences as possible

Features

Recursive depth-first search algorithm to find optimal shift assignments.

Supports worker restrictions, such as:
  Maximum workdays
  Back-to-back shift limitations
  Day-of-week restrictions
  Shift type preferences
  Incompatible pairings

Automatic placeholder handling for missing workers without altering their assignment.

Randomized worker selection to ensure variability.

Detailed debugging output to track the scheduling process.

Getting Started

Prerequisites

Java SE 9 or higher

Eclipse IDE (recommended)

Installation

Clone the repository:

git clone https://github.com/username/shift-scheduling.git
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

Usage

Customize worker and shift data in the source files, then run the project to generate optimized schedules. You can adjust the restrictions and preferences as needed.

Troubleshooting

If you encounter unexpected results, check the console for debug messages.

Ensure that all necessary dependencies and Java versions are properly configured.

Contributing

Contributions are welcome! Feel free to open issues or submit pull requests to enhance functionality or fix bugs.

License

This project is licensed under the MIT License.

