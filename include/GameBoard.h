#ifndef GAMEBOARD_H
#define GAMEBOARD_H

#include "Snake.h" // Snake class will be aggregated
#include "Food.h"  // Food class will be aggregated

class GameBoard {
public:
    GameBoard(int width, int height);

    bool CheckWallCollision(const Point& p) const;
    // Getters for dimensions might be useful for drawing
    int GetWidth() const;
    int GetHeight() const;

    // Note: Snake and Food will be managed by the main game logic,
    // but GameBoard defines the boundaries.
    // Alternatively, GameBoard could own Snake and Food.
    // For now, let's assume GameBoard primarily handles dimensions and wall collisions.
    // The main game loop will orchestrate Snake and Food interactions with GameBoard.

private:
    int boardWidth;
    int boardHeight;
};

#endif // GAMEBOARD_H
