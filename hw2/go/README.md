# README

## Description

This project simulates a restaurant environment where philosophers (customers) order meals, waiters take and serve orders, and chefs cook the meals. The simulation involves multiple threads representing philosophers, waiters, and chefs, each performing their respective tasks concurrently. The goal is to demonstrate the interactions between these entities while ensuring proper synchronization and handling of concurrent operations.

## Technologies Used

- **Go**: The primary programming language used for implementing the simulation.
- **Go Concurrency**: Utilizes Go's goroutines and channels to manage concurrent operations and ensure thread safety.

## Getting Started

### Prerequisites

- **Go**: Ensure you have Go installed on your machine. You can download it from [the official Go website](https://golang.org/dl/).
- **Command Line Interface (CLI)**: Basic familiarity with using the command line for compiling and running Go programs.

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
cd cmsi_6998/hw2/go
```

2. Build the project using the following command:

```bash
go build -o restaurant .
```

3. Run the executable:

```bash
./restaurant
```

Alternatively, you can use the provided `run.sh` script:

```bash
./run.sh
```

### Expected Output

The simulation will start, and you will see logs printed to the console indicating the actions performed by philosophers, waiters, and chefs. The simulation will continue until all philosophers have either run out of money or the program is manually terminated.

### Project Structure

- **assert.go**: Provides an assertion function to panic if a condition is not met.
- **chef.go**: Represents a chef who cooks meals.
- **logger.go**: Provides logging functions to print messages to the console.
- **main.go**: The entry point of the simulation.
- **meal.go**: Defines the different meals available in the restaurant.
- **order.go**: Represents an order placed by a philosopher.
- **philosopher.go**: Represents a philosopher who orders meals and eats.
- **restaurant.go**: Manages the overall restaurant operations, including starting and shutting down the simulation.
- **table.go**: Manages the seating and chopsticks for philosophers.
- **waiter.go**: Represents a waiter who takes orders from philosophers and serves cooked meals.

### Contributing

Contributions to this project are welcome. If you find any issues or have suggestions for improvements, please feel free to open an issue or submit a pull request.

### License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
