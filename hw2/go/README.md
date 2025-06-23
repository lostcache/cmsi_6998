# 🍽️ Dining Philosophers Restaurant Simulation (Go)

This project is a concurrent simulation of a restaurant based on the **Dining Philosophers Problem**, written in Go. It models realistic restaurant behavior using goroutines and channels to coordinate philosophers (customers), waiters, and chefs in a synchronized environment.

## 🧩 Problem Modeled

This simulation extends the classic _Dining Philosophers_ problem with a richer ecosystem:

- **Philosophers** represent customers who arrive, order food, eat, and leave.
- **Waiters** act as intermediaries between philosophers and chefs, handling orders.
- **Chefs** prepare meals and place them on a shared kitchen counter.
- **A kitchen counter** acts as a buffer between the chefs and waiters.
- **A seating system** ensures limited seat availability at the restaurant.

## 🔧 Features

- Fully concurrent using Go's goroutines and channels
- Order queue and food preparation logic with refund handling
- Graceful shutdown of waiters and chefs
- Simulated money balance for philosophers
- Simulated time delays for cooking, eating, and waiter interactions

## 🏗️ Architecture

```

```

            +------------------+
            |   Restaurant     |
            +------------------+
             |        |       |

+-----------+ +--+--+ ++--+
| | | | |

```

+----v---+       +-----v+ +--v--+v--+v--+
\| Phil 1 |       | Waiter 1 |  Chef 1  |
\| Phil 2 |       | Waiter 2 |  Chef 2  |
\|  ...   |       |    ...   |  Chef 3  |
+--------+       +----------+----------+

```

- **Philosophers** place orders → **Waiters** collect & pass to **Chefs**
- **Chefs** cook → place on **kitchen counter** → **Waiters** serve

## 🚀 How to Run

### Requirements

- Go 1.20+ (tested with Go 1.22)

### Build and Execute

```bash
go build -o restaurant .
./restaurant
```

Or, if you have a `run.sh`:

```bash
./run.sh
```

### Output

Logs will be printed to the console, simulating restaurant activity:

- Philosopher actions
- Waiter/chef interactions
- Payments and refunds
- Shutdown and deadlock checks

## 📁 Project Structure

```
.
├── main.go            # Entry point
├── restaurant.go      # Core restaurant logic
├── philosopher.go     # Philosopher behavior
├── waiter.go          # Waiter logic
├── chef.go            # Chef logic
├── order.go           # Order structure & meals
├── util.go            # Logging, assertions
├── run.sh             # Startup script
```

## 🧠 Concurrency Concepts Used

- Goroutines for each Philosopher, Waiter, and Chef
- Buffered and unbuffered channels
- Select statement with timeout and shutdown
- WaitGroups for graceful termination
- Avoiding deadlocks and starvation

## 🧪 Example Meals

A few of the meals used in simulation:

- `Sambal_Goreng_Udang`
- `Spanokopita`
- `Paella`
- `Moui_Nagden`
- `Wu_Hsiang_Chi`
- `Bogrács_Gulyás`

## 📚 Learnings

This project is ideal for understanding:

- Synchronization between multiple producer-consumer roles
- Realistic resource contention (limited seats, chefs)
- Proper use of Go's `select`, `chan`, and `sync.WaitGroup`

## 📄 License

This project is for educational purposes. No license attached yet.

---

> Made with 🧠, 🍜, and `go run` by \[Your Name]
