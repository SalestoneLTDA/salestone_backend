package com.salestonetech.salestone.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reports")
@Getter
@Setter
public class Report {
    @Id
    private String id;
    private String userId;
    private String s3Key;
}
