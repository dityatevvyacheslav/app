package src.main.java.org.example.utils;

import java.util.ArrayList;
import java.util.List;

public class ValidatorsChain {
    private List<ValidationUtils> validators;

    public ValidatorsChain() {
        this.validators = new ArrayList<>();
        validators.add(new NameValidator());
        validators.add(new AgeValidator());
        validators.add(new ScoreValidator());
    }
    public String validate (Person person) {
        for (ValidationUtils validation : validators) {
            if (!validation.validate(person)) {
                return validation.getErrorMessage();
            }
        }
        return null;
    }
    public boolean validateAll(List<Person> persons) {
        boolean allValid = true;
        for (int i = 0; i < persons.size(); i++) {
            String error = validate(persons.get(i));
            if (error != null) {
                System.out.println("Ошибка в записи " + (i + 1) + ": " + error);
                allValid = false;
            }
        }
        return allValid;
    }
    public void addValidator(ValidationUtils validator) {
        this.validators.add(validator);
    }
}
