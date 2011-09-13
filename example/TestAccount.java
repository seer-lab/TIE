

public class TestAccount {

	
	public static Thread t_d1;
	public static Thread t_d2;
	
	
	// thread class
	private static class Transactions extends Thread{
		Account x;

		public void run(){
			x.deposit(10);
			x.withdraw(10);
		}
		
	}
	
	public static void main(String[] args) throws InterruptedException{
		
		// testing deadlock
		deadlockStart();
		t_d1.join();
		t_d2.join();
		
		Account x = new Account(100);
		
		Transactions t1 = new Transactions();
		Transactions t2 = new Transactions();

		t1.x = x;
		t2.x = x;
		
		// start threads
		t1.start();
		t2.start();

		// join threads
		t1.join();
		t2.join();

		// get results, the value should be 100
		//System.out.println("Account Value: " + x.getBalance());
	
	}
	
	
	
	// deadlock stuff
	public static void deadlockStart() {
	    // These are the two resource objects we'll try to get locks for
	    final Object resource1 = "resource1";
	    final Object resource2 = "resource2";
	    // Here's the first thread.  It tries to lock resource1 then resource2
	    t_d1 = new Thread() {
	      public void run() {
	        // Lock resource 1
	        synchronized(resource1) {
	          System.out.println("Thread 1: locked resource 1");

	          // Pause for a bit, simulating some file I/O or something.  
	          // Basically, we just want to give the other thread a chance to
	          // run.  Threads and deadlock are asynchronous things, but we're
	          // trying to force deadlock to happen here...
	          try { Thread.sleep(50); } catch (InterruptedException e) {}
	          
	          // Now wait 'till we can get a lock on resource 2
	          synchronized(resource2) {
	            System.out.println("Thread 1: locked resource 2");
	          }
	        }
	      }
	    };
	    
	    // Here's the second thread.  It tries to lock resource2 then resource1
	    t_d2 = new Thread() {
	      public void run() {
	        // This thread locks resource 2 right away
	        synchronized(resource2) {
	          //System.out.println("Thread 2: locked resource 2");

	          // Then it pauses, for the same reason as the first thread does
	          try { Thread.sleep(50); } catch (InterruptedException e) {}

	          // Then it tries to lock resource1.  But wait!  Thread 1 locked
	          // resource1, and won't release it 'till it gets a lock on
	          // resource2.  This thread holds the lock on resource2, and won't
	          // release it 'till it gets resource1.  We're at an impasse. Neither
	          // thread can run, and the program freezes up.
	          synchronized(resource1) {
	           // System.out.println("Thread 2: locked resource 1");
	          }
	        }
	      }
	    };
	    
	    // Start the two threads. If all goes as planned, deadlock will occur, 
	    // and the program will never exit.
	    t_d1.start(); 
	    t_d2.start();
	  }
	
}
