package BankRaceCondition;


import BankRaceCondition.Account;

public class TestAccountMulti {
	
	// thread class
	private static class Transactions extends Thread{
		Account x;

		public void run(){
			x.deposit(10);
			x.withdraw(10);
		}
		
	}
	
	private static class TransactionsDeposit extends Thread{
		Account x;

		public void run(){
			x.deposit(10);
		}
		
	}
	
	private static class TransactionsNothing extends Thread{
		Account x;

		public void run(){
			
		}
		
	}
	
	
	public static void main(String[] args) throws InterruptedException{
		Account x = new Account(100);
		
		TransactionsDeposit t1 = new TransactionsDeposit();
		TransactionsNothing t2 = new TransactionsNothing();
		TransactionsNothing t3 = new TransactionsNothing();
		TransactionsNothing t4 = new TransactionsNothing();
		Transactions t5 = new Transactions();

		t1.x = x;
		t2.x = x;
		t3.x = x;
		t4.x = x;
		t5.x = x;
		
		// start threads
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		t5.start();

		// join threads
		t1.join();
		t2.join();
		t3.join();
		t4.join();
		t5.join();

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
