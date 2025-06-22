package main

func main() {
	restaurantEnv := InitEnv()
	StartSim(restaurantEnv)
}

func InitEnv() *Restaurant {
	return newRestaurant()
}

func StartSim(restaurant *Restaurant) {
	restaurant.run()
}
