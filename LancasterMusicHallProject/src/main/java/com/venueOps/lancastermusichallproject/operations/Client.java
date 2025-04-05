package com.venueOps.lancastermusichallproject.operations;

public class Client {
    private int clientID;
    private String companyName;
    private String contactFirstName;
    private String contactLastName;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String postcode;

    public Client(int clientID, String companyName, String contactFirstName, String contactLastName,
                  String email, String phone, String address, String city, String postcode) {
        this.clientID = clientID;
        this.companyName = companyName;
        this.contactFirstName = contactFirstName;
        this.contactLastName = contactLastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.postcode = postcode;
    }

    public int getClientID() { return clientID; }
    public void setClientID(int clientID) { this.clientID = clientID; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getContactFirstName() { return contactFirstName; }
    public void setContactFirstName(String contactFirstName) { this.contactFirstName = contactFirstName; }

    public String getContactLastName() { return contactLastName; }
    public void setContactLastName(String contactLastName) { this.contactLastName = contactLastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPostcode() { return postcode; }
    public void setPostcode(String postcode) { this.postcode = postcode; }
}
