public class Person {
    private final String name;
    private final int age;
    private final int score;

    private Person(Builder builder) {
        this.name = builder.name;
        this.age = builder.age;
        this.score = builder.score;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return String.format("Name: %s, Age: %d, Score: %d", name, age, score);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private int age;
        private int score;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder age(int age) {
            this.age = age;
            return this;
        }

        public Builder score(int score) {
            this.score = score;
            return this;
        }

        public Person build() {
            return new Person(this);
        }
    }


}
