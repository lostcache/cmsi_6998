package main

type Order struct {
	orderedBy *Philosopher
	refunded  bool
	meal      Meal
}

func (o *Order) markRefunded() {
	o.refunded = true
}

func (o *Order) isRefunded() bool {
	return o.refunded
}

func (o *Order) price() float64 {
	return o.meal.price
}

func (o *Order) name() string {
	return o.meal.getName()
}
