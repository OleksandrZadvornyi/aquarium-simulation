# Aquarium Simulation

A Java-based interactive aquarium simulation where fish swim around and can be fed by clicking on the screen.

## Project Overview

This simulation creates a graphical window displaying an aquarium with multiple fish that move independently. Users can interact with the simulation by clicking anywhere on the screen to drop food, which the fish will swim toward.

## Features

- Animated fish that swim with natural-looking movement patterns
- User interaction through mouse clicks to drop food
- Fish that detect and move toward the nearest food
- Food particles that fall through the water
- Double-buffered graphics for smooth animation

## How to Run

1. Make sure you have Java Development Kit (JDK) installed
2. Compile the Java files:
   ```
   javac Aquarium.java Fish.java Food.java
   ```
3. Run the application:
   ```
   java Aquarium
   ```

## How to Use

- The simulation starts automatically, creating a window with swimming fish
- Click anywhere in the aquarium to drop food
- Fish will detect nearby food and swim toward it
- Close the window to exit the program

## Technical Details

- Uses Java AWT (Abstract Window Toolkit) for graphics
- Implements the `Runnable` interface for animation
- Utilizes double-buffering to prevent flickering during animation
- Handles window events for proper application termination
- Fish movement is randomized with attraction to food sources
- Food falls at a consistent rate until reaching the bottom

## Future Improvements

Potential enhancements for future versions:

- Add different types of fish with varying behaviors
- Implement fish growth/reproduction mechanics
- Add water bubbles and other decorative elements
- Create settings to adjust simulation parameters
- Add sound effects

## Requirements

- Java Runtime Environment (JRE) 8 or higher
- Minimum screen resolution: 640x480
