#include "Snake.h"

Snake::Snake(int initialX, int initialY) : currentDirection(Direction::RIGHT), justAteFood_(false) {
    body.push_back(Point(initialX, initialY));
}

void Snake::Move() {
    Point newHead = body.front();
    switch (currentDirection) {
        case Direction::UP:
            newHead.y--;
            break;
        case Direction::DOWN:
            newHead.y++;
            break;
        case Direction::LEFT:
            newHead.x--;
            break;
        case Direction::RIGHT:
            newHead.x++;
            break;
    }
    body.insert(body.begin(), newHead);

    if (justAteFood_) {
        justAteFood_ = false; // Reset for next move
    } else {
        if (body.size() > 1) { // Don't pop if snake is just a head and hasn't eaten
            body.pop_back();
        }
    }
}

void Snake::Grow() {
    justAteFood_ = true;
}

bool Snake::CheckSelfCollision() const {
    if (body.size() <= 1) {
        return false;
    }
    Point head = body.front();
    for (std::size_t i = 1; i < body.size(); ++i) {
        if (body[i] == head) {
            return true;
        }
    }
    return false;
}

const std::vector<Point>& Snake::GetBody() const {
    return body;
}

Point Snake::GetHead() const {
    if (!body.empty()) {
        return body.front();
    }
    // Return a point indicating an error or off-board position
    // This case should ideally not be reached in normal gameplay.
    return Point(-1, -1); 
}

void Snake::SetDirection(Direction newDirection) {
    // Prevent immediate 180-degree turns
    bool invalidMove = false;
    if (currentDirection == Direction::UP && newDirection == Direction::DOWN) invalidMove = true;
    if (currentDirection == Direction::DOWN && newDirection == Direction::UP) invalidMove = true;
    if (currentDirection == Direction::LEFT && newDirection == Direction::RIGHT) invalidMove = true;
    if (currentDirection == Direction::RIGHT && newDirection == Direction::LEFT) invalidMove = true;

    if (!invalidMove) {
        currentDirection = newDirection;
    }
}

Direction Snake::GetDirection() const {
    return currentDirection;
}
