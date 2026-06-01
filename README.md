Postmortem
A Java-based bug logging and tracking application for capturing, organizing, and reviewing software defects.

Prototype Notice: This project is currently in early prototype stage. Features, UI flows, and implementation details are still evolving. It is not yet production-ready.


Overview
Postmortem is a desktop bug logging tool built in Java. It provides a structured interface for recording and managing bug reports — designed to streamline the process of documenting defects during development and testing cycles.

Features (Prototype)

Log and categorize software bugs
View and review existing bug entries
Lightweight desktop launcher via batch script (Run-Postmortem.bat)
Custom UI styling with CSS


Project Structure
Postmortem/
├── lib/                    # External libraries and dependencies
├── src/
│   └── postmortem/         # Core Java source files
├── Run-Postmortem.bat      # Windows launcher script
└── README.md

Requirements

Java JDK 11 or higher
Windows (for the .bat launcher; cross-platform support planned)


Getting Started

Clone the repository

bash   git clone https://github.com/Sir-Hasn/Postmortem.git
   cd Postmortem

Compile the project

bash   javac -cp lib/* -d out src/postmortem/*.java

Run the application
On Windows, double-click Run-Postmortem.bat, or from the terminal:

bash   Run-Postmortem.bat
Alternatively, run manually:
bash   java -cp out;lib/* postmortem.Main

Roadmap
Planned improvements for upcoming releases:

Better stability and performance
More complete feature coverage (filtering, sorting, export)
UI and UX refinements
Improved test coverage and maintainability
Cross-platform support


Contributing
This project is in active prototype development. Contributions, suggestions, and feedback are welcome. Feel free to open an issue or submit a pull request.
