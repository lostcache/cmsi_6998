package main

import "math/rand"

type Meal struct {
	id    int
	name  string
	price float64
}

var Meals = []Meal{
	{1, "Paella", 13.25},
	{2, "Wu_Hsiang_Chi", 10.00},
	{3, "Bogrács_Gulyás", 11.25},
	{4, "Spanokopita", 6.50},
	{5, "Moui_Nagden", 12.95},
	{6, "Sambal_Goreng_Udang", 14.95},
}

func (m *Meal) getPrice() float64 {
	return m.price
}

func (m *Meal) getName() string {
	return m.name
}

func (m *Meal) ID() int {
	return m.id
}

func getRandomMeal() Meal {
	return Meals[rand.Intn(len(Meals))]
}
