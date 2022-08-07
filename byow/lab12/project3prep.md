# Project 3 Prep

**For tessellating hexagons, one of the hardest parts is figuring out where to place each hexagon/how to easily place hexagons on screen in an algorithmic way.
After looking at your own implementation, consider the implementation provided near the end of the lab.
How did your implementation differ from the given one? What lessons can be learned from it?**

Answer: The main difference between my implementation and the provided implementation is the 
simplicity of drawing the hexagons. Instead of realizing that a hexagon basically had two 
identical halves, I used a double for loop with pretty complex logic to draw my hexagons. As for 
the tesselation, I had roughly the same logic using neighbors as the given implementation.

-----

**Can you think of an analogy between the process of tessellating hexagons and randomly generating a world using rooms and hallways?
What is the hexagon and what is the tesselation on the Project 3 side?**

Answer: The individual hexagons would be equivalent to a room or a hallway and the tesselation 
corresponds to the arrangement of the hallways and rooms.

-----
**If you were to start working on world generation, what kind of method would you think of writing first? 
Think back to the lab and the process used to eventually get to tessellating hexagons.**

Answer: The first method that I would write is a method to generate a room/hallway.

-----
**What distinguishes a hallway from a room? How are they similar?**

Answer: A hallway is different from a room in that it is one tile wide and is open on two ends. 
They are similar in that they both enclose some amount of space.
