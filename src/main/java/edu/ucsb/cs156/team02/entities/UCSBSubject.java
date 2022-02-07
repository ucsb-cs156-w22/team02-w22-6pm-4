package edu.ucsb.cs156.team02.entities;

//Imports taken from Todo.java, 
//not really sure if each one is necessary.

import javax.persistence.Entity;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name="ucsb_subjects")
public class UCSBSubject{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //private User user;

    private String subjectCode;
    private String subjectTranslation;
    private String deptCode;
    private String collegeCode;
    private String relatedDeptCode;
    private boolean inactive;

    public long getId() {
      return id;
    }
    //public void setUser(User user) {
      //this.user = user;
    //}
    public static UCSBSubject dummySubject(long id) {
      UCSBSubject subject = new UCSBSubject();
      subject.id = id;
      subject.subjectCode = "GPS";
      subject.subjectTranslation = "Global Peace and Security";
      subject.collegeCode = "UCSB";
      subject.deptCode = "GPS";
      subject.relatedDeptCode = "GPS";
      subject.inactive = true;
      return subject;
    }
}
