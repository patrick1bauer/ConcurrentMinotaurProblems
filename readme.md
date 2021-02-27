# Minotaur Problems

Patrick Bauer
COP 4520 - Concepts of Parallel and Distributed Processing
2/27/2021

Files:
Problem 1/BirthdayScenario.java
Problem 1/Guest.java
Problem 2/VaseScenario.java
Problem 2/Guest.java

## Problem 1: Minotaur's Birthday Party

The Minotaur invited N guests to his birthday party. When the guests arrived, he made the following
announcement. The guests may enter his labyrinth, one at a time and only when he invites them to do
so. At the end of the labyrinth, the Minotaur placed a birthday cupcake on a plate. When a guest
finds a way out of the labyrinth, he or she may decide to eat the birthday cupcake or leave it. If
the cupcake is eaten by the previous guest, the next guest will find the cupcake plate empty and
may request another cupcake by asking the Minotaur’s servants. When the servants bring a new
cupcake the guest may decide to eat it or leave it on the plate. The Minotaur’s only request for
each guest is to not talk to the other guests about her or his visit to the labyrinth after the
game has started. The guests are allowed to come up with a strategy prior to the beginning of the
game. There are many birthday cupcakes, so the Minotaur may pick the same guests multiple times and
ask them to enter the labyrinth. Before the party is over, the Minotaur wants to know if all of his
guests have had the chance to enter his labyrinth. To do so, the guests must announce that they
have all visited the labyrinth at least once. Now the guests must come up with a strategy to let
the Minotaur know that every guest entered the Minotaur’s labyrinth. It is known that there is
already a birthday cupcake left at the labyrinth’s exit at the start of the game. How would the
guests do this and not disappoint his generous and a bit temperamental host?

Create a program to simulate the winning strategy (protocol) where each guest is represented by one
running thread. In your program you can choose a concrete number for N or ask the user to specify N
at the start.

### Installation

1. Open a terminal
2. Navigate to the location of BirthdayScenario.java
3. Compile the program:
```bash
javac BirthdayScenario.java
```
4. Run the program:
```bash
java BirthdayScenario
```

### Output

Whenever a guest completes the maze:
```bash
Guest X completed maze
```

Whenever a guest eats a cupacke:
```bash
Guest X ate the cupcake
```

Whenever a guest replaces the cupcake:
```bash
Guest X replaced the cupcake, Y guests recorded
```

When the guests announce that they all have completed the maze:
```bash
All the guests have visited the maze!
Party Completed
```

### Proof of Correctness

This program uses a Java Semaphore lock to only allow 1 of the 10 guests (threads) access to the
maze at a time. No other guests can enter the maze when another guest has the Semaphore permit.
When the guest in the maze escapes and the cupcake is present, they eat the cupcake to mark that
they have been through the maze. That guest will not eat the cupcake or reset it again, even if
they enter the maze again. When guest 0 completes the maze, they check for the status of the
cupcake. If it has been eaten, they increment their tally for number of guests that completed the
maze and then they reset the cupcake. When guest 0 marks that n-1 guests have visited the maze,
they announce that all guests have visited the maze to the Minotaur and the birthday party is over.

### Experimental Evaluation, Efficiency

While approaching this problem, I thought of ways that multiple guests can keep track of the number
of guests that visited the maze but since they were not allowed to communicate with each other
about the maze during the party the only way to keep track was using one guest as the counter and
all other guests as markers.

## Problem 2: Minotaur's Crystal Vase

The Minotaur decided to show his favorite crystal vase to his guests in a dedicated showroom with a
single door. He did not want many guests to gather around the vase and accidentally break it. For
this reason, he would allow only one guest at a time into the showroom. He asked his guests to
choose from one of three possible strategies for viewing the Minotaur’s favorite crystal vase:

1) Any guest could stop by and check whether the showroom’s door is open at any time and try to
enter the room. While this would allow the guests to roam around the castle and enjoy the party,
this strategy may also cause large crowds of eager guests to gather around the door. A particular
guest wanting to see the vase would also have no guarantee that she or he will be able to do so and
when.

2) The Minotaur’s second strategy allowed the guests to place a sign on the door indicating when
the showroom is available. The sign would read “AVAILABLE” or “BUSY.” Every guest is responsible
to set the sign to “BUSY” when entering the showroom and back to “AVAILABLE” upon exit. That way
guests would not bother trying to go to the showroom if it is not available.

3) The third strategy would allow the quests to line in a queue. Every guest exiting the room was
responsible to notify the guest standing in front of the queue that the showroom is available.
Guests were allowed to queue multiple times.

Which of these three strategies should the guests choose? Please discuss the advantages and
disadvantages. Implement the strategy/protocol of your choice where each guest is represented by 1
running thread. You can choose a concrete number for the number of guests or ask the user to
specify it at the start.

### Installation

1. Open a terminal
2. Navigate to the location of VaseScenario.java
3. Compile the program:
```bash
javac VaseScenario.java
```
4. Run the program:
```bash
java VaseScenario
```

### Output

When the first guest in the queue is called by the Minotaur to the vase room:
```bash
Minotaur called guest X to the vase room
```

When the leaving guest calls the next guest in the queue to the vase room:
```bash
Guest X called guest Y to the vase room
```

When a guest enters the vase room:
```bash
Guest X entered the vase room
```

When a guest leaves the vase room:
```bash
Guest X left the vase room
```

When all the guests have seen the vase:
```bash
All guests have seen the vase!
Event Completed
```

### Proof of Correctness

This program uses a Java Semaphore lock to only allow one guest (thread) access to the vase room
sign up sheet at a time as well as access for one guest to the vase room at a time. To start things
off, the Minotaur opens the vase room and calls the first guest to the vase room. If it is the
first time seeing the vase, the guest increments their personal counter for number of guests that
visited the vase. After the guest leaves, it calls the next guest in the queue and passes on the
counter for number of guests that visited the vase. When the last guest visits the crystal vase
and their counter reaches the total number of guests at the party, when they leave, instead of
calling the next guest in the queue, they announce that all the guests have seen the vase and the
event ends.

### Experimental Evaluation, Efficiency

From the three strategies provided, the guests should choose strategy #3. Strategies 1 and 2 create
lots of contention around the door of the crystal vase room. When a guest tries to enter when the
door is open or when there is a sign on the door, they have no guarantee and they need to keep
trying to view the vase. This wastes lots of cycles. In strategy #3, when a guest wants to view the
vase, they just enter the queue and wait for their turn. In the mean time, they can continue to
attend the party (work on other concurrent tasks).
This program can be improved farther by restarting threads if they encounter an exception while in
the vase room to prevent a deadlock.

## Roadmap

More work can be done to increase the efficiency of the program by changing the lock systems to
a synchronized method system. This has the potential to decrease the execution time.