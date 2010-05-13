package BankRaceCondition;

public class TestAccount {

	// thread class
	private static class Transactions extends Thread{
		Account x;

		public void run(){
			x.deposit(10);
			x.withdraw(10);
		}
		
	}
	
	public static void main(String[] args) throws InterruptedException{
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
		System.out.println("Account Value: " + x.getBalance());
	
	
	    // ARRAY
		/*  
		    Transactions t[] = new Transactions[1000];
			for(int i = 0; i < t.length; i++){
				t[i] = new Transactions();
				t[i].x = x;
			}
			for(int i = 0; i < t.length; i++){
			t[i].start();
		    }
			for(int i = 0; i < t.length; i++){
			t[i].join();
		   }
		*/
	
	}
}
