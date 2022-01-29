/**
 * 
 */
package com.crs.flipkart.dao;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.crs.flipkart.bean.Course;
import com.crs.flipkart.bean.GradeCard;
import com.crs.flipkart.bean.Professor;
import com.crs.flipkart.bean.Student;
import com.crs.flipkart.bean.User;
import com.crs.flipkart.constants.GenderConstant;
import com.crs.flipkart.constants.RoleConstant;
import com.crs.flipkart.constants.SQLQueriesConstant;
import com.crs.flipkart.exceptions.CourseAlreadyExistsException;
import com.crs.flipkart.exceptions.CourseNotDeletedException;
import com.crs.flipkart.exceptions.CourseNotFoundException;
import com.crs.flipkart.exceptions.StudentNotFoundForApprovalException;
import com.crs.flipkart.exceptions.ProfessorNotAddedException;
import com.crs.flipkart.exceptions.ProfessorNotFoundException;
import com.crs.flipkart.exceptions.ProfessorNotDeletedException;
import com.crs.flipkart.exceptions.UserNotAddedException;
import com.crs.flipkart.exceptions.UserIdAlreadyInUseException;

import java.util.Vector;

import com.crs.flipkart.utils.DBUtils;

/**
 * @author devanshugarg, iamshambhavi, JainamSanghvi91
 *
 */
public class AdminDaoOperation implements AdminDaoInterface {
	
	private static volatile AdminDaoOperation instance = null;
	private static Logger logger = Logger.getLogger(AdminDaoOperation.class);
	private PreparedStatement statement = null;
	Connection connection = DBUtils.getConnection();
	
	/**
	 * Default Constructor
	 */
	private AdminDaoOperation() {
		
	}
	
	/**
	 * Method to make AdminDaoOperation Singleton
	 * @return
	 */
	public static AdminDaoOperation getInstance() {
		
		if(instance == null) {
			
			synchronized(AdminDaoOperation.class) {
				
				instance = new AdminDaoOperation();
			}
		}
		return instance;
	}
	
	/**
	 * 
	 * @param user
	 * @throws UserNotAddedException
	 * @throws UserIdAlreadyInUseException
	 */
	@Override
	public void addUser(User user) throws UserNotAddedException, UserIdAlreadyInUseException {
		Connection connection = DBUtils.getConnection();
		statement = null;
		
		try {
			String sql = SQLQueriesConstant.ADD_USER_QUERY;
			statement = connection.prepareStatement(sql);
			statement.setInt(1, user.getUserId());
			statement.setString(2, user.getUserName());
			statement.setString(3, user.getUserEmailId());
			statement.setString(4, user.getUserPassword());
			statement.setString(5, user.getRole().toString());
			statement.setString(6, user.getPhoneNo());
			statement.setString(7, user.getGender().toString());
			statement.setString(8, user.getAddress());
			int row = statement.executeUpdate();
			logger.info(row + " user added.");
			if (row == 0) {
				throw new UserNotAddedException(user.getUserId()); 
			} else {
				logger.info("User with User Id " + user.getUserId() + " added.");
			}
		} catch (SQLException e) {
			logger.error("Error: " + e.getMessage());
			throw new UserIdAlreadyInUseException(user.getUserId());
		}
	}
	
	/**
	 * 
	 * @param professor
	 * @throws UserIdAlreadyInUseException
	 * @throws ProfessorNotAddedException
	 */
	@Override
	public void addProfessor(Professor professor) throws UserIdAlreadyInUseException, ProfessorNotAddedException {
		Connection connection = DBUtils.getConnection();
		try {
			this.addUser(professor);
		} catch (UserNotAddedException e) {
			logger.error("Error: " + e.getMessage());
			throw new ProfessorNotAddedException(professor.getUserId());
		} catch (UserIdAlreadyInUseException e) {
			logger.error("Error: " + e.getMessage());
			throw e;
		}
		
		statement = null;
		
		try {
			String sql = SQLQueriesConstant.ADD_PROFESSOR_QUERY;
			statement = connection.prepareStatement(sql);
			statement.setInt(1, professor.getUserId());
			statement.setInt(2, professor.getProfessorId());
			statement.setString(3, professor.getDepartment());
			statement.setString(4, professor.getDesignation());
			int row = statement.executeUpdate();
			logger.info(row + " professor added.");
			if (row == 0) {
				logger.info("Professor with Professor Id " + professor.getProfessorId() + " already exists.");
				throw new ProfessorNotAddedException(professor.getUserId());
			} else {
				logger.info("Professor with Professor Id " + professor.getProfessorId() + " added.");
			}
		} catch (SQLException e) {
			logger.error("Error: " + e.getMessage());
			throw new UserIdAlreadyInUseException(professor.getUserId());
		}
	}
	
