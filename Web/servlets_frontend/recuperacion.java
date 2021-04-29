package servlets_frontend;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;

import org.json.JSONObject;

/**
 * Servlet implementation class recuperacion
 */
@WebServlet("/recuperacion")
public class recuperacion extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public recuperacion() {
        super();
        // TODO Auto-generated constructor stub
    }

    private final static String MAIL = "mhentertainment@outlook.es";
    private final static String PASSWORD = "MargaretHamilton2021";
    
    private boolean enviarCorreo(String email, String code)
    {
    	Properties props = new Properties();
    	props.put("mail.smtp.auth", "true");
    	props.put("mail.smtp.starttls.enable", "true");
    	props.put("mail.smtp.host", "smtp.office365.com");
    	props.put("mail.smtp.port", "587");
    	
    	Session session = Session.getInstance(props, new Authenticator() {
    		@Override
    		protected PasswordAuthentication getPasswordAuthentication() {
    			return new PasswordAuthentication(MAIL, PASSWORD);
    		}
    	});
    	
    	String body = "Este es el código\n" + code;
    	
    	Message message = new MimeMessage(session);
    	try {
    		message.setFrom(new InternetAddress(MAIL));
    		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
    		message.setSubject("Recuperación de la contraseña");
    		message.setText(body);
    		Transport.send(message);
    		return true;
    	} catch (Exception e) {
    		System.out.println("El correo no existe");
    		return false;
    	}
    }
    
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String phase = request.getParameter("phase");
		String email = request.getParameter("email");
		String code = request.getParameter("code");
		String newpass = request.getParameter("newpass");
		// Toca mandar el código de recuperación
		if (phase != null && phase.contentEquals("r") && email != null && !email.contentEquals(""))
		{
			Rck_conn con = new Rck_conn();
			JSONObject obj = con.connect("Recuperacion?f=r&email=" + email);
			code = obj.getString("code");
			request.setAttribute("phase", "c");
			request.setAttribute("email", email);
			if(!code.isEmpty()) {
				enviarCorreo(email, code);
			}
		} 
		// Toca comprobar el código de recuperación
		else if (phase != null && phase.contentEquals("c") && code != null && !code.contentEquals("") && email != null && !email.contentEquals(""))
		{
			Rck_conn con = new Rck_conn();
			JSONObject obj = con.connect("Recuperacion?f=c&email=" + email + "&code=" + code);
			boolean result = obj.getBoolean("result");
			// Si el código es correcto
			if (result) 
			{
				request.setAttribute("phase", "np");
			} 
			else 
			{
				request.setAttribute("phase", "c");
				request.setAttribute("error", "Código incorrecto");
			}
			request.setAttribute("email", email);
		}
		// Toca meter la nueva contraseña
		else if (phase != null && phase.contentEquals("np") && newpass != null && !newpass.contentEquals("") && email != null && !email.contentEquals(""))
		{
			Rck_conn con = new Rck_conn();
			JSONObject obj = con.connect("Recuperacion?f=np&email=" + email + "&pass=" + newpass);
			boolean result = obj.getBoolean("result");
			// Si la contraseña es válida
			if (result) 
			{
				request.setAttribute("phase", "fin");
			} 
			else 
			{
				request.setAttribute("phase", "np");
				request.setAttribute("error", "Vuelva a intentarlo");
			}
			request.setAttribute("email", email);
		}
		else
		{
			request.setAttribute("phase", phase);
			request.setAttribute("error", "Vuelva a intentarlo");
		}
		request.getRequestDispatcher("recuperacion.jsp").forward(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}

