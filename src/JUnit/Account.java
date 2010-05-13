package JUnit;

public class Account{
	int balance = 0;
	
	public Account(int initial_value){
		this.balance = initial_value;
	}
	
	public void deposit(int amount){
		this.balance += amount;
	}
	
	public void withdraw(int amount){
		this.balance -= amount;
	}
	
	public int getBalance(){
		return this.balance;
	}
	
}
