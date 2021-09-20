package aud3;

import java.util.Arrays;

public class Bank {
    private Account [] accounts;
    private int totalAccounts;
    private int maxAccounts;

    public Bank(int maxAccounts) {
        this.maxAccounts = maxAccounts;
        this.accounts = new Account[maxAccounts];
        this.totalAccounts = 0;
    }

    public void addAccount(Account account)
    {
        if (totalAccounts==maxAccounts)
        {
            accounts = Arrays.copyOf(accounts,maxAccounts*2);
        }
        accounts[totalAccounts++] = account;
    }

    public double totalAssets()
    {
        return Arrays.stream(accounts).mapToDouble(Account::getCurrentAmount).sum();
    }
    public void addInterestAccounts()
    {
        for (Account account: accounts)
        {
            if (account instanceof InterestBearingAccount)
            {
                InterestBearingAccount interestBearingAccount = (InterestBearingAccount) account;
                interestBearingAccount.addInterest();
            }
        }
    }
}
