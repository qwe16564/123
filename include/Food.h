#ifndef FOOD_H
#define FOOD_H

#include "Point.h"
#include <vector> // Required for PlaceNewFood signature

class Food {
public:
    Food(int boardWidth, int boardHeight); // Constructor
    void PlaceNewFood(const std::vector<Point>& snakeBody); // Ensure food isn't placed on the snake
    Point GetPosition() const;

private:
    Point position;
    int boardWidth;
    int boardHeight;
};

#endif // FOOD_H
