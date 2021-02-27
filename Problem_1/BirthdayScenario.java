// Patrick Bauer
// Processes of Parallel Distributed Processing
// 2-23-2021

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.Random;

public class BirthdayScenario
{
    public boolean cupcake = true;
    public static boolean allGuestsVisitedMaze = false;
    public List<Guest> guests;
    public List<Thread> guestsThreads;
    public final Semaphore labyrinthKey = new Semaphore(1, true);

    public void eatCupcake()
    {
        this.cupcake = false;
    }

    public void replaceCupcake()
    {
        this.cupcake = true;
    }

    public static void markAllGuestsVisitedMaze()
    {
        allGuestsVisitedMaze = true;
    }

    public static void main(String args[])
    {
        BirthdayScenario scenario = new BirthdayScenario();

        int numberOfGuests = 10;

        // Create the Guests
        scenario.guests = new ArrayList<>();
        scenario.guestsThreads = new ArrayList<>();

        for (int i = 0; i < numberOfGuests; i++)
        {
            Guest guest = new Guest(scenario, i, numberOfGuests);
            scenario.guests.add(guest);
            Thread th = new Thread(guest);
            scenario.guestsThreads.add(th);
            scenario.guestsThreads.get(i).start();
        }

        // Start the party
        boolean party = true;
        boolean guestInMaze = false;
        Random random = new Random();
        Guest selectedGuest = scenario.guests.get(0);
        while (party)
        {
            // Wait for no guests to be in labyrinth
            while (guestInMaze)
            {
                if (selectedGuest.getState() == Guest.GuestState.INMAZE)
                {
                    guestInMaze = true;
                }
                else
                {
                    guestInMaze = false;
                    System.out.println("Guest " + selectedGuest.guestNumber + " completed maze");
                }
            }

            // Check if the guests announced that they all have visited the maze
            if (allGuestsVisitedMaze)
            {
                party = false;
                break;
            }

            if (party == true)
            {
                // Pick a random guest
                int randomGuest = random.nextInt(numberOfGuests);
                selectedGuest = scenario.guests.get(randomGuest);
    
                // Give the selected guest the key to the labyrinth
                selectedGuest.setState(Guest.GuestState.INMAZE);
                guestInMaze = true;

            }
        }

        // Kick all the guests out because the party is over
        for (Thread i : scenario.guestsThreads)
        {
            i.interrupt();
        }
        System.out.println("Party Completed");
    }
}
