package aud3;

public class PlatinumCheckingAccount extends InterestCheckingAccount{

    public PlatinumCheckingAccount(String name, int number, double currentAmount) {
        super(name, number, currentAmount);
    }

    @Override
    public void addInterest() {
        addAmount(getCurrentAmount() * InterestCheckingAccount.INTEREST*2);
    }
}
