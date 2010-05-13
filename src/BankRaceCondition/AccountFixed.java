package BankRaceCondition;

import java.util.concurrent.atomic.AtomicInteger;

public class AccountFixed{
	AtomicInteger balance = new AtomicInteger();
	
	public AccountFixed(int initial_value){
		this.balance.set(initial_value);
	}
	
	public void deposit(int amount){
		this.balance.addAndGet(amount);
	}
	
	public void withdraw(int amount){
		this.balance.addAndGet(-amount);
	}
	
	public int getBalance(){
		return this.balance.intValue();
	}
	
}
