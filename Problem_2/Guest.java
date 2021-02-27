// Patrick Bauer
// Processes of Parallel Distributed Processing
// 2-23-2021

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class Guest implements Runnable, Comparable<Guest>
{
    enum GuestState
    {
        ATEVENT, INQUEUE, VIEWINGVASE, TERMINATED
    };

    private VaseScenario scenario;
    private GuestState state;
    private int guestNumber;
    private boolean visitedMaze;
    private boolean sawVase;
    private int numberOfGuests;
    private int numberOfGuestsSawVase;
	private Random rand = new Random();

    public Guest(VaseScenario scenario, int guestNumber, int numberOfGuests)
    {
        this.guestNumber = guestNumber;
        this.numberOfGuests = numberOfGuests;
        this.numberOfGuestsSawVase = 0;
        this.scenario = scenario;
        this.state = GuestState.ATEVENT;
        this.sawVase = false;
    }

    public int compareTo(Guest x)
    {
        return 1;
    }

    public GuestState getState()
    {
        return state;
    }

    public void setState(GuestState state)
    {
        this.state = state;
    }

    public int getGuestNumber()
    {
        return this.guestNumber;
    }

    public void setNumberOfGuestsSawVase(int numberOfGuestsSawVase)
    {
        this.numberOfGuestsSawVase = numberOfGuestsSawVase;
    }

    private void admireVase()
    {
        // Oooo, Ahhhhh, crystal vase!
    }

    @Override
    public void run()
    {
        while(true)
        {
            // Time Passes
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
                case ATEVENT:
                {
                    // Celebrate the party!

                    // 1% chance to want to view the vase
                    if (rand.nextDouble() < 0.01)
                    {
                        try
                        {
                            // Enter vase room queue
                            this.scenario.vaseRoomSignUpSheet.acquire();
                            this.state = GuestState.INQUEUE;
                            this.scenario.vaseRoomQueue.add(this);
                            this.scenario.vaseRoomSignUpSheet.release();
                        }
                        catch (InterruptedException e)
                        {
                            System.out.println("[Exception] " + e);
                        }
                    }
                    break;
                }
                case INQUEUE:
                {
                    // Wait to be first in line and called by the guest leaving the vase room
                    break;
                }
                case VIEWINGVASE:
                {
                    try
                    {
                        // Enter the vase room
                        this.scenario.vaseRoomKey.acquire();
                        System.out.println("Guest " + this.guestNumber + " entered the vase room");

                        // Admire the vase
                        this.admireVase();

                        // If this is the first time viewing the vase, incriment numberOfGuestsViewedVase
                        if (this.sawVase == false)
                        {
                            this.numberOfGuestsSawVase++;
                            this.sawVase = true;
                        }
                        // Leave vaseRoom
                        this.state = GuestState.ATEVENT;
                        System.out.println("Guest " + this.guestNumber + " left the vase room");
                        this.scenario.vaseRoomKey.release();

                        // Check to see if all guests have seen the vase
                        if (this.numberOfGuestsSawVase == this.numberOfGuests)
                        {
                            // Announce that all guests have seen the vase
                            System.out.println("All guests have seen the vase!");
                            this.scenario.updateEventStatus();
                        }
                        else
                        {
                            // Find first guest in queue
                            Guest nextGuest = this.scenario.vaseRoomQueue.poll();
                            
                            // If there is no next guest in the queue, wait a little bit and then check again.
                            while (nextGuest == null)
                            {
                                try
                                {
                                    Thread.sleep(1);
                                    nextGuest = this.scenario.vaseRoomQueue.poll();
                                }
                                catch (InterruptedException e)
                                {
                                    System.out.println("[Exception] " + e);
                                }
                            }
                            
                            // Give them the number of guests that saw the vase
                            nextGuest.setNumberOfGuestsSawVase(this.numberOfGuestsSawVase);

                            // Tell them that they can go view the vase now
                            System.out.println("Guest " + this.guestNumber + " called guest " + nextGuest.getGuestNumber() + " to the vase room");
                            nextGuest.setState(GuestState.VIEWINGVASE);
                        }
                    }
                    // If the guest died in the vase room, explain why
                    catch (InterruptedException e)
                    {
                        this.state = GuestState.TERMINATED;
                        System.out.println("[Exception]: " + e);
                        return;
                    }

                    // Return to party
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
