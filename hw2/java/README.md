# README

## Description

This project simulates a restaurant environment where philosophers (customers) order meals, waiters take and serve orders, and chefs cook the meals. The simulation involves multiple threads representing philosophers, waiters, and chefs, each performing their respective tasks concurrently. The goal is to demonstrate the interactions between these entities while ensuring proper synchronization and handling of concurrent operations.

## Technologies Used

- **Java**: The primary programming language used for implementing the simulation.
- **Java Concurrency**: Utilizes Java's concurrency utilities (e.g., `Thread`, `ReentrantLock`, `ConcurrentLinkedQueue`) to manage multiple threads and ensure thread safety.

## Getting Started

### Prerequisites

- **Java Development Kit (JDK)**: Ensure you have JDK installed on your machine. You can download it from [Oracle's official website](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) or use an alternative JDK provider.
- **Command Line Interface (CLI)**: Basic familiarity with using the command line for compiling and running Java programs.

### Cloning the Repository

1. Open your terminal or command prompt.
2. Navigate to the directory where you want to clone the repository.
3. Run the following command to clone the repository:

```bash
git clone git@github.com:lostcache/cmsi_6998.git
```

### Running the Project

1. Navigate to the cloned repository directory:

```bash
cd cmsi_6998/hw2/java
```

2. Compile the Java files using the following command:

```bash
javac *.java
```

3. Run the main class `Main` with the following command:

```bash
java -ea Main
```

The `-ea` flag enables assertions, which are used for debugging purposes in the code.

### Expected Output

The simulation will start, and you will see logs printed to the console indicating the actions performed by philosophers, waiters, and chefs. The simulation will continue until all philosophers have either run out of money or the program is manually terminated.

### Project Structure

- **Chef.java**: Represents a chef who cooks meals.
- **Kitchen.java**: Manages the kitchen's order queue and provides access to available chefs.
- **Logger.java**: Provides a simple logging mechanism to print messages to the console.
- **Main.java**: The entry point of the simulation.
- **Meal.java**: Defines the different meals available in the restaurant.
- **Order.java**: Represents an order placed by a philosopher.
- **Pair.java**: A utility class to represent a pair of values.
- **Philosopher.java**: Represents a philosopher who orders meals and eats.
- **Restaurant.java**: Manages the overall restaurant operations, including starting and shutting down the simulation.
- **Table.java**: Manages the seating and chopsticks for philosophers.
- **Waiter.java**: Represents a waiter who takes orders from philosophers and serves cooked meals.

### Contributing

Contributions to this project are welcome. If you find any issues or have suggestions for improvements, please feel free to open an issue or submit a pull request.

### License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
