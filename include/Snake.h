#ifndef SNAKE_H
#define SNAKE_H

#include <vector>
#include "Point.h"

enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT
};

class Snake {
public:
    Snake(int initialX, int initialY);

    void Move();
    void Grow();
    bool CheckSelfCollision() const;
    const std::vector<Point>& GetBody() const;
    Point GetHead() const;
    void SetDirection(Direction newDirection);
    Direction GetDirection() const;

private:
    std::vector<Point> body;
    Direction currentDirection;
    bool justAteFood_; // Flag to indicate if the snake has just eaten food
};

#endif // SNAKE_H
