package collections.Banking;
public enum InvestCategories {
    SETUP_A_BUSINESS("Setup a business"),
    OVERDRAFT_COVER("Overdraft cover"),
    INVESTMENT("Investment"),
    HAPPY_EVENT("Happy Event"),
    RENOVATE("Renovate");

    private String displayName;

    InvestCategories(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() { return displayName; }

    // Optionally and/or additionally, toString.
    @Override public String toString() { return displayName; }
}