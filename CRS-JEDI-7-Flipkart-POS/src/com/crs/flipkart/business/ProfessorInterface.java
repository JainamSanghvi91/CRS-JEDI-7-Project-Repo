package com.crs.flipkart.business;

import java.sql.SQLException;
import java.util.Vector;

import com.crs.flipkart.bean.Course;
import com.crs.flipkart.bean.EnrolledStudent;

public interface ProfessorInterface {

	/**
	 * 
	 * @param studentId
	 * @param courseCode
	 * @param gpa
	 * @param semesterId
	 * @return
	 * @throws SQLException 
	 */
	boolean addGrade(int studentId, int courseCode, double gpa, int semesterId) throws SQLException;

	/**
	 * 
	 * @param professorId
	 * @return
	 * @throws SQLException 
	 */
	Vector<EnrolledStudent> viewEnrolledStudents(int professorId) throws SQLException;

	/**
	 * 
	 * @param professorId
	 * @return
	 * @throws SQLException 
	 */
	Vector<Course> viewCourses(int professorId) throws SQLException;

	/**
	 * 
	 * @return
	 * @throws SQLException 
	 */
	Vector<Course> viewAvailableCourses() throws SQLException;

	/**
	 * 
	 * @param professorId
	 * @return
	 * @throws SQLException 
	 */
	String getProfessorById(int professorId) throws SQLException;

	/**
	 * 
	 * @param professorId
	 * @param courseSelectedId
	 * @return
	 * @throws SQLException 
	 */
	boolean addCourse(int professorId, int courseSelectedId) throws SQLException;

	/**
	 * 
	 * @param userId
	 * @return
	 * @throws SQLException 
	 */
	int getProfessorId(int userId) throws SQLException;

}