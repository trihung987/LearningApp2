package me.trihung.learningapp2.Entity.type;

public enum EnglishLevel {
    BEGINNER(1, "Người mới bắt đầu"),
    ELEMENTARY(2, "Sơ cấp"),
    INTERMEDIATE(3, "Trung bình"),
    UPPER_INTERMEDIATE(4, "Khá"),
    ADVANCED(5, "Tốt"),
    PROFICIENT(6, "Thành thạo");

    private final int levelCode;
    private final String name;

    EnglishLevel(int levelCode, String name) {
        this.levelCode = levelCode;
        this.name = name;
    }

    public int getLevelCode() {
        return levelCode;
    }

    public String getName(){
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