	/**
	 * 
	 * @return
	 */
	@Override
	public Vector<Professor> viewProfessor() {
		
		statement = null;
		Connection connection = DBUtils.getConnection();
		Vector<Professor> professorList = new Vector<>();
		
		try {
			String sql = SQLQueriesConstant.VIEW_PROFESSOR_QUERY;
			statement = connection.prepareStatement(sql);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				Professor professor = new Professor();
				professor.setProfessorId(resultSet.getInt(1));
				professor.setUserName(resultSet.getString(2));
				professor.setGender(GenderConstant.stringToGender(resultSet.getString(3)));
				professor.setDepartment(resultSet.getString(4));
				professor.setDesignation(resultSet.getString(5));
				professorList.add(professor);
			}
			logger.info("Total Number of Professors in the Institute: " + professorList.size());
		} catch (SQLException e) {
			logger.error("Error: " + e.getMessage());
		}
		return professorList;
	}
	
	/**
	 * 
	 * @param professorId
	 * @throws ProfessorNotFoundException
	 * @throws ProfessorNotDeletedException
	 */
	@Override
	public void deleteProfessor(int professorId) throws ProfessorNotFoundException, ProfessorNotDeletedException {
		Connection connection = DBUtils.getConnection();
		statement = null;
		
		try {
			String sql = SQLQueriesConstant.DELETE_PROFESSOR_QUERY;
			statement = connection.prepareStatement(sql);
			statement.setInt(1, professorId);
			int row = statement.executeUpdate();
			logger.info(row + " professor deleted.");
			if (row == 0) {
				logger.info("Professor with Professor Id " + professorId + " does not exists.");
			} else {
				logger.info("Professor with Professor Id " + professorId + " deleted.");
			}
		} catch (SQLException e) {
			logger.error("Error: " + e.getMessage());
		}
	}
	
	/**
	 * 
	 * @param studentId
	 * @throws StudentNotFoundForApprovalException
	 */
	@Override
	public void approveStudentRegistration(int studentId) throws StudentNotFoundForApprovalException {
		Connection connection = DBUtils.getConnection();
		statement = null;
		
		try {
			String sql = SQLQueriesConstant.APPROVE_STUDENT_QUERY;
			statement = connection.prepareStatement(sql);
			statement.setInt(1, studentId);
			int row = statement.executeUpdate();
			logger.info(row + " student approved.");
			if (row == 0) {
				throw new StudentNotFoundForApprovalException(studentId);
			} else {
				logger.info("Student with Student Id " + studentId + " is approved.");
			}
		} catch (SQLException e) {
			logger.error("Error: " + e.getMessage());
		}
	}
	
	/**
	 * 
	 * @return
	 */
	@Override
	public Vector<Student> viewPendingAdmissions() {
		Connection connection = DBUtils.getConnection();
 		statement = null;
 		Vector<Student> pendingStudents = new Vector<>();
 		
 		try {
 			String sql = SQLQueriesConstant.VIEW_PENDING_ADMISSION_QUERY;
 			statement = connection.prepareStatement(sql);
 			ResultSet resultSet = statement.executeQuery();
 			while(resultSet.next()) {
 				Student student = new Student();
 				student.setUserId(resultSet.getInt(1));
 				student.setUserName(resultSet.getString(2));
 				student.setUserPassword(resultSet.getString(3));
 				student.setRole(RoleConstant.stringToRole(resultSet.getString(4)));
 				student.setGender(GenderConstant.stringToGender(resultSet.getString(5)));
 				student.setAddress(resultSet.getString(6));
 				student.setStudentId(resultSet.getInt(7));
 				pendingStudents.add(student);
 			}
 			logger.info(pendingStudents.size() + " students have approval pending.");
 		} catch(SQLException e) {
 			logger.error("Error " + e.getMessage());
 		}
 		return pendingStudents;
 	}
	
	/**
	 * 
	 * @param studentId
	 * @param semesterId
	 * @return
	 */
	@Override
	public Vector<GradeCard> generateGradeCard(int studentId, int semesterId) {
		Connection connection = DBUtils.getConnection();
		statement = null;
		
		Vector<GradeCard> grades = new Vector<>();
		
		try {
			String sql = SQLQueriesConstant.VIEW_COURSES_GRADE;
			statement = connection.prepareStatement(sql);
			statement.setInt(1, studentId);
			statement.setInt(2, semesterId);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				GradeCard gradeCard = new GradeCard();
				gradeCard.setCourseId(resultSet.getInt("courseId"));
				gradeCard.setSemesterId(semesterId);
				gradeCard.setGpa(resultSet.getDouble("gpa"));
				gradeCard.setStudentId(studentId);
				grades.add(gradeCard);
			}
		} catch (SQLException e) {
			logger.error("Error: " + e.getMessage());
		}
		return grades;
	}
	
	/**
	 * 
	 * @param course
	 * @throws CourseExistsAlreadyException
	 */
	@Override
	public void addCourse(Course course) throws CourseAlreadyExistsException {
		Connection connection = DBUtils.getConnection();
		statement = null;
		
		try {
			String sql = SQLQueriesConstant.ADD_COURSE_QUERY;
			statement = connection.prepareStatement(sql);
			statement.setInt(1, course.getCourseId());
 			statement.setString(2, course.getCourseName());
 			statement.setString(3, course.getCourseDescription());
 			statement.setDouble(4, course.getCourseFee());
 			statement.setInt(5, course.getCourseSeats());
			int row = statement.executeUpdate();
			logger.info(row + " course added.");
			if (row == 0) {
				throw new CourseAlreadyExistsException(course.getCourseId());
			} else {
				logger.info("Course with Course Code " + course.getCourseId() + " added.");
			}
		} catch (SQLException e) {
			logger.error("Error: " + e.getMessage());
			throw new CourseAlreadyExistsException(course.getCourseId());
		}
	}
	
	/**
	 * 
	 * @return
	 */
	@Override
	public Vector<Course> viewCourse() {
		Connection connection = DBUtils.getConnection();
		statement = null;
		
		Vector<Course> courseList = new Vector<>();
		
		try {
			String sql = SQLQueriesConstant.VIEW_COURSE_QUERY;
			statement = connection.prepareStatement(sql);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				Course course = new Course();
				course.setCourseId(resultSet.getInt(1));
 				course.setCourseName(resultSet.getString(2));
 				course.setCourseDescription(resultSet.getString(3));
 				course.setCourseFee(resultSet.getDouble(4));
 				course.setCourseSeats(resultSet.getInt(5));
				courseList.add(course);
			}
			logger.info("Total Number of Courses: " + courseList.size());
		} catch (SQLException e) {
			logger.error("Error: " + e.getMessage());
		}
		return courseList;
	}
	
	/**
	 * 
	 * @param courseId
	 * @throws CourseNotFoundException
	 * @throws CourseNotDeletedException
	 */
	@Override
	public void deleteCourse(int courseId) throws CourseNotFoundException, CourseNotDeletedException {
		Connection connection = DBUtils.getConnection();
		statement = null;
		
		try {
			String sql = SQLQueriesConstant.DELETE_COURSE_QUERY;
			statement = connection.prepareStatement(sql);
			statement.setInt(1, courseId);
			int row = statement.executeUpdate();
			logger.info(row + " course deleted.");
			if (row == 0) {
				throw new CourseNotFoundException(courseId);
			} else {
				logger.info("Course with Course Id " + courseId + " deleted.");
			}
		} catch (SQLException e) {
			logger.error("Error: " + e.getMessage());
			throw new CourseNotDeletedException(courseId);
		}
	}
	
	/**
	 * 
	 * @param studentId
	 */
	@Override
	public void setIsGenerateGrade(int studentId) {
		Connection connection = DBUtils.getConnection();
 		statement = null;

 		try {
 			String sql = SQLQueriesConstant.SET_GRADECARD_STATUS; // TODO: isGenerated field not present in any table
 			statement = connection.prepareStatement(sql);
 			statement.setInt(1, studentId);
 			statement.executeUpdate();
 			logger.info("Student with Student Id " + studentId +"'s GradeCard is generated.");
 		} catch (SQLException e) {
 			logger.error("Error: " + e.getMessage());
 		}
 	}
	
}
