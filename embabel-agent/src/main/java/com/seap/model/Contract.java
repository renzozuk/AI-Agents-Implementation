package com.seap.model;


import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "contract")
public class Contract implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "final_json", columnDefinition = "TEXT")
    private String finalJson;

    @Column(name = "missing_data", columnDefinition = "TEXT")
    private String missingData;

    @Column(name = "email_resp")
    private String emailResp;

    @Column(name = "complete")
    private String complete;


    public Contract() {
    }

    public Contract(String name, String finalJson, String missingData, String emailResp, String complete) {
        this.name = name;
        this.finalJson = finalJson;
        this.missingData = missingData;
        this.emailResp = emailResp;
        this.complete = complete;
    }

    // --- Getters e Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFinalJson() {
        return finalJson;
    }

    public void setFinalJson(String finalJson) {
        this.finalJson = finalJson;
    }

    public String getMissingData() {
        return missingData;
    }

    public void setMissingData(String missingData) {
        this.missingData = missingData;
    }

    public String getEmailResp() {
        return emailResp;
    }

    public void setEmailResp(String emailResp) {
        this.emailResp = emailResp;
    }

    public String getComplete() {
        return complete;
    }

    public void setComplete(String complete) {
        this.complete = complete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contract contrato = (Contract) o;
        return Objects.equals(id, contrato.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}