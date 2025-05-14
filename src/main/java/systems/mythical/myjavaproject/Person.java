package systems.mythical.myjavaproject;

public class Person {
    private final String firstName;
    private final String lastName;
    private final String street;
    private final String houseNumber;
    private final String postalCode;
    private final String city;
    private final int points;
    
    public Person(String firstName, String lastName, String street, String houseNumber, 
                 String postalCode, String city, int points) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("Vorname darf nicht leer sein");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nachname darf nicht leer sein");
        }
        if (street == null || street.trim().isEmpty()) {
            throw new IllegalArgumentException("Straße darf nicht leer sein");
        }
        if (houseNumber == null || houseNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Hausnummer darf nicht leer sein");
        }
        if (postalCode == null || postalCode.trim().isEmpty()) {
            throw new IllegalArgumentException("PLZ darf nicht leer sein");
        }
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("Ort darf nicht leer sein");
        }
        if (points < 0 || points > 100) {
            throw new IllegalArgumentException("Punkte müssen zwischen 0 und 100 liegen");
        }
        
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.street = street.trim();
        this.houseNumber = houseNumber.trim();
        this.postalCode = postalCode.trim();
        this.city = city.trim();
        this.points = points;
    }
    
    public Person(String[] data) {
        if (data == null || data.length != 7) {
            throw new IllegalArgumentException("Ungültige Daten für Person");
        }
        
        this.firstName = data[0].trim();
        this.lastName = data[1].trim();
        this.street = data[2].trim();
        this.houseNumber = data[3].trim();
        this.postalCode = data[4].trim();
        this.city = data[5].trim();
        
        try {
            this.points = Integer.parseInt(data[6].trim());
            if (this.points < 0 || this.points > 100) {
                throw new IllegalArgumentException("Punkte müssen zwischen 0 und 100 liegen");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Punkte müssen eine gültige Zahl sein");
        }
        
        if (this.firstName.isEmpty()) {
            throw new IllegalArgumentException("Vorname darf nicht leer sein");
        }
        if (this.lastName.isEmpty()) {
            throw new IllegalArgumentException("Nachname darf nicht leer sein");
        }
        if (this.street.isEmpty()) {
            throw new IllegalArgumentException("Straße darf nicht leer sein");
        }
        if (this.houseNumber.isEmpty()) {
            throw new IllegalArgumentException("Hausnummer darf nicht leer sein");
        }
        if (this.postalCode.isEmpty()) {
            throw new IllegalArgumentException("PLZ darf nicht leer sein");
        }
        if (this.city.isEmpty()) {
            throw new IllegalArgumentException("Ort darf nicht leer sein");
        }
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public String getStreet() {
        return street;
    }
    
    public String getHouseNumber() {
        return houseNumber;
    }
    
    public String getPostalCode() {
        return postalCode;
    }
    
    public String getCity() {
        return city;
    }
    
    public int getPunktezahl() {
        return points;
    }
    
    @Override
    public String toString() {
        return String.format("%s %s\n%s %s\n%s %s\n%d Punkte", 
            firstName, lastName, street, houseNumber, postalCode, city, points);
    }
} 