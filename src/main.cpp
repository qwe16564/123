#include <iostream>
#include <vector>
#include <chrono>
#include <thread>
// Conditional includes for input handling
#if defined(_WIN32) || defined(_WIN64)
#include <conio.h> // For _kbhit() and _getch() on Windows
#else
#include <ncurses.h> // For ncurses on Linux/macOS
#include <unistd.h> // for usleep
#endif

#include "Point.h"
#include "Snake.h"
#include "Food.h"
#include "GameBoard.h"

// Game settings
const int BOARD_WIDTH = 20;
const int BOARD_HEIGHT = 10;
const int INITIAL_SNAKE_X = BOARD_WIDTH / 2;
const int INITIAL_SNAKE_Y = BOARD_HEIGHT / 2;
const int GAME_SPEED_MS = 200; // Milliseconds per game update

// Function to initialize ncurses (Linux/macOS)
#if !(defined(_WIN32) || defined(_WIN64))
void InitNcurses() {
    initscr();            // Start ncurses mode
    cbreak();             // Line buffering disabled, Pass on everty thing to me
    noecho();             // Don't echo() while we do getch
    keypad(stdscr, TRUE); // Enable function keys like arrow keys
    nodelay(stdscr, TRUE); // Make getch non-blocking
    curs_set(0);          // Make cursor invisible
}

// Function to end ncurses (Linux/macOS)
void EndNcurses() {
    endwin();
}
#endif

// Function to clear screen (cross-platform)
void ClearScreen() {
#if defined(_WIN32) || defined(_WIN64)
    system("cls");
#else
    // Using ANSI escape codes for wider terminal compatibility than clear command
    // std::cout << "[2J[1;1H"; // Clears screen and moves cursor to top-left - previous version
    // Alternatively, if ncurses is used for drawing:
    clear(); // ncurses clear screen
#endif
}

// Simple drawing function (can be expanded)
void Draw(const GameBoard& board, const Snake& snake, const Food& food, int score) {
    ClearScreen(); // Clears screen using system("cls") or ncurses clear()
    for (int y = 0; y < board.GetHeight(); ++y) {
        for (int x = 0; x < board.GetWidth(); ++x) {
            Point currentPoint(x, y);
            if (currentPoint == snake.GetHead()) {
#if defined(_WIN32) || defined(_WIN64)
                std::cout << "H";
#else
                mvprintw(y, x, "H");
#endif
            } else {
                bool isBodyPart = false;
                const auto& body = snake.GetBody();
                // Check tail segments (excluding head)
                for (size_t i = 1; i < body.size(); ++i) {
                    if (body[i] == currentPoint) {
#if defined(_WIN32) || defined(_WIN64)
                        std::cout << "o";
#else
                        mvprintw(y, x, "o");
#endif
                        isBodyPart = true;
                        break;
                    }
                }
                if (isBodyPart) continue;

                if (currentPoint == food.GetPosition()) {
#if defined(_WIN32) || defined(_WIN64)
                    std::cout << "F";
#else
                    mvprintw(y, x, "F");
#endif
                } else {
#if defined(_WIN32) || defined(_WIN64)
                    std::cout << "."; // Empty space
#else
                    mvprintw(y, x, "."); // Empty space
#endif
                }
            }
        }
#if defined(_WIN32) || defined(_WIN64)
        std::cout << std::endl;
#endif
    }
#if defined(_WIN32) || defined(_WIN64)
    std::cout << "Score: " << score << std::endl;
#else
    mvprintw(board.GetHeight(), 0, "Score: %d", score);
#endif

    // For ncurses, refresh() would be called here if using ncurses printw functions
    #if !(defined(_WIN32) || defined(_WIN64))
    refresh(); // ncurses refresh
    #endif
}


int main() {
    GameBoard board(BOARD_WIDTH, BOARD_HEIGHT);
    Snake snake(INITIAL_SNAKE_X, INITIAL_SNAKE_Y);
    Food food(BOARD_WIDTH, BOARD_HEIGHT);
    food.PlaceNewFood(snake.GetBody()); // Initial food placement

    int score = 0;
    bool gameOver = false;
    Direction inputDirection = snake.GetDirection(); // Start with current direction

#if !(defined(_WIN32) || defined(_WIN64))
    InitNcurses();
#endif

    while (!gameOver) {
        // 1. Handle Input
#if defined(_WIN32) || defined(_WIN64)
        if (_kbhit()) {
            char ch = _getch();
            // Arrow keys in Windows might be two chars, e.g., 224 then H/K/M/P
            // For simplicity, using w/a/s/d
            if (ch == 'w' || ch == 'W') inputDirection = Direction::UP;
            else if (ch == 's' || ch == 'S') inputDirection = Direction::DOWN;
            else if (ch == 'a' || ch == 'A') inputDirection = Direction::LEFT;
            else if (ch == 'd' || ch == 'D') inputDirection = Direction::RIGHT;
            // else if (ch == 224) { // Arrow key prefix
            //     char ch2 = _getch();
            //     if (ch2 == 72) inputDirection = Direction::UP;    // Up
            //     else if (ch2 == 80) inputDirection = Direction::DOWN;  // Down
            //     else if (ch2 == 75) inputDirection = Direction::LEFT;  // Left
            //     else if (ch2 == 77) inputDirection = Direction::RIGHT; // Right
            // }
        }
#else // Linux/macOS with ncurses
        int ch = getch(); // Non-blocking due to nodelay(stdscr, TRUE)
        if (ch != ERR) {
            switch (ch) {
                case KEY_UP:    inputDirection = Direction::UP;    break;
                case KEY_DOWN:  inputDirection = Direction::DOWN;  break;
                case KEY_LEFT:  inputDirection = Direction::LEFT;  break;
                case KEY_RIGHT: inputDirection = Direction::RIGHT; break;
            }
        }
#endif
        snake.SetDirection(inputDirection);

        // 2. Update Game State
        snake.Move();

        // Check for wall collision
        if (board.CheckWallCollision(snake.GetHead())) {
            gameOver = true;
        }

        // Check for self collision
        if (!gameOver && snake.CheckSelfCollision()) {
            gameOver = true;
        }

        // Check for food consumption
        if (!gameOver && snake.GetHead() == food.GetPosition()) {
            snake.Grow();
            score++;
            food.PlaceNewFood(snake.GetBody());
        }

        // 3. Draw Game
        Draw(board, snake, food, score);


        // 4. Control Game Speed
#if defined(_WIN32) || defined(_WIN64)
        // Sleep(GAME_SPEED_MS); // This was in the prompt, but std::this_thread::sleep_for is more standard
        std::this_thread::sleep_for(std::chrono::milliseconds(GAME_SPEED_MS));
#else
        usleep(GAME_SPEED_MS * 1000); // usleep takes microseconds
#endif
    }

#if !(defined(_WIN32) || defined(_WIN64))
    EndNcurses();
#endif

    // Clear screen one last time before showing final score, especially for non-ncurses
    // For ncurses, EndNcurses() reverts terminal, so std::cout is fine.
    // For Windows, a final cls might be good if not using ncurses.
#if defined(_WIN32) || defined(_WIN64)
    ClearScreen(); 
#else
    // ncurses already ended, std::cout will work on a new line.
#endif

    std::cout << "Game Over!" << std::endl;
    std::cout << "Final Score: " << score << std::endl;

    return 0;
}
