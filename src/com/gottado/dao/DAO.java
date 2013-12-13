package com.gottado.dao;

import java.text.ParseException;
import java.util.List;

import com.gottado.dom.Task;

/**
 * A Data Access Object interface
 * @author drakuwa
 *
 */
public interface DAO {
	
	// Tasks CRUD
	public void addTask(Task t);
	
	// public void addAllTasks(List<Task> t);
	
	public List<Task> getAllTasks() throws ParseException;
	
	public List<Task> getAllCompletedTasks(boolean finished) throws ParseException;
	
	public List<Task> getAllTasksDueToday() throws ParseException;
	
	public List<Task> getAllTasksByPriority(String priority) throws ParseException;
	
	// public Task getTask(long id) throws ParseException;
	
	public int updateTask(Task t);
	
	public void deleteTask(Task t);
	
	public void clearExpiredTasks();
	
	public void clearCompletedTasks();
}
