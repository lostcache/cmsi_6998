package main

import (
	"math/rand"
	"sync"
)

const (
	NUM_PHILOSOPHER      uint = 5
	NUM_WAITERS          uint = 2
	NUM_SEATS            uint = NUM_PHILOSOPHER
	NUM_CHOPSTICKS       uint = NUM_SEATS
	KITCHEN_COUNTER_SIZE uint = NUM_PHILOSOPHER
	NUM_CHEFS            uint = 3
)

var PHIL_NAMES = []string{"Phil 1", "Phil 2", "Phil 3", "Phil 4", "Phil 5"}
var WAITER_NAMES = []string{"Waiter 1", "Waiter 2"}
var CHEF_NAMES = []string{"Chef 1", "Chef 2", "Chef 3"}

type Restaurant struct {
	philosophers   []*Philosopher
	waiters        []*Waiter
	chefs          []*Chef
	kitchenCounter chan *Order
	table          *Table
}

func newRestaurant() *Restaurant {
	log("Building Restaurant")
	r := &Restaurant{
		philosophers:   make([]*Philosopher, NUM_PHILOSOPHER),
		waiters:        make([]*Waiter, NUM_WAITERS),
		chefs:          make([]*Chef, NUM_CHEFS),
		kitchenCounter: make(chan *Order, KITCHEN_COUNTER_SIZE),
		table:          newTable(),
	}

	r.initWaiters()
	r.initChefs()
	r.initPhilosophers()

	return r
}

func (r *Restaurant) initPhilosophers() {
	for i := range NUM_PHILOSOPHER {
		r.philosophers[i] = newPhilosopher(PHIL_NAMES[i], r)
	}
	log("philosophers ready")
}

func (r *Restaurant) initWaiters() {
	for i := range r.waiters {
		r.waiters[i] = newWaiter(WAITER_NAMES[i], r)
	}
	log("Hiring waiters")
}

func (r *Restaurant) initChefs() {
	for i := range NUM_CHEFS {
		r.chefs[i] = newChef(CHEF_NAMES[i], r)
	}
	log("Hiring chefs")
}

func (r *Restaurant) giveSeat() uint {
	return r.table.giveSeat()
}

func (r *Restaurant) takeSeatBack(seat uint) {
	r.table.takeSeatBack(seat)
}

func (r *Restaurant) waitGetChopsticks(seat uint) {
	r.table.waitGetChopsticks(seat)
}

func (r *Restaurant) releaseChopsticks(seat uint) {
	r.table.releaseChopsticks(seat)
}

func (r *Restaurant) getRandomPhilosopher() *Philosopher {
	phil := r.philosophers[rand.Intn((int)(NUM_PHILOSOPHER))]
	assert(phil != nil)
	return phil
}

func (r *Restaurant) getRandomChef() *Chef {
	chef := r.chefs[rand.Intn(len(r.chefs))]
	assert(chef != nil)
	return chef
}

func (r *Restaurant) putOrderOnKitchenCounter(order *Order) {
	r.kitchenCounter <- order
}

func (r *Restaurant) terminateChefs() {
	for i := range r.chefs {
		r.chefs[i].terminate()
	}
}

func (r *Restaurant) terminateWaiters() {
	for i := range r.waiters {
		r.waiters[i].terminate()
	}
}

func (r *Restaurant) run() {
	pwg := sync.WaitGroup{}
	wg := sync.WaitGroup{}
	pwg.Add((int)(NUM_PHILOSOPHER))
	wg.Add((int)(NUM_WAITERS + NUM_CHEFS))
	log("Restaurant is now open for business")
	for i := range r.philosophers {
		go func(p *Philosopher) {
			assert(p != nil)
			defer pwg.Done()
			p.run()
		}(r.philosophers[i])
	}

	for i := range r.waiters {
		go func(w *Waiter) {
			assert(w != nil)
			defer wg.Done()
			w.run()
		}(r.waiters[i])
	}

	for i := range r.chefs {
		go func(c *Chef) {
			assert(c != nil)
			defer wg.Done()
			c.run()
		}(r.chefs[i])
	}
	pwg.Wait()
	r.terminateChefs()
	r.terminateWaiters()
	wg.Wait()
}
