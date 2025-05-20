#include "../include/Food.h"
#include "../include/Snake.h" // For creating a dummy snake body
#include "../include/Point.h"
#include "test_assertions.h"
#include <vector>
#include <iostream>

void TestFoodPlacement() {
    std::cout << "\n--- Testing Food Placement ---" << std::endl;
    int boardWidth = 10;
    int boardHeight = 10;
    Food food(boardWidth, boardHeight);
    
    // Test 1: Placement on an empty board
    std::vector<Point> emptySnakeBody;
    food.PlaceNewFood(emptySnakeBody);
    Point foodPos1 = food.GetPosition();
    Assert(foodPos1.x >= 0 && foodPos1.x < boardWidth && foodPos1.y >= 0 && foodPos1.y < boardHeight,
           "Food placed within board boundaries (empty board)");

    // Test 2: Placement when snake occupies almost the entire board
    Snake snake(0,0); // Dummy snake for body parts
    std::vector<Point> fullSnakeBody;
    // Fill almost all cells except one, e.g., (5,5)
    for (int y = 0; y < boardHeight; ++y) {
        for (int x = 0; x < boardWidth; ++x) {
            if (x == 5 && y == 5) continue; // Leave one cell empty
            fullSnakeBody.push_back(Point(x,y));
        }
    }
    
    food.PlaceNewFood(fullSnakeBody);
    Point foodPos2 = food.GetPosition();
    AssertEqual(Point(5,5), foodPos2, "Food placed in the only available spot");

    // Test 3: Food is not placed on the snake
    // Create a snake that covers a few specific cells
    std::vector<Point> specificSnakeBody;
    specificSnakeBody.push_back(Point(1,1));
    specificSnakeBody.push_back(Point(1,2));
    specificSnakeBody.push_back(Point(1,3));

    bool foodOnSnake = false;
    for (int i=0; i<100; ++i) { // Run multiple times due to randomness
        food.PlaceNewFood(specificSnakeBody);
        Point foodPos3 = food.GetPosition();
         Assert(foodPos3.x >= 0 && foodPos3.x < boardWidth && foodPos3.y >= 0 && foodPos3.y < boardHeight,
           "Food placed within board boundaries (specific snake)");
        for(const auto& segment : specificSnakeBody) {
            if (foodPos3 == segment) {
                foodOnSnake = true;
                break;
            }
        }
        Assert(!foodOnSnake, "Food not placed on snake body segment (iteration " + std::to_string(i) + ")");
        if (foodOnSnake) break;
    }
    Assert(!foodOnSnake, "Final check: Food not placed on snake body after multiple attempts");

}

void RunFoodTests() {
    TestFoodPlacement();
}
