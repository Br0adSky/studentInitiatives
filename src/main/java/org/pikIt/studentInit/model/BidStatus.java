package org.pikIt.studentInit.model;

public enum BidStatus {
    New("Новая"),
    Moderation("Модерация"),
    Voting_stud("Голосование_студ_состав"),
    Voting_expert("Голосование_эксперт_состав"),
    Working("Осуществление_работ"),
    Done("Выполнена");
    public final String s;

    BidStatus(String s) {
        this.s = s;
    }

    public static BidStatus getByName(String value) {
        for (BidStatus bs : values())
            if (bs.s.equalsIgnoreCase(value))
                return bs;
        throw new IllegalArgumentException();
    }

    @Override
    public String toString() {
        return s;
    }
}
