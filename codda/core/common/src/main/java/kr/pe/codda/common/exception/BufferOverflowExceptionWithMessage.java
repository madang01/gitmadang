package kr.pe.codda.common.exception;

import java.nio.BufferOverflowException;

/**
 * <pre>
 * this class is an {@link BufferOverflowException} wrapper class having a message and occurs under a controlled state. 
 * this class is distinct from the {@link BufferOverflowException}.
 * the {@link BufferOverflowException} class has no message and occurs under a uncontrolled state. 
 * </pre>
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class BufferOverflowExceptionWithMessage extends Exception {
	/**
	 * 생성자
	 * 
	 * @param errorMessage
	 *            에러 내용
	 */
	public BufferOverflowExceptionWithMessage(String errorMessage) {
		super(errorMessage);
	}
}
