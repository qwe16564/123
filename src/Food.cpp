#include "Food.h"
#include <cstdlib>  // For rand() and srand()
#include <ctime>    // For time()
#include <algorithm> // For std::find

Food::Food(int _boardWidth, int _boardHeight) : boardWidth(_boardWidth), boardHeight(_boardHeight) {
    srand(static_cast<unsigned int>(time(nullptr))); // Seed random number generator
    // Initial placement will be handled by a call to PlaceNewFood from GameBoard or main
}

void Food::PlaceNewFood(const std::vector<Point>& snakeBody) {
    bool placedOnSnake;
    do {
        placedOnSnake = false;
        position.x = rand() % boardWidth;
        position.y = rand() % boardHeight;

        // Check if the new food position is on the snake's body
        for (const auto& segment : snakeBody) {
            if (segment == position) {
                placedOnSnake = true;
                break;
            }
        }
    } while (placedOnSnake);
}

Point Food::GetPosition() const {
    return position;
}
