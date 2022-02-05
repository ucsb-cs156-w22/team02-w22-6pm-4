package edu.ucsb.cs156.team02.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity(name = "ucsb_requirements")
public class UCSBRequirement
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String requirementCode;
    private String requirementTranslation;
    private String collegeCode;
    private String objCode;

    private int courseCount;
    private int units;

    private boolean inactive;

    public static UCSBRequirement dummy(long id) {
        UCSBRequirement requirement = new UCSBRequirement();
        requirement.id = id;
        requirement.requirementCode = "AMH";
        requirement.requirementTranslation = "American History and Institution";
        requirement.collegeCode = "UCSB";
        requirement.objCode = "UG";
        requirement.courseCount = 1;
        requirement.units = 4;
        requirement.inactive = true;
        return requirement;
    }
}
