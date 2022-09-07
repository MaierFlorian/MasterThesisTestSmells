# MasterThesisTestSmells

In the field of software engineering, the term "smell" refers to a state of code in which it appears necessary to improve it using refactoring methods. In this way, for example, long, confusing code is made readable and easy to understand again. This makes it easier for the code to be maintained and reduces the occurrence of bugs. Such smells occur not only in production code, but also in test code. In software projects, tests are of great importance, and therefore it is also important to write smell-free tests.

This master thesis is dedicated to the creation of a test smell detection tool that detects a total of five test smells, namely the anonymous test, assertion roulette, long test, rotten green test and conditional test logic smell, in JUnit tests. This not only adds our tool to the list of existing detection tools, but also expands the list of recognizable smells, as it is the first tool to recognize the anonymous test.

Furthermore, in the course of this master thesis, a dataset of JUnit tests was created to measure the accuracy of our tool. The tool proved to be highly accurate with a precision of about 87-100\%.

## How to run the tool

Execute the TestSmellDetector.jar file with the command `java -jar TestSmellDetector.jar` or by double-clicking on it.
