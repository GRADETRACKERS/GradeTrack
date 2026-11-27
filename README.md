# GradeTrack - Student Grade and GPA Tracking System

OOP101 Final Project. Java Swing, no libraries, no database. All code is in the `src` folder (19 small files, about 630 lines of code).

## Run it
- Install JDK 17 or newer (https://adoptium.net), then double-click `GradeTrack.jar` (or `java -jar GradeTrack.jar`).
- From source: run `run.bat` (Windows) or `./run.sh` (Mac/Linux).
- NetBeans / IntelliJ: open the `src` folder as sources, main class = `Main`.

Data is saved automatically in `<your home folder>/.gradetrack/gradetrack_data.txt`.

## Website
`index.html` + `GradeTrack.jar` are the website (CheerpJ runs the Java app in the browser).
Upload everything to a GitHub repository, then Settings -> Pages -> Deploy from a branch -> `main` / `(root)`.
If you change the code, rebuild the jar:
`javac --release 17 -d out src/*.java` then `jar cfe GradeTrack.jar Main -C out .`

## The classes
| File | What it does | OOP / Java idea (lesson) |
|---|---|---|
| `Assessment` | One graded item: name, score, "Out of". | Abstract class, encapsulation (private fields, getters/setter), constructor with `this` |
| `Quiz`, `Test`, `Examination`, `Laboratory`, `Seatwork`, `Assignment`, `Recitation`, `Project`, `PerformanceTask` | The nine score types. Each only says its type. | Inheritance (`extends`, `super`), overriding, polymorphism |
| `Course` | Name, credits, weights, list of scores. Adds / removes / searches / sorts scores and calculates its own grade. | `ArrayList`, `HashMap`, arrays, `switch`, method overloading, nested loops (bubble sort) |
| `Gpa` | Percent -> grade point, and the GPA weighted by credits. | `static` methods, arrays, `for` loop with `return` |
| `Input` | Turns typed text into numbers. | `try-catch`, `throw` |
| `GradeException` | One custom exception for every input mistake. | Custom exception, `throws` |
| `DataFile` | Saves and loads everything in a text file. | File handling, `IOException`, multi-catch |
| `Main` | The window, the list of courses shared by all tabs, and 3 small `static` helpers and `ReadOnlyModel`. | Swing `JFrame`, `static` methods, `extends`, `try-catch-finally` |
| `CoursesPanel`, `TargetPanel`, `SummaryPanel` | The three tabs. | Swing panels, `implements ActionListener`, `if / else if` |

## How the grade is computed
- A course has nine score types: Quiz, Test, Examination (prelim / midterm / final), Laboratory, Seatwork, Assignment, Recitation, Project, Performance Task. Weights add up to 100%; a type with 0% is simply not used by that course (new courses start at Quiz 20, Test 20, Exam 40, Lab 20).
- **Average of a type** = points earned / points graded (only graded work counts).
- **Current grade** = weighted average of the types that have graded work. A score left empty ("not graded yet") never changes it.
- **Graded %** = how much of the whole course is already graded.
- **Target grade**: needed average = (target - points already earned) / (course % still not graded) x 100.
- **GPA scale** (1.00 best): 97-100 = 1.00, 94-96 = 1.25, 91-93 = 1.50, 88-90 = 1.75, 85-87 = 2.00, 82-84 = 2.25, 79-81 = 2.50, 76-78 = 2.75, 75 = 3.00, below 75 = 5.00.
  Example: Physics 77.7 (4 credits), Calculus 85.8 (3), Intro 88.0 (3) -> GPA 2.23. Change the two arrays at the top of `Gpa` for another scale.

## Tests
`test/Tests.java` has 47 checks (grades, GPA, target, errors, search/sort, file saving):
```
javac -d out src/*.java
javac -cp out -d testout test/Tests.java
java -cp out:testout Tests
```
