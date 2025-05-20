#ifndef POINT_H
#define POINT_H

struct Point {
    int x;
    int y;

    Point(int _x = 0, int _y = 0);

    bool operator==(const Point& other) const;
};

#endif // POINT_H
