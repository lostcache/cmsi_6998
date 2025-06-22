package main

import "time"

type Table struct {
	emptySeats chan uint
	chopsticks []chan struct{}
}

func newTable() *Table {
	t := &Table{
		emptySeats: make(chan uint, NUM_SEATS),
		chopsticks: make([]chan struct{}, NUM_CHOPSTICKS),
	}

	for i := range NUM_CHOPSTICKS {
		t.chopsticks[i] = make(chan struct{}, 1)
		t.chopsticks[i] <- struct{}{}
	}

	for i := range NUM_SEATS {
		t.emptySeats <- i
	}

	return t
}

func (t *Table) waitGetChopsticks(seat uint) {
	left := seat % (NUM_CHOPSTICKS)
	right := (seat + 1) % (NUM_CHOPSTICKS)

	ltout := time.After(time.Second)
	for {
		select {
		case <-t.chopsticks[left]:
			rtout := time.After(time.Second)
			select {
			case <-t.chopsticks[right]:
				return
			case <-rtout:
				t.chopsticks[left] <- struct{}{}
				time.Sleep(time.Second)
			}
		case <-ltout:
			time.Sleep(time.Second)
		}
	}

}

func (t *Table) releaseChopsticks(seatNo uint) {
	t.chopsticks[seatNo%NUM_CHOPSTICKS] <- struct{}{}
	t.chopsticks[(seatNo+1)%NUM_CHOPSTICKS] <- struct{}{}
}

func (t *Table) giveSeat() uint {
	return <-t.emptySeats
}

func (t *Table) takeSeatBack(seat uint) {
	t.emptySeats <- seat
}
