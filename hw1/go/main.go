package main

import (
	"fmt"
	"sync"
)

const (
	MIN_INT = 2
	MAX_INT = 100
)

func primeFilter(divisor int, inChan chan int, wg *sync.WaitGroup) {
	defer wg.Done()

	fmt.Println(divisor)

	var nextChan chan int
	var nextStarted bool

	for {
		num := <-inChan

		if num == -1 {
			if nextChan != nil {
				nextChan <- -1
			}
			break
		}

		if num%divisor != 0 {
			if !nextStarted {
				nextChan = make(chan int, 100)
				nextStarted = true
				wg.Add(1)
				go primeFilter(num, nextChan, wg)
			}

			if nextChan != nil {
				nextChan <- num
			}
		}
	}
}

func main() {
	var wg sync.WaitGroup

	firstChan := make(chan int, 100)

	wg.Add(1)
	go primeFilter(2, firstChan, &wg)

	for i := MIN_INT; i < MAX_INT; i++ {
		firstChan <- i
	}

	firstChan <- -1

	wg.Wait()
}
