package main

import "time"

type Chef struct {
	name         string
	restaurant   *Restaurant
	orderChan    chan *Order
	cookedOrders int
	dayOver      chan struct{}
}

func newChef(name string, restaurant *Restaurant) *Chef {
	return &Chef{
		name:       name,
		restaurant: restaurant,
		orderChan:  make(chan *Order, 1),
		dayOver:    make(chan struct{}, 1),
	}
}

func (c *Chef) waitForOrder() *Order {
	logf("%s is waiting for order", c.name)
	return <-c.orderChan
}

func (c *Chef) cook(order *Order) {
	logf("%s is cooking order %s", c.name, order.name())
	time.Sleep(time.Second)
}

func (c *Chef) putOrderOnKitchenCounter(order *Order) {
	logf("%s finished cooking order %s and puts it on kitchen counter", c.name, order.name())
	c.restaurant.putOrderOnKitchenCounter(order)
}

func (c *Chef) takeCoffeeBreakIfItsTime() {
	if c.cookedOrders != 0 && c.cookedOrders%4 == 0 {
		logf("%s takes a coffee break", c.name)
		time.Sleep(time.Minute)
	}
}

func (c *Chef) terminate() {
	c.dayOver <- struct{}{}
}

func (c *Chef) run() {
	for {
		select {
		case <-c.dayOver:
			logf("%s is closing for the day", c.name)
			return
		case order := <-c.orderChan:
			c.cook(order)
			c.putOrderOnKitchenCounter(order)
			c.takeCoffeeBreakIfItsTime()
		}
	}
}
