// Patrick Bauer
// Processes of Parallel Distributed Processing
// 2-23-2021

public class Guest implements Runnable
{
    enum GuestState
    {
        ATPARTY, INMAZE, TERMINATED
    };

    private BirthdayScenario scenario;
    public GuestState state;
    public int guestNumber;
    private boolean visitedMaze;
    private boolean ateCupcake;
    private int numberOfGuests;
    private int numberOfGuestsVisitedMaze;

    public Guest(BirthdayScenario scenario, int guestNumber, int numberOfGuests)
    {
        this.guestNumber = guestNumber;
        this.numberOfGuests = numberOfGuests;
        this.scenario = scenario;
        this.state = GuestState.ATPARTY;
        this.visitedMaze = false;
        this.ateCupcake = false;
    }

    public GuestState getState()
    {
        return state;
    }

    public void setState(GuestState state)
    {
        this.state = state;
    }

    @Override
    public void run()
    {
        while(true)
        {
            // Attend party
            try
            {
                Thread.sleep(1);
            }
            catch (InterruptedException e)
            {
                setState(GuestState.TERMINATED);
                Thread.currentThread().interrupt();
                return;
            }
            
            switch (this.state)
            {
                case ATPARTY:
                {
                    // Celebrate the party!

                    // If guest 0, then check if all guests have visited the maze
                    if (this.guestNumber == 0)
                    {
                        if (this.numberOfGuests == this.numberOfGuestsVisitedMaze)
                        {
                            // Announce that all guests have visited maze
                            BirthdayScenario.markAllGuestsVisitedMaze();
                            System.out.println("All the guests have visited the maze!");
                        }
                    }
                    break;
                }
                case INMAZE:
                {
                    // Do maze
                    // Reach cupcake
                    try
                    {
                        scenario.labyrinthKey.acquire();
                        // Guest 0 protocol
                        if (this.guestNumber == 0)
                        {
                            // Check if this is the first time guest 0 has completed the maze
                            if (this.visitedMaze == false)
                            {
                                this.visitedMaze = true;
                                this.numberOfGuestsVisitedMaze++;
                            }

                            // Check if cupcake has been eaten
                            if (this.scenario.cupcake == false)
                            {
                                this.numberOfGuestsVisitedMaze++;
                                this.scenario.replaceCupcake();
                                System.out.println("Guest 0 replaced the cupcake, " + this.numberOfGuestsVisitedMaze + " guests recorded");
                            }

                        }
                        // All other guests' protocol
                        else
                        {
                            // Check if this is the first time the guest has completed the maze
                            if (this.visitedMaze == false)
                            {
                                this.visitedMaze = true;
                            }

                            // Check if cupcake is present
                            if (this.scenario.cupcake == true)
                            {
                                // Check if this guest has eaten cupcake yet
                                if (this.ateCupcake == false)
                                {
                                    this.scenario.eatCupcake();
                                    System.out.println("Guest " + this.guestNumber + " ate the cupcake");
                                }
                            }
                        }
                        // Leave maze & return key to Minotaur
                        this.scenario.labyrinthKey.release();
                    }
                    // If the guest died in maze, explain why
                    catch (InterruptedException e)
                    {
                        this.state = GuestState.TERMINATED;
                        System.out.println(e);
                        return;
                    }
                    // Return to party
                    this.state = GuestState.ATPARTY;
                    break;
                }
                case TERMINATED:
                {
                    // RIP guest
                    return;
                }
            }
        }
    }
}
