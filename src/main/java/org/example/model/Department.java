package org.example.model;

public class Department {
    private int id;
    private String name;
    private Double budget;

    private Department(int id, String name, Double budget) {
        this.id = id;
        this.name = name;
        this.budget = budget;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getBudget() {
        return budget;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private  int id = 0;
        private  String name = null;
        private  Double budget = null;

        private Builder() {
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder budget(double budget) {
            this.budget = budget;
            return this;
        }

        public Department build() {
            return new Department(id, name, budget);
        }

    }

    @Override
    public String toString() {
        return "Department{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", budget=" + budget +
               '}';
    }
}
