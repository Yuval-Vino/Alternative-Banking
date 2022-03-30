public enum InvestCategories {
    SETUP_A_BUSINESS("Setup a business"),
    OVERDRAFT_COVER("Overdraft cover"),
    INVESTMENT("Investment"),
    HAPPY_EVENT("Happy event"),
    RENOVATE("Renovate");

    private String displayName;

     private InvestCategories(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() { return displayName; }

    // Optionally and/or additionally, toString.
    @Override public String toString() { return displayName; }
}