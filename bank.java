

public class bank
{
    // instance variables - replace the example below with your own
    private int balance;
    
    public bank()
    {
        balance = 0;// initialise instance variables
        
    }
    
     public bank(int initialBalance)
    {
        balance = initialBalance;
        
    }
    
     public void deposit(int amount)
    {
        balance += amount;
        
    }
    
     public void withdrawl(int amount)
    {
        balance -= amount;
        
    }
    
    public int getBalance()
    {
        return balance;
    }

  
}
