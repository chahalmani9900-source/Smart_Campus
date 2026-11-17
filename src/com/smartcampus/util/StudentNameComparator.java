package com.smartcampus.util;

import com.smartcampus.model.Student;
import java.util.Comparator;

/**
 * Custom Comparator to sort Student objects alphabetically by Full Name.
 */
public class StudentNameComparator implements Comparator<Student> {
    @Override
    public int compare(Student s1, Student s2) {
        return s1.getFullName().compareToIgnoreCase(s2.getFullName());
    }
}
