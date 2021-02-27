// Patrick Bauer
// Processes of Parallel Distributed Processing
// 2-23-2021

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;

public class VaseScenario
{
    public static boolean allGuestsSawVase = false;
    public List<Guest> guests;
    public List<Thread> guestsThreads;
    public final Semaphore vaseRoomKey = new Semaphore(1, true);
    public final Semaphore vaseRoomSignUpSheet = new Semaphore(1, true);
	public static PriorityBlockingQueue<Guest> vaseRoomQueue;

    public static void updateEventStatus()
    {
        allGuestsSawVase = true;
    }

    public static void main(String args[])
    {
        VaseScenario scenario = new VaseScenario();
		scenario.vaseRoomQueue = new PriorityBlockingQueue<>();

        // Create the Guests
        int numberOfGuests = 10;
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
        boolean event = true;
        boolean firstGuestInVaseRoom = false;
        while (event)
        {
            // Check if the guests announced that they all saw the crystal vase
            if (allGuestsSawVase)
            {
                event = false;
                break;
            }

            // Minotaur opens the vase room and calls the first guest to view the vase
            if (firstGuestInVaseRoom == false && vaseRoomQueue.peek() != null)
            {
                // Find first guest in queue
                Guest nextGuest = scenario.vaseRoomQueue.remove();
                            
                // Give them the number of guests that saw the vase
                nextGuest.setNumberOfGuestsSawVase(0);

                // Tell them that they can go view the vase now
                System.out.println("Minotaur called guest " + nextGuest.getGuestNumber() + " to the vase room");
                nextGuest.setState(Guest.GuestState.VIEWINGVASE);
                firstGuestInVaseRoom = true;
            }

            // Time passes
            try
            {
                Thread.sleep(1);
            }
            catch (InterruptedException e)
            {
                System.out.println("[Exception]: " + e);
            }
        }

        // Kick all the guests out because the event is over
        for (Thread i : scenario.guestsThreads)
        {
            i.interrupt();
        }
        System.out.println("Event Completed");
    }
}
