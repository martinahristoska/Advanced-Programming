package aud3;

public class InterestCheckingAccount extends Account implements InterestBearingAccount{
    public static final double INTEREST = 0.03;
    public InterestCheckingAccount(String name, int number, double currentAmount) {
        super(name, number, currentAmount);
    }

    @Override
    public void addInterest() {
        addAmount(getCurrentAmount() * INTEREST);
    }
}
