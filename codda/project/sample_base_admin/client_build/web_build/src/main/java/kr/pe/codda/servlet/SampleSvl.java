package kr.pe.codda.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SampleSvl extends HttpServlet {
	
	private static final long serialVersionUID = 8628211532775121434L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		String tmp = req.getParameter("tmp");
		
		System.out.printf("1.the parameter tmp=[%s]", tmp);
		System.out.println();
		try {
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/sample.jsp");

			dispatcher.forward(req, res);
		} catch (Exception |  Error e) {
			e.printStackTrace();
			System.out.println("1. fail to request dispatch");
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		String tmp = req.getParameter("tmp");
		
		System.out.printf("2.the parameter tmp=[%s]", tmp);
		System.out.println();
		
		try {
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/sample.jsp");

			dispatcher.forward(req, res);
		} catch (Exception |  Error e) {
			e.printStackTrace();
			System.out.println("1. fail to request dispatch");
		}
	}
}
