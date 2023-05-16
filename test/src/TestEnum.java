public enum TestEnum {
    FIRST_OPTION,
    SECOND_OPTION,
    THIRD_OPTION,
    FOURTH_OPTION,
    FIFTH_OPTION;
    @Override
    public String toString() {
        return switch (this.ordinal()) {
            case 0 -> "FIRST_OPTION";
            case 1 -> "SECOND_OPTION";
            case 2 -> "THIRD_OPTION";
            case 3 -> "FOURTH_OPTION";
            case 4 -> "FIFTH_OPTION";
            default -> "";
        };
    }
}
