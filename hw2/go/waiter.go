package main

import (
	"time"
)

type Waiter struct {
	name       string
	restaurant *Restaurant
	dayOver    chan struct{}
}

func newWaiter(name string, r *Restaurant) *Waiter {
	return &Waiter{name: name, restaurant: r, dayOver: make(chan struct{}, 1)}
}

func (w *Waiter) tryTakeOrderFromPhilosopher() *Order {
	logf("%s waiting for order from philosopher", w.name)
	philosopher := w.restaurant.getRandomPhilosopher()
	select {
	case order := <-philosopher.orderChan:
		logf("%s takes order %s from %s", w.name, order.name(), philosopher.name)
		philosopher.notifyOrderSuccess()
		return order
	case <-time.After(1 * time.Second):
		return nil
	}
}

func (w *Waiter) tryGiveOrderToChef(order *Order) bool {
	chef := w.restaurant.getRandomChef()
	select {
	case chef.orderChan <- order:
		logf("%s gives order %s of %s to %s", w.name, order.name(), order.orderedBy.name, chef.name)
		return true
	case <-time.After(1 * time.Second):
		logf("%s failed to give order %s to %s, will now refund %s", w.name, order.name(), chef.name, order.orderedBy.name)
		return false
	}
}

func (w *Waiter) markAsRefund(order *Order) {
	order.markRefunded()
}

func (w *Waiter) serveOrder(order *Order) {
	assert(order != nil)
	logf("%s serves order %s to %s", w.name, order.name(), order.orderedBy.name)
	order.orderedBy.receiveOrder(order)
}

func (w *Waiter) terminate() {
	w.dayOver <- struct{}{}
}

func (w *Waiter) run() {
	for {
		select {
		case <-w.dayOver:
			logf("%s is closing for the day", w.name)
			return
		case order := <-w.restaurant.kitchenCounter:
			w.serveOrder(order)
		default:
			order := w.tryTakeOrderFromPhilosopher()
			if order != nil {
				success := w.tryGiveOrderToChef(order)
				if !success {
					w.markAsRefund(order)
					w.serveOrder(order)
				}
			}
			time.Sleep(10 * time.Millisecond)
		}
	}
}
