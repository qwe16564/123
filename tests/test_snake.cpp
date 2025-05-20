#include "../include/Snake.h"
#include "../include/Point.h" // Required by test_assertions.h for Point specialization
#include "test_assertions.h"
#include <vector>
#include <iostream> // Added this include

void TestSnakeMovement() {
    std::cout << "\n--- Testing Snake Movement ---" << std::endl;
    Snake snake(5, 5); // Initial position
    snake.SetDirection(Direction::RIGHT);
    snake.Move(); // Head at (6,5), Tail removed from (5,5)
    AssertEqual(Point(6, 5), snake.GetHead(), "Move Right");
    Assert(snake.GetBody().size() == 1, "Body size after initial move");

    snake.SetDirection(Direction::UP);
    snake.Move(); // Head at (6,4)
    AssertEqual(Point(6, 4), snake.GetHead(), "Move Up");

    // Test 180-degree turn prevention
    snake.SetDirection(Direction::DOWN); // Try to reverse
    Assert(snake.GetDirection() == Direction::UP, "Prevent 180 turn (UP to DOWN)");
    snake.Move(); // Should continue UP
    AssertEqual(Point(6, 3), snake.GetHead(), "Continue UP after failed 180 turn");
}

void TestSnakeGrowth() {
    std::cout << "\n--- Testing Snake Growth ---" << std::endl;
    Snake snake(5, 5);
    snake.SetDirection(Direction::RIGHT);

    snake.Grow(); // Signal that snake ate food
    snake.Move(); // Head at (6,5), tail (5,5) should remain
    AssertEqual(Point(6, 5), snake.GetHead(), "Grow: Head position");
    Assert(snake.GetBody().size() == 2, "Grow: Body size increased to 2");
    AssertEqual(Point(5,5), snake.GetBody()[1], "Grow: Tail position correct");


    snake.Move(); // Move again without growing: Head (7,5), Body: (7,5), (6,5)
    AssertEqual(Point(7, 5), snake.GetHead(), "Move after Grow: Head position");
    Assert(snake.GetBody().size() == 2, "Move after Grow: Body size remains 2");
    AssertEqual(Point(6,5), snake.GetBody()[1], "Move after Grow: Tail position correct");
}

void TestSnakeSelfCollision() {
    std::cout << "\n--- Testing Snake Self Collision ---" << std::endl;
    Snake snake(5, 5); // Starts at (5,5)
    snake.SetDirection(Direction::RIGHT);

    // Grow snake to 4 segments: (5,5) -> H(6,5) -> H(7,5)B(6,5) -> H(8,5)B(7,5)B(6,5)
    snake.Grow(); snake.Move(); // H(6,5), B(5,5)
    snake.Grow(); snake.Move(); // H(7,5), B(6,5), B(5,5)
    snake.Grow(); snake.Move(); // H(8,5), B(7,5), B(6,5), B(5,5)
    
    Assert(snake.GetBody().size() == 4, "Self-collision setup: body size is 4");
    Assert(!snake.CheckSelfCollision(), "No self-collision when straight");

    // Turn to cause collision: (8,5) -> (8,4) -> (7,4) -> (7,5) collides!
    snake.SetDirection(Direction::UP);    snake.Move(); // H(8,4), B(8,5), (7,5), (6,5)
    snake.SetDirection(Direction::LEFT);  snake.Move(); // H(7,4), B(8,4), (8,5), (7,5)
    snake.SetDirection(Direction::DOWN);  snake.Move(); // H(7,5), B(7,4), (8,4), (8,5) -> Collision with (7,5) part
    
    Assert(snake.CheckSelfCollision(), "Self-collision detected");
}

// Function to be called by a test runner
void RunSnakeTests() {
    TestSnakeMovement();
    TestSnakeGrowth();
    TestSnakeSelfCollision();
}
