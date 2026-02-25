package com.example.productapi.entity;

import com.example.productapi.listener.CustomRevisionListener;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.ModifiedEntityNames;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "revinfo")
@RevisionEntity(CustomRevisionListener.class)
@Getter
@Setter
public class CustomRevisionEntity {

    @Id
    @GeneratedValue
    @RevisionNumber
    @Column(name = "rev")
    private int id;

    @RevisionTimestamp
    @Column(name = "revtstmp")
    private long timestamp;

    @ModifiedEntityNames
    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(name = "revchanges", joinColumns = @JoinColumn(name = "rev"))
    @Column(name = "entityname")
    private Set<String> modifiedEntityNames = new HashSet<>();
}
