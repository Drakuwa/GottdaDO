package com.gottado.utilities;

import java.util.List;
import com.gottado.dom.Task;

/**
 * a Callback interface
 * @author drakuwa
 *
 */
public interface CallBackListener {
	public void callback();
	public void callback(List<Task> tasks, String priority);
}