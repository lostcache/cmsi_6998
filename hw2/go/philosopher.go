package main

import (
	"time"
)

type Philosopher struct {
	name              string
	balance           float64
	restaurant        *Restaurant
	seatNo            int
	forkPair          *ForkPair
	orderChan         chan *Order
	orderNotification chan struct{}
}

func newPhilosopher(name string, restaurant *Restaurant) *Philosopher {
	return &Philosopher{
		name:              name,
		balance:           30.0,
		restaurant:        restaurant,
		seatNo:            -1,
		forkPair:          nil,
		orderChan:         make(chan *Order, 1),
		orderNotification: make(chan struct{}, 1),
	}
}

func (p *Philosopher) orderFood() {
	assert(p.seatNo != -1)
	order := &Order{
		orderedBy: p,
		meal:      getRandomMeal(),
		refunded:  false,
	}
	logf("%s orders %s", p.name, order.name())
	p.orderChan <- order
}

func (p *Philosopher) takeSeat() {
	assert(p.seatNo == -1)
	p.seatNo = (int)(p.restaurant.giveSeat())
	logf("%s takes a seat at chair %d", p.name, p.seatNo)
}

func (p *Philosopher) emptySeat() {
	p.restaurant.takeSeatBack((uint)(p.seatNo))
}

func (p *Philosopher) leaveRestaurant() {
	assert(p.seatNo != -1)
	p.emptySeat()
	p.seatNo = -1
	logf("%s leaves the restaurant", p.name)
	time.Sleep(time.Duration(1000))
}

func (p *Philosopher) waitForCookedOrder() *Order {
	logf("%s waits for cooked order", p.name)
	assert(p.seatNo != -1)
	order := <-p.orderChan
	assert(order.orderedBy == p)
	logf("%s receives order %s", p.name, order.name())
	return order
}

func (p *Philosopher) eat(order *Order) {
	assert(p.seatNo != -1)
	time.Sleep(time.Duration(time.Second))
	logf("%s finished eating %s", p.name, order.name())
}

func (p *Philosopher) pay(order *Order) {
	assert(order != nil)
	assert(p.seatNo != -1)
	p.balance -= order.price()
	logf("%s pays for %s, new balance %f", p.name, order.name(), p.balance)
}

func (p *Philosopher) waitForWaiterToTakeOrder() bool {
	logf("%s waits for waiter to take order", p.name)
	assert(p.seatNo != -1)
	timeout := time.After(1 * time.Second)
	select {
	case <-p.orderNotification:
		logf("%s received notification from waiter", p.name)
		return true
	case <-timeout:
		<-p.orderChan
		return false
	}
}

func (p *Philosopher) receiveOrder(order *Order) {
	logf("%s receives order %s at seat %d", p.name, order.name(), p.seatNo)
	assert(order != nil)
	assert(p.seatNo != -1)
	p.orderChan <- order
}

func (p *Philosopher) getRefund() {
	assert(p.seatNo != -1)
	p.balance += 5
}

func (p *Philosopher) notifyOrderSuccess() {
	p.orderNotification <- struct{}{}
}

func (p *Philosopher) waitForChopsticks() {
	logf("%s waiting for chopsticks", p.name)
	assert(p.seatNo != -1)
	p.restaurant.waitGetChopsticks((uint)(p.seatNo))
	logf("%s acquired chopsticks", p.name)
}

func (p *Philosopher) releaseChopsticks() {
	assert(p.seatNo != -1)
	p.restaurant.releaseChopsticks((uint)(p.seatNo))
	logf("%s released chopsticks", p.name)
}

func (p *Philosopher) run() {
	for p.balance > 0 {
		p.takeSeat()
		p.orderFood()
		success := p.waitForWaiterToTakeOrder()
		if !success {
			p.leaveRestaurant()
			continue
		}

		receivedOrder := p.waitForCookedOrder()

		if receivedOrder.isRefunded() {
			p.getRefund()
			p.leaveRestaurant()
			continue
		}

		p.waitForChopsticks()
		p.eat(receivedOrder)
		p.releaseChopsticks()
		p.pay(receivedOrder)
		p.leaveRestaurant()
	}
}
